package ca.footeware.web.models;

import java.io.File;

/**
 * @author Footeware.ca
 *
 */
public class Gallery {

	private String name;
	private boolean secret;
	private File folder;

	/**
	 * @return the folder
	 */
	public File getFolder() {
		return folder;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the secret
	 */
	public boolean isSecret() {
		return secret;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(File folder) {
		this.folder = folder;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param secret the secret to set
	 */
	public void setSecret(boolean secret) {
		this.secret = secret;
	}

}
