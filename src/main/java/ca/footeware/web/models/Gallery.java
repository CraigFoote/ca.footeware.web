package ca.footeware.web.models;

import java.io.File;

public class Gallery {

	private String name;
	private boolean secret;
	private File folder;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the secret
	 */
	public boolean isSecret() {
		return secret;
	}
	/**
	 * @param secret the secret to set
	 */
	public void setSecret(boolean secret) {
		this.secret = secret;
	}
	/**
	 * @return the folder
	 */
	public File getFolder() {
		return folder;
	}
	/**
	 * @param folder the folder to set
	 */
	public void setFolder(File folder) {
		this.folder = folder;
	}
	
}
