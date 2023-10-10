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
import es.um.dis.tecnomod.huron.services.RDFUtils;

public class DataPropertiesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "DataProperties with no description";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tDataProperty\tWithNoDescription\n");
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfDataPropertiesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature().collect(Collectors.toList())){
			if(owlDataProperty.isOWLTopDataProperty()){
				continue;
			}			
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlDataProperty);
			if (localNumberOfDescriptions == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlDataProperty.toStringID(), true));
				RDFUtils.createObservation(rdfModel, owlDataProperty.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(true), timestamp);
				numberOfDataPropertiesWithNoDescription++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlDataProperty.toStringID(), false));
				RDFUtils.createObservation(rdfModel, owlDataProperty.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(false), timestamp);
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfDataPropertiesWithNoDescription)) / numberOfEntities;
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			RDFUtils.createObservation(rdfModel, ontologyIRI.toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), timestamp);
		});
		return new MetricResult(metricValue, rdfModel);		
	}

	@Override
	public String getName() {
		return METRIC_NAME;
	}

	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "DataPropertiesWithNoDescriptionMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.DESCRIPTIONS;
	}

}
