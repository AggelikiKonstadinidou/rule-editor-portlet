package org.ruleEditor.utils;

public class Term {
	
	private String type;
	private String id;
	private String value;
	private String rating;
	private String name;
	public Term(String type, String id, String value, String rating, String name) {
		super();
		this.type = type;
		this.id = id;
		this.value = value;
		this.rating = rating;
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
