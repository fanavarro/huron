package es.um.dis.tecnomod.huron.dto;

import java.io.Serializable;
import java.util.Objects;

import org.apache.jena.rdf.model.Model;


public class MetricResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7856546561278399963L;
	
	
	private Double metricValue;
	private Model rdf;
	
	
	public MetricResult(Double metricValue, Model rdf) {
		super();
		this.metricValue = metricValue;
		this.rdf = rdf;
	}
	public Double getMetricValue() {
		return metricValue;
	}
	public void setMetricValue(Double metricValue) {
		this.metricValue = metricValue;
	}
	public Model getRdf() {
		return rdf;
	}
	public void setRdf(Model rdf) {
		this.rdf = rdf;
	}
	@Override
	public int hashCode() {
		return Objects.hash(metricValue, rdf);
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
		return Objects.equals(metricValue, other.metricValue) && Objects.equals(rdf, other.rdf);
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetricResult [metricValue=");
		builder.append(metricValue);
		builder.append(", rdf=");
		builder.append(rdf);
		builder.append("]");
		return builder.toString();
	}
	
	

}
