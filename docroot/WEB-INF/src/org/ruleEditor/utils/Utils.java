package org.ruleEditor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.ruleEditor.ontology.BuiltinMethod;
import org.ruleEditor.ontology.Message;
import org.ruleEditor.ontology.OntologyClass;
import org.ruleEditor.ontology.OntologyProperty;
import org.ruleEditor.ontology.OntologyProperty.DataProperty;
import org.ruleEditor.ontology.OntologyProperty.ObjectProperty;
import org.ruleEditor.ontology.PointElement;
import org.ruleEditor.utils.MessageForJson.Group;
import org.ruleEditor.utils.MessageForJson.Group.TextMessage;
import org.ruleEditor.utils.Rule.RuleType;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.liferay.portal.util.PortalUtil;
import com.sun.faces.component.visit.FullVisitContext;

public class Utils {

	public static String prefix_c4a = "@prefix c4a: <http://rbmm.org/schemas/cloud4all/0.1/>.";
	public static String prefix_rdfs = "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.";
	private static int initialX = 3;
	private static int initialY = 3;
	private static int objectCounter = 0;
	private static int counter = 0;
	private static List<PointElement> conditions = new ArrayList<PointElement>();
	private static List<PointElement> conclusions = new ArrayList<PointElement>();

	public static String createRule(ArrayList<PointElement> conditions,
			ArrayList<PointElement> conclusions, String ruleName) {

		String rule = prefix_c4a + "\n" + prefix_rdfs + "\n\n" + "[ruleName \n";

		rule = rule + convertListToRule(conditions);

		rule = rule + "->\n";

		rule = rule + convertListToRule(conclusions);

		rule = rule + "]";

		return rule;
	}

	public static String createFeedBackRule(String className, String scope,
			String id, ArrayList<PointElement> conditions) {

		String rule = prefix_c4a + "\n" + prefix_rdfs + "\n\n" + "[ruleName \n";

		rule = rule + convertListToRule(conditions);

		rule = rule + "makeSkolem(?newMetaData, ?" + className + ", ?" + scope
				+ ")\n" + "->\n" + "(?newMetaData rdf:type c4a:Metadata)\n"
				+ "(?newMetaData c4a:scope ?" + scope + ")\n"
				+ "(?newMetaData c4a:messageType \"helpMessage\")\n"
				+ "(?newMetaData c4a:refersTo c4a:" + id + ")\n" + "(?"
				+ className + " c4a:hasMetadata ?newMetaData)\n]";

		return rule;
	}

	public static String convertListToRule(ArrayList<PointElement> list) {
		String rule = "";

		// work on conditions
		for (PointElement el : list) {
			if (el.getType() == PointElement.Type.CLASS) {

				rule = rule + "(" + "?" + el.getVarName() + " rdf:type "
						+ "c4a:" + el.getElementName() + ")\n";

			} else if (el.getType() == PointElement.Type.DATA_PROPERTY) {

				OntologyProperty property = (DataProperty) el.getProperty();
				if (el.getConnections().size() > 0)
					rule = rule + "(?"
							+ el.getConnections().get(0).getVarName() + " c4a:"
							+ el.getElementName() + " ?"
							+ ((DataProperty) property).getValue() + ")\n";

			} else if (el.getType() == PointElement.Type.OBJECT_PROPERTY) {

				// TODO: check that there are two connections
				// and define the correct order
				// in case that something is missing, throw away
				// the property node
				if (el.getConnections().size() > 1)
					rule = rule + "(?"
							+ el.getConnections().get(0).getVarName() + " c4a:"
							+ el.getElementName() + " " + "?"
							+ el.getConnections().get(1).getVarName() + ")\n";

			} else if (el.getType() == PointElement.Type.BUILTIN_METHOD) {

				rule = rule + el.getMethod().getOriginalName() + "(";
				for (PointElement temp : el.getConnections()) {
					DataProperty property = (DataProperty) temp.getProperty();
					rule = rule + "?" + property.getValue() + ",";
				}

				rule = rule.substring(0, rule.length() - 1);
				rule = rule + ")\n";
			}

		}

		return rule;
	}

	public UIComponent findComponent(final String id) {

		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot root = context.getViewRoot();
		final UIComponent[] found = new UIComponent[1];

		root.visitTree(new FullVisitContext(context), new VisitCallback() {
			@Override
			public VisitResult visit(VisitContext context, UIComponent component) {
				if (component.getId().equals(id)) {
					found[0] = component;
					return VisitResult.COMPLETE;
				}
				return VisitResult.ACCEPT;
			}
		});

		return found[0];

	}

	public static EndPoint createDotEndPoint(EndPointAnchor anchor) {
		DotEndPoint endPoint = new DotEndPoint(anchor);
		endPoint.setScope("panel");
		endPoint.setTarget(true);
		endPoint.setStyle("{fillStyle:'#98AFC7'}");
		endPoint.setHoverStyle("{fillStyle:'#5C738B'}");
		endPoint.setRadius(5);

		return endPoint;
	}

	public static EndPoint createRectangleEndPoint(EndPointAnchor anchor) {
		RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
		endPoint.setScope("panel");
		endPoint.setSource(true);
		endPoint.setStyle("{fillStyle:'#98AFC7'}");
		endPoint.setHoverStyle("{fillStyle:'#5C738B'}");
		endPoint.setWidth(10);
		endPoint.setHeight(10);

		return endPoint;
	}

	public static boolean findPanelOfElement(String id,
			ArrayList<PointElement> conditions,
			ArrayList<PointElement> conclusions) {

		boolean flag = false;
		for (PointElement el : conditions) {
			if (el.getVarName().equalsIgnoreCase(id)) {
				flag = true;
				break;
			}
		}

		if (!flag)
			for (PointElement el : conclusions) {
				if (el.getVarName().equalsIgnoreCase(id)) {
					flag = false;
					break;
				}
			}

		return flag;

	}

	public static Element getElementFromID(DefaultDiagramModel model, String id) {
		Element el = new Element();

		for (Element temp : model.getElements()) {
			PointElement node = (PointElement) temp.getData();
			if (node.getId().equalsIgnoreCase(id)) {
				el = temp;
				break;
			}
		}

		return el;
	}

	public static String writeMessagesInJsonLdFile(InputStream inputStream,
			List<Message> list) throws IOException {

		String json = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder out = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
		}

		String result = out.toString();
		System.out.println(result);
		reader.close();

		Gson gson = new Gson();
		Type type = new TypeToken<MessageForJson>() {
		}.getType();

		result = result.replace("c4a:", "").replace("@type", "type")
				.replace("@id", "id");

		// System.out.println(result);
		MessageForJson test = (MessageForJson) gson.fromJson(result, type);

		// append messages in the json
		String idforMessages = "aaa";
		json = appendMessages(list, test, idforMessages);

		return json;

	}

	public static String appendMessages(List<Message> list,
			MessageForJson test, String id) {
		String s = "";

		JsonObject json = new JsonObject();
		JsonObject context = new JsonObject();
		JsonArray graph = new JsonArray();

		context.add("c4a", new JsonPrimitive(test.getContext().getC4a()));
		context.add("rdfs", new JsonPrimitive(test.getContext().getRdfs()));

		JsonObject msg;
		JsonObject innerObj;
		JsonArray messages;
		// add existing messages
		for (Group temp : test.getGraph()) {
			msg = new JsonObject();
			messages = new JsonArray();
			msg.add("@id", new JsonPrimitive(temp.getId()));
			msg.add("@type", new JsonPrimitive(temp.getType()));

			for (TextMessage textMsg : temp.getMessages()) {
				innerObj = new JsonObject();
				innerObj.add(textMsg.getLanguage(),
						new JsonPrimitive(textMsg.getText()));
				messages.add(innerObj);
			}
			msg.add("@messages", messages);
			graph.add(msg);
		}

		// add new messages
		msg = new JsonObject();
		messages = new JsonArray();
		msg.add("@id", new JsonPrimitive(id));
		msg.add("@type", new JsonPrimitive("Message"));
		for (Message temp : list) {
			innerObj = new JsonObject();
			innerObj.add(temp.getLanguage(), new JsonPrimitive(temp.getText()));
			messages.add(innerObj);
		}

		msg.add("@messages", messages);
		graph.add(msg);

		json.add("context", context);
		json.add("graph", graph);

		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
				.serializeNulls()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.create();

		s = gson.toJson(json);

		return s;
	}

	static public String splitCamelCase(String s) {
		return s.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}

	public static List<List<PointElement>> convertRuleToDiagram(
			Rule selectedRule, List<ArrayList<OntologyClass>> classes,
			List<BuiltinMethod> methods) throws IOException {

		InputStream inputStream = new ByteArrayInputStream(selectedRule
				.getBody().getBytes());
		List<List<PointElement>> list = new ArrayList<List<PointElement>>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder out = new StringBuilder();
		String line;
		String ruleName = "";

		PointElement element;
		String panel = "conditions";
		conditions = new ArrayList<PointElement>();
		conclusions = new ArrayList<PointElement>();
		initialX = 3;
		initialY = 3;
		objectCounter = 0;

		while ((line = reader.readLine()) != null) {
			element = new PointElement();
			if (line.contains("[")) {

				ruleName = line.replace("[", "").replace(":", "");
				// System.out.println(ruleName);

			} else if (line.contains("]")) {
				// System.out.println("end of rule");
			} else if (line.contains("->")) {

				System.out.println("change of panel");
				panel = "conclusions";
				initialX = 3;
				initialY = 3;
				objectCounter = 0;

			} else if (!line.isEmpty() && !line.contains(prefix_rdfs)
					&& !line.contains(prefix_c4a)) {
				element = convertRuleLineToPointElement(line.trim(), classes,
						methods, panel);
				if (panel.equalsIgnoreCase("conditions"))
					conditions.add(element);
				else
					conclusions.add(element);

			}
			out.append(line);
		}

		// fill missing values in object properties
		fillMissingClasses(classes);

		list.add(conditions);
		list.add(conclusions);

		return list;
	}

	public static PointElement convertRuleLineToPointElement(String line,
			List<ArrayList<OntologyClass>> classes,
			List<BuiltinMethod> methods, String panel) {

		PointElement element = new PointElement();
		String[] splitted = null;
		String varName = "";
		String className = "";
		String propertyName = "";
		// declaration of a class
		if (line.contains("rdf:type")) {

			splitted = line.split(" ");
			varName = splitted[0].replace("(?", "");
			className = splitted[2].replace("c4a:", "").replace(")", "");
			element.setVarName(varName);
			element.setType(PointElement.Type.CLASS);
			element.setElementName(className);
			element.setPanel(panel);
			element.setId(setUniqueID());
			element = setPosition(element);

		}
		// declaration of a property
		else if (!line.contains("rdf:type") && line.contains("c4a")) {

			splitted = line.split(" ");
			element.setId(setUniqueID());
			element.setVarName(element.getId());
			element = setPosition(element);
			element.setPanel(panel);
			propertyName = splitted[1].replace("c4a:", "");
			varName = splitted[0].replace("(?", "");
			// find from varName which class owns this property
			PointElement classEl = findElementClassByVarName(varName);
			// find what type of property is, and the whole property
			OntologyProperty property = findPropertyByName(propertyName,
					classes, classEl.getElementName());
			element.setElementName(propertyName);
			// set the type of the element according to the type of the property
			if (property instanceof DataProperty) {
				DataProperty dataProp = (DataProperty) property.clone();
				dataProp.setValue(splitted[2].replace("?", "").replace(")", ""));
				element.setType(PointElement.Type.DATA_PROPERTY);
				element.setProperty(dataProp);
				element.getConnections().add(classEl);
			} else {
				element.setType(PointElement.Type.OBJECT_PROPERTY);
				String objectValue = splitted[2].replace("?", "").replace(")",
						"");
				PointElement objValue = findElementClassByVarName(objectValue);

				if (classEl.getId().isEmpty())
					classEl.setVarName(varName);

				element.getConnections().add(classEl);

				if (objValue.getId().isEmpty())
					objValue.setVarName(objectValue);

				if (property == null) {
					property = new OntologyProperty(propertyName, "");
				}

				element.getConnections().add(objValue);
				element.setProperty(property);
			}

		}
		// declaration of a built in method
		// TODO for primitive jena methods
		else {

		}

		return element;
	}

	public static void fillMissingClasses(List<ArrayList<OntologyClass>> classes) {
		for (PointElement el : conditions) {
			if (el.getType() == PointElement.Type.OBJECT_PROPERTY) {
				if (el.getConnections().get(0).getId().isEmpty()) {
					PointElement classEl = findElementClassByVarName(el
							.getConnections().get(0).getVarName());
					el.getConnections().remove(0);
					el.getConnections().add(0, classEl);
				}

				if (el.getConnections().get(1).getId().isEmpty()) {
					PointElement classEl = findElementClassByVarName(el
							.getConnections().get(1).getVarName());
					el.getConnections().remove(1);
					el.getConnections().add(1, classEl);
				}

				if (el.getProperty().getOntologyURI().isEmpty()) {
					OntologyProperty property = findPropertyByName(el
							.getProperty().getPropertyName(), classes, el
							.getConnections().get(0).getElementName());
					el.setProperty(property);
				}
			}
		}

		for (PointElement el : conclusions) {
			if (el.getType() == PointElement.Type.OBJECT_PROPERTY) {
				if (el.getConnections().get(0).getId().isEmpty()) {
					PointElement classEl = findElementClassByVarName(el
							.getConnections().get(0).getVarName());
					el.getConnections().remove(0);
					el.getConnections().add(0, classEl);
				}

				if (el.getConnections().get(1).getId().isEmpty()) {
					PointElement classEl = findElementClassByVarName(el
							.getConnections().get(1).getVarName());
					el.getConnections().remove(1);
					el.getConnections().add(1, classEl);
				}

				if (el.getProperty().getOntologyURI().isEmpty()) {
					OntologyProperty property = findPropertyByName(el
							.getProperty().getPropertyName(), classes, el
							.getConnections().get(0).getElementName());
					el.setProperty(property);
				}
			}
		}
	}

	public static PointElement findElementClassByVarName(String name) {

		PointElement element = new PointElement();

		for (PointElement pel : conditions) {
			if (pel.getType() == PointElement.Type.CLASS) {
				if (pel.getVarName().equalsIgnoreCase(name)) {
					element = pel.clone();
					break;
				}
			}
		}

		if (element.getId().isEmpty())
			for (PointElement pel : conclusions) {
				if (pel.getType() == PointElement.Type.CLASS) {
					if (pel.getVarName().equalsIgnoreCase(name)) {
						element = pel.clone();
						break;
					}
				}
			}
		return element;
	}

	public static OntologyProperty findPropertyByName(String name,
			List<ArrayList<OntologyClass>> classes, String className) {

		OntologyProperty property = new OntologyProperty("", "");

		for (ArrayList<OntologyClass> temp : classes) {

			if (className.equalsIgnoreCase(temp.get(0).getClassName())) {
				// look in data properties
				for (DataProperty dataProp : temp.get(0).getDataProperties()) {

					if (dataProp.getPropertyName().equalsIgnoreCase(name)) {
						property = (DataProperty) dataProp.clone();
						return property;
					}
				}

				// look in object properties

				for (ObjectProperty objProp : temp.get(0).getObjectProperties()) {
					if (objProp.getPropertyName().equalsIgnoreCase(name)) {
						property = (ObjectProperty) objProp.clone();
						return property;
					}
				}
			} else {

				for (OntologyClass childClass : temp.get(0).getChildren()) {
					property = findPropertyByNameInChildren(name, childClass,
							className);
					if (property != null)
						break;
				}
			}

		}

		return property;
	}

	public static OntologyProperty findPropertyByNameInChildren(String name,
			OntologyClass ontClass, String className) {

		OntologyProperty property = null;
		if (ontClass.getClassName().equalsIgnoreCase(className)) {
			// look in data properties
			for (DataProperty dataProp : ontClass.getDataProperties()) {
				if (dataProp.getPropertyName().equalsIgnoreCase(name)) {
					property = (DataProperty) dataProp.clone();
					return property;

				}
			}

			// look in object properties
			for (ObjectProperty objProp : ontClass.getObjectProperties()) {
				if (objProp.getPropertyName().equalsIgnoreCase(name)) {
					property = (ObjectProperty) objProp.clone();
					return property;
				}
			}
		} else {

			// recursion, look in the children classes
			for (OntologyClass temp : ontClass.getChildren()) {
				property = findPropertyByNameInChildren(name, temp, className);
				if (property != null)
					break;
			}
		}

		return property;

	}

	public static ArrayList<Rule> getRulesFromFile(InputStream inputStream)
			throws IOException {
		ArrayList<Rule> list = new ArrayList<Rule>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder out = new StringBuilder();
		String line;

		Rule rule = null;
		String name = "";
		String body = "";
		RuleType ruleType = RuleType.CONFLICT;
		String feedbackClass = "";
		String feedbackScope = "";
		String feedbackID = "";

		while ((line = reader.readLine()) != null) {
			if (!line.equalsIgnoreCase(prefix_c4a)
					&& !line.equalsIgnoreCase(prefix_rdfs)
					&& !line.contains("//") && !line.isEmpty())
				out.append(line.trim() + "\n");
		}

		String stringFile = out.toString().trim();
		String[] splitted = stringFile.split("]");
		for (int i = 0; i < splitted.length; i++) {

			name = splitted[i].trim().substring(1, splitted[i].indexOf(":"))
					.trim();
			name = name.replace(":", "").replace("(", "").trim();
			body = splitted[i].trim() + "]";

			// TODO check if its both conflict and feedback rule
			if (body.contains("Metadata")) {
				ruleType = RuleType.FEEDBACK;

				String[] splitted2 = null;
				String[] lines = body.split(">")[1].split("\n");

				for (int j = 0; j < lines.length; j++) {
					if (lines[j].contains("hasMetadata"))
						feedbackClass = lines[j].substring(2,
								lines[j].indexOf(" "));
					else if (lines[j].contains("scope")) {
						splitted2 = lines[j].split(" ");
						feedbackScope = splitted2[2].replace("?", "").replace(
								")", "");

					} else if (lines[j].contains("refersTo")) {
						splitted2 = lines[j].split(" ");
						feedbackID = splitted2[2].replace("c4a:", "").replace(
								")", "");
					}
				}

			} else
				ruleType = RuleType.CONFLICT;

			rule = new Rule(name, body, ruleType);
			rule.setFeedbackClass(feedbackClass);
			rule.setFeedbackScope(feedbackScope);
			rule.setFeedbackID(feedbackID);

			list.add(rule);
		}

		return list;
	}

	public static String showPrettyBodyRule(String body) {
		String newBody = "";

		String[] splitted = body.split(")");
		for (int i = 0; i < splitted.length; i++) {
			newBody = newBody.concat(splitted[i] + ")\n");
		}
		return newBody;
	}

	public static PointElement setPosition(PointElement el) {
		int x = 0;
		int y = 0;
		if (objectCounter == 0) {
			x = initialX;
			y = initialY;
			objectCounter++;
		} else if (objectCounter == 1) {
			x = initialX + 15;
			y = initialY;
			objectCounter++;
		} else if (objectCounter == 2) {
			x = initialX + 30;
			y = initialY;
			objectCounter = 0;
			initialY = initialY + 8;
		}

		el.setX(x);
		el.setY(y);

		return el;
	}

	public static String setUniqueID() {
		return "X_" + counter++;
	}
	
	public static ArrayList<String> getRuleArray(InputStream inputStream)
			throws IOException {
		ArrayList<String> rules = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder out = new StringBuilder();
		String line;
		String ruleString = "";
		int counter = 0;
		while ((line = reader.readLine()) != null) {
			if (line.contains("rules=")) {
				ruleString = ruleString.concat(line);
				counter++;
			}
			else if (!line.contains("queries=") && !line.isEmpty() && counter > 0) {
				ruleString = ruleString.concat(line);
				counter++;
			}

			else if (line.contains("queries="))
				counter = 0;
			
		}

		
		String[] splitted = ruleString.split(";");
		for (int i = 0; i < splitted.length; i++) {
			rules.add(splitted[i].replace("testData/rules/", ""));
		}
		
		return rules;
	}

}
