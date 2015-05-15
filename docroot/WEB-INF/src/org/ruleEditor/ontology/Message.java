package org.ruleEditor.ontology;


public class Message {
	
	private String language;
	private String text;
	
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
	public Message() {
		super();
		this.language = "";
		this.text = "";
	}
	
	

}
