package org.ruleEditor.utils;

public class Term {
	
	private String type;
	private String id;
	private String value;
	private String rating;
	private String name;
	private boolean abstractTerm;
	
	public Term(String type, String id, String value, String rating, String name) {
		super();
		this.type = type;
		this.id = id;
		this.value = value;
		this.rating = rating;
		this.name = name;
		this.abstractTerm = false;
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
	public boolean isAbstractTerm() {
		return abstractTerm;
	}
	public void setAbstractTerm(boolean abstractTerm) {
		this.abstractTerm = abstractTerm;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getType().equals(((Term) obj).getType())
				&& this.getId().equals(((Term) obj).getId())
				&& this.getRating().equals(((Term) obj).getRating())
				&& this.getName().equals(((Term) obj).getName())
				&& this.getValue().equals(((Term) obj).getValue())) {
			return true;
		} else
			return false;
	}
	
	public Term clone() {
		Term term = new Term("","", "", "", "");
		term.setType(this.type);
		term.setId(this.id);
		term.setName(this.name);
		term.setValue(this.value);
		term.setRating(this.rating);
		term.setAbstractTerm(this.abstractTerm);

		return term;
	}
	
	
}
