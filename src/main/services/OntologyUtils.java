package services;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplBoolean;

/**
 * The Class OntologyUtils.
 */
public class OntologyUtils {
	
	/**
	 * Checks if a class is obsolete or not.
	 *
	 * @param owlClass the owl class
	 * @param ontology the ontology
	 * @return true, if is obsolete
	 */
	public static boolean isObsolete(OWLClass owlClass, OWLOntology ontology) {
		Set<OWLAnnotationAssertionAxiom> axioms = owlClass.getAnnotationAssertionAxioms(ontology);
		for (OWLAnnotationAssertionAxiom axiom : axioms) {
			IRI propertyIRI = axiom.getProperty().asOWLAnnotationProperty().getIRI();
			if (propertyIRI.equals(OWLRDFVocabulary.OWL_DEPRECATED.getIRI())
					|| propertyIRI.equals(OWLRDFVocabulary.OWL_DEPRECATED_CLASS.getIRI())
					|| propertyIRI.equals(OWLRDFVocabulary.OWL_DEPRECATED_PROPERTY.getIRI())) {
				if (axiom.getValue() instanceof OWLLiteralImplBoolean) {
					OWLLiteralImplBoolean value = (OWLLiteralImplBoolean) axiom.getValue();
					return value.parseBoolean();
				}
			}
		}
		return false;
	}
}
