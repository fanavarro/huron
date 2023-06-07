package es.um.dis.tecnomod.huron.tasks;

import java.io.Serializable;
import java.util.Objects;

import org.apache.jena.rdf.model.Model;

// TODO: Auto-generated Javadoc
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
	
	/**  The metric result in RDF statements. */
	private Model rdf;
	
	
	/**
	 * Instantiates a new metric calculation task result.
	 *
	 * @param metricName the metric name
	 * @param result the result
	 * @param owlFile the owl file
	 * @param rdf the rdf
	 */
	public MetricCalculationTaskResult(String metricName, double result, String owlFile, Model rdf) {
		super();
		this.metricName = metricName;
		this.result = result;
		this.owlFile = owlFile;
		this.rdf = rdf;
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

	/**
	 * Gets the rdf.
	 *
	 * @return the rdf
	 */
	public Model getRdf() {
		return rdf;
	}

	/**
	 * Sets the rdf.
	 *
	 * @param rdf the new rdf
	 */
	public void setRdf(Model rdf) {
		this.rdf = rdf;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(metricName, owlFile, rdf, result);
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
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
				&& Objects.equals(rdf, other.rdf)
				&& Double.doubleToLongBits(result) == Double.doubleToLongBits(other.result);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetricCalculationTaskResult [metricName=");
		builder.append(metricName);
		builder.append(", result=");
		builder.append(result);
		builder.append(", owlFile=");
		builder.append(owlFile);
		builder.append(", rdf=");
		builder.append(rdf);
		builder.append("]");
		return builder.toString();
	}
	
	

	
}
