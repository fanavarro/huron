package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.SetUtils;
import org.ontoenrich.beans.Label;
import org.ontoenrich.config.TypeOfDelimiterStrategy;
import org.ontoenrich.config.TypeOfTargetEntity;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.ontoenrich.filters.RemoveNoClasses;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import services.OntologyGraphService;
import services.OntologyGraphServiceImpl;
import services.OntologyUtils;

/**
 * The Class SystematicNamingMetric.
 */
public class SystematicNamingMetric extends Metric {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(SystematicNamingMetric.class.getName());
	
	private static final float ONTOENRICH_LABEL_COVERAGE_THRESHOLD = 0.1f;
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Systematic naming";
	
	/** The reasoner. */
	private OWLReasoner reasoner;
	
	/** The ontology graph service. */
	private OntologyGraphService ontologyGraphService;
	
	/**
	 * Instantiates a new systematic naming metric.
	 */
	public SystematicNamingMetric(){
		this.ontologyGraphService = new OntologyGraphServiceImpl();
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		super.writeToDetailedOutputFile("Metric\tClass\tClass depth\tLR\tPositive Cases\tPositive cases average depth\tPositive cases average distance to LR class\tNegative Cases\tNegative cases average depth\tNegative cases average distance to LR class\tMetric Value\n" );
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();

		// INIT owlReasoner
		reasoner = createReasoner(getOntology());

		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.calculateNumberOfClassesThresholdFromCoverage(ONTOENRICH_LABEL_COVERAGE_THRESHOLD);
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold /* Minimum Coverage (in labels) */);
				

		// STEP 3: apply a filter to get just LRs that are classes
		RemoveNoClasses.execute(lexicalRegularities);

		// STEP 4: calculate the metric
		int positiveCasesCount = 0;
		int negativeCasesCount = 0;
		for (LexicalRegularity lexicalRegularity : lexicalRegularities) {
			for (OWLClass owlClassA : this.getRepresentingClasses(lexicalEnvironment, lexicalRegularity)) {
				int owlClassADepth = -1;
				if (OntologyUtils.isObsolete(owlClassA, getOntology())) {
					continue;
				}
				Set<OWLClass> subClassesOfA = reasoner.getSubClasses(owlClassA, false).getFlattened();
				subClassesOfA.remove(getOntology().getOWLOntologyManager().getOWLDataFactory().getOWLNothing());
				Set<OWLClass> classeslexicallyRelatedWithA = this.getClassesWithPattern(lexicalRegularity);
				classeslexicallyRelatedWithA.remove(owlClassA);
				
				Set<OWLClass> localPositiveCases = SetUtils.intersection(subClassesOfA, classeslexicallyRelatedWithA);
				int localPositiveCasesCount = localPositiveCases.size();
				
				Set<OWLClass> localNegativeCases = SetUtils.difference(subClassesOfA, classeslexicallyRelatedWithA);
				int localNegativeCasesCount = localNegativeCases.size();
				
				if(super.isOpenDetailedOutputFile()){
					if (owlClassADepth == -1){
						owlClassADepth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClassA);
					}
					double localMetricResult = (double) localPositiveCasesCount / (localPositiveCasesCount + localNegativeCasesCount);
					double averageDepthLocalPositiveCases = this.getAverageDepth(localPositiveCases);
					double averageDepthLocalNegativeCases = this.getAverageDepth(localNegativeCases);
					double averageDistanceToLRClassLocalPositiveCases = this.getAverageDistanceToDepth(localPositiveCases, owlClassADepth);
					double averageDistanceToLRClassLocalNegativeCases = this.getAverageDistanceToDepth(localNegativeCases, owlClassADepth);
					super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\t%s\t%d\t%.3f\t%.3f\t%d\t%.3f\t%.3f\t%.3f\n", this.getName(), owlClassA.toStringID(), owlClassADepth, lexicalRegularity.strPattern, localPositiveCasesCount, averageDepthLocalPositiveCases,averageDistanceToLRClassLocalPositiveCases, localNegativeCasesCount, averageDepthLocalNegativeCases, averageDistanceToLRClassLocalNegativeCases, localMetricResult));
				}
				positiveCasesCount += localPositiveCasesCount;
				negativeCasesCount += localNegativeCasesCount;
				for(OWLClass c : localNegativeCases){
					LOGGER.log(Level.INFO, String.format("Class %s is subclass of %s but there are no lexical regularities in common.", c.toStringID(), owlClassA.toStringID()));
				}
			}
		}
		reasoner.flush();
		reasoner.dispose();
		// STEP 5: return the calculated value
		return (double) positiveCasesCount / (positiveCasesCount + negativeCasesCount);

	}
	
	/**
	 * Gets the average distance to depth.
	 *
	 * @param owlClasses the owl classes
	 * @param parentDepth the parent depth
	 * @return the average distance to depth
	 */
	private double getAverageDistanceToDepth(Set<OWLClass> owlClasses, int parentDepth) {
		int distanceToParentSum = 0;
		int totalClasses = 0;
		for(OWLClass owlClass : owlClasses){
			int depth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClass);
			if(depth != -1){
				distanceToParentSum += Math.abs(depth - parentDepth);
				totalClasses++;
			}
		}
		return (double)distanceToParentSum/totalClasses;
	}
	
	/**
	 * Gets the average depth.
	 *
	 * @param owlClasses the owl classes
	 * @return the average depth
	 */
	private double getAverageDepth(Set<OWLClass> owlClasses) {
		int depthSum = 0;
		int totalClasses = 0;
		for(OWLClass owlClass : owlClasses){
			int depth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClass);
			if(depth != -1){
				depthSum += depth;
				totalClasses++;
			}
		}
		return (double)depthSum/totalClasses;
	}
	
	/**
	 * Gets the classes with pattern.
	 *
	 * @param lexicalRegularity the lexical regularity
	 * @return the classes with pattern
	 */
	private Set<OWLClass> getClassesWithPattern(LexicalRegularity lexicalRegularity) {
		Set<OWLClass> classesWithPattern = new HashSet<OWLClass>();
		for (Label l : lexicalRegularity.idLabelsWhereItAppears) {
			OWLClass classWithPattern = getOntology().getOWLOntologyManager().getOWLDataFactory()
					.getOWLClass(IRI.create(l.getIdLabel()));
			if (!OntologyUtils.isObsolete(classWithPattern, getOntology())) {
				classesWithPattern.add(classWithPattern);
			}
			
		}
		return classesWithPattern;
	}
	
	private int calculateNumberOfClassesThresholdFromCoverage(float ontoenrichLabelCoverageThreshold) {
		// TODO: Choose if we calculate this with or without obsolete classes.
		// Now, we are not taken into account obsolete classes for calculating the coverage in order to have the same results than the previous implementation.
		//long nClasses = this.getOntology().getClassesInSignature().parallelStream().filter(owlClass -> (!OntologyUtils.isObsolete(owlClass, getOntology()))).count();
		long nClasses = this.getOntology().getClassesInSignature().size(); 
		return Math.round((float)(nClasses) * ontoenrichLabelCoverageThreshold);
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
	
	private LexicalEnvironment getLexicalEnvironment() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		LexicalEnvironment le = new LexicalEnvironment(
				TypeOfTargetEntity.CLASS_RDF_LABELS, 
				false /* Case Sensitive */, 
				TypeOfDelimiterStrategy.CHARACTER_BLANK,
				this.getOntology(),
				null /* discardedStopwordsNodes*/);
		return le;
	}


	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}

}
