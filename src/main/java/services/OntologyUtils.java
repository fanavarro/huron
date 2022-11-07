package services;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
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
	 * @param owlEntity the owl class
	 * @param ontology the ontology
	 * @return true, if is obsolete
	 */
	public static boolean isObsolete(OWLEntity owlEntity, OWLOntology ontology) {
		Set<OWLAnnotationAssertionAxiom> axioms = owlEntity.getAnnotationAssertionAxioms(ontology);
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
	
	/**
	 * Gets all the annotations assertion axioms of one entity type from an ontology.   
	 * 
	 * @param entity
	 * @param ontology
	 * @param includeImports
	 * @return a set of OWLAnnotationAssertionAxiom
	 */
	public static Set<OWLAnnotationAssertionAxiom> getOWLAnnotationAssertionAxiom(OWLEntity entity, OWLOntology ontology, boolean includeImports) {
		Set<OWLAnnotationAssertionAxiom> annotationAssertionAxioms = entity.getAnnotationAssertionAxioms(ontology);
		if (includeImports) {
			for (OWLOntology importedOntology : ontology.getImports()) {
				Set<OWLAnnotationAssertionAxiom> importedAnnotationAssertionAxioms = entity.getAnnotationAssertionAxioms(importedOntology);
				annotationAssertionAxioms.addAll(importedAnnotationAssertionAxioms);
			}
		}
		return annotationAssertionAxioms;
	}
}
