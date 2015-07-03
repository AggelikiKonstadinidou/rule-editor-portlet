package org.ruleEditor.ontology;

import org.ruleEditor.ontology.PointElement.Type;

public class BuiltinMethod {
	
	private String usingName;
	private String originalName;
	private String description;
	private int numberOfParams;
	private Type typeOfParam;
	private String helpString;
	private String watermarkDescription;
	private boolean flag; // if the built in method requires a help string e.g a parameter, text etc.
	
	public BuiltinMethod(String usingName, String originalName,
			String description, int numberOfParams, Type typeOfParam) {
		super();
		this.usingName = usingName;
		this.originalName = originalName;
		this.description = description;
		this.numberOfParams = numberOfParams;
		this.typeOfParam = typeOfParam;
		this.helpString = "empty";
		this.watermarkDescription = "";
		this.flag = false;
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
	public String getHelpString() {
		return helpString;
	}
	public void setHelpString(String helpString) {
		this.helpString = helpString;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getWatermarkDescription() {
		return watermarkDescription;
	}
	public void setWatermarkDescription(String watermarkDescription) {
		this.watermarkDescription = watermarkDescription;
	}
	@Override
	public boolean equals(Object obj) {
		if (this.getUsingName().equals(((BuiltinMethod) obj).getUsingName())) {
			return true;
		} else
			return false;
	}

	public BuiltinMethod clone() {
		BuiltinMethod method = new BuiltinMethod("", "", "", 0,
				Type.BUILTIN_METHOD);
		method.setUsingName(this.getUsingName());
		method.setOriginalName(this.getOriginalName());
		method.setDescription(this.getDescription());
		method.setNumberOfParams(this.getNumberOfParams());
		method.setTypeOfParam(this.getTypeOfParam());
		method.setHelpString(this.helpString);
		method.setWatermarkDescription(this.watermarkDescription);
		method.setFlag(this.flag);
		return method;
	}
	
	

}
