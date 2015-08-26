package org.ruleEditor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.ruleEditor.ontology.OntologyProperty;
import org.ruleEditor.ontology.OntologyProperty.ObjectProperty;
import org.ruleEditor.ontology.PointElement;
import org.ruleEditor.ontology.OntologyProperty.DataProperty;
import org.ruleEditor.ontology.PointElement.Type;

public class RuleCreationUtilities {

	public static String prefix_c4a = "@prefix c4a: <http://rbmm.org/schemas/cloud4all/0.1/>.";
	public static String prefix_rdfs = "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.";

	public static List<String> categoryOfMethods_1 = Arrays.asList("isLiteral",
			"notLiteral", "isFunctor", "notFunctor", "isBNode", "notBNode");
	public static List<String> categoryOfMethods_2a = Arrays.asList("equal",
			"notEqual");
	public static List<String> categoryOfMethods_2b = Arrays.asList("lessThan",
			"greaterThan", "le", "ge");
	public static List<String> categoryOfMethods_3 = Arrays.asList("strConcat",
			"uriConcat", "sum", "addOne", "difference", "min", "max",
			"product", "quotient");
	public static List<String> categoryOfMethods_4 = Arrays
			.asList("makeSkolem");
	public static List<String> categoryOfMethods_5 = Arrays.asList("drop",
			"remove");
	public static List<String> categoryOfMethods_6 = Arrays.asList("noValue");
	public static List<String> categoryOfMethods_7 = Arrays.asList("print");
	public static List<String> categoryOfMethods_8 = Arrays.asList("now");
	public static List<String> categoryOfMethods_9 = Arrays.asList("makeTemp");
	public static List<String> categoryOfMethods_10 = Arrays
			.asList("makeInstance");

	public static ArrayList<String> declaredClassVariables;
	public static ArrayList<String> declaredObjectVariables;

	public static void saveRule(String ruleName, String fileName,
			ArrayList<PointElement> conditions,
			ArrayList<PointElement> conclusions, boolean flag,
			boolean createNewFile, String feedbackClass, String feedbackScope,
			String feedbackId, ArrayList<Rule> existingRules,
			InputStream fileStream) throws IOException {

		// create the rule string
		String rule = "";
		if (feedbackClass.isEmpty())
			rule = createRule(conditions, conclusions, ruleName);
		else
			rule = createFeedBackRule(feedbackClass, feedbackScope, feedbackId,
					conditions, conclusions, ruleName);

		// export the rule
		if (!rule.isEmpty()) {
			if (!createNewFile) {
				existingRules = Utils.getRulesFromFile(fileStream);
				String allRuleString = Utils.prefix_c4a + "\n"
						+ Utils.prefix_rdfs + "\n";
				for (Rule temp : existingRules) {
					allRuleString = allRuleString.concat(temp.getBody()) + "\n";
				}
				allRuleString = allRuleString.concat(rule
						.replace(Utils.prefix_c4a, "")
						.replace(Utils.prefix_rdfs, "").trim());
				rule = allRuleString;
			}

			FileDownloadController.writeGsonAndExportFile(fileName, rule);
		}

	}

	public static String createRule(ArrayList<PointElement> conditions,
			ArrayList<PointElement> conclusions, String ruleName) {

		declaredClassVariables = new ArrayList<String>();
		declaredObjectVariables = new ArrayList<String>();

		String rule = prefix_c4a + "\n" + prefix_rdfs + "\n\n" + "[" + ruleName
				+ "\n";

		rule = rule + convertListToRule(conditions);

		rule = rule + "->\n";

		rule = rule + convertListToRule(conclusions);

		rule = rule + "]";

		// if (rule.contains("noValue("))
		// rule = removeExtraNoValueSentence(rule);

		System.out.println(rule);

		return rule;
	}

	// public static void main(String args[]) {
	// String rule = "[Res1bTwoSolPreferred:\n"
	// + "(?conflict rdf:type c4a:Conflict)\n"
	// +"(?conflict c4a:deactivated ?x)\n"
	// + "(?conflict c4a:class ?class)\n"
	// + "(?config_b c4a:isActive \"true\")\n"
	// + "noValue(?conflict c4a:deactivated ?x)\n" + "->\n" + "drop(4, 5)\n"
	// + "]";
	//
	// if(rule.contains("noValue("))
	// rule = removeExtraNoValueSentence(rule);
	//
	// System.out.println(rule);
	//
	// }

	public static String removeExtraNoValueSentence(String rule) {
		String[] splitted = rule.split("\n");
		String noValueSentence = "";
		ArrayList<String> indexesToRemove = new ArrayList<String>();

		for (int i = 0; i < splitted.length; i++) {
			if (splitted[i].contains("noValue")) {
				noValueSentence = splitted[i];

				if (!noValueSentence.isEmpty())
					noValueSentence = noValueSentence.replace("noValue", "");

				for (int j = 0; j < splitted.length; j++) {
					if (splitted[j].equalsIgnoreCase(noValueSentence)) {
						indexesToRemove.add("" + j);
						break;
					}

				}

			}
		}

		String newRule = "";
		for (int i = 0; i < splitted.length; i++) {
			if (!indexesToRemove.contains("" + i))
				newRule = newRule.concat(splitted[i] + "\n");
		}

		return newRule;
	}

	public static String createFeedBackRule(String className, String scope,
			String id, ArrayList<PointElement> conditions,
			ArrayList<PointElement> conclusions, String ruleName) {

		String rule = prefix_c4a + "\n" + prefix_rdfs + "\n\n" + "[" + ruleName
				+ "\n";

		rule = rule + convertListToRule(conditions);

		rule = rule + "makeSkolem(?newMetaData, " + className + ", " + scope
				+ ")\n";

		rule = rule + "->\n";

		if (!conclusions.isEmpty())
			rule = rule + convertListToRule(conclusions);

		rule = rule + "(?newMetaData rdf:type c4a:Metadata)\n"
				+ "(?newMetaData c4a:scope " + scope + ")\n"
				+ "(?newMetaData c4a:messageType \"helpMessage\")\n"
				+ "(?newMetaData c4a:refersTo c4a:" + id + ")\n" + "("
				+ className + " c4a:hasMetadata ?newMetaData)\n]";

		return rule;
	}

	public static String convertListToRule(ArrayList<PointElement> list) {
		String rule = "";
		String tripleString = "";

		for (PointElement el : list) {

			if (el.getType() == PointElement.Type.CLASS) {
                String className = el.getElementName();
                String classVar = el.getClassVariable();
                rule = rule + "(" + classVar + " rdf:type c4a:"
						+ className + ")\n";
                if(!declaredClassVariables.contains(classVar))
                	declaredClassVariables.add(classVar);
			}

			else if (el.getType() == PointElement.Type.DATA_PROPERTY) {

				OntologyProperty property = (DataProperty) el.getProperty();
				String value = ((DataProperty) property).getValue();
				String classVar = property.getClassVar();

				if (!value.contains("?") && value.indexOf("\"") != 0)
					value = "\"" + value + "\"";

				// check if the property has class var or it is connected with
				// an instance
				if (el.getConnections().size() > 0) {
					classVar = el.getConnections().get(0).getInstance().getId();
				}

				if (classVar.contains("?"))
					if (!declaredClassVariables.contains(classVar)
							&& !declaredObjectVariables.contains(classVar)) {
//						rule = rule + "(" + classVar + " rdf:type c4a:"
//								+ property.getClassName() + ")\n";
						declaredClassVariables.add(classVar);
					}

				rule = rule + "(" + classVar + " c4a:" + el.getElementName()
						+ " " + value + ")\n";

			} else if (el.getType() == PointElement.Type.OBJECT_PROPERTY) {

				OntologyProperty property = (ObjectProperty) el.getProperty();
				String className = property.getClassName();
				String classRange = ((ObjectProperty) property)
						.getRangeOfClasses().get(0);
				String source = property.getClassVar();
				String target = property.getValue();

				if (el.getConnections().size() > 0) {
					for (PointElement temp : el.getConnections()) {

						if (temp.getType() == Type.INSTANCE) {
							if (temp.getInstance().getClassName()
									.equalsIgnoreCase(className))
								source = temp.getInstance().getId();
							else if (temp.getInstance().getClassName()
									.equalsIgnoreCase(classRange))
								target = temp.getInstance().getId();
						}

					}
				}

				// check if class of source is declared
				if (source.contains("?"))
					if (!declaredClassVariables.contains(source)
							&& !source.isEmpty()
							&& !declaredObjectVariables.contains(source)) {
//						rule = rule + "(" + source + " rdf:type c4a:"
//								+ property.getClassName() + ")\n";
						declaredClassVariables.add(source);
					}

				// check if class of target is declared
				if (target.contains("?"))
					if (!declaredObjectVariables.contains(target)
							&& !target.isEmpty()) {
						declaredObjectVariables.add(target);
					}

				rule = rule + "(" + source + " c4a:" + el.getElementName()
						+ " " + target + ")\n";

			} else if (el.getType() == PointElement.Type.BUILTIN_METHOD) {

				// covers at least the case of 1,2a,2b,3,4,5,7,8,9
				String s = "";
				if (categoryOfMethods_7.contains(el.getMethod()
						.getOriginalName())) {
					s = el.getMethod().getHelpString();
					el.getMethod().setHelpString("\"" + s + "\"");
				}

				// case of makeInstance method, find the order number of the
				// triple
				// create the triple and remove it from the rule body
				if (categoryOfMethods_6.contains(el.getMethod()
						.getOriginalName())) {
					int orderNumber = Integer.parseInt(el.getMethod()
							.getHelpString());
					PointElement triple = null;

					for (PointElement testEl : list) {
						if (testEl.getOrder() == orderNumber) {
							triple = testEl;
							break;
						}
					}

					// TODO create the triple
					tripleString = getTripleFromElement(triple);
					rule = rule.replace(tripleString, "");
				}

				if (!categoryOfMethods_6.contains(el.getMethod()
						.getOriginalName()))
					rule = rule + el.getMethod().getOriginalName() + "("
							+ el.getMethod().getHelpString() + ")\n";
				else
					rule = rule + el.getMethod().getOriginalName() + "("
							+ tripleString.replace("(", "").replace(")", "")
							+ ")\n";

			}

		}

		return rule;
	}

	public static String getTripleFromElement(PointElement el) {
		String triple = "";
		if (el.getType() == PointElement.Type.DATA_PROPERTY) {

			OntologyProperty property = (DataProperty) el.getProperty();
			String value = ((DataProperty) property).getValue();
			String classVar = property.getClassVar();

			if (!value.contains("?") && value.indexOf("\"") != 0)
				value = "\"" + value + "\"";

			// check if the property has class var or it is connected with
			// an instance
			if (el.getConnections().size() > 0) {
				classVar = el.getConnections().get(0).getInstance().getId();
			}

			if (classVar.contains("?"))
				if (!declaredClassVariables.contains(classVar)
						&& !declaredObjectVariables.contains(classVar)) {
					triple = "(" + classVar + " rdf:type c4a:"
							+ property.getClassName() + ")\n";
					declaredClassVariables.add(classVar);
				}

			triple = "(" + classVar + " c4a:" + el.getElementName() + " "
					+ value + ")\n";

		} else if (el.getType() == PointElement.Type.OBJECT_PROPERTY) {

			OntologyProperty property = (ObjectProperty) el.getProperty();
			String className = property.getClassName();
			String classRange = ((ObjectProperty) property).getRangeOfClasses()
					.get(0);
			String source = property.getClassVar();
			String target = property.getValue();

			if (el.getConnections().size() > 0) {
				for (PointElement temp : el.getConnections()) {

					if (temp.getType() == Type.INSTANCE) {
						if (temp.getInstance().getClassName()
								.equalsIgnoreCase(className))
							source = temp.getInstance().getId();
						else if (temp.getInstance().getClassName()
								.equalsIgnoreCase(classRange))
							target = temp.getInstance().getId();
					}

				}
			}

			// check if class of source is declared
			if (source.contains("?"))
				if (!declaredClassVariables.contains(source)
						&& !source.isEmpty()
						&& !declaredObjectVariables.contains(source)) {
					triple = "(" + source + " rdf:type c4a:"
							+ property.getClassName() + ")\n";
					declaredClassVariables.add(source);
				}

			// check if class of target is declared
			if (target.contains("?"))
				if (!declaredObjectVariables.contains(target)
						&& !target.isEmpty()) {
					declaredObjectVariables.add(target);
				}

			triple = "(" + source + " c4a:" + el.getElementName() + " "
					+ target + ")\n";

		}

		return triple;
	}

	public static String getCategoryByName(String name) {
		String category = "";

		if (categoryOfMethods_1.contains(name))
			category = "1";
		else if (categoryOfMethods_2a.contains(name))
			category = "2a";
		else if (categoryOfMethods_2b.contains(name))
			category = "2b";
		else if (categoryOfMethods_3.contains(name))
			category = "3";
		else if (categoryOfMethods_4.contains(name))
			category = "4";
		else if (categoryOfMethods_5.contains(name))
			category = "5";
		else
			category = "-1"; // non defined category yet

		return category;
	}

	public static ArrayList<String> getDeclaredClassVariables() {
		return declaredClassVariables;
	}

	public static void setDeclaredClassVariables(
			ArrayList<String> declaredClassVariables) {
		RuleCreationUtilities.declaredClassVariables = declaredClassVariables;
	}

	public static ArrayList<String> getDeclaredObjectVariables() {
		return declaredObjectVariables;
	}

	public static void setDeclaredObjectVariables(
			ArrayList<String> declaredObjectVariables) {
		RuleCreationUtilities.declaredObjectVariables = declaredObjectVariables;
	}

}
