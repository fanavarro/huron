package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * The Class SynonymsPerPropertyMetric.
 */
public class SynonymsPerPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	public SynonymsPerPropertyMetric() {
		super();
		// TODO Auto-generated constructor stub
	}


	public SynonymsPerPropertyMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}


	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Synonyms per property";
	
	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tProperty\tMetric Value\n");
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfSynonyms = 0;
		int totalProperties = 0;
		
		for(OWLObjectProperty owlObjectProperty : super.getOntology().objectPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlObjectProperty, getOntology(), this.getConfig().getImports()) || owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlObjectProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlObjectProperty.toStringID(), localNumberOfSynonyms));
			this.notifyExporterListeners(ontologyIRI, owlObjectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), Integer.valueOf(localNumberOfSynonyms), timestamp);
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlDataProperty, getOntology(), this.getConfig().getImports()) || owlDataProperty.isOWLTopDataProperty()){
				continue;
			}
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlDataProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlDataProperty.toStringID(), localNumberOfSynonyms));
			this.notifyExporterListeners(ontologyIRI, owlDataProperty.getIRI().toString(), OWL.DatatypeProperty.getURI(), Integer.valueOf(localNumberOfSynonyms), timestamp);
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().annotationPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlAnnotationProperty, getOntology(), this.getConfig().getImports())){
				continue;
			}
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlAnnotationProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlAnnotationProperty.toStringID(), localNumberOfSynonyms));
			this.notifyExporterListeners(ontologyIRI, owlAnnotationProperty.getIRI().toString(), OWL.AnnotationProperty.getURI(), Integer.valueOf(localNumberOfSynonyms), timestamp);
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		
		double metricValue = ((double) (numberOfSynonyms)) / totalProperties;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp);
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
		return Namespaces.OQUO_NS + "SynonymsPerPropertyMetric";
	}


	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.SYNONYMS;
	}

}
