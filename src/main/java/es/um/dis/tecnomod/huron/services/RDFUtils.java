package es.um.dis.tecnomod.huron.services;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import es.um.dis.tecnomod.oquo.utils.Namespaces;

public class RDFUtils {

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

	/**
	 * Return the IRI of an ontology. First, it tries to retrieve the version IRI.
	 * If is not available, it tries to retrieve the ontology IRI. If it is not
	 * available, return null.
	 * 
	 * @param ontology
	 * @return
	 */
	public static String getOntologyIRI(OWLOntology ontology) {
		IRI ontologyIRI = ontology.getOntologyID().getVersionIRI()
				.orElse(ontology.getOntologyID().getOntologyIRI().orElse(null));
		if (ontologyIRI != null) {
			return ontologyIRI.toString();
		}
		return null;
	}
}
