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
//	private ArrayList<HelpObject> selectedValues;
	private String newNodeName;
	private String category;
	private HelpObject value1;
	private HelpObject value2;
	private HelpObject value3;
	private HelpObject value4;
	private HelpObject value5;
	private HelpObject value6;
	
	public BuiltinMethod(String usingName, String originalName,
			String description, int numberOfParams) {
		super();
		this.usingName = usingName;
		this.originalName = originalName;
		this.description = description;
		this.numberOfParams = numberOfParams;
		this.helpString = "no values selected";
		this.watermarkDescription = "";
		this.flag = false;
		this.listOfVarClassesLists = new ArrayList<ArrayList<String>>();
		this.listOfVarValuesLists = new ArrayList<ArrayList<String>>();
//		this.selectedValues = new ArrayList<HelpObject>();
		// create lists according to number(1st) of parameters and type of
		// parameters (2nd step)
		this.value1 = new HelpObject("", "");
		this.value2 = new HelpObject("", "");
		this.value3 = new HelpObject("", "");
		this.value4 = new HelpObject("", "");
		this.value5 = new HelpObject("", "");
		this.value6 = new HelpObject("", "");
		
		HelpObject temp = null;
		ArrayList<String> tempList;
		for (int i = 0; i < this.numberOfParams; i++) {
			tempList = new ArrayList<String>();
			
//			this.selectedValues.add(temp);
//			this.selectedValues.add(temp);
//			this.selectedValues.add(temp);
			this.listOfVarClassesLists.add(tempList);
			this.listOfVarValuesLists.add(tempList);
		}
		
		this.newNodeName = "";
		this.category = "";
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public String getNewNodeName() {
		return newNodeName;
	}

	public void setNewNodeName(String newNodeName) {
		this.newNodeName = newNodeName;
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

//	public ArrayList<HelpObject> getSelectedValues() {
//		return selectedValues;
//	}
//
//	public void setSelectedValues(ArrayList<HelpObject> selectedValues) {
//		this.selectedValues = selectedValues;
//	}
	
	

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
//		method.setSelectedValues(this.selectedValues);
		method.setNewNodeName(this.newNodeName);
		method.setCategory(this.category);
		method.setValue1(this.value1);
		method.setValue2(this.value2);
		method.setValue3(this.value3);
		method.setValue4(this.value4);
		method.setValue5(this.value5);
		method.setValue6(this.value6);
		
		
		return method;
	}
	
	public HelpObject getValue1() {
		return value1;
	}

	public void setValue1(HelpObject value1) {
		this.value1 = value1;
	}

	public HelpObject getValue2() {
		return value2;
	}

	public void setValue2(HelpObject value2) {
		this.value2 = value2;
	}

	public HelpObject getValue3() {
		return value3;
	}

	public void setValue3(HelpObject value3) {
		this.value3 = value3;
	}

	public HelpObject getValue4() {
		return value4;
	}

	public void setValue4(HelpObject value4) {
		this.value4 = value4;
	}

	public HelpObject getValue5() {
		return value5;
	}

	public void setValue5(HelpObject value5) {
		this.value5 = value5;
	}

	public HelpObject getValue6() {
		return value6;
	}

	public void setValue6(HelpObject value6) {
		this.value6 = value6;
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
