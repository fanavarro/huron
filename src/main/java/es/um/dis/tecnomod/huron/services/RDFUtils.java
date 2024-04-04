package es.um.dis.tecnomod.huron.services;

import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import es.um.dis.tecnomod.huron.namespaces.Namespaces;

public class RDFUtils {
	public static final String ISSUE_CLASS_IRI = Namespaces.OQUO_NS + "Issue";
	public static final String HAS_ISSUE_IRI = Namespaces.OQUO_NS + "hasIssue";
	public static final String WAS_GENERATED_BY = Namespaces.PROV_NS + "wasGeneratedBy";

	public static final String RAW_SCALE = Namespaces.OQUO_NS + "RawScale";
	public static final String RANKING_FUNCTION_HIGHER_BEST = Namespaces.QM_NS + "HigherBest";
	public static final String RANKING_FUNCTION_LOWER_BEST = Namespaces.QM_NS + "LowerBest";


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
