package org.ruleEditor.utils;

public class Rule {

	private String name;
	private String body;
	private RuleType ruleType;
	
	public Rule(String name, String body, RuleType ruleType) {
		super();
		this.name = name;
		this.body = body;
		this.ruleType = ruleType;
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
	
	

	@Override
	public String toString() {
		return "Rule [name=" + name + ", body=" + body + ", ruleType="
				+ ruleType + "]";
	}



	public enum RuleType {
		CONFLICT, FEEDBACK, BOTH;
	}
}
