package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.ontoenrich.config.TypeOfDelimiterStrategy;
import org.ontoenrich.config.TypeOfTargetEntity;
import org.ontoenrich.core.LexicalEnvironment;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.OntologyUtils;

public abstract class OntoenrichMetric extends Metric{
	public OntoenrichMetric() {
		super();
	}

	public OntoenrichMetric(Config config) {
		super(config);
	}

	private static final float ONTOENRICH_LABEL_COVERAGE_THRESHOLD = 0.1f;
	
	protected LexicalEnvironment getLexicalEnvironment() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		LexicalEnvironment le = new LexicalEnvironment(
				TypeOfTargetEntity.CLASS_RDF_LABELS, 
				false /* Case Sensitive */, 
				TypeOfDelimiterStrategy.CHARACTER_BLANK,
				this.getOntology(),
				null /* discardedStopwordsNodes*/);
		return le;
	}
	
	protected int calculateNumberOfClassesThresholdFromCoverage(float ontoenrichLabelCoverageThreshold) {
		long nClasses = this.getNumberOfClasses();
		return Math.max(2, Math.round((float)(nClasses) * ontoenrichLabelCoverageThreshold));
	}
	
	protected int getNumberOfClassesThreshold() {
		return this.calculateNumberOfClassesThresholdFromCoverage(this.getOntoenrichLabelCoverageThreshold());
	}
	
	protected float getOntoenrichLabelCoverageThreshold () {
		return ONTOENRICH_LABEL_COVERAGE_THRESHOLD;
	}
	
	protected long getNumberOfClasses() {		
		return this.getOntology().classesInSignature().filter(owlClass -> (!OntologyUtils.isObsolete(owlClass, getOntology(), this.getConfig().getImports()) && !owlClass.isOWLThing())).count();
	}
}
