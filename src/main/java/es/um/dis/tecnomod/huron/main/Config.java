package es.um.dis.tecnomod.huron.main;

import java.io.Serializable;

import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * The Class Config.
 */
public class Config implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4121194238969339824L;
	
	/** The imports. */
	private  Imports imports;
	
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
}
