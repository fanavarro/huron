package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.vocabulary.OWL;
import org.ontoenrich.beans.Label;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.ontoenrich.filters.RemoveNoClasses;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.issues.IssueTypes;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.OntologyGraphService;
import es.um.dis.tecnomod.huron.services.OntologyGraphServiceImpl;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

/**
 * The Class LexicallySuggestLogicallyDefineMetric.
 */
public class LexicallySuggestLogicallyDefineMetric extends OntoenrichMetric {

	private static final String ISSUE_MSG_TEMPLATE = "The class %s ('%s') is exhibiting the name of the class %s ('%s'), which suggest a semantic relationship between them. Nonetheless, this relationship is not explicit in the ontology. If they are related, an axiom should be created in the ontology relating them.";

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(LexicallySuggestLogicallyDefineMetric.class.getName());
	
	/** The max depth taken into account to check if two classes are axiomatically related */
	private static final int MAX_DEPTH = 5;
	
	/** The Constant NAME. */
	private static final String NAME = "Lexically suggest logically define";

	/** The Constant IGNORED_AXIOMS. */
	private static final List<AxiomType<?>> IGNORED_AXIOMS = Arrays.asList(AxiomType.DISJOINT_CLASSES,
			AxiomType.DISJOINT_DATA_PROPERTIES, AxiomType.DISJOINT_OBJECT_PROPERTIES, AxiomType.DISJOINT_UNION);
	
	
	
	/** The reasoner. */
	private OWLReasoner reasoner;
	
	/** The ontology graph service. */
	private OntologyGraphService ontologyGraphService;

	/**
	 * Instantiates a new lexically suggest logically define metric.
	 */
	public LexicallySuggestLogicallyDefineMetric() {
		ontologyGraphService = new OntologyGraphServiceImpl();
	}
	
	public LexicallySuggestLogicallyDefineMetric(Config config) {
		super(config);
		ontologyGraphService = new OntologyGraphServiceImpl();
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		Calendar timestamp = Calendar.getInstance();
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();

		// INIT owlReasoner
		reasoner = createReasoner(getOntology());

		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold /* Minimum Coverage (in labels) */);
		
		

		// STEP 3: apply a filter to get just LRs that are classes
		RemoveNoClasses.execute(lexicalRegularities);

		// STEP 4: calculate the metric
		int positiveCasesCount = 0;
		int negativeCasesCount = 0;
		for (LexicalRegularity lexicalRegularity : lexicalRegularities) {
			for (OWLClass owlClassA : this.getRepresentingClasses(lexicalEnvironment, lexicalRegularity)) {
				Set<OWLClass> localPositiveCases = new HashSet<OWLClass>();
				Set<OWLClass> localNegativeCases = new HashSet<OWLClass>();
				List<IssueInfoDTO> issues = new ArrayList<>();
				if (OntologyUtils.isObsolete(owlClassA, getOntology(), this.getConfig().getImports())) {
					continue;
				}
				for (Label l : lexicalRegularity.getIdLabelsWhereItAppears()) {
					if (l.getIdLabel().equals(owlClassA.toStringID())) {
						continue;
					}
					OWLClass owlClassCi = getOntology().getOWLOntologyManager().getOWLDataFactory()
							.getOWLClass(IRI.create(l.getIdLabel()));
					if (OntologyUtils.isObsolete(owlClassCi, getOntology(), this.getConfig().getImports())) {
						continue;
					}
					if (ontologyGraphService.isRelated(reasoner, owlClassA, owlClassCi, IGNORED_AXIOMS, this.getConfig().getImports(), MAX_DEPTH)) {
						localPositiveCases.add(owlClassCi);
					} else {
						localNegativeCases.add(owlClassCi);
						String issueMsg = String.format(ISSUE_MSG_TEMPLATE, owlClassCi.toStringID(), l.getStrLabel(), owlClassA.toStringID(), lexicalRegularity.getStrPattern());
						LOGGER.log(Level.INFO, issueMsg);
						IssueInfoDTO issue = new IssueInfoDTO(IssueTypes.LEXICALLY_SUGGEST_LOGICALLY_DEFINE_ISSUE, owlClassA.toStringID(), owlClassCi.toStringID(), issueMsg);
						issues.add(issue);
					}
				}
				double localMetricResult = (double) localPositiveCases.size() / (localPositiveCases.size() + localNegativeCases.size());
				this.notifyExporterListeners(ontologyIRI, owlClassA.getIRI().toString(), OWL.Class.getURI(), Double.valueOf(localMetricResult), timestamp, issues);
				
				positiveCasesCount += localPositiveCases.size();
				negativeCasesCount += localNegativeCases.size();
			}
			
		}
		reasoner.flush();
		reasoner.dispose();

		// STEP 5: return the calculated value
		double metricValue = (double) positiveCasesCount / (positiveCasesCount + negativeCasesCount);
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());

		return new MetricResult(metricValue);

	}


	/**
	 * Gets the representing classes.
	 *
	 * @param la the la
	 * @param p the p
	 * @return the representing classes
	 */
	private Set<OWLClass> getRepresentingClasses(LexicalEnvironment lexicalEnvironment, LexicalRegularity lexicalRegularity) {
		
		Set<Label> labels = lexicalEnvironment.getLabel(lexicalRegularity);
		Set<OWLClass> classes = new HashSet<OWLClass>();
		for (Label label : labels) {
			label.getIdLabel();
			OWLClass owlClass = lexicalEnvironment.owlApiOntology.getOWLOntologyManager().getOWLDataFactory()
					.getOWLClass(IRI.create(label.getIdLabel()));
			classes.add(owlClass);
		}
		return classes;
	}

	/**
	 * Creates the reasoner.
	 *
	 * @param ontology the ontology
	 * @return the OWL reasoner
	 */
	private OWLReasoner createReasoner(OWLOntology ontology) {
		OWLReasonerFactory factory = new ElkReasonerFactory();
		OWLReasoner reasoner =  factory.createReasoner(ontology);
		try {
			reasoner.isConsistent();
		} catch (Exception e){
			LOGGER.warning("ELK reasoner not supported, using structural reasoner.");
			factory = new StructuralReasonerFactory();
			reasoner = factory.createReasoner(ontology);
		}
		return reasoner;
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
		return Namespaces.OQUO_NS + "LexicallySuggestLogicallyDefineMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.LSLD_PRINCIPLE;
	}

	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}
}
