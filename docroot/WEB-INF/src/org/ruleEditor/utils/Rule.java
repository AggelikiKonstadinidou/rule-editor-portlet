package org.ruleEditor.utils;

import java.sql.Timestamp;

public class Rule {

	private String name;
	private String body;
	private int uniqueID;
	private RuleType ruleType;
	private String feedbackClass;
	private String feedbackScope;
	private String feedbackID;
	private String description;
	private Timestamp creationDate;
	private Timestamp lastModifiedDate;
	
	public Rule() {
		super();
		this.name = "";
		this.body = "";
		this.ruleType = RuleType.CONFLICT;
		this.feedbackClass ="";
		this.feedbackScope ="";
		this.feedbackID = "";
		this.uniqueID = -1;
	}
	

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(int uniqueID) {
		this.uniqueID = uniqueID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBody() {
		
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	
	public String getFeedbackClass() {
		return feedbackClass;
	}

	public void setFeedbackClass(String feedbackClass) {
		this.feedbackClass = feedbackClass;
	}

	public String getFeedbackScope() {
		return feedbackScope;
	}

	public void setFeedbackScope(String feedbackScope) {
		this.feedbackScope = feedbackScope;
	}

	public String getFeedbackID() {
		return feedbackID;
	}

	public void setFeedbackID(String feedbackID) {
		this.feedbackID = feedbackID;
	}

	@Override
	public String toString() {
		return "Rule [name=" + name + ", body=" + body + ", ruleType="
				+ ruleType + "]";
	}

	public enum RuleType {
		CONFLICT, FEEDBACK, GENERAL;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getName().equals(((Rule) obj).getName())
				&& this.getUniqueID() == (((Rule) obj).getUniqueID())
				&& this.getBody().equals(((Rule) obj).getBody())) {
			return true;
		} else
			return false;
	}
	
	public Rule clone() {
		Rule rule = new Rule();
		rule.setName(this.name);
		rule.setDescription(this.description);
		rule.setBody(this.body);
		rule.setCreationDate(this.creationDate);
		rule.setLastModifiedDate(this.lastModifiedDate);
		rule.setFeedbackClass(this.feedbackClass);
		rule.setFeedbackID(this.feedbackID);
		rule.setFeedbackScope(this.feedbackScope);
		rule.setRuleType(this.ruleType);
		
		return rule;
	}
}
