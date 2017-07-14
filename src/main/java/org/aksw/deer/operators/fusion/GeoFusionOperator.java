package org.aksw.deer.operators.fusion;

import java.util.List;
import java.util.Map;

import org.aksw.deer.operators.DeerOperator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.vocabulary.OWL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Basic implementation of a fusion operator for geospatial properties. Expects
 * two source models.
 * 
 * @author wauer
 *
 */
public class GeoFusionOperator implements DeerOperator {

	private static final Logger logger = LoggerFactory.getLogger(GeoFusionOperator.class);

	/**
	 * required parameter, value is one of {@link GeoFusionAction}
	 */
	private static final String FUSION_ACTION_PARAM = "fusionAction";
	/**
	 * optional parameter, boolean, default <code>true</code>
	 */
	private static final String MERGE_OTHER_STATEMENTS_PARAM = "mergeOtherStatements";

	/**
	 * Geospatial fusion actions:
	 * <ul>
	 * <li>{@link #takeA} always use geometry from first model</li>
	 * <li>{@link #takeB} always use geometry from second model, if ambiguous
	 * take most detailed</li>
	 * <li>{@link #takeAll} merge all geometries</li>
	 * <li>{@link #takeA} use most detailed geometry from any model, e.g., in
	 * terms of lexical length of latitude and longitude values</li>
	 * </ul>
	 * 
	 * @author wauer
	 *
	 */
	public enum GeoFusionAction {
		takeA, takeB, takeAll, takeMostDetailed
	}

	/**
	 * Helper class storing a candidate geometry
	 * 
	 * @author wauer
	 *
	 */
	protected class CandidateGeometry {

		private final Resource subject;
		private final Model geometryModel;

		public CandidateGeometry(Resource subject, Model geometryModel) {
			super();
			this.subject = subject;
			this.geometryModel = geometryModel;
		}

		public Resource getSubject() {
			return subject;
		}

		public Model getGeometryModel() {
			return geometryModel;
		}

		@Override
		public String toString() {
			return "CandidateGeometry [subject=" + subject + ", geometryModel=" + geometryModel + "]";
		}

	}

	/**
	 * Computes the geospatial fusion of the given models.
	 */
	@Override
	public List<Model> process(List<Model> models, Map<String, String> parameters) {
		logger.info("Invoking GeoFusionOperator of {} models with parameters {}", models.size(), parameters);

		try {
			logger.info(" Model A: {}", models.get(0));
			logger.info(" Model B: {}", models.get(1));
		} catch (Exception e) {
			logger.error("GeoFusionOperator expected two models, failed:", e);
			return Lists.newArrayList();
		}

		GeoFusionAction action = GeoFusionAction.valueOf(parameters.get(FUSION_ACTION_PARAM));
		boolean mergeOtherStatements = Boolean.valueOf(parameters.getOrDefault(MERGE_OTHER_STATEMENTS_PARAM, "true"));

		// Fuse geometries
		Model targetModel = ModelFactory.createDefaultModel();
		List<Resource> visitedResources = Lists.newArrayList();
		ResIterator subjects = models.get(0).listSubjects();
		while (subjects.hasNext()) {
			Resource subject = subjects.next();
			List<Resource> sameAsResources = getSameAsResources(models.get(0), subject);
			List<CandidateGeometry> candidateGeometries = getCandidateGeometries(models.get(0), models.get(1), subject,
					sameAsResources);

			fuseCandidateGeometries(models.get(0), models.get(1), subject, sameAsResources, candidateGeometries, action,
					mergeOtherStatements, targetModel);

			visitedResources.add(subject);
			visitedResources.addAll(sameAsResources);
		}

		addNonVisitedResources(models.get(1), targetModel, visitedResources, mergeOtherStatements);

		logger.info("Target model returned by GeoFusionOperator: {}", targetModel);

		return Lists.newArrayList(targetModel);
	}

	private void fuseCandidateGeometries(Model model, Model model2, Resource subject, List<Resource> sameAsResources,
			List<CandidateGeometry> candidateGeometries, GeoFusionAction action, boolean mergeOtherStatements,
			Model targetModel) {
		CandidateGeometry selectedCandidateGeometry = selectCandidateGeometry(model, candidateGeometries, subject,
				action);
		logger.info("Selected candidate geometry: {}", selectedCandidateGeometry);
		// if not all geometries are taken, compute dropped geometries
		Model droppedGeometries = action.equals(GeoFusionAction.takeAll) ? ModelFactory.createDefaultModel()
				: getDroppedGeometries(candidateGeometries, selectedCandidateGeometry);

		if (!mergeOtherStatements) {
			// only use selected geometry statements, possibly adapted to
			// subject
			StmtIterator geometryStatements = selectedCandidateGeometry.getGeometryModel().listStatements();
			while (geometryStatements.hasNext()) {
				Statement statement = geometryStatements.next();
				targetModel.add(subject, statement.getPredicate(), statement.getObject());
			}
			return;
		}

		// add all statements of subject from model if they haven't been dropped
		StmtIterator subjectStatements = model.listStatements(subject, null, (RDFNode) null);
		while (subjectStatements.hasNext()) {
			Statement statement = subjectStatements.next();
			if (!droppedGeometries.contains(statement)) {
				targetModel.add(statement);
			}
		}
		// iterate over all sameAs resources from model2 and fuse them if they
		// haven't been dropped
		for (Resource sameAsResource : sameAsResources) {
			StmtIterator sameAsStatements = model2.listStatements(sameAsResource, null, (RDFNode) null);
			while (sameAsStatements.hasNext()) {
				Statement statement = sameAsStatements.next();
				if (!droppedGeometries.contains(statement)) {
					targetModel.add(subject, statement.getPredicate(), statement.getObject());
				}
			}
		}
	}

	private void addNonVisitedResources(Model model2, Model targetModel, List<Resource> visitedResources,
			boolean mergeOtherStatements) {
		// add non-visited resources
		List<Resource> nonVisitedSubjects = model2.listSubjects().toList();
		nonVisitedSubjects.removeAll(visitedResources);
		for (Resource nonVisitedSubject : nonVisitedSubjects) {
			if (mergeOtherStatements) {
				targetModel.add(model2.listStatements(nonVisitedSubject, null, (RDFNode) null));
			} else {
				// extract geometry
				Model geometryModel = getGeometry(model2, nonVisitedSubject);
				targetModel.add(geometryModel);
			}
		}
	}

	private CandidateGeometry selectCandidateGeometry(Model model, List<CandidateGeometry> candidateGeometries,
			Resource subject, GeoFusionAction action) {
		CandidateGeometry selected = null;
		int score = -1; // score for most detailed geometry
		if (action.equals(GeoFusionAction.takeAll)) {
			Model mergedGeometries = ModelFactory.createDefaultModel();
			for (CandidateGeometry candidateGeometry : candidateGeometries) {
				mergedGeometries.add(candidateGeometry.geometryModel);
			}
			return new CandidateGeometry(subject, mergedGeometries);
		}
		for (CandidateGeometry candidateGeometry : candidateGeometries) {
			boolean isFromA = candidateGeometry.subject.equals(subject);
			if (action.equals(GeoFusionAction.takeA) && isFromA) {
				return candidateGeometry;
			}
			if (action.equals(GeoFusionAction.takeB) && isFromA) {
				continue;
			}
			int candidateScore = computeCandidateScore(model, candidateGeometry);
			if (candidateScore > score) {
				score = candidateScore;
				selected = candidateGeometry;
			}
		}
		return selected;
	}

	private Model getDroppedGeometries(List<CandidateGeometry> candidateGeometries,
			CandidateGeometry selectedCandidateGeometry) {
		Model droppedGeometriesModel = ModelFactory.createDefaultModel();
		for (CandidateGeometry candidateGeometry : candidateGeometries) {
			if (!candidateGeometry.getSubject().equals(selectedCandidateGeometry.getSubject())) {
				droppedGeometriesModel.add(candidateGeometry.getGeometryModel());
			}
		}
		return droppedGeometriesModel;
	}

	private int computeCandidateScore(Model model, CandidateGeometry candidateGeometry) {
		NodeIterator latitudeNodes = candidateGeometry.getGeometryModel().listObjectsOfProperty(null,
				new PropertyImpl(model.expandPrefix("geo:lat")));
		NodeIterator longitudeNodes = candidateGeometry.getGeometryModel().listObjectsOfProperty(null,
				new PropertyImpl(model.expandPrefix("geo:long")));
		int latScore = -1, longScore = -1;
		// very simple heuristic: if both latitude and longitude are there,
		// score is the sum of both literal lengths
		while (latitudeNodes.hasNext()) {
			RDFNode latitudeNode = latitudeNodes.next();
			try {
				latScore = latitudeNode.asLiteral().getLexicalForm().length();
			} catch (Exception e) {
				logger.warn("Unexpected issue parsing latitude literal node " + latitudeNode, e);
			}
		}
		if (latScore == -1) {
			logger.warn("No latitude node found in {}, returning score -1", candidateGeometry);
			return -1;
		}
		while (longitudeNodes.hasNext()) {
			RDFNode longitudeNode = longitudeNodes.next();
			try {
				longScore = longitudeNode.asLiteral().getLexicalForm().length();
			} catch (Exception e) {
				logger.warn("Unexpected issue parsing longitude literal node " + longitudeNode, e);
			}
		}
		if (longScore == -1) {
			logger.warn("No longitude node found in {}, returning score -1", candidateGeometry);
			return -1;
		}
		return latScore + longScore;
	}

	private Model getGeometry(Model model, Resource subject) {
		List<Statement> latitudes = model
				.listStatements(subject, new PropertyImpl(model.expandPrefix("geo:lat")), (RDFNode) null).toList();
		List<Statement> longitudes = model
				.listStatements(subject, new PropertyImpl(model.expandPrefix("geo:long")), (RDFNode) null).toList();
		return ModelFactory.createDefaultModel().add(latitudes).add(longitudes);
	}

	private List<CandidateGeometry> getCandidateGeometries(Model sourceA, Model sourceB, Resource subject,
			List<Resource> sameAsResources) {
		List<CandidateGeometry> candidateGeometries = Lists.newArrayList();
		Model sourceAGeometry = getGeometry(sourceA, subject);
		if (!sourceAGeometry.isEmpty()) {
			// add geometry from source A
			candidateGeometries.add(new CandidateGeometry(subject, sourceAGeometry));
		}
		// get sameAs subjects of sourceB
		for (Resource sameAs : sameAsResources) {
			// if they have a geometry, add them as candidates
			Model sameAsGeometry = getGeometry(sourceB, sameAs);
			if (!sameAsGeometry.isEmpty()) {
				candidateGeometries.add(new CandidateGeometry(sameAs, sameAsGeometry));
			}
		}
		return candidateGeometries;
	}

	private List<Resource> getSameAsResources(Model sourceA, Resource subject) {
		List<RDFNode> sameAsNodes = sourceA.listObjectsOfProperty(subject, OWL.sameAs).toList();
		List<Resource> sameAsResources = Lists.newArrayList();
		for (RDFNode rdfNode : sameAsNodes) {
			if (rdfNode.isURIResource()) {
				sameAsResources.add(rdfNode.asResource());
			}
		}
		return sameAsResources;
	}

	@Override
	public List<String> getParameters() {
		return Lists.newArrayList(FUSION_ACTION_PARAM, MERGE_OTHER_STATEMENTS_PARAM);
	}

	@Override
	public List<String> getNecessaryParameters() {
		return Lists.newArrayList(FUSION_ACTION_PARAM);
	}

}
