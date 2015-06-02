package org.ruleEditor.utils;

import java.util.ArrayList;

public class RuleFile {
	
	private String fileName;
	private ArrayList<Rule> rules;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ArrayList<Rule> getRules() {
		return rules;
	}
	public void setRules(ArrayList<Rule> rules) {
		this.rules = rules;
	}
	public RuleFile(String fileName, ArrayList<Rule> rules) {
		super();
		this.fileName = fileName;
		this.rules = rules;
	}
	

}
