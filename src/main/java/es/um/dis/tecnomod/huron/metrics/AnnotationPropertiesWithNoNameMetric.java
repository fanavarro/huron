package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.RDFUtils;


public class AnnotationPropertiesWithNoNameMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "AnnotationProperties with no name";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tAnnotationProperty\tWithNoName\n");
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		int numberOfAnnotationPropertiesWithNoName = 0;
		int numberOfEntities = 0;
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().annotationPropertiesInSignature().collect(Collectors.toList())){
			int localNumberOfNames = getNumberOfNames(owlAnnotationProperty);
			if (localNumberOfNames == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlAnnotationProperty.toStringID(), true));
				RDFUtils.createObservation(rdfModel, owlAnnotationProperty.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(true), timestamp);

				numberOfAnnotationPropertiesWithNoName++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlAnnotationProperty.toStringID(), false));
				rdfModel.createResource(owlAnnotationProperty.getIRI().toString()).addLiteral(metricProperty, false);
				RDFUtils.createObservation(rdfModel, owlAnnotationProperty.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(false), timestamp);

			}
			numberOfEntities ++;
		}
		double metricValue = ((double) (numberOfAnnotationPropertiesWithNoName)) / numberOfEntities;
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
		return Namespaces.OQUO_NS + "AnnotationPropertiesWithNoNameMetric";
	}


	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.NAMES;
	}
}
