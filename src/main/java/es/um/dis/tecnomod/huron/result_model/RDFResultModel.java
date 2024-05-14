package es.um.dis.tecnomod.huron.result_model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

/**
 * The Class RDFResultModel.
 */
public abstract class RDFResultModel implements ResultModelInterface {
	
	/** The output file. */
	protected File outputFile;
	
	/** The rdf model. */
	protected Model rdfModel;

	/**
	 * Instantiates a new RDF result model.
	 *
	 * @param outputFile the output file
	 */
	public RDFResultModel(File outputFile) {
		super();
		this.outputFile = outputFile;
		this.rdfModel = ModelFactory.createDefaultModel();
	}

	/**
	 * Adds the observation.
	 *
	 * @param observationInfo the observation info
	 */
	@Override
	public abstract void addObservation(ObservationInfoDTO observationInfo);

	/**
	 * Export.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void export() throws IOException {
		this.rdfModel.setNsPrefixes(Namespaces.getPrefixMap());
		this.rdfModel.write(new FileOutputStream(this.outputFile), "TTL");
	}

}
