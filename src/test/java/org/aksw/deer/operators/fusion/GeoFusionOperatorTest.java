/**
 * 
 */
package org.aksw.deer.operators.fusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.aksw.deer.io.Reader;
import org.aksw.deer.operators.fusion.GeoFusionOperator.GeoFusionAction;
import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author wauer
 *
 */
public class GeoFusionOperatorTest {

//	private static final String TEST_SUBJECT = "http://example.org/deer/geofusion/r1";

	private Reader reader;
	private Model firstModel, secondModel, expectedModel;
	private GeoFusionOperator operator;

	@Before
	public void setUp() {
		reader = new Reader();
		firstModel = reader.readModel("src/test/resources/geofusion/firstSource.ttl");
		secondModel = reader.readModel("src/test/resources/geofusion/secondSource.ttl");
		operator = new GeoFusionOperator();
	}

	/**
	 * Test method for
	 * {@link org.aksw.deer.operators.fusion.GeoFusionOperator#process(java.util.List, java.util.Map)}
	 * .
	 */
	@Test
	public void testProcessMostDetailedMerge() {
		expectedModel = reader.readModel("src/test/resources/geofusion/expectedMostDetailedMerge.ttl");
		List<Model> models = Lists.newArrayList(firstModel, secondModel);

		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("fusionAction", GeoFusionAction.takeMostDetailed.name());
		List<Model> result = operator.process(models, parameters);
		assertEquals(result.size(), 1);
		assertTrue(result.get(0).isIsomorphicWith(expectedModel));
		// assertTrue(result.get(0).getResource(TEST_SUBJECT).hasProperty(new
		// PropertyImpl(firstModel.expandPrefix("geo:lat"))));
		// NodeIterator latitudeNodes = result.get(0).listObjectsOfProperty(new
		// ResourceImpl(TEST_SUBJECT), new
		// PropertyImpl(firstModel.expandPrefix("geo:lat")));
		// assertTrue(latitudeNodes.hasNext());
		// assertEquals("51.3487",
		// latitudeNodes.next().asLiteral().getLexicalForm());
	}

	@Test
	public void testProcessTakeA() {
		expectedModel = reader.readModel("src/test/resources/geofusion/expectedTakeA.ttl");
		List<Model> models = Lists.newArrayList(firstModel, secondModel);

		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("fusionAction", GeoFusionAction.takeA.name());
		parameters.put("mergeOtherStatements", "false");
		List<Model> result = operator.process(models, parameters);
		assertEquals(result.size(), 1);
		assertTrue(result.get(0).isIsomorphicWith(expectedModel));
	}

	@Test
	public void testProcessTakeBMerge() {
		expectedModel = reader.readModel("src/test/resources/geofusion/expectedTakeBMerge.ttl");
		List<Model> models = Lists.newArrayList(firstModel, secondModel);

		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("fusionAction", GeoFusionAction.takeB.name());
		List<Model> result = operator.process(models, parameters);
		assertEquals(result.size(), 1);
		assertTrue(result.get(0).isIsomorphicWith(expectedModel));
	}

	@Test
	public void testProcessTakeAllMerge() {
		expectedModel = reader.readModel("src/test/resources/geofusion/expectedTakeAllMerge.ttl");
		List<Model> models = Lists.newArrayList(firstModel, secondModel);

		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("fusionAction", GeoFusionAction.takeAll.name());
		List<Model> result = operator.process(models, parameters);
		assertEquals(result.size(), 1);
		assertTrue(result.get(0).isIsomorphicWith(expectedModel));
	}

}
