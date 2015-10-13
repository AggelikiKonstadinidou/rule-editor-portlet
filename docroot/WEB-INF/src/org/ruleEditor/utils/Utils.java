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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.coode.owlapi.owlxml.renderer.OWLXMLOntologyStorer;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.ruleEditor.ontology.BuiltinMethod;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.ontology.Message;
import org.ruleEditor.ontology.OntologyClass;
import org.ruleEditor.ontology.OntologyProperty;
import org.ruleEditor.ontology.OntologyProperty.DataProperty;
import org.ruleEditor.ontology.OntologyProperty.ObjectProperty;
import org.ruleEditor.ontology.PointElement;
import org.ruleEditor.utils.MessageForJson.Group;
import org.ruleEditor.utils.MessageForJson.Group.TextMessage;
import org.ruleEditor.utils.RecommendationForJson.Recommendation;
import org.ruleEditor.utils.Rule.RuleType;

import com.github.jsonldjava.core.JsonLdApi;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.RDFDatasetUtils;
import com.github.jsonldjava.utils.JSONUtils;
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
	private static List<PointElement> conditions;
	private static List<PointElement> conclusions;
	private static HashMap<String, String> usedVariablesForClasses;// subjects
	private static HashMap<String, String> usedVariables; // values and objects
	private static int orderCounter;

	public static void initializeLists() {
		conditions = new ArrayList<PointElement>();
		conclusions = new ArrayList<PointElement>();
		usedVariablesForClasses = new HashMap<String, String>();
		usedVariables = new HashMap<String, String>();
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
			if (el.getId().equalsIgnoreCase(id)) {
				flag = true;
				break;
			}
		}

		if (!flag)
			for (PointElement el : conclusions) {
				if (el.getId().equalsIgnoreCase(id)) {
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

	public static ArrayList<Message> correlateRules(
			HashMap<String, InputStream> hashmap,
			List<PointElement> conditions, List<PointElement> conclusions,
			Main main) throws IOException {
		ArrayList<Message> correlatedFiles = new ArrayList<Message>();
		// the string is the name of the file, the list contain the rules of the
		// file
		HashMap<String, ArrayList<Rule>> hashmap2 = new HashMap<String, ArrayList<Rule>>();
		ArrayList<Rule> rulesList;

		Iterator it = hashmap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			rulesList = getRulesFromFile((InputStream) pair.getValue());
			hashmap2.put(pair.getKey().toString(), rulesList);
		}

		HashMap<String, ArrayList<String>> returnedHashMap = convertRulesToLists(
				hashmap2, main);

		ArrayList<String> ruleUsedClassesFromConditions = getUsedClassesFromList(conditions);
		ArrayList<String> ruleUsedClassesFromConclusions = getUsedClassesFromList(conclusions);

		for (String s : ruleUsedClassesFromConclusions) {
			if (!ruleUsedClassesFromConditions.contains(s))
				ruleUsedClassesFromConditions.add(s);
		}

		correlatedFiles = findCorrelatedRules(returnedHashMap,
				ruleUsedClassesFromConditions);

		return correlatedFiles;

	}

	public static ArrayList<String> getUsedClassesFromList(
			List<PointElement> list) {
		ArrayList<String> ruleUsedClasses = new ArrayList<String>();
		String className = "";
		for (PointElement el : list) {
			if (el.getType() != PointElement.Type.BUILTIN_METHOD) {
				if (el.getType() == PointElement.Type.INSTANCE) {
					className = el.getInstance().getClassName();

				} else if (el.getType() == PointElement.Type.DATA_PROPERTY
						|| el.getType() == PointElement.Type.OBJECT_PROPERTY) {

					className = el.getProperty().getClassName();

				} else if (el.getType() == PointElement.Type.CLASS) {
					className = el.getElementName();
				}

				if (!ruleUsedClasses.contains(className))
					ruleUsedClasses.add(className);
			}
		}

		return ruleUsedClasses;
	}

	public static ArrayList<Message> findCorrelatedRules(
			HashMap<String, ArrayList<String>> hashmap,
			ArrayList<String> usedClassesFromCreatedRule) {
		ArrayList<Message> list = new ArrayList<Message>();
		ArrayList<String> tempList = new ArrayList<String>();
		ArrayList<String> relatedClassesList = new ArrayList<String>();
		String relatedClassesString = "";

		int numberOfUsedClasses = usedClassesFromCreatedRule.size();
		int numberOfCommonClasses = 0;
		Message msg = null;

		Iterator it = hashmap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			tempList = (ArrayList<String>) pair.getValue();
			for (String s : usedClassesFromCreatedRule) {
				if (tempList.contains(s)) {
					numberOfCommonClasses++;
					if (!relatedClassesList.contains(s))
						relatedClassesList.add(s);
				}
			}

			System.out
					.println("correlation "
							+ (double) ((double) numberOfCommonClasses / (double) numberOfUsedClasses));

			// if ((double) ((double) numberOfCommonClasses / (double)
			// numberOfUsedClasses) > 0.5) {
			msg = new Message();
			String[] splitted = pair.getKey().toString().split("__");
			msg.setLanguage(splitted[0]);
			msg.setText(splitted[1]);
			msg.setRelatedClasses(relatedClassesList);
			int length = relatedClassesList.size() - 1;
			for (String s : relatedClassesList) {
				if (relatedClassesList.indexOf(s) != length)
					relatedClassesString = relatedClassesString.concat(s + ",");
				else
					relatedClassesString = relatedClassesString.concat(s);
			}
			if (relatedClassesString.isEmpty())
				relatedClassesString = "-";
			msg.setRelatedClassesString(relatedClassesString);
			list.add(msg);
			// }

			numberOfCommonClasses = 0;
			relatedClassesString = "";
			relatedClassesList = new ArrayList<String>();
		}

		return list;
	}

	public static HashMap<String, ArrayList<String>> convertRulesToLists(
			HashMap<String, ArrayList<Rule>> hashmap, Main main)
			throws IOException {

		ArrayList<Rule> tempList = new ArrayList<Rule>();
		List<List<PointElement>> list;
		ArrayList<String> usedClasses;
		HashMap<String, ArrayList<String>> hashMapToReturn = new HashMap<String, ArrayList<String>>();

		// iterate the hashmap and return a new hashmap with
		// key the fileName+ruleName and value a list with all the classes
		// that are modified through the specific rule
		Iterator it = hashmap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			tempList = (ArrayList<Rule>) pair.getValue();
			for (Rule tempRule : tempList) {
				list = convertRuleToDiagram(tempRule, main.getAllClasses(),
						main.getMethods());
				usedClasses = getUsedClassesFromPointElements(list);
				hashMapToReturn.put(
						pair.getKey().toString() + "__" + tempRule.getName(),
						usedClasses);
			}

		}

		return hashMapToReturn;

	}

	public static ArrayList<String> getUsedClassesFromPointElements(
			List<List<PointElement>> list) {
		ArrayList<String> usedClasses = new ArrayList<String>();
		String className = "";
		for (List<PointElement> tempList : list) {
			for (PointElement el : tempList) {
				if (el.getType() != PointElement.Type.BUILTIN_METHOD) {
					if (el.getType() == PointElement.Type.INSTANCE) {
						className = el.getInstance().getClassName();

					} else if (el.getType() == PointElement.Type.DATA_PROPERTY
							|| el.getType() == PointElement.Type.OBJECT_PROPERTY) {

						className = el.getProperty().getClassName();

					} else if (el.getType() == PointElement.Type.CLASS) {
						className = el.getElementName();
					}

					if (!usedClasses.contains(className))
						usedClasses.add(className);
				}
			}
		}

		return usedClasses;

	}

	public static String writeMessagesInJsonLdFile(InputStream inputStream,
			List<Message> list) throws IOException {

		String json = "";
		Gson gson = new Gson();

		Type type = new TypeToken<MessageForJson>() {
		}.getType();

		String result = readJsonLd(inputStream);

		// System.out.println(result);
		MessageForJson test = (MessageForJson) gson.fromJson(result, type);

		// append messages in the json
		String idforMessages = "aaa";
		json = appendMessages(list, test, idforMessages);

		return json;

	}

	public static String createJsonLdKnowledge(
			ArrayList<RecommendationForJson> terms) {

		String jsonString = "";
		JsonObject json = new JsonObject();
		JsonObject context = new JsonObject();
		JsonArray graph = new JsonArray();

		context.add("c4a", new JsonPrimitive(Utils.prefix_c4a));
		context.add("rdfs", new JsonPrimitive(Utils.prefix_rdfs));

		JsonObject abstractTerm = null;
		JsonObject recommendation = null;
		JsonArray recommendations = null;

		for (RecommendationForJson temp : terms) {
			abstractTerm = new JsonObject();
			abstractTerm.add("@type", new JsonPrimitive("c4a:AbstractTerm"));
			abstractTerm.add("c4a:id", new JsonPrimitive(temp.getId()));
			abstractTerm.add("c4a:value", new JsonPrimitive(temp.isValue()));
			abstractTerm.add("c4a:rating", new JsonPrimitive(temp.getRating()));

			recommendations = new JsonArray();
			for (Recommendation temp2 : temp.getHasRecommendation()) {
				recommendation = new JsonObject();
				recommendation
						.add("@type", new JsonPrimitive("Recommendation"));
				recommendation.add("c4a:id", new JsonPrimitive(temp2.getId()));
				recommendation.add("c4a:name",
						new JsonPrimitive(temp2.getName()));
				recommendation.add("c4a:value",
						new JsonPrimitive(temp2.getValue()));
				recommendations.add(recommendation);

			}

			abstractTerm.add("c4a:hasRecommendation", recommendations);
			graph.add(abstractTerm);
		}

		json.add("@context", context);
		json.add("@graph", graph);

		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
				.serializeNulls()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.create();

		jsonString = gson.toJson(json);

		return jsonString;
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

	public static String readJsonLd(InputStream inputStream) throws IOException {
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

		result = result.replace("c4a:", "").replace("@type", "type")
				.replace("@id", "id");

		return result;

	}

	public static ArrayList<RecommendationForJson> getObjectsFromJsonLd(
			InputStream inputStream, String result) throws IOException,
			JsonLdError {

		LinkedHashMap<String, Object> hashmap = (LinkedHashMap<String, Object>) JSONUtils
				.fromString(result);
		Iterator it = hashmap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = "
					+ JSONUtils.toPrettyString(pair.getValue()));
			if (pair.getKey().toString().contains("graph")) {
				result = JSONUtils.toPrettyString(pair.getValue());
			}
		}

		Gson gson = new Gson();

		Type type = new TypeToken<ArrayList<RecommendationForJson>>() {
		}.getType();

		ArrayList<RecommendationForJson> list = (ArrayList<RecommendationForJson>) gson
				.fromJson(result, type);

		return list;

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
		usedVariablesForClasses = new HashMap<String, String>();
		usedVariables = new HashMap<String, String>();
		orderCounter = 1;

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

			} else if (line.equalsIgnoreCase("]")) {
				// System.out.println("end of rule");
			} else if (line.contains("->")) {

				System.out.println("change of panel");
				panel = "conclusions";
				initialX = 3;
				initialY = 3;
				objectCounter = 0;
				orderCounter = 1;

			} else if (!line.isEmpty() && !line.contains(prefix_rdfs)
					&& !line.contains(prefix_c4a)) {

				if (line.contains("]"))
					line = line.replace("]", "");

				element = convertRuleLineToPointElement(line.trim(), classes,
						methods, panel);
				if (element != null)
					if (panel.equalsIgnoreCase("conditions"))
						conditions.add(element);
					else
						conclusions.add(element);

			}
			out.append(line);
		}

		// fill missing values
		fillMissingClasses(classes);

		list.add(conditions);
		list.add(conclusions);

		return list;
	}

	public static void fillMissingClasses(List<ArrayList<OntologyClass>> classes) {

		String className = "";
		String varName = "";
		String propertyName = "";
		for (PointElement el : conditions) {
			if (el.getType() != PointElement.Type.BUILTIN_METHOD
					&& el.getType() != PointElement.Type.CLASS)
				if (el.getProperty().getClassName().isEmpty()
						&& (!el.getProperty().getClassVar().isEmpty() || el
								.getLabel().isEmpty())) {
					varName = el.getProperty().getClassVar();
					className = findElementClassByVarName(varName);
					propertyName = el.getProperty().getPropertyName();
					// find what type of property is, and the whole property
					OntologyProperty property = findPropertyByName(
							propertyName, classes, className);
					el.setElementName(propertyName);
					el.setLabel(className);

					if (property instanceof DataProperty) {
						DataProperty dataProp = (DataProperty) property.clone();
						dataProp.setClassVar(varName);
						dataProp.setValue(el.getProperty().getValue());
						el.setType(PointElement.Type.DATA_PROPERTY);
						el.setProperty(dataProp);
						// usedVariables.put(value, value);
					} else if (property instanceof ObjectProperty) {
						el.setType(PointElement.Type.OBJECT_PROPERTY);
						ObjectProperty objProp = (ObjectProperty) property
								.clone();

						if (el.getProperty().getValue().contains("?"))
							usedVariablesForClasses.put(el.getProperty()
									.getValue(), objProp.getRangeOfClasses()
									.get(0));

						objProp.setValue(el.getProperty().getValue());
						objProp.setClassVar(varName);

						// TODO: case of instance
						el.setProperty(objProp);
					}

				}
		}
	}

	public static PointElement convertRuleLineToPointElement(String line,
			List<ArrayList<OntologyClass>> classes,
			List<BuiltinMethod> methods, String panel) {

		PointElement element = new PointElement();
		String[] splitted = null;
		String varName = "";
		String className = "";
		String propertyName = "";
		String value = "";
		String range = "";

		// declaration of a property
		if (line.contains("rdf:type c4a")) {
			splitted = line.split(" ");
			varName = splitted[0].replace("(", "");
			className = splitted[2].replace("c4a:", "").replace(")", "");
			usedVariablesForClasses.put(varName, className);
			element.setId(setUniqueID());
			element = setPosition(element);
			element.setPanel(panel);
			element.setClassVariable(varName);
			element.setElementName(className);
			element.setLabel("Class");
			element.setType(PointElement.Type.CLASS);
			element.setOrder(orderCounter);
			orderCounter++;

		} else if (!line.contains("rdf:type") && line.contains("c4a")
				&& !line.contains("noValue")) {

			splitted = line.split(" ");
			element.setId(setUniqueID());
			element = setPosition(element);
			element.setPanel(panel);
			propertyName = splitted[1].replace("c4a:", "");
			varName = splitted[0].replace("(", "");
			// find from varName which class owns this property
			className = findElementClassByVarName(varName);
			element.setOrder(orderCounter);
			orderCounter++;

			if (!className.isEmpty()) {
				// find what type of property is, and the whole property
				OntologyProperty property = findPropertyByName(propertyName,
						classes, className);
				element.setElementName(propertyName);
				element.setLabel(className);

				usedVariablesForClasses.put(varName, className);

				// set the type of the element according to the type of the
				// property
				if (property instanceof DataProperty) {
					DataProperty dataProp = (DataProperty) property.clone();
					value = splitted[2].replace(")", "");
					dataProp.setValue(value);
					dataProp.setClassVar(varName);
					element.setType(PointElement.Type.DATA_PROPERTY);
					element.setProperty(dataProp);
					usedVariables.put(value, value);
				} else if (property instanceof ObjectProperty) {
					element.setType(PointElement.Type.OBJECT_PROPERTY);
					ObjectProperty objProp = (ObjectProperty) property.clone();
					String objectValue = splitted[2].replace(")", "");

					if (objectValue.contains("?"))
						usedVariablesForClasses.put(objectValue, objProp
								.getRangeOfClasses().get(0));

					objProp.setValue(objectValue);
					objProp.setClassVar(varName);

					// TODO: case of instance
					element.setProperty(objProp);
				}
			}

			// the case where the declaration of the class
			// follows the declaration of the property
			if (className.isEmpty()) {
				element.setType(PointElement.Type.DATA_PROPERTY);
				element.getProperty().setClassVar(varName);
				// case of e.g. noValue(?config c4a:solPreferred)
				if (splitted.length > 2)
					value = splitted[2].replace(")", "");
				else
					value = "";
				element.getProperty().setValue(value);
				element.getProperty().setPropertyName(propertyName);
			}

		}
		// declaration of a built in method
		// TODO for primitive jena methods
		else {
			String argument = line.substring(line.indexOf("(") + 1,
					line.indexOf(")"));
			String originName = line.replace(argument, "").replace("(", "")
					.replace(")", "");

			// String category =
			// RuleCreationUtilities.getCategoryByName(originName);

			BuiltinMethod method = null;
			// find the method object by the original name
			for (BuiltinMethod temp : methods) {
				if (temp.getOriginalName().equals(originName)) {
					method = temp.clone();
					break;
				}
			}

			String[] argumentsSplitted = null;
			boolean flag = false;
			if (!method.getCategory().equals("1"))
				argumentsSplitted = argument.split(",");

			if (method.getCategory().equals("1")) {
				method.getValue1().setValue(argument.trim());

			} else if (method.getCategory().equals("2a")) {

				// check if the arguments are variables or classes
				// if they are classes assigned them to 1 and 2, else
				// assign them to 3 and 4.
				flag = findTypeOfVariable(argumentsSplitted[0].replace(" ", ""));
				if (flag) {
					method.getValue1().setValue(
							argumentsSplitted[0].replace(" ", ""));
					method.getValue2().setValue(
							argumentsSplitted[1].replace(" ", ""));
				} else {
					method.getValue3().setValue(
							argumentsSplitted[0].replace(" ", ""));
					method.getValue4().setValue(
							argumentsSplitted[1].replace(" ", ""));
					method.setFlag(true);
				}

			} else if (method.getCategory().equals("2b")) {

				method.getValue1().setValue(
						argumentsSplitted[0].replace(" ", ""));
				method.getValue2().setValue(
						argumentsSplitted[1].replace(" ", ""));

			} else if (method.getCategory().equals("3")) {
				method.getValue1().setValue(
						argumentsSplitted[0].replace(" ", ""));
				method.getValue2().setValue(
						argumentsSplitted[1].replace(" ", ""));
				method.getValue3().setValue(
						argumentsSplitted[2].replace(" ", ""));

			} else if (method.getCategory().equals("4")) {

				method.getValue1().setValue(
						argumentsSplitted[0].replace(" ", ""));
				method.getValue2().setValue(
						argumentsSplitted[1].replace(" ", ""));
				method.getValue3().setValue(
						argumentsSplitted[2].replace(" ", ""));

			} // case of noValue method
			else if (method.getCategory().equals("6")) {
				// !! CREATE TRIPLE FOR NOVALUE METHOD
				String[] triple = argument.split(" ");
				PointElement tripleEl = new PointElement();
				tripleEl.setId(setUniqueID());
				tripleEl = setPosition(tripleEl);
				tripleEl.setPanel(panel);
				propertyName = triple[1].replace("c4a:", "");
				varName = triple[0].replace("(", "");
				// find from varName which class owns this property
				className = findElementClassByVarName(varName);
				tripleEl.setOrder(orderCounter);
				orderCounter++;

				if (!className.isEmpty()) {
					// find what type of property is, and the whole property
					OntologyProperty property = findPropertyByName(
							propertyName, classes, className);
					tripleEl.setElementName(propertyName);
					tripleEl.setLabel(className);

					usedVariablesForClasses.put(varName, className);

					// set the type of the element according to the type of the
					// property
					if (property instanceof DataProperty) {
						DataProperty dataProp = (DataProperty) property.clone();
						if (triple.length > 2)
							value = triple[2].replace(")", "");
						else
							value = "";
						dataProp.setValue(value);
						dataProp.setClassVar(varName);
						tripleEl.setType(PointElement.Type.DATA_PROPERTY);
						tripleEl.setProperty(dataProp);
						usedVariables.put(value, value);
					} else if (property instanceof ObjectProperty) {
						element.setType(PointElement.Type.OBJECT_PROPERTY);
						ObjectProperty objProp = (ObjectProperty) property
								.clone();
						String objectValue = "";
						if (triple.length > 2)
							objectValue = triple[2].replace(")", "");

						if (objectValue.contains("?"))
							usedVariablesForClasses.put(objectValue, objProp
									.getRangeOfClasses().get(0));

						objProp.setValue(objectValue);
						objProp.setClassVar(varName);

						// TODO: case of instance
						tripleEl.setProperty(objProp);

					}
				}

				// the case where the declaration of the class
				// follows the declaration of the property
				if (className.isEmpty()) {
					tripleEl.getProperty().setClassVar(varName);
					// case of e.g. noValue(?config c4a:solPreferred)
					if (triple.length > 2)
						value = triple[2].replace(")", "");
					else
						value = "";
					tripleEl.getProperty().setValue(value);
					tripleEl.getProperty().setPropertyName(propertyName);
				}

				if (tripleEl != null)
					if (panel.equalsIgnoreCase("conditions"))
						conditions.add(tripleEl);
					else
						conclusions.add(tripleEl);

				argument = "" + tripleEl.getOrder();
				method.getValue1().setValue(argument.trim());
				// !!END OF CREATION TRIPLE FOR METHOD

			} else if (method.getCategory().equals("5")
					|| method.getCategory().equals("9")
					|| method.getCategory().equals("7")
					|| method.getCategory().equals("8")) {
				method.getValue1().setValue(argument.trim());

				if (method.getCategory().equals("8"))
					usedVariablesForClasses.put(argument.trim(),
							"category8Method");
				else if (method.getCategory().equals("9"))
					usedVariables.put(argument.trim(), "category9Method");
			}

			method.setHelpString(argument.trim());
			element.setMethod(method);
			element.setLabel("Method");
			element.setElementName(method.getUsingName());
			element.setType(PointElement.Type.BUILTIN_METHOD);
			element.setId(setUniqueID());
			element = setPosition(element);
			element.setPanel(panel);
			element.setOrder(orderCounter);
			orderCounter++;

		}

		return element;
	}

	public static boolean findTypeOfVariable(String var) {
		boolean flag = false;
		Iterator it = usedVariablesForClasses.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if (pair.getKey().equals(var)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static String findElementClassByVarName(String varName) {
		String className = "";
		Iterator it = usedVariablesForClasses.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if (pair.getKey().equals(varName)) {
				className = pair.getValue().toString();
				break;
			}
		}

		if (className.isEmpty()) {
			it = usedVariables.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				if (pair.getKey().equals(varName)) {
					className = pair.getValue().toString();
					break;
				}
			}
		}

		return className;

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
		String description = "";
		Timestamp creationDate = null;
		Timestamp lastModifiedDate = null;

		RuleType ruleType = RuleType.CONFLICT;
		String feedbackClass = "";
		String feedbackScope = "";
		String feedbackID = "";
		int counter = 0;
		boolean flag = true;

		while ((line = reader.readLine()) != null) {
			if (line.contains("//") && !line.contains("//registry.gpii.net"))
				flag = false;

			if (line.contains("C4A_DESCRIPTION"))
				description = line.replace("// C4A_DESCRIPTION : ", "").trim();
			if (line.contains("C4A_LAST_MODIFIED_DATE"))
				lastModifiedDate = convertStringToTimeStamp(line.replace(
						"// C4A_LAST_MODIFIED_DATE : ", "").trim());

			if (line.contains("C4A_CREATION_DATE"))
				creationDate = convertStringToTimeStamp(line.replace(
						"// C4A_CREATION_DATE : ", "").trim());

			if (line.contains("[")) {
				counter++;
				rule = new Rule();
				rule.setDescription(description);
				rule.setCreationDate(creationDate);
				rule.setLastModifiedDate(lastModifiedDate);
				list.add(rule);
				description = "";
				creationDate = null;
				lastModifiedDate = null;
			}

			if (!line.equalsIgnoreCase(prefix_c4a)
					&& !line.equalsIgnoreCase(prefix_rdfs) && !line.isEmpty()
					&& flag)
				out.append(line.trim() + "\n");

			flag = true;
		}

		counter = 0;

		String stringFile = out.toString().trim();
		String[] splitted = stringFile.split("]");
		for (int i = 0; i < splitted.length; i++) {

			name = splitted[i].trim().substring(1, splitted[i].indexOf(":"))
					.trim();
			name = name.replace(":", "").replace("(", "").trim();
			body = splitted[i].trim() + "]";

			// TODO check if its both conflict and feedback rule
			if (body.contains("Metadata")) {
				ruleType = RuleType.GENERAL;

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
						ruleType = RuleType.FEEDBACK;
					}
				}

			} else
				ruleType = RuleType.CONFLICT;

			Rule tempRule = list.get(counter).clone();

			tempRule.setName(name);
			tempRule.setBody(body);
			tempRule.setRuleType(ruleType);
			tempRule.setUniqueID(counter);
			tempRule.setFeedbackClass(feedbackClass);
			tempRule.setFeedbackScope(feedbackScope);
			tempRule.setFeedbackID(feedbackID);

			list.remove(counter);
			list.add(counter, tempRule);
			counter++;

		}

		return list;
	}

	public static Timestamp convertStringToTimeStamp(String s) {
		Timestamp timestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss.SSS");
			java.util.Date parsedDate = dateFormat.parse(s);
			timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch (Exception e) {

		}

		return timestamp;
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
			x = initialX + 20;
			y = initialY;
			objectCounter = 0;
			initialY = initialY + 12;
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
			} else if (!line.contains("queries=") && !line.isEmpty()
					&& counter > 0) {
				ruleString = ruleString.concat(line);
				counter++;
			}

			else if (line.contains("queries="))
				counter = 0;

			out.append(line + "\n");

		}

		String fileInput = out.toString();

		String[] splitted = ruleString.split(";");
		for (int i = 0; i < splitted.length; i++) {
			rules.add(splitted[i].replace("testData/rules/", "")
					.replace("rules=", "").replace(".rules", ""));
		}

		rules.add(fileInput);
		return rules;
	}

	public static String getInputOfFileToString(InputStream inputStream)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder out = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
		}

		return out.toString();
	}

	public static ArrayList<String> getUsedVariablesForClassesList() {
		ArrayList<String> usedVariablesForClassesList = new ArrayList<String>();
		Iterator entries = usedVariablesForClasses.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			usedVariablesForClassesList.add((String) entry.getKey());
		}

		return usedVariablesForClassesList;
	}

	public static ArrayList<String> getUsedVariablesForValuesList() {
		ArrayList<String> usedVariablesForValuesList = new ArrayList<String>();

		Iterator entries = usedVariables.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			usedVariablesForValuesList.add((String) entry.getKey());
		}

		return usedVariablesForValuesList;
	}

	public static HashMap<String, String> getUsedVariablesForClasses() {
		return usedVariablesForClasses;
	}

	public static void setUsedVariablesForClasses(
			HashMap<String, String> usedVariablesForClasses) {
		Utils.usedVariablesForClasses = usedVariablesForClasses;
	}

	public static HashMap<String, String> getUsedVariables() {
		return usedVariables;
	}

	public static void setUsedVariables(HashMap<String, String> usedVariables) {
		Utils.usedVariables = usedVariables;
	}

	public static void main(String args[]) {
		// UndirectedGraph<PointElement, DefaultEdge> conditionsGraph =
		// createStringGraph();
		// System.out.println(conditionsGraph.toString());
	}

	public static UndirectedGraph<PointElement, DefaultEdge> createGraph() {
		UndirectedGraph<PointElement, DefaultEdge> g = new SimpleGraph<PointElement, DefaultEdge>(
				DefaultEdge.class);
		return g;
	}

}
