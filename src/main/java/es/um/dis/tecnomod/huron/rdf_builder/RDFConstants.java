package es.um.dis.tecnomod.huron.rdf_builder;

import es.um.dis.tecnomod.huron.namespaces.Namespaces;

public class RDFConstants {
	public static final String OBSERVATION = Namespaces.OQUO_NS + "Observation";
	public static final String FEATURE_OF_INTEREST = Namespaces.OQUO_NS + "FeatureOfInterest";
	public static final String OBSERVABLE_PROPERTY = Namespaces.OQUO_NS + "ObservableProperty";
	public static final String UNIT = Namespaces.OQUO_NS + "UnitOfMeasure";
	public static final String MEASUREMENT = Namespaces.OQUO_NS + "Measurement";
	public static final String INSTRUMENT = Namespaces.OQUO_NS + "Instrument";
	
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
	
	public static final String HURON = Namespaces.OQUO_NS + "Huron";
}
