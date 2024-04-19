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
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.utils.IssueTypes;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

public class DataPropertiesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric {

	public DataPropertiesWithNoDescriptionMetric() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataPropertiesWithNoDescriptionMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/** The Constant NAME. */
	private static final String METRIC_NAME = "DataProperties with no description";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfDataPropertiesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(owlDataProperty.isOWLTopDataProperty()){
				continue;
			}			
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlDataProperty);
			if (localNumberOfDescriptions == 0) {
				IssueInfoDTO issue = new IssueInfoDTO(IssueTypes.MAJOR_ISSUE, String.format("The data property %s does not have any description.", owlDataProperty.getIRI().toQuotedString()));
				this.notifyExporterListeners(ontologyIRI, owlDataProperty.getIRI().toString(), OWL.DatatypeProperty.getURI(), Integer.valueOf(1), timestamp, issue);
				
				numberOfDataPropertiesWithNoDescription++;
			}else {
				
				this.notifyExporterListeners(ontologyIRI, owlDataProperty.getIRI().toString(), OWL.DatatypeProperty.getURI(), Integer.valueOf(0), timestamp, Collections.emptyList());
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfDataPropertiesWithNoDescription)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());

		return new MetricResult(metricValue);		
	}

	@Override
	public String getName() {
		return METRIC_NAME;
	}

	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "DataPropertiesWithNoDescriptionMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.DESCRIPTIONS;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_LOWER_BEST;
	}

}
