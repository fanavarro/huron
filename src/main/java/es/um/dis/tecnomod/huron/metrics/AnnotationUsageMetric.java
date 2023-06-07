package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;


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
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		int usage = 0;
		if (getOntology().containsAnnotationPropertyInSignature(annotationIRI)) {
			OWLAnnotationProperty annotationProperty = getOntology().getOWLOntologyManager().getOWLDataFactory()
					.getOWLAnnotationProperty(annotationIRI);
			Set<OWLAxiom> referencingAxioms = getOntology().referencingAxioms(annotationProperty).collect(Collectors.toSet());
			for(OWLAxiom axiom : referencingAxioms){
				if(axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)){
					usage++;
				}
			}
			rdfModel.createResource(annotationProperty.getIRI().toString()).addLiteral(metricProperty, usage);
		}
		
		
		double metricValue = usage;
		return new MetricResult(metricValue, rdfModel);

	}


	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return String.format("Use of %s", annotationIRI.toString());
	}

	@Override
	public String getIRI() {
		throw new NotImplementedException(String.format("The metric %s does not have any IRI assigned yet.", getName()));
	}
}
