package org.ruleEditor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
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
import org.ruleEditor.ontology.Message;
import org.ruleEditor.ontology.OntologyProperty;
import org.ruleEditor.ontology.OntologyProperty.DataProperty;
import org.ruleEditor.ontology.PointElement;
import org.ruleEditor.utils.MessageForJson.Group;
import org.ruleEditor.utils.MessageForJson.Group.TextMessage;

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
	
	private static String prefix_c4a = "@prefix c4a: <http://rbmm.org/schemas/cloud4all/0.1/>.";
	private static String prefix_rdfs = "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.";

	public static String createRule(ArrayList<PointElement> conditions,
			ArrayList<PointElement> conclusions, String ruleName) {

		String rule = prefix_c4a + "\n" + prefix_rdfs + "\n\n"+"[ruleName \n";

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
					rule = rule + "(?" + el.getConnections().get(0).getVarName()
							+ " c4a:" + el.getElementName() + " ?"
							+ ((DataProperty) property).getValue() + ")\n";

			} else if (el.getType() == PointElement.Type.OBJECT_PROPERTY) {

				// TODO: check that there are two connections
				// and define the correct order
				// in case that something is missing, throw away
				// the property node
				if (el.getConnections().size() > 1)
					rule = rule + "(?" + el.getConnections().get(0).getVarName()
							+ " c4a:" + el.getElementName() + " " + "?"
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
		
		//append messages in the json
		String idforMessages = "aaa";
		json = appendMessages(list, test, idforMessages);
		
		return json;

	}
	
	public static String appendMessages(List<Message> list, MessageForJson test, String id){
		String s = "";
		
		JsonObject json = new JsonObject();
		JsonObject context = new JsonObject();
		JsonArray graph = new JsonArray();

		context.add("c4a", new JsonPrimitive(test.getContext().getC4a()));
		context.add("rdfs", new JsonPrimitive(test.getContext().getRdfs()));

		JsonObject msg;
		JsonObject innerObj;
		JsonArray messages;
		//add existing messages
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
		
		//add new messages
		msg = new JsonObject();
		messages = new JsonArray();
		msg.add("@id", new JsonPrimitive(id));
		msg.add("@type", new JsonPrimitive("Message"));
		for(Message temp: list){
			innerObj = new JsonObject();
			innerObj.add(temp.getLanguage(),
					new JsonPrimitive(temp.getText()));
			messages.add(innerObj);
		}
		
		msg.add("@messages", messages);
		graph.add(msg);
		
		json.add("context", context);
		json.add("graph", graph);
		
		Gson gson = new GsonBuilder().setPrettyPrinting()
				.disableHtmlEscaping().serializeNulls()
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


}
