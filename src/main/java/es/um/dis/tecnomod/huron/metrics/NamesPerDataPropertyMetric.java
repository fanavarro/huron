package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

/**
 * The Class NamesPerDataPropertyMetric.
 */
public class NamesPerDataPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	public NamesPerDataPropertyMetric() {
		super();
		// TODO Auto-generated constructor stub
	}



	public NamesPerDataPropertyMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}



	/** The Constant NAME. */
	private static final String NAME = "Names per data property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfNames = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if (OntologyUtils.isObsolete(owlDataProperty, getOntology(), this.getConfig().getImports()) || owlDataProperty.isOWLTopDataProperty()) {
				continue;
			}
			int localNumberOfNames = getNumberOfNames(owlDataProperty);
			
			this.notifyExporterListeners(ontologyIRI, owlDataProperty.getIRI().toString(), OWL.DatatypeProperty.getURI(), Integer.valueOf(localNumberOfNames), timestamp, Collections.emptyList());
			numberOfNames = numberOfNames + localNumberOfNames;
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfNames)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());
		
		return new MetricResult(metricValue);
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
		return Namespaces.OQUO_NS + "NamesPerDataPropertyMetric";
	}



	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.NAMES;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}
}
