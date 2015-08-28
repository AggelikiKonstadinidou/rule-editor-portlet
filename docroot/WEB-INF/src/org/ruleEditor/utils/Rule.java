package org.ruleEditor.utils;

public class Rule {

	private String name;
	private String body;
	private int uniqueID;
	private RuleType ruleType;
	private String feedbackClass;
	private String feedbackScope;
	private String feedbackID;
	
	public Rule(String name, String body, RuleType ruleType,int uniqueID) {
		super();
		this.name = name;
		this.body = body;
		this.ruleType = ruleType;
		this.feedbackClass ="";
		this.feedbackScope ="";
		this.feedbackID = "";
		this.uniqueID = uniqueID;
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
		CONFLICT, FEEDBACK, BOTH;
	}
}
