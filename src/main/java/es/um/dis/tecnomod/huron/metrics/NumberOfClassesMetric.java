package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;

import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

/**
 * The Class NumberOfClassesMetric.
 */
public class NumberOfClassesMetric extends Metric {
	
	public NumberOfClassesMetric() {
		super();
		// TODO Auto-generated constructor stub
	}



	public NumberOfClassesMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}



	/** The Constant NAME. */
	private static final String NAME = "Number of classes";


	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		double metricValue = getOntology().classesInSignature(this.getConfig().getImports()).filter(owlClass -> (!OntologyUtils.isObsolete(owlClass, getOntology(), this.getConfig().getImports()))).count();
		
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), Calendar.getInstance(), Collections.emptyList());
		
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
		return Namespaces.OQUO_NS + "NumberOfClassesMetric";
	}



	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.NUMBER_OF_CLASSES;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}
}
