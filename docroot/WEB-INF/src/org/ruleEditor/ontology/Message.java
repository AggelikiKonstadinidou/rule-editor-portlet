package org.ruleEditor.ontology;

import java.util.ArrayList;


public class Message {
	
	private String language;
	private String text;
	private ArrayList<String> relatedClasses;
	private String relatedClassesString;
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ArrayList<String> getRelatedClasses() {
		return relatedClasses;
	}
	public void setRelatedClasses(ArrayList<String> relatedClasses) {
		this.relatedClasses = relatedClasses;
	}
	public String getRelatedClassesString() {
		return relatedClassesString;
	}
	public void setRelatedClassesString(String relatedClassesString) {
		this.relatedClassesString = relatedClassesString;
	}
	public Message() {
		super();
		this.language = "";
		this.text = "";
		this.relatedClasses = new ArrayList<String>();
		this.relatedClassesString = "";
	}
	
	

}
