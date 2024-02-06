package es.um.dis.tecnomod.huron.dto;

import java.io.Serializable;
import java.util.Objects;

public class MetricResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7856546561278399963L;

	private Double metricValue;

	public MetricResult(double metricValue) {
		this.metricValue = metricValue;
	}

	public Double getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(Double metricValue) {
		this.metricValue = metricValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(metricValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetricResult other = (MetricResult) obj;
		return Objects.equals(metricValue, other.metricValue);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetricResult [metricValue=");
		builder.append(metricValue);
		builder.append("]");
		return builder.toString();
	}

}
