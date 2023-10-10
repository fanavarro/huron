package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * The Class DescriptionsPerObjectPropertyMetric.
 */
public class DescriptionsPerObjectPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	/** The Constant NAME. */
	private static final String NAME = "Descriptions per object property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tObject Property\tMetric Value\n");
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfDescriptions = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty objectProperty : super.getOntology().objectPropertiesInSignature().collect(Collectors.toList())){
			if (OntologyUtils.isObsolete(objectProperty, getOntology()) || objectProperty.isOWLTopObjectProperty()) {
				continue;
			}
			int localNumberOfdescriptions = getNumberOfDescriptions(objectProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), objectProperty.toStringID(), localNumberOfdescriptions));
			RDFUtils.createObservation(rdfModel, objectProperty.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Integer(localNumberOfdescriptions), timestamp);
			numberOfDescriptions = numberOfDescriptions + localNumberOfdescriptions;
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfDescriptions)) / numberOfEntities;
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
		return Namespaces.OQUO_NS + "DescriptionsPerObjectPropertyMetric";
	}
	
	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.DESCRIPTIONS;
	}
}
