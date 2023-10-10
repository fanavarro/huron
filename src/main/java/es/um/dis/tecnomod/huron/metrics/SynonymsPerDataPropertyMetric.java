package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * The Class SynonymsPerDataPropertyMetric.
 */
public class SynonymsPerDataPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	/** The Constant NAME. */
	private static final String NAME = "Synonyms per data property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tData Property\tMetric Value\n");
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfSynonyms = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty dataProperty : super.getOntology().dataPropertiesInSignature().collect(Collectors.toList())){
			if (OntologyUtils.isObsolete(dataProperty, getOntology()) || dataProperty.isOWLTopDataProperty()) {
				continue;
			}
			int localNumberOfSynonyms = getNumberOfSynonyms(dataProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), dataProperty.toStringID(), localNumberOfSynonyms));
			RDFUtils.createObservation(rdfModel, dataProperty.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Integer(localNumberOfSynonyms), timestamp);
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfSynonyms)) / numberOfEntities;
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			RDFUtils.createObservation(rdfModel, ontologyIRI.toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), timestamp);
		});
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
		return Namespaces.OQUO_NS + "SynonymsPerDataPropertyMetric";
	}



	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.SYNONYMS;
	}
}


