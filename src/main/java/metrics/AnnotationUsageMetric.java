package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;


/**
 * The Class AnnotationUsageMetric.
 */
public class AnnotationUsageMetric extends Metric {

	/** The annotation IRI. */
	private IRI annotationIRI;

	/**
	 * Instantiates a new annotation usage metric.
	 *
	 * @param annotationIRI the annotation IRI
	 */
	public AnnotationUsageMetric(IRI annotationIRI) {
		this.annotationIRI = annotationIRI;
	}

	/**
	 * Instantiates a new annotation usage metric.
	 *
	 * @param annotationIRIString the annotation IRI string
	 */
	public AnnotationUsageMetric(String annotationIRIString) {
		this.annotationIRI = IRI.create(annotationIRIString);
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		int usage = 0;
		if (getOntology().containsAnnotationPropertyInSignature(annotationIRI)) {
			OWLAnnotationProperty annotationProperty = getOntology().getOWLOntologyManager().getOWLDataFactory()
					.getOWLAnnotationProperty(annotationIRI);
			Set<OWLAxiom> referencingAxioms = getOntology().getReferencingAxioms(annotationProperty);
			for(OWLAxiom axiom : referencingAxioms){
				if(axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)){
					usage++;
				}
			}
		}
		return usage;

	}


	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return String.format("Use of %s", annotationIRI.toString());
	}
}
