package es.um.dis.tecnomod.huron.result_model;

import java.io.IOException;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;


/**
 * The Interface ExporterInterface.
 */
public interface ResultModelInterface {

	/**
	 * Adds the observation.
	 *
	 * @param observationInfo Information about the observation.
	 */
	public void addObservation (ObservationInfoDTO observationInfo);
	
	
	/**
	 * Export.
	 * @throws IOException 
	 */
	public void export() throws IOException;
}
