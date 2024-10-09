package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

/**
 * The Class DescriptionsPerPropertyMetric.
 */
public class DescriptionsPerPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	public DescriptionsPerPropertyMetric() {
		super();
		// TODO Auto-generated constructor stub
	}


	public DescriptionsPerPropertyMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Descriptions per property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfDescriptions = 0;
		int totalProperties = 0;
		
		for(OWLObjectProperty owlObjectProperty : super.getOntology().objectPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlObjectProperty, getOntology(), this.getConfig().getImports()) || owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}
			totalProperties++;
			int localNumberOfDescriptions = getNumberOfDescriptions(owlObjectProperty);
			
			this.notifyExporterListeners(ontologyIRI, owlObjectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), Integer.valueOf(localNumberOfDescriptions), timestamp, Collections.emptyList());
			numberOfDescriptions = numberOfDescriptions + localNumberOfDescriptions;
		}
		
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlDataProperty, getOntology(), this.getConfig().getImports()) || owlDataProperty.isOWLTopDataProperty()){
				continue;
			}
			totalProperties++;
			int localNumberOfDescriptions = getNumberOfDescriptions(owlDataProperty);
			
			this.notifyExporterListeners(ontologyIRI, owlDataProperty.getIRI().toString(), OWL.DatatypeProperty.getURI(), Integer.valueOf(localNumberOfDescriptions), timestamp, Collections.emptyList());
			numberOfDescriptions = numberOfDescriptions + localNumberOfDescriptions;
		}
		
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().annotationPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlAnnotationProperty, getOntology(), this.getConfig().getImports())){
				continue;
			}
			totalProperties++;
			int localNumberOfDescriptions = getNumberOfDescriptions(owlAnnotationProperty);
			
			this.notifyExporterListeners(ontologyIRI, owlAnnotationProperty.getIRI().toString(), OWL.AnnotationProperty.getURI(), Integer.valueOf(localNumberOfDescriptions), timestamp, Collections.emptyList());
			numberOfDescriptions = numberOfDescriptions + localNumberOfDescriptions;
		}
		
		double metricValue = ((double) (numberOfDescriptions)) / totalProperties;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());
		
		return new MetricResult(metricValue);
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
		return Namespaces.OQUO_NS + "DescriptionsPerPropertyMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.DESCRIPTIONS;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}
}
