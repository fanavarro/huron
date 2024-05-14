package es.um.dis.tecnomod.huron.result_model;

import java.io.File;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.service.InstanceCreator;

/**
 * The Class DetailedRDFResultModel.
 */
public class DetailedRDFResultModel extends RDFResultModel {

	/**
	 * Instantiates a new detailed RDF result model.
	 *
	 * @param outputFile the output file
	 */
	public DetailedRDFResultModel(File outputFile) {
		super(outputFile);
	}

	/**
	 * Adds the observation.
	 *
	 * @param observationInfo the observation info
	 */
	@Override
	public synchronized void addObservation(ObservationInfoDTO observationInfo) {
		InstanceCreator.createObservation(rdfModel, observationInfo);
	}
}
