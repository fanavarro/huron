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
 * The Class SynonymsPerDataPropertyMetric.
 */
public class SynonymsPerDataPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	public SynonymsPerDataPropertyMetric() {
		super();
		// TODO Auto-generated constructor stub
	}



	public SynonymsPerDataPropertyMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}



	/** The Constant NAME. */
	private static final String NAME = "Synonyms per data property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfSynonyms = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty dataProperty : super.getOntology().dataPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if (OntologyUtils.isObsolete(dataProperty, getOntology(), this.getConfig().getImports()) || dataProperty.isOWLTopDataProperty()) {
				continue;
			}
			int localNumberOfSynonyms = getNumberOfSynonyms(dataProperty);
			
			this.notifyExporterListeners(ontologyIRI, dataProperty.getIRI().toString(), OWL.DatatypeProperty.getURI(), Integer.valueOf(localNumberOfSynonyms), timestamp, Collections.emptyList());
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfSynonyms)) / numberOfEntities;
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
		return Namespaces.OQUO_NS + "SynonymsPerDataPropertyMetric";
	}



	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.SYNONYMS;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}
}


