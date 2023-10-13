package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * The Class NamesPerClassMetric.
 */
public class NamesPerClassMetric extends AnnotationsPerEntityAbstractMetric {
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Names per class";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tClass\tMetric Value\n");
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfNames = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().classesInSignature().collect(Collectors.toList())){
			if (OntologyUtils.isObsolete(owlClass, getOntology()) || owlClass.isOWLThing()) {
				continue;
			}
			int localNumberOfNames = getNumberOfNames(owlClass);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlClass.toStringID(), localNumberOfNames));
			RDFUtils.createObservation(rdfModel, ontologyIRI, owlClass.toStringID(), OWL.Class.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Integer(localNumberOfNames), timestamp);
			numberOfNames = numberOfNames + localNumberOfNames;
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfNames)) / numberOfEntities;
			RDFUtils.createObservation(rdfModel, ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), timestamp);

		return new MetricResult(metricValue, rdfModel);
	}
	

	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "NamesPerClassMetric";
	}
	
	public String getObservablePropertyIRI() {
		return RDFConstants.NAMES;
	}
	
	public String getInstrumentIRI() {
		return RDFConstants.HURON;
	}
	
	public String getUnitOfMeasureIRI() {
		return null;
	}
}