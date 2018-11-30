/**
 * 
 */
package com.soen.risk.boundary.request;

import com.soen.risk.boundary.Request;

/**
 * @author fly
 *
 */
public class LoadGameRequest implements Request {
	private String fileName;
	
	 public LoadGameRequest(String... args) {
	        setFileName(args[0]);
	        
	    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}