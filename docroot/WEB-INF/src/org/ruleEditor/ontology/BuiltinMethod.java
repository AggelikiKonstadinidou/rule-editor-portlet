package org.ruleEditor.ontology;

import org.ruleEditor.ontology.PointElement.Type;

public class BuiltinMethod {
	
	private String usingName;
	private String originalName;
	private String description;
	private int numberOfParams;
	private Type typeOfParam;
	
	public BuiltinMethod(String usingName, String originalName,
			String description, int numberOfParams, Type typeOfParam) {
		super();
		this.usingName = usingName;
		this.originalName = originalName;
		this.description = description;
		this.numberOfParams = numberOfParams;
		this.typeOfParam = typeOfParam;
	}
	
	public String getUsingName() {
		return usingName;
	}
	public void setUsingName(String usingName) {
		this.usingName = usingName;
	}
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getNumberOfParams() {
		return numberOfParams;
	}
	public void setNumberOfParams(int numberOfParams) {
		this.numberOfParams = numberOfParams;
	}
	public Type getTypeOfParam() {
		return typeOfParam;
	}
	public void setTypeOfParam(Type typeOfParam) {
		this.typeOfParam = typeOfParam;
	}
	
	
	
	

}
