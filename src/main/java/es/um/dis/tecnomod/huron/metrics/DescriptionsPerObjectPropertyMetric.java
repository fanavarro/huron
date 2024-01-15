package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * The Class DescriptionsPerObjectPropertyMetric.
 */
public class DescriptionsPerObjectPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	public DescriptionsPerObjectPropertyMetric() {
		super();
		// TODO Auto-generated constructor stub
	}


	public DescriptionsPerObjectPropertyMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/** The Constant NAME. */
	private static final String NAME = "Descriptions per object property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tObject Property\tMetric Value\n");
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfDescriptions = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty objectProperty : super.getOntology().objectPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if (OntologyUtils.isObsolete(objectProperty, getOntology(), this.getConfig().getImports()) || objectProperty.isOWLTopObjectProperty()) {
				continue;
			}
			int localNumberOfdescriptions = getNumberOfDescriptions(objectProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), objectProperty.toStringID(), localNumberOfdescriptions));
			RDFUtils.createObservation(rdfModel, ontologyIRI, objectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Integer(localNumberOfdescriptions), timestamp);
			numberOfDescriptions = numberOfDescriptions + localNumberOfdescriptions;
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfDescriptions)) / numberOfEntities;
		RDFUtils.createObservation(rdfModel, ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), timestamp);

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
