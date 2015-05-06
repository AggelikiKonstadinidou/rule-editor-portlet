package org.ruleEditor.ontology;

import java.util.ArrayList;
import java.util.List;

public class OntologyClass {
	private String className;
	private List<OntologyClass> children;
	private List<String> dataProperties;
	private List<String> objectProperties;
	
	public OntologyClass() {
		super();
		this.className = "";
		this.children = new ArrayList<OntologyClass>();
		this.dataProperties = new ArrayList<String>();
		this.objectProperties = new ArrayList<String>();
	}

	public List<OntologyClass> getChildren() {
		return children;
	}

	public void setChildren(List<OntologyClass> children) {
		this.children = children;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getDataProperties() {
		return dataProperties;
	}

	public void setDataProperties(List<String> dataProperties) {
		this.dataProperties = dataProperties;
	}

	public List<String> getObjectProperties() {
		return objectProperties;
	}

	public void setObjectProperties(List<String> objectProperties) {
		this.objectProperties = objectProperties;
	}
	
	
	
}
