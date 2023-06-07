package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.services.OntologyUtils;

/**
 * The Class SynonymsPerObjectPropertyMetric.
 */
public class SynonymsPerObjectPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	/** The Constant NAME. */
	private static final String NAME = "Synonyms per object property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tObject Property\tMetric Value\n");
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		int numberOfSynonyms = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty objectProperty : super.getOntology().objectPropertiesInSignature().collect(Collectors.toList())){
			if (OntologyUtils.isObsolete(objectProperty, getOntology()) || objectProperty.isOWLTopObjectProperty()) {
				continue;
			}
			int localNumberOfSynonyms = getNumberOfSynonyms(objectProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), objectProperty.toStringID(), localNumberOfSynonyms));
			rdfModel.createResource(objectProperty.getIRI().toString()).addLiteral(metricProperty, localNumberOfSynonyms);
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfSynonyms)) / numberOfEntities;
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			rdfModel.createResource(ontologyIRI.toString()).addLiteral(metricProperty, metricValue);
		});
		return new MetricResult(metricValue, rdfModel);
	}


	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "SynonymsPerObjectPropertyMetric";
	}
}


