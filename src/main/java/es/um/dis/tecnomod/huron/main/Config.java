package es.um.dis.tecnomod.huron.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.parameters.Imports;

import es.um.dis.tecnomod.huron.result_model.ResultModelInterface;

/**
 * The Class Config.
 */
public class Config implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4121194238969339824L;
	
	/** The imports. */
	private  Imports imports;
	
	/** The exporters. */
	private List<ResultModelInterface> resultModels;
	
	public Config() {
		this.resultModels = new ArrayList<>();
		this.imports = Imports.EXCLUDED;
		this.resultModels = new ArrayList<ResultModelInterface> ();
	}
	/**
	 * Gets the imports.
	 *
	 * @return the imports
	 */
	public Imports getImports() {
		return imports;
	}
	
	/**
	 * Sets the imports.
	 *
	 * @param imports the new imports
	 */
	public void setImports(Imports imports) {
		this.imports = imports;
	}

	/**
	 * Gets the exporters.
	 *
	 * @return the exporters
	 */
	public List<ResultModelInterface> getResultModels() {
		return resultModels;
	}

	/**
	 * Sets the exporters.
	 *
	 * @param exporters the new exporters
	 */
	public void setResultModels(List<ResultModelInterface> exporters) {
		this.resultModels = exporters;
	}
	
	
	/**
	 * Adds the exporter.
	 *
	 * @param exporter the exporter
	 */
	public void addResultModel(ResultModelInterface exporter) {
		this.resultModels.add(exporter);
	}
}
