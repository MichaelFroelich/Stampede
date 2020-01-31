package org.stampede.model;

import java.io.Serializable;
import java.util.Map;

public class ApplicationStatus implements Serializable {

	private static final long serialVersionUID = ApplicationStatus.class.getSimpleName().hashCode();

	private String application;
	private String instance;
	private Map<String, Status> roles;
	private Map<String, Status> resources;
	private Status status;

	public ApplicationStatus(String application, String instance) {
		this.application = application;
		this.instance = instance;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public Map<String, Status> getRoles() {
		return roles;
	}

	public void setRoles(Map<String, Status> roles) {
		this.roles = roles;
	}

	public Map<String, Status> getResources() {
		return resources;
	}

	public void setResources(Map<String, Status> resources) {
		this.resources = resources;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
