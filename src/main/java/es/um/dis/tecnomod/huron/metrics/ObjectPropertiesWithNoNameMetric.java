package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.RDFUtils;

public class ObjectPropertiesWithNoNameMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "ObjectProperties with no name";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tObject property\tWithNoName\n");
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		int numberOfObjectPropertiesWithNoName = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty owlObjectProperty : super.getOntology().objectPropertiesInSignature().collect(Collectors.toList())){
			if(owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}			
			int localNumberOfNames = getNumberOfNames(owlObjectProperty);
			if (localNumberOfNames == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlObjectProperty.toStringID(), true));
				RDFUtils.createObservation(rdfModel, owlObjectProperty.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(true), timestamp);
				// TODO: create issue here?
				// RDFUtils.createIssue(rdfModel, metricProperty, owlObjectProperty, String.format("The entity %s does not have any name.", owlObjectProperty.getIRI().toQuotedString()));
				numberOfObjectPropertiesWithNoName++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlObjectProperty.toStringID(), false));
				RDFUtils.createObservation(rdfModel, owlObjectProperty.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(false), timestamp);
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfObjectPropertiesWithNoName)) / numberOfEntities;
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			rdfModel.createResource(ontologyIRI.toString()).addLiteral(metricProperty, metricValue);
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
		return Namespaces.OQUO_NS + "ObjectPropertiesWithNoNameMetric";
	}


	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.NAMES;
	}

}
