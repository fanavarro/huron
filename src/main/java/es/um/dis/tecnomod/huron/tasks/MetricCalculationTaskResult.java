package es.um.dis.tecnomod.huron.tasks;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Class MetricCalculationTaskResult.
 */
public class MetricCalculationTaskResult implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 436305010795217064L;
	
	/** The metric name. */
	private String metricName;
	
	/** The result. */
	private double result;
	
	/** The owl file. */
	private String owlFile;
	
	
	/**
	 * Instantiates a new metric calculation task result.
	 *
	 * @param metricName the metric name
	 * @param result the result
	 * @param owlFile the owl file
	 * @param rdf the rdf
	 */
	public MetricCalculationTaskResult(String metricName, double result, String owlFile) {
		super();
		this.metricName = metricName;
		this.result = result;
		this.owlFile = owlFile;
	}


	/**
	 * Gets the metric name.
	 *
	 * @return the metric name
	 */
	public String getMetricName() {
		return metricName;
	}
	
	/**
	 * Sets the metric name.
	 *
	 * @param metricName the new metric name
	 */
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	
	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public double getResult() {
		return result;
	}
	
	/**
	 * Sets the result.
	 *
	 * @param result the new result
	 */
	public void setResult(double result) {
		this.result = result;
	}
	
	/**
	 * Gets the owl file.
	 *
	 * @return the owl file
	 */
	public String getOwlFile() {
		return owlFile;
	}
	
	/**
	 * Sets the owl file.
	 *
	 * @param owlFile the new owl file
	 */
	public void setOwlFile(String owlFile) {
		this.owlFile = owlFile;
	}


	@Override
	public int hashCode() {
		return Objects.hash(metricName, owlFile, result);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetricCalculationTaskResult other = (MetricCalculationTaskResult) obj;
		return Objects.equals(metricName, other.metricName) && Objects.equals(owlFile, other.owlFile)
				&& Double.doubleToLongBits(result) == Double.doubleToLongBits(other.result);
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetricCalculationTaskResult [metricName=");
		builder.append(metricName);
		builder.append(", result=");
		builder.append(result);
		builder.append(", owlFile=");
		builder.append(owlFile);
		builder.append("]");
		return builder.toString();
	}



}
