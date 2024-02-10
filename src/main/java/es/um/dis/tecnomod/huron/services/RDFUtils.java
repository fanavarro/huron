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

import es.um.dis.tecnomod.huron.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;

public class RDFUtils {
	public static final String ISSUE_CLASS_IRI = Namespaces.OQUO_NS + "Issue";
	public static final String HAS_ISSUE_IRI = Namespaces.OQUO_NS + "hasIssue";
	public static final String WAS_GENERATED_BY = Namespaces.PROV_NS + "wasGeneratedBy";
	
	public static final String OBSERVATION = Namespaces.OQUO_NS + "Observation";
	public static final String FEATURE_OF_INTEREST = Namespaces.OQUO_NS + "FeatureOfInterest";
	public static final String OBSERVABLE_PROPERTY = Namespaces.OQUO_NS + "ObservableProperty";
	public static final String UNIT = Namespaces.OQUO_NS + "UnitOfMeasure";
	public static final String MEASUREMENT = Namespaces.OQUO_NS + "Measurement";
	public static final String INSTRUMENT = Namespaces.OQUO_NS + "Instrument";
	public static final String RAW_SCALE = Namespaces.OQUO_NS + "RawScale";
	public static final String RANKING_FUNCTION_HIGHER_BEST = Namespaces.QM_NS + "HigherBest";
	public static final String RANKING_FUNCTION_LOWER_BEST = Namespaces.QM_NS + "LowerBest";
	
	public static final String HAS_MEASUREMENT = Namespaces.OQUO_NS + "hasMeasurement";
	public static final String HAS_OBSERVED_PROPERTY = Namespaces.OQUO_NS + "hasObservedProperty";
	public static final String HAS_FEATURE_OF_INTEREST = Namespaces.OQUO_NS + "hasFeatureOfInterest";
	public static final String HAS_UNIT = Namespaces.OQUO_NS + "hasUnit";
	public static final String HAS_TIMESTAMP = Namespaces.OQUO_NS + "hasTimestamp";
	public static final String HAS_VALUE = Namespaces.OQUO_NS + "hasValue";
	public static final String MEASURED_BY = Namespaces.OQUO_NS + "measuredBy";
	public static final String METRIC_USED = Namespaces.OQUO_NS + "metricUsed";
	
	public static final String NAMES = Namespaces.OQUO_NS + "Names";
	public static final String DESCRIPTIONS = Namespaces.OQUO_NS + "Descriptions";
	public static final String SYNONYMS = Namespaces.OQUO_NS + "Synonyms";
	public static final String LSLD_PRINCIPLE = Namespaces.OQUO_NS + "LexicallySuggestLogicallyDefinePrinciple";
	public static final String SYSTEMATIC_NAMING_PRINCIPLE = Namespaces.OQUO_NS + "SystematicNamingPrinciple";
	public static final String NUMBER_OF_CLASSES = Namespaces.OQUO_NS + "NumberOfClasses";
	public static final String NUMBER_OF_LR = Namespaces.OQUO_NS + "NumberOfLexicalRegularities";
	public static final String NUMBER_OF_LR_CLASSES = Namespaces.OQUO_NS + "NumberOfLexicalRegularityClasses";
	public static final String NUMBER_OF_LRS_PER_CLASS = Namespaces.OQUO_NS + "NumberOfLexicalRegularitiesPerClass";
	public static final String PERCENTAGE_OF_LR_CLASSES = Namespaces.OQUO_NS + "PercentageOfLexicalRegularityClasses";
	
	public static final String HURON = Namespaces.OQUO_NS + "Huron";
	
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
		
		Resource observation = rdfModel.createResource(observationId, rdfModel.createResource(OBSERVATION));
		Property hasMeasurement = rdfModel.createProperty(HAS_MEASUREMENT);
		Resource measurement = rdfModel.createResource(measurementId, rdfModel.createResource(MEASUREMENT));
		
		rdfModel.add(observation, hasMeasurement, measurement);
		
		Property hasFeatureOfInterest = rdfModel.createProperty(HAS_FEATURE_OF_INTEREST);
		rdfModel.add(observation, hasFeatureOfInterest, rdfModel.createResource(featureOfInterestIRI));
		
		Property hasObservedProperty = rdfModel.createProperty(HAS_OBSERVED_PROPERTY);
		rdfModel.add(observation, hasObservedProperty, rdfModel.createResource(observablePropertyIRI));
		
		if (unitIRI != null) {
			Property hasUnit = rdfModel.createProperty(HAS_UNIT);
			rdfModel.add(measurement, hasUnit, unitIRI);
		}
		
		Property hasValue = rdfModel.createProperty(HAS_VALUE);
		rdfModel.add(measurement, hasValue, rdfModel.createTypedLiteral(value));
		
		Property measuredBy = rdfModel.createProperty(MEASURED_BY);
		rdfModel.add(measurement, measuredBy, instrumentIRI);
		rdfModel.add(measurement, measuredBy, metricUsedIRI);
		
		Property hasTimestamp = rdfModel.createProperty(HAS_TIMESTAMP);
		rdfModel.add(measurement, hasTimestamp, rdfModel.createTypedLiteral(timestamp));
		
	}	
	
	public static void createObservation(Model rdfModel, ObservationInfoDTO observationInfo) {
		
		String featureOfInterestIRI = observationInfo.getFeatureOfInterestIRI();
		String featureOfInterestTypeIRI = observationInfo.getFeatureOfInterestTypeIRI();
		String sourceDocumentIRI = observationInfo.getSourceDocumentIRI();
		String metricUsedIRI = observationInfo.getMetricUsedIRI();
		String scaleIRI = observationInfo.getScaleIRI();
		String scaleTypeIRI = observationInfo.getScaleTypeIRI();
		String rankingFunctionIRI = observationInfo.getRankingFunctionIRI();
		Object value = observationInfo.getValue();
		Calendar timestamp = observationInfo.getTimestamp();
		
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
			rdfModel.add(evaluationData, RDF.type, rdfModel.createResource("http://purl.org/net/EvaluationResult#EvaluationData"));
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
		
		/* Measurement scale */
		Resource measurementScale = rdfModel.createResource(scaleIRI, rdfModel.createResource(scaleTypeIRI));
		
		/* Measurement scale and quality value*/
		Property isMeasuredOnScale = rdfModel.createProperty("http://purl.org/net/EvaluationResult#isMeasuredOnScale");
		rdfModel.add(qualityValue, isMeasuredOnScale, measurementScale);
		
		/* Ranking function */
		Resource rankingFunction = rdfModel.createResource(rankingFunctionIRI, rdfModel.createResource("http://purl.org/net/QualityModel#RankingFunction"));
		
		/* Measurement scale and ranking function */
		Property hasRankingFunction = rdfModel.createProperty("http://purl.org/net/QualityModel#hasRankigFunction");
		rdfModel.add(measurementScale, hasRankingFunction, rankingFunction);

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
