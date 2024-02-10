package es.um.dis.tecnomod.huron.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * The Class ObservationInfoDTO.
 */
public class ObservationInfoDTO implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -168082823892871235L;
	
	/** The source document IRI. */
	private String sourceDocumentIRI;
	
	/** The feature of interest IRI. */
	private String featureOfInterestIRI;
	
	/** The feature of interest type IRI. */
	private String featureOfInterestTypeIRI;
	
	/** The observable property IRI. */
	private String observablePropertyIRI;
	
	/** The metric used IRI. */
	private String metricUsedIRI;
	
	/** The scale IRI. */
	private String scaleIRI;
	
	/** The scale type IRI. */
	private String scaleTypeIRI;
	
	/** The ranking function IRI. */
	private String rankingFunctionIRI;
	
	/** The instrument IRI. */
	private String instrumentIRI;
	
	/** The unit of measure IRI. */
	private String unitOfMeasureIRI;
	
	/** The value. */
	private Object value;
	
	/** The timestamp. */
	private Calendar timestamp;
	
	/**
	 * Gets the source document IRI.
	 *
	 * @return the source document IRI
	 */
	public String getSourceDocumentIRI() {
		return sourceDocumentIRI;
	}
	
	/**
	 * Sets the source document IRI.
	 *
	 * @param sourceDocumentIRI the new source document IRI
	 */
	public void setSourceDocumentIRI(String sourceDocumentIRI) {
		this.sourceDocumentIRI = sourceDocumentIRI;
	}
	
	/**
	 * Gets the feature of interest IRI.
	 *
	 * @return the feature of interest IRI
	 */
	public String getFeatureOfInterestIRI() {
		return featureOfInterestIRI;
	}
	
	/**
	 * Sets the feature of interest IRI.
	 *
	 * @param featureOfInterestIRI the new feature of interest IRI
	 */
	public void setFeatureOfInterestIRI(String featureOfInterestIRI) {
		this.featureOfInterestIRI = featureOfInterestIRI;
	}
	
	/**
	 * Gets the feature of interest type IRI.
	 *
	 * @return the feature of interest type IRI
	 */
	public String getFeatureOfInterestTypeIRI() {
		return featureOfInterestTypeIRI;
	}
	
	/**
	 * Sets the feature of interest type IRI.
	 *
	 * @param featureOfInterestTypeIRI the new feature of interest type IRI
	 */
	public void setFeatureOfInterestTypeIRI(String featureOfInterestTypeIRI) {
		this.featureOfInterestTypeIRI = featureOfInterestTypeIRI;
	}
	
	/**
	 * Gets the observable property IRI.
	 *
	 * @return the observable property IRI
	 */
	public String getObservablePropertyIRI() {
		return observablePropertyIRI;
	}
	
	/**
	 * Sets the observable property IRI.
	 *
	 * @param observablePropertyIRI the new observable property IRI
	 */
	public void setObservablePropertyIRI(String observablePropertyIRI) {
		this.observablePropertyIRI = observablePropertyIRI;
	}
	
	/**
	 * Gets the metric used IRI.
	 *
	 * @return the metric used IRI
	 */
	public String getMetricUsedIRI() {
		return metricUsedIRI;
	}
	
	/**
	 * Sets the metric used IRI.
	 *
	 * @param metricUsedIRI the new metric used IRI
	 */
	public void setMetricUsedIRI(String metricUsedIRI) {
		this.metricUsedIRI = metricUsedIRI;
	}
	
	/**
	 * Gets the scale IRI.
	 *
	 * @return the scale IRI
	 */
	public String getScaleIRI() {
		return scaleIRI;
	}
	
	/**
	 * Sets the scale IRI.
	 *
	 * @param scaleIRI the new scale IRI
	 */
	public void setScaleIRI(String scaleIRI) {
		this.scaleIRI = scaleIRI;
	}
	
	/**
	 * Gets the scale type IRI.
	 *
	 * @return the scale type IRI
	 */
	public String getScaleTypeIRI() {
		return scaleTypeIRI;
	}
	
	/**
	 * Sets the scale type IRI.
	 *
	 * @param scaleTypeIRI the new scale type IRI
	 */
	public void setScaleTypeIRI(String scaleTypeIRI) {
		this.scaleTypeIRI = scaleTypeIRI;
	}
	
	/**
	 * Gets the ranking function IRI.
	 *
	 * @return the ranking function IRI
	 */
	public String getRankingFunctionIRI() {
		return rankingFunctionIRI;
	}
	
	/**
	 * Sets the ranking function IRI.
	 *
	 * @param rankingFunctionIRI the new ranking function IRI
	 */
	public void setRankingFunctionIRI(String rankingFunctionIRI) {
		this.rankingFunctionIRI = rankingFunctionIRI;
	}
	
	/**
	 * Gets the instrument IRI.
	 *
	 * @return the instrument IRI
	 */
	public String getInstrumentIRI() {
		return instrumentIRI;
	}
	
	/**
	 * Sets the instrument IRI.
	 *
	 * @param instrumentIRI the new instrument IRI
	 */
	public void setInstrumentIRI(String instrumentIRI) {
		this.instrumentIRI = instrumentIRI;
	}
	
	/**
	 * Gets the unit IRI.
	 *
	 * @return the unit IRI
	 */
	public String getUnitOfMeasureIRI() {
		return unitOfMeasureIRI;
	}
	
	/**
	 * Sets the unit IRI.
	 *
	 * @param unitIRI the new unit IRI
	 */
	public void setUnitOfMeasureIRI(String unitIRI) {
		this.unitOfMeasureIRI = unitIRI;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public Calendar getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(featureOfInterestIRI, featureOfInterestTypeIRI, instrumentIRI, metricUsedIRI,
				observablePropertyIRI, rankingFunctionIRI, scaleIRI, scaleTypeIRI, sourceDocumentIRI, timestamp,
				unitOfMeasureIRI, value);
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
		ObservationInfoDTO other = (ObservationInfoDTO) obj;
		return Objects.equals(featureOfInterestIRI, other.featureOfInterestIRI)
				&& Objects.equals(featureOfInterestTypeIRI, other.featureOfInterestTypeIRI)
				&& Objects.equals(instrumentIRI, other.instrumentIRI)
				&& Objects.equals(metricUsedIRI, other.metricUsedIRI)
				&& Objects.equals(observablePropertyIRI, other.observablePropertyIRI)
				&& Objects.equals(rankingFunctionIRI, other.rankingFunctionIRI)
				&& Objects.equals(scaleIRI, other.scaleIRI) && Objects.equals(scaleTypeIRI, other.scaleTypeIRI)
				&& Objects.equals(sourceDocumentIRI, other.sourceDocumentIRI)
				&& Objects.equals(timestamp, other.timestamp) && Objects.equals(unitOfMeasureIRI, other.unitOfMeasureIRI)
				&& Objects.equals(value, other.value);
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ObservationInfoDTO [sourceDocumentIRI=");
		builder.append(sourceDocumentIRI);
		builder.append(", featureOfInterestIRI=");
		builder.append(featureOfInterestIRI);
		builder.append(", featureOfInterestTypeIRI=");
		builder.append(featureOfInterestTypeIRI);
		builder.append(", observablePropertyIRI=");
		builder.append(observablePropertyIRI);
		builder.append(", metricUsedIRI=");
		builder.append(metricUsedIRI);
		builder.append(", scaleIRI=");
		builder.append(scaleIRI);
		builder.append(", scaleTypeIRI=");
		builder.append(scaleTypeIRI);
		builder.append(", rankingFunctionIRI=");
		builder.append(rankingFunctionIRI);
		builder.append(", instrumentIRI=");
		builder.append(instrumentIRI);
		builder.append(", unitIRI=");
		builder.append(unitOfMeasureIRI);
		builder.append(", value=");
		builder.append(value);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append("]");
		return builder.toString();
	}
	
	
}
