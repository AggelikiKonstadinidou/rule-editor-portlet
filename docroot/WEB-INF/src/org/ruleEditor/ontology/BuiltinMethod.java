package org.ruleEditor.ontology;

import java.util.ArrayList;
import java.util.HashMap;

import org.ruleEditor.ontology.PointElement.Type;

public class BuiltinMethod {
	
	private String usingName;
	private String originalName;
	private String description;
	private int numberOfParams;
	private String helpString;
	private String watermarkDescription;
	private boolean flag; // if the built in method requires a help string e.g a parameter, text etc.
	private ArrayList<ArrayList<String>> listOfVarClassesLists;
	private ArrayList<ArrayList<String>> listOfVarValuesLists;
	private ArrayList<HelpObject> selectedValues;
	
	public BuiltinMethod(String usingName, String originalName,
			String description, int numberOfParams) {
		super();
		this.usingName = usingName;
		this.originalName = originalName;
		this.description = description;
		this.numberOfParams = numberOfParams;
		this.helpString = "empty";
		this.watermarkDescription = "";
		this.flag = true;
		this.listOfVarClassesLists = new ArrayList<ArrayList<String>>();
		this.listOfVarValuesLists = new ArrayList<ArrayList<String>>();
		// create lists according to number(1st) of parameters and type of
		// parameters (2nd step)
		ArrayList<String> tempList;
		for (int i = 0; i < this.numberOfParams; i++) {
			tempList = new ArrayList<String>();
			this.listOfVarClassesLists.add(tempList);
			this.listOfVarValuesLists.add(tempList);
		}
		
		this.selectedValues = new ArrayList<HelpObject>();
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

	public ArrayList<ArrayList<String>> getListOfVarClassesLists() {
		return listOfVarClassesLists;
	}

	public void setListOfVarClassesLists(
			ArrayList<ArrayList<String>> listOfVarClassesLists) {
		this.listOfVarClassesLists = listOfVarClassesLists;
	}

	public ArrayList<ArrayList<String>> getListOfVarValuesLists() {
		return listOfVarValuesLists;
	}

	public void setListOfVarValuesLists(
			ArrayList<ArrayList<String>> listOfVarValuesLists) {
		this.listOfVarValuesLists = listOfVarValuesLists;
	}

	public ArrayList<HelpObject> getSelectedValues() {
		return selectedValues;
	}

	public void setSelectedValues(ArrayList<HelpObject> selectedValues) {
		this.selectedValues = selectedValues;
	}

	public BuiltinMethod clone() {
		BuiltinMethod method = new BuiltinMethod("", "", "", 0);
		method.setUsingName(this.getUsingName());
		method.setOriginalName(this.getOriginalName());
		method.setDescription(this.getDescription());
		method.setNumberOfParams(this.getNumberOfParams());
		method.setHelpString(this.helpString);
		method.setWatermarkDescription(this.watermarkDescription);
		method.setFlag(this.flag);
		method.setListOfVarClassesLists(this.listOfVarClassesLists);
		method.setListOfVarValuesLists(this.listOfVarValuesLists);
		method.setSelectedValues(this.selectedValues);
		return method;
	}
	
	public class HelpObject{
		private String key;
		private String value;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public HelpObject(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this.getKey().equals(((HelpObject) obj).getKey())
					&& this.getValue().equals(((HelpObject) obj).getValue())) {
				return true;
			} else
				return false;
		}
		
		public HelpObject clone() {
			HelpObject obj = new HelpObject("", "");
			obj.setKey(this.getKey());
			obj.setValue(this.getValue());
			return obj;
		}
			
		
	}
	
	

}
