package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * The Class NumberOfClassesMetric.
 */
public class NumberOfClassesMetric extends Metric {
	
	/** The Constant NAME. */
	private static final String NAME = "Number of classes";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		Model rdfModel = ModelFactory.createDefaultModel();
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		double metricValue = getOntology().classesInSignature().filter(owlClass -> (!OntologyUtils.isObsolete(owlClass, getOntology()))).count();
		
		RDFUtils.createObservation(rdfModel, ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), Calendar.getInstance());
		
		return new MetricResult(metricValue, rdfModel);
	}



	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}



	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "NumberOfClassesMetric";
	}



	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.NUMBER_OF_CLASSES;
	}
	

}
