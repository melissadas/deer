package org.aksw.deer.enrichments;

import org.aksw.faraday_cage.engine.ValidatableParameterMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class PredicateConformationEnrichmentOperatorTest {

  private PredicateConformationEnrichmentOperator op;
  private Model input, expected;
  private ValidatableParameterMap expectedParameters;

  @Before
  public void setUp() {
    input = ModelFactory.createDefaultModel();
    expected = ModelFactory.createDefaultModel();
    input.read(new StringReader(
      "@prefix ex: <http://example.org/> ." +
        "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ." +
        "ex:subject rdfs:comment \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ." +
        "ex:subject2 rdfs:comment \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ." +
        "ex:subject3 rdfs:comment \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ." +
        "ex:subject4 rdfs:label \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ." +
        "ex:subject5 rdfs:label \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ."
    ), null, "TTL");
    expected.read(new StringReader(
      "@prefix ex: <http://example.org/> ." +
        "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ." +
        "ex:subject rdfs:label \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ." +
        "ex:subject2 rdfs:label \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ." +
        "ex:subject3 rdfs:label \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ." +
        "ex:subject4 rdfs:comment \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ." +
        "ex:subject5 rdfs:comment \"Goethe lived in Leipzig. Amazon has a parcel center in Leipzig.\" ."
    ), null, "TTL");
    op = new PredicateConformationEnrichmentOperator();
    expectedParameters = op.createParameterMap();
    expectedParameters.add(PredicateConformationEnrichmentOperator.OPERATION, expectedParameters.createResource()
      .addProperty(PredicateConformationEnrichmentOperator.SOURCE_PREDICATE, RDFS.comment)
      .addProperty(PredicateConformationEnrichmentOperator.TARGET_PREDICATE, RDFS.label)
    ).add(PredicateConformationEnrichmentOperator.OPERATION, expectedParameters.createResource()
      .addProperty(PredicateConformationEnrichmentOperator.SOURCE_PREDICATE, RDFS.label)
      .addProperty(PredicateConformationEnrichmentOperator.TARGET_PREDICATE, RDFS.comment)
    ).init();
    op.initDegrees(1,1);
    op.initPluginId(ResourceFactory.createResource("urn:ex/test/pc-test"));
  }

  @Test
  public void safeApply() {
    op.initParameters(expectedParameters);
    Model actual = op.apply(List.of(input)).get(0);
    assertTrue("It should conform rdfs:comment to rdfs:label predicate.", expected.isIsomorphicWith(actual));
  }

  @Test
  public void predictApplicability() {
    double applicability = op.predictApplicability(List.of(input), expected);
    assertEquals("It should detect perfect applicability.", 1d, applicability, 0.01d);
  }

  @Test
  public void learnParameterMap() {
    ValidatableParameterMap actualLearned = op.learnParameterMap(List.of(input), expected, null);
    Resource id = op.getId();
    assertTrue("It should learn the appropriate configuration", actualLearned.parametrize(id).isIsomorphicWith(expectedParameters.parametrize(id)));
  }

  @Test
  public void reverseApply() {
    Model actual = op.reverseApply(List.of(input), expected).get(0);
    assertTrue("It should revert the operation.", input.isIsomorphicWith(actual));
  }

}