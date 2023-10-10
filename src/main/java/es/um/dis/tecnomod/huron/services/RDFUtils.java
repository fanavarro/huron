package es.um.dis.tecnomod.huron.services;

import java.util.Calendar;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.model.OWLEntity;

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
	
	public static void createObservation(Model rdfModel, String featureOfInterestIRI, String observablePropertyIRI, String metricUsedIRI, String instrumentIRI, String unitIRI, Object value, Calendar timestamp) {
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
}
