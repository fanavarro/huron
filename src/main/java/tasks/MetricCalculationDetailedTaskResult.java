package tasks;

public class MetricCalculationDetailedTaskResult extends MetricCalculationTaskResult {
	
	/** The dividend. */
	private int dividend;
	
	/** The divisor. */
	private int divisor;

	public MetricCalculationDetailedTaskResult(String metricName, double result, String owlFile, int dividend, int divisor) {
		super(metricName, result, owlFile);
		this.dividend = dividend;
		this.divisor = divisor;
	}

	/**
	 * Gets the divisor.
	 *
	 * @return the divisor
	 */
	public int getDividend() {
		return dividend;
	}

	/**
	 * Sets the dividend.
	 *
	 * @param result the new dividend
	 */
	public void setDividend(int dividend) {
		this.dividend = dividend;
	}

	/**
	 * Gets the divisor.
	 *
	 * @return the divisor
	 */
	public int getDivisor() {
		return divisor;
	}

	/**
	 * Sets the divisor.
	 *
	 * @param result the new divisor
	 */
	public void setDivisor(int divisor) {
		this.divisor = divisor;
	}

}
