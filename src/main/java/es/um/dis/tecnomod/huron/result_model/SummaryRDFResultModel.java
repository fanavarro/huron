package es.um.dis.tecnomod.huron.result_model;

import java.io.File;

import org.eclipse.rdf4j.model.vocabulary.OWL;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.service.InstanceCreator;

public class SummaryRDFResultModel extends RDFResultModel{

	public SummaryRDFResultModel(File outputFile) {
		super(outputFile);
	}

	/**
	 * Adds the observation if it is about an ontology.
	 *
	 * @param observationInfo the observation info
	 */
	@Override
	public synchronized void addObservation(ObservationInfoDTO observationInfo) {
		/* Only register observations about ontologies */
		if (observationInfo.getFeatureOfInterestTypeIRI().equals(OWL.ONTOLOGY.toString())) {
			InstanceCreator.createObservation(rdfModel, observationInfo);
		}
		
	}

}
