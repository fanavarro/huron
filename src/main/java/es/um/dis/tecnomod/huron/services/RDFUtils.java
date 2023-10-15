package es.um.dis.tecnomod.huron.services;

import java.util.Calendar;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;

public class RDFUtils {
	private static final String ISSUE_CLASS_IRI = Namespaces.OQUO_NS + "Issue";
	private static final String HAS_ISSUE_IRI = Namespaces.OQUO_NS + "hasIssue";
	private static final String WAS_GENERATED_BY = Namespaces.PROV_NS + "wasGeneratedBy";
	
	public static void createIssue(Model rdfModel, Property metricProperty, OWLEntity owlEntity,
			String message) {
		String issueID = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		Resource issue = rdfModel.createResource(issueID, rdfModel.createResource(ISSUE_CLASS_IRI));
		
		issue.addLiteral(RDFS.comment, message);		
		issue.addProperty(rdfModel.createProperty(WAS_GENERATED_BY), metricProperty);
		

		rdfModel.createResource(owlEntity.getIRI().toString()).addProperty(rdfModel.createProperty(HAS_ISSUE_IRI), issue);
	}
	
	public static void createObservationBak(Model rdfModel, String featureOfInterestIRI, String observablePropertyIRI, String metricUsedIRI, String instrumentIRI, String unitIRI, Object value, Calendar timestamp) {
		String observationId = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		String measurementId = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		
		Resource observation = rdfModel.createResource(observationId, rdfModel.createResource(RDFConstants.OBSERVATION));
		Property hasMeasurement = rdfModel.createProperty(RDFConstants.HAS_MEASUREMENT);
		Resource measurement = rdfModel.createResource(measurementId, rdfModel.createResource(RDFConstants.MEASUREMENT));
		
		rdfModel.add(observation, hasMeasurement, measurement);
		
		Property hasFeatureOfInterest = rdfModel.createProperty(RDFConstants.HAS_FEATURE_OF_INTEREST);
		rdfModel.add(observation, hasFeatureOfInterest, rdfModel.createResource(featureOfInterestIRI));
		
		Property hasObservedProperty = rdfModel.createProperty(RDFConstants.HAS_OBSERVED_PROPERTY);
		rdfModel.add(observation, hasObservedProperty, rdfModel.createResource(observablePropertyIRI));
		
		if (unitIRI != null) {
			Property hasUnit = rdfModel.createProperty(RDFConstants.HAS_UNIT);
			rdfModel.add(measurement, hasUnit, unitIRI);
		}
		
		Property hasValue = rdfModel.createProperty(RDFConstants.HAS_VALUE);
		rdfModel.add(measurement, hasValue, rdfModel.createTypedLiteral(value));
		
		Property measuredBy = rdfModel.createProperty(RDFConstants.MEASURED_BY);
		rdfModel.add(measurement, measuredBy, instrumentIRI);
		rdfModel.add(measurement, measuredBy, metricUsedIRI);
		
		Property hasTimestamp = rdfModel.createProperty(RDFConstants.HAS_TIMESTAMP);
		rdfModel.add(measurement, hasTimestamp, rdfModel.createTypedLiteral(timestamp));
		
	}
	
	public static void createObservation(Model rdfModel, String sourceDocumentIRI, String featureOfInterestIRI,
			String featureOfInterestTypeIRI, String observablePropertyIRI, String metricUsedIRI, String instrumentIRI, String unitIRI, Object value, Calendar timestamp) {
		/* Evaluation and evaluationSubject */
		String evaluationIRI = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		Resource evaluationSubject = rdfModel.createResource(featureOfInterestIRI, rdfModel.createResource("http://purl.org/net/EvaluationResult#EvaluationSubject"));
		Resource evaluation = rdfModel.createResource(evaluationIRI, rdfModel.createResource("http://purl.org/net/EvaluationResult#Evaluation"));
		Property evaluatedSubject = rdfModel.createProperty("http://purl.org/net/EvaluationResult#evaluatedSubject");
		rdfModel.add(evaluation, evaluatedSubject, evaluationSubject);
		Resource featureOfInterestType = rdfModel.createResource(featureOfInterestTypeIRI);
		rdfModel.add(evaluationSubject, RDF.type, featureOfInterestType);
		
		/* Instant */
		String instantIRI = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		Resource instant = rdfModel.createResource(instantIRI, rdfModel.createResource("http://www.w3.org/2006/time#Instant"));
		Property inTime = rdfModel.createProperty("http://www.w3.org/2006/time#inXSDDateTime");
		rdfModel.add(instant, inTime, rdfModel.createTypedLiteral(timestamp));
		
		/* Evaluation and instant */
		Property performedOn = rdfModel.createProperty("http://purl.org/net/EvaluationResult#performedOn");
		rdfModel.add(evaluation, performedOn, instant);
		
		/* Evaluation and EvaluationData. We used this to store the source ontology IRI */
		if (sourceDocumentIRI != null) {
			Resource evaluationData = rdfModel.createResource(sourceDocumentIRI, OWL.Ontology);
			Property inputData = rdfModel.createProperty("http://purl.org/net/EvaluationResult#inputData");
			rdfModel.add(evaluation, inputData, evaluationData);
		}
		
		/* Quality value */
		String qualityValueIRI = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		Resource qualityValue = rdfModel.createResource(qualityValueIRI, rdfModel.createResource("http://purl.org/net/EvaluationResult#QualityValue"));
		Property hasLiteralValue = rdfModel.createProperty("http://purl.org/net/EvaluationResult#hasLiteralValue");
		rdfModel.add(qualityValue, hasLiteralValue, rdfModel.createTypedLiteral(value));
		
		/* Quality value and evaluation*/
		Property obtainedFrom = rdfModel.createProperty("http://purl.org/net/EvaluationResult#obtainedFrom");
		Property producedQualityValue = rdfModel.createProperty("http://purl.org/net/EvaluationResult#producedQualityValue");
		rdfModel.add(qualityValue, obtainedFrom, evaluation);
		rdfModel.add(evaluation, producedQualityValue, qualityValue);
		
		/* Quality value and quality measure */
		Resource qualityMeasure = rdfModel.createResource(metricUsedIRI, rdfModel.createResource("http://purl.org/net/QualityModel#QualityMeasure"));
		Property forMeasure = rdfModel.createProperty("http://purl.org/net/EvaluationResult#forMeasure");
		rdfModel.add(qualityValue, forMeasure, qualityMeasure);

	}
	
	/**
	 * Return the IRI of an ontology. First, it tries to retrieve the version IRI. If is not available, it tries to retrieve the ontology IRI. If it is not available, return null.
	 * @param ontology
	 * @return
	 */
	public static String getOntologyIRI(OWLOntology ontology) {
		IRI ontologyIRI =  ontology.getOntologyID().getVersionIRI().orElse(ontology.getOntologyID().getOntologyIRI().orElse(null));
		if (ontologyIRI != null) {
			return ontologyIRI.toString();
		}
		return null;
	}
}
