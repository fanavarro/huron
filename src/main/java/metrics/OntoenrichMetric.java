package metrics;

import services.OntologyUtils;

public abstract class OntoenrichMetric extends Metric{
	private static final float ONTOENRICH_LABEL_COVERAGE_THRESHOLD = 0.1f;
	
	protected int calculateNumberOfClassesThresholdFromCoverage(float ontoenrichLabelCoverageThreshold) {
		long nClasses = this.getOntology().classesInSignature().filter(owlClass -> (!OntologyUtils.isObsolete(owlClass, getOntology()))).count();
		return Math.round((float)(nClasses) * ontoenrichLabelCoverageThreshold);
	}
	
	protected int getNumberOfClassesThreshold() {
		return this.calculateNumberOfClassesThresholdFromCoverage(this.getOntoenrichLabelCoverageThreshold());
	}
	
	protected float getOntoenrichLabelCoverageThreshold () {
		return ONTOENRICH_LABEL_COVERAGE_THRESHOLD;
	}
}
