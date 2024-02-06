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
	private List<ResultModelInterface> exporters;
	
	public Config() {
		this.exporters = new ArrayList<>();
		this.imports = Imports.EXCLUDED;
		this.exporters = new ArrayList<ResultModelInterface> ();
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
	public List<ResultModelInterface> getExporters() {
		return exporters;
	}

	/**
	 * Sets the exporters.
	 *
	 * @param exporters the new exporters
	 */
	public void setExporters(List<ResultModelInterface> exporters) {
		this.exporters = exporters;
	}
	
	
	/**
	 * Adds the exporter.
	 *
	 * @param exporter the exporter
	 */
	public void addExporter(ResultModelInterface exporter) {
		this.exporters.add(exporter);
	}
}
