package org.ruleEditor.beans;

import java.awt.MenuItem;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.contextmenu.ContextMenu;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.event.diagram.ConnectionChangeEvent;
import org.primefaces.event.diagram.DisconnectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;
import org.ruleEditor.ontology.BuiltinMethod;
import org.ruleEditor.ontology.ElementComparator;
import org.ruleEditor.ontology.Instance;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.ontology.Message;
import org.ruleEditor.ontology.Ontology;
import org.ruleEditor.ontology.OntologyClass;
import org.ruleEditor.ontology.OntologyProperty;
import org.ruleEditor.ontology.OntologyProperty.DataProperty;
import org.ruleEditor.ontology.OntologyProperty.ObjectProperty;
import org.ruleEditor.ontology.PointElement;
import org.ruleEditor.ontology.PointElement.Type;
import org.ruleEditor.utils.FileDownloadController;
import org.ruleEditor.utils.FileUploadController;
import org.ruleEditor.utils.Rule;
import org.ruleEditor.utils.RuleCreationUtilities;
import org.ruleEditor.utils.Utils;

import sun.rmi.runtime.NewThreadAction;

import com.sun.faces.component.visit.FullVisitContext;

import org.ruleEditor.ontology.BuiltinMethod.HelpObject;

@ManagedBean(name = "addNewRuleBean")
@SessionScoped
public class AddNewRuleBean {

	private Main main;
	private AddRuleStep1Bean addRuleStep1Bean;
	private DefaultTreeNode root;
	private TreeNode selectedNode = null;
	private String oldFileName = "";
	private InputStream fileStream;
	private DefaultDiagramModel conditionsModel;
	private DefaultDiagramModel conclusionsModel;
	private List<DataProperty> datatypes = null;
	private List<ObjectProperty> objects = null;
	private List<Instance> instances = null;
	private OntologyProperty selectedDataProperty = new OntologyProperty("", "");
	private OntologyProperty selectedObjectProperty = new OntologyProperty("",
			"");
	private ArrayList<PointElement> conditions;
	private ArrayList<PointElement> conclusions;
	private int counter;
	private int initialX = 3;
	private int initialY = 3;
	private int objectCounter = 0;
	private String nodeForRemove;
	private PointElement clonedTargetElement = null;
	private PointElement targetElement = null;
	private PointElement sourceElement = null;
	private PointElement originalTargetElement = null;
	private PointElement clonedOriginalTargetElement = null;
	private PointElement cloneSelectedNode = null;
	private PointElement selectedNodeOriginal = null;
	private BuiltinMethod selectedMethod = null;
	private boolean flag = false;// false : for simple rule
									// true : for feedback rule
	private Instance selectedInstance = null;
	private Rule rule = null;
	private ArrayList<Rule> existingRules = new ArrayList<Rule>();
	private boolean feedback = true;
	private int counterOfConnections = 0;

	private ArrayList<String> usedVariablesForClasses = new ArrayList<String>();

	private ArrayList<String> usedVariablesForValues = new ArrayList<String>();

	private ArrayList<String> variables = new ArrayList<String>();
	private String argument = "";
	private boolean gridType1 = true;
	private boolean selectedClasses = true;
	private boolean selectedVariables = false;
	// private List<String> methodsWithoutConnections = Arrays.asList(
	// "makeSkolem", "print", "drop");
	private String previousStep = "";
	private HashMap<String, InputStream> filesToCompare;
	private ArrayList<Message> correlatedFiles;
	private UndirectedGraph<PointElement, DefaultEdge> conditionsGraph = null;
	private UndirectedGraph<PointElement, DefaultEdge> conclusionsGraph = null;
	private ArrayList<String> types = null;
	private ArrayList<OntologyProperty> propertiesList = null;
	private ArrayList<String> jenaLists = null;
	private ArrayList<String> tempVariablesForClasses = null;
	private ArrayList<String> tempVariablesForValues = null;
	private ArrayList<String> tempInstancesForClasses = null;
	private ArrayList<String> tempInstancesForValues = null;
	private String option = "";
	private String ruleDescription = "";
	private String preview = "";
	private EditRuleStep1Bean editRuleStep1Bean;

	public AddNewRuleBean() {
		super();

		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
		addRuleStep1Bean = (AddRuleStep1Bean) context.getApplication()
				.evaluateExpressionGet(context, "#{addRuleStep1Bean}",
						AddRuleStep1Bean.class);
		editRuleStep1Bean = (EditRuleStep1Bean) context.getApplication()
				.evaluateExpressionGet(context, "#{editRuleStep1Bean}",
						EditRuleStep1Bean.class);
		createOntologyTree(main.getOntology());

	}

	public void init(boolean tempFlag) {

		flag = tempFlag;
		datatypes = new ArrayList<DataProperty>();
		objects = new ArrayList<ObjectProperty>();
		instances = new ArrayList<Instance>();
		conditions = new ArrayList<PointElement>();
		conclusions = new ArrayList<PointElement>();
		counter = 0;
		initialX = 3;
		initialY = 3;
		objectCounter = 0;
		selectedNode = null;
		selectedDataProperty = new OntologyProperty("", "");
		selectedObjectProperty = new OntologyProperty("", "");
		nodeForRemove = "";
		clonedTargetElement = new PointElement();
		targetElement = new PointElement();
		sourceElement = new PointElement();
		originalTargetElement = new PointElement();
		clonedOriginalTargetElement = new PointElement();
		oldFileName = "";
		counterOfConnections = 0;
		filesToCompare = new HashMap<String, InputStream>();
		correlatedFiles = new ArrayList<Message>();
		Message msg = new Message();
		msg.setLanguage("fileName");
		msg.setText("rule");
		// correlatedFiles.add(msg);
		option = "class";
		ruleDescription = "";
		// rule = null;
		preview = "";

		usedVariablesForClasses = new ArrayList<String>();
		usedVariablesForClasses.add("-");

		usedVariablesForValues = new ArrayList<String>();
		usedVariablesForValues.add("-");

		tempVariablesForClasses = new ArrayList<String>();
		tempVariablesForClasses.add("-");

		tempVariablesForValues = new ArrayList<String>();
		tempVariablesForValues.add("-");

		tempInstancesForClasses = new ArrayList<String>();
		tempInstancesForClasses.add("-");

		tempInstancesForValues = new ArrayList<String>();
		tempInstancesForValues.add("-");

		types = new ArrayList<String>();
		types.add("-");
		types.add("string");
		types.add("long");
		types.add("integer");

		propertiesList = new ArrayList<OntologyProperty>();
		jenaLists = new ArrayList<String>();

		argument = "";
		variables = new ArrayList<String>();
		gridType1 = false;
		selectedClasses = true;
		selectedVariables = false;

		// Initialization of graphs
		conditionsGraph = Utils.createGraph();
		conclusionsGraph = Utils.createGraph();

		// Initialization of conditions model
		conditionsModel = new DefaultDiagramModel();
		conditionsModel.setMaxConnections(-1);

		conditionsModel.getDefaultConnectionOverlays().add(
				new ArrowOverlay(20, 20, 1, 1));

		// create a connector
		StraightConnector connector = new StraightConnector();
		connector.setPaintStyle("{strokeStyle:'#98AFC7', lineWidth:2}");
		connector.setHoverPaintStyle("{strokeStyle:'#5C738B'}");

		conditionsModel.setDefaultConnector(connector);

		// Initialization of conclusions model
		conclusionsModel = new DefaultDiagramModel();
		conclusionsModel.setMaxConnections(-1);
		conclusionsModel.getDefaultConnectionOverlays().add(
				new ArrowOverlay(20, 20, 1, 1));
		conclusionsModel.setDefaultConnector(connector);

	}

	public void editRule(boolean flag, ArrayList<PointElement> conditionsList,
			ArrayList<PointElement> conclusionsList, Rule ruleForEdit,
			ArrayList<Rule> rulesList) {

		rule = ruleForEdit;
		if (rule.getRuleType() == Rule.RuleType.FEEDBACK) {
			feedback = true;
		} else
			feedback = false;

		existingRules = rulesList;
		gridType1 = false;
		datatypes = new ArrayList<DataProperty>();
		objects = new ArrayList<ObjectProperty>();
		instances = new ArrayList<Instance>();
		conditions = (ArrayList<PointElement>) conditionsList.clone();
		conclusions = (ArrayList<PointElement>) conclusionsList.clone();

		// fill graphs with point elements from conditions and conclusions
		// of the new rule
		// Initialization of graphs
		conditionsGraph = Utils.createGraph();
		conclusionsGraph = Utils.createGraph();
		fillGraphsWithPointElements();

		counter = 0;
		initialX = 3;
		initialY = 3;
		objectCounter = 0;
		selectedNode = null;
		selectedDataProperty = new OntologyProperty("", "");
		selectedObjectProperty = new OntologyProperty("", "");
		nodeForRemove = "";
		clonedTargetElement = new PointElement();
		targetElement = new PointElement();
		sourceElement = new PointElement();
		originalTargetElement = new PointElement();
		clonedOriginalTargetElement = new PointElement();

		// Initialization of conditions model
		conditionsModel = new DefaultDiagramModel();
		conditionsModel.setMaxConnections(-1);

		conditionsModel.getDefaultConnectionOverlays().add(
				new ArrowOverlay(20, 20, 1, 1));

		// create a connector
		StraightConnector connector = new StraightConnector();
		connector.setPaintStyle("{strokeStyle:'#98AFC7', lineWidth:2}");
		connector.setHoverPaintStyle("{strokeStyle:'#5C738B'}");

		conditionsModel.setDefaultConnector(connector);

		// Initialization of conclusions model
		conclusionsModel = new DefaultDiagramModel();
		conclusionsModel.setMaxConnections(-1);
		conclusionsModel.getDefaultConnectionOverlays().add(
				new ArrowOverlay(20, 20, 1, 1));
		conclusionsModel.setDefaultConnector(connector);

		createModels("conditions", conditions);
		createModels("conclusions", conclusions);

	}

	public void fillGraphsWithPointElements() {
		for (PointElement temp : conditions) {
			conditionsGraph.addVertex(temp);
		}

		for (PointElement temp : conclusions) {
			conclusionsGraph.addVertex(temp);
		}
	}

	public void createModels(String panelID, ArrayList<PointElement> list) {

		Element element = null;
		EndPoint endPointCA = null;

		// create the diagram models with the point elements
		for (PointElement el : list) {

			if (el.getType() == Type.CLASS) {
				element = new Element(el, String.valueOf(el.getX() + "em"),
						String.valueOf(el.getY() + "em"));

				endPointCA = Utils
						.createRectangleEndPoint(EndPointAnchor.BOTTOM);
				endPointCA.setTarget(true);
				element.addEndPoint(endPointCA);
			}

			else if (el.getType() == Type.DATA_PROPERTY
					|| el.getType() == Type.OBJECT_PROPERTY) {

				element = new Element(el, String.valueOf(el.getX() + "em"),
						String.valueOf(el.getY() + "em"));

				endPointCA = Utils
						.createDotEndPoint(EndPointAnchor.AUTO_DEFAULT);
				endPointCA.setTarget(true);
				element.addEndPoint(endPointCA);

			} else if (el.getType() == Type.BUILTIN_METHOD) {

				element = new Element(el, String.valueOf(el.getX() + "em"),
						String.valueOf(el.getY() + "em"));

				endPointCA = Utils
						.createRectangleEndPoint(EndPointAnchor.CONTINUOUS);
				if (el.getMethod().getOriginalName().equals("noValue")) {

					endPointCA.setSource(true);
					element.addEndPoint(endPointCA);

				} else {
					endPointCA.setSource(false);
					endPointCA.setTarget(false);
					element.addEndPoint(endPointCA);

				}

			} else if (el.getType() == Type.INSTANCE) {
				// TODO
			}

			if (panelID.equalsIgnoreCase("conditions"))
				conditionsModel.addElement(element);
			else
				conclusionsModel.addElement(element);
		}

		// int sourceIndex = -1;
		// int targetIndex = -1;
		// // make connections between the elements of the diagram models
		// for (PointElement el : list) {
		// if (el.getType() == Type.OBJECT_PROPERTY
		// || el.getType() == Type.DATA_PROPERTY
		// || el.getType() == Type.BUILTIN_METHOD) {
		//
		// targetIndex = findIndexOfElementByPointElement(el,
		// conditionsModel);
		//
		// boolean flag = false;
		// for (PointElement temp : el.getConnections()) {
		//
		// sourceIndex = findIndexOfElementByPointElement(temp,
		// conditionsModel);
		//
		// if (sourceIndex == -1) {
		// sourceIndex = findIndexOfElementByPointElement(temp,
		// conclusionsModel);
		// flag = true;
		// }
		//
		// if (panelID.equalsIgnoreCase("conclusions"))
		// targetIndex = findIndexOfElementByPointElement(el,
		// conclusionsModel);
		//
		// if (panelID.equalsIgnoreCase("conditions")) {
		//
		// conditionsModel.connect(new Connection(
		// conditionsModel.getElements()
		// .get(sourceIndex).getEndPoints()
		// .get(0), conditionsModel
		// .getElements().get(targetIndex)
		// .getEndPoints().get(0)));
		//
		// } else {
		//
		// if (flag)
		// conclusionsModel.connect(new Connection(
		// conclusionsModel.getElements()
		// .get(sourceIndex).getEndPoints()
		// .get(0), conclusionsModel
		// .getElements().get(targetIndex)
		// .getEndPoints().get(0)));
		// else {
		//
		// //a conclusion method/property has to be connected with
		// // a class that exists in conditions
		// Element conclElement = new Element(temp,
		// String.valueOf(temp.getX() + "em"),
		// String.valueOf(temp.getY() + "em"));
		// EndPoint endPointCon = Utils
		// .createRectangleEndPoint(EndPointAnchor.BOTTOM);
		// endPointCon.setSource(true);
		// conclElement.addEndPoint(endPointCon);
		// conclusionsModel.addElement(conclElement);
		//
		// sourceIndex = conclusionsModel.getElements().size()-1;
		//
		// conclusionsModel.connect(new Connection(
		// conclusionsModel.getElements()
		// .get(sourceIndex).getEndPoints()
		// .get(0), conclusionsModel
		// .getElements().get(targetIndex)
		// .getEndPoints().get(0)));
		// }
		//
		// }
		// }
		// }
		// }

	}

	public int findIndexOfElementByPointElement(PointElement element,
			DiagramModel model) {

		int index = -1;
		for (Element el : model.getElements()) {
			PointElement pel = (PointElement) el.getData();
			if (element.getId().equalsIgnoreCase(pel.getId())) {
				index = model.getElements().indexOf(el);
				break;
			}
		}

		return index;
	}

	public void editNode() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String nodeForRemoveId = params.get("id");
		String panel = params.get("panel");

		// find the node that is going to be edited
		if (panel.equalsIgnoreCase("conditions")) {
			for (PointElement el : conditions) {
				if (el.getId().equals(nodeForRemoveId)) {
					cloneSelectedNode = el.clone();
					selectedNodeOriginal = el.clone();
					break;
				}
			}
		} else {

			for (PointElement el : conclusions) {
				if (el.getId().equals(nodeForRemoveId)) {
					cloneSelectedNode = el.clone();
					selectedNodeOriginal = el.clone();
					break;
				}
			}

		}
		// preparations in case of PROPERTY
		if (cloneSelectedNode.getType() == Type.DATA_PROPERTY
				|| cloneSelectedNode.getType() == Type.OBJECT_PROPERTY) {
			tempVariablesForClasses = new ArrayList<String>();
			tempInstancesForClasses = new ArrayList<String>();
			tempVariablesForValues = new ArrayList<String>();
			tempInstancesForValues = new ArrayList<String>();
			tempVariablesForClasses.add("-");
			tempInstancesForClasses.add("-");
			tempVariablesForValues.add("-");
			tempInstancesForValues.add("-");

			String tempClassName = cloneSelectedNode.getProperty()
					.getClassName();
			ArrayList<PointElement> tempList = new ArrayList<PointElement>();
			if (panel.equalsIgnoreCase("conditions"))
				tempList = (ArrayList<PointElement>) conditions.clone();
			else
				tempList = (ArrayList<PointElement>) conclusions.clone();

			for (PointElement el : tempList) {
				if (el.getType() == PointElement.Type.CLASS) {
					if (el.getElementName().equalsIgnoreCase(tempClassName)) {
						if (!el.getClassVariable().equals("empty"))
							tempVariablesForClasses.add(el.getClassVariable());
					}
				}

				if (el.getType() == PointElement.Type.INSTANCE) {
					if (el.getInstance().getClassName()
							.equalsIgnoreCase(tempClassName)) {
						tempInstancesForClasses.add(el.getInstance()
								.getInstanceName());
					}
				}
			}

			// more specifically in case of OBJECT_PROPERTY
			if (cloneSelectedNode.getType() == Type.OBJECT_PROPERTY) {
				OntologyProperty objProp = (ObjectProperty) cloneSelectedNode
						.getProperty();
				String classRange = ((ObjectProperty) objProp)
						.getRangeOfClasses().get(0);
				for (PointElement el : tempList) {
					if (el.getType() == PointElement.Type.CLASS) {
						if (el.getElementName().equalsIgnoreCase(classRange)) {
							if (!el.getClassVariable().equals("empty"))
								tempVariablesForValues.add(el
										.getClassVariable());
						}
					}

					if (el.getType() == PointElement.Type.INSTANCE) {
						if (el.getInstance().getClassName()
								.equalsIgnoreCase(classRange)) {
							tempInstancesForValues.add(el.getInstance()
									.getInstanceName());
						}
					}
				}
			}

		}

		// preparations in case of BUILTIN METHOD
		if (cloneSelectedNode.getType() == Type.BUILTIN_METHOD) {
			fillListsWithVariables();
			// equal, notEqual
			if (cloneSelectedNode.getMethod().getCategory().equals("2a")) {
				gridType1 = cloneSelectedNode.getMethod().isFlag();
				selectedClasses = !cloneSelectedNode.getMethod().isFlag();
				selectedVariables = cloneSelectedNode.getMethod().isFlag();
			}

			// table
			if (cloneSelectedNode.getMethod().getCategory().equals("16")
					|| cloneSelectedNode.getMethod().getCategory().equals("20")
					|| cloneSelectedNode.getMethod().getCategory().equals("21")) {
				getAllPropertiesForAllClasses();
			}
		}

	}

	// TODO it does not work
	public void spinnerValueChangeForClasses(AjaxBehaviorEvent event) {

		// String id =
		// FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("index");
		// String indexOfList =
		// (String)event.getComponent().getAttributes().get("index");
		// String newValue =
		// (String)event.getComponent().getAttributes().get("newValue");

		for (int i = 0; i < cloneSelectedNode.getMethod().getNumberOfParams(); i++) {

		}

	}

	public void fillListsWithVariables() {

		for (int i = 0; i < cloneSelectedNode.getMethod().getNumberOfParams(); i++) {
			cloneSelectedNode.getMethod().getListOfVarClassesLists()
					.set(i, usedVariablesForClasses);
			cloneSelectedNode.getMethod().getListOfVarValuesLists()
					.set(i, usedVariablesForValues);

		}
	}

	public void uploadFileForSaveAs(FileUploadEvent event) throws IOException {
		oldFileName = event.getFile().getFileName();
		fileStream = event.getFile().getInputstream();
	}

	public void uploadFileForCorrelation(FileUploadEvent event)
			throws IOException {
		filesToCompare.put(event.getFile().getFileName(), event.getFile()
				.getInputstream());
	}

	public void findCorrelation() throws IOException {
		correlatedFiles = Utils.correlateRules(filesToCompare, conditions,
				conclusions, main);
	}

	public void goToPreviousStep() throws IOException {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.redirect(previousStep);
	}

	public void getPreviousStep(String step) {
		previousStep = step;
		oldFileName = "";
	}

	public void previewRule() {
		preview = RuleCreationUtilities.createRule(conditions, conclusions, "");
		preview = preview.replace(RuleCreationUtilities.prefix_c4a, "").trim();
		preview = preview.replace(RuleCreationUtilities.prefix_rdfs, "").trim();
		addOrderToRulePreview();
	}

	public void addOrderToRulePreview() {
		String s = preview;
		s = s.replace("[\n", "").replace("]", "").trim();
		// String[] splitted = s.split("\\(");
		String[] splitted = s.split("\n");
		String orderPreview = "[\n";
		int counter = 1;
		for (int i = 0; i < splitted.length; i++) {
			if (!splitted[i].isEmpty()) {

				if (!splitted[i].equalsIgnoreCase("->")) {
					orderPreview = orderPreview.concat(counter + ": "
							+ splitted[i] + "\n");
					counter++;
				} else {
					orderPreview = orderPreview.concat(splitted[i] + "\n");
					counter = 1;
				}

			}

		}

		preview = orderPreview.concat("\n]");
	}

	public void saveRuleInServer() throws IOException {

		String finalFileName = "cloud4All_RuleFile";
		boolean createNewFile = true;

		if (previousStep.contains("addRule") && !oldFileName.trim().isEmpty()) {
			createNewFile = false;
			finalFileName = oldFileName;
		} else {
			finalFileName = editRuleStep1Bean.getFileName();
		}

		java.util.Date date = new java.util.Date();
		Timestamp creationDate = new Timestamp(date.getTime());
		Timestamp lastModifiedDate = new Timestamp(date.getTime());

		if (rule != null)
			creationDate = rule.getCreationDate();

		// flag for automatic deployment
		boolean automaticUpdate = true;
		String ruleName = addRuleStep1Bean.getRuleName();
		if (rule.getName() != null && !rule.getName().isEmpty())
			ruleName = rule.getName();

		RuleCreationUtilities.saveRule(ruleName.trim(), finalFileName,
				conditions, conclusions, false, createNewFile, "", "", "",
				existingRules, fileStream, creationDate, lastModifiedDate,
				ruleDescription, main.getTypeOfUser(), automaticUpdate);

	}

	public void saveRuleLocally() throws IOException {

		String finalFileName = "cloud4All_RuleFile";
		boolean createNewFile = true;
		if (previousStep.contains("addRule") && !oldFileName.trim().isEmpty()) {
			createNewFile = false;
			finalFileName = oldFileName;
		} else {
			finalFileName = editRuleStep1Bean.getFileName();
		}

		java.util.Date date = new java.util.Date();
		Timestamp creationDate = new Timestamp(date.getTime());
		Timestamp lastModifiedDate = new Timestamp(date.getTime());

		if (rule != null)
			creationDate = rule.getCreationDate();

		// flag for automatic deployment
		boolean automaticUpdate = false;
		String ruleName = addRuleStep1Bean.getRuleName();
		if (rule.getName() != null && !rule.getName().isEmpty())
			ruleName = rule.getName();

		RuleCreationUtilities.saveRule(ruleName.trim(), finalFileName,
				conditions, conclusions, false, createNewFile, "", "", "",
				existingRules, fileStream, creationDate, lastModifiedDate,
				ruleDescription, main.getTypeOfUser(), automaticUpdate);

	}

	public void saveEditOfNode() {

		// case of PROPERTIES, CLASSES
		// get the used variables for classes and generally for values
		String s = cloneSelectedNode.getProperty().getClassVar();
		String value = cloneSelectedNode.getProperty().getValue();
		String classVar = cloneSelectedNode.getClassVariable();

		if (!usedVariablesForClasses.contains(classVar)
				&& classVar.contains("?"))
			usedVariablesForClasses.add(classVar);

		if (!usedVariablesForClasses.contains(s) && s.contains("?"))
			usedVariablesForClasses.add(s);

		if (cloneSelectedNode.getType() == Type.DATA_PROPERTY)
			if (!usedVariablesForValues.contains(value) && value.contains("?"))
				usedVariablesForValues.add(value);

		if (cloneSelectedNode.getType() == Type.OBJECT_PROPERTY)
			if (!usedVariablesForClasses.contains(value) && value.contains("?"))
				usedVariablesForClasses.add(value);
		// ----------------------------------------------------------------

		// case of BUILTIN METHOD

		if (cloneSelectedNode.getType() == Type.BUILTIN_METHOD) {
			String helpString = "";
			String category = cloneSelectedNode.getMethod().getCategory();
			String value1 = cloneSelectedNode.getMethod().getValue1()
					.getValue();
			String value2 = cloneSelectedNode.getMethod().getValue2()
					.getValue();
			String value3 = cloneSelectedNode.getMethod().getValue3()
					.getValue();
			String value4 = cloneSelectedNode.getMethod().getValue4()
					.getValue();
			String value5 = cloneSelectedNode.getMethod().getValue5()
					.getValue();
			String value6 = cloneSelectedNode.getMethod().getValue6()
					.getValue();
			if (category.equals("1")) {
				helpString = value1;

			} else if (category.equals("2a")) {

				value5 = "\"" + value5 + "\"";
				value6 = "\"" + value6 + "\"";

				if (!gridType1)
					helpString = value1 + "," + value2;
				else if (gridType1) {
					if (!value3.equals("-"))
						helpString = value3 + ",";
					else if (!value5.equals("\"-\""))
						helpString = value5 + ",";

					if (!value4.equals("-"))
						helpString = helpString + value4;
					else
						helpString = helpString + value6;

				}

			} else if (category.equals("2b")) {

				value3 = "\"" + value3 + "\"";
				value4 = "\"" + value4 + "\"";

				if (!value1.equals("-"))
					helpString = value1 + ",";
				else if (!value3.equals("\"-\""))
					helpString = value3 + ",";

				if (!value2.equals("-"))
					helpString = helpString + value2 + ",";
				else
					helpString = helpString + value4;

			} else if (category.equals("3")) {

				if (!value1.equals("-"))
					helpString = value1 + ",";
				else
					helpString = value4 + ",";

				if (!value2.equals("-"))
					helpString = helpString + value2 + ",";
				else
					helpString = helpString + value5 + ",";

				if (!value3.equals("-"))
					helpString = helpString + value3;
				else
					helpString = helpString + value6;

			} else if (category.equals("4")) {

				helpString = value1 + "," + value2 + "," + value3;

			} else if (category.equals("5") || category.equals("7")
					|| category.equals("8") || category.equals("9")
					|| category.equals("6")) {
				helpString = value1;

				if (category.equals("8"))
					usedVariablesForValues.add(value1);
				else if (category.equals("9"))
					usedVariablesForClasses.add(value1);
			} else if (category.equals("10")) {
				helpString = value1 + "," + value2;
			} else if (category.equals("11")) {
				helpString = value1 + "," + value2 + ",";
				if (!value3.equals("-"))
					helpString = helpString + value3 + ",";
				helpString = helpString + value4 + ",";
			} else if (category.equals("12")) {
				helpString = value1 + "," + value2;
			} else if (category.equals("13") || category.equals("14")) {
				helpString = value1 + "," + value2;
			} else if (category.equals("16")) {
				helpString = value1;
			} else if (category.equals("17")) {
				helpString = value1;
			} else if (category.equals("18")) {
				helpString = value1 + "," + value2;
			} else if (category.equals("19")) {
				helpString = value1 + "," + value2 + "," + value3;
			} else if (category.equals("20") || category.equals("21")) {
				helpString = value1 + "," + value2 // TODO value 2 is a
													// property, how to handle
													// it
						+ "," + value3;
			}
			// TODO the category 10

			cloneSelectedNode.getMethod().setHelpString(helpString);
		}

		ArrayList<PointElement> clonedList = new ArrayList<PointElement>();

		// find the panel which the selected node belong to
		// and clone the list (in order to work in one list)
		if (cloneSelectedNode.getPanel().equals("conditions"))
			clonedList = (ArrayList<PointElement>) conditions.clone();
		else
			clonedList = (ArrayList<PointElement>) conclusions.clone();

		// find the index of the old node in the list
		int index = -1;
		index = clonedList.indexOf(cloneSelectedNode);

		// remove the old node, add the cloned node (changes added)
		if (index != -1) {
			clonedList.remove(index);
			clonedList.add(index, cloneSelectedNode);
		}

		// update the variable of a class in all connections
		if (cloneSelectedNode.getType() == Type.CLASS) {
			String oldName = selectedNodeOriginal.getClassVariable();
			String newName = cloneSelectedNode.getClassVariable();

			if (!newName.equals("empty") && !oldName.equals("empty")) {
				// if the class variable of the CLASS node has been edited,
				// update the properties
				// that have already used it
				if (!newName.equals(oldName)) {
					for (PointElement el : clonedList) {
						if (el.getType() == Type.DATA_PROPERTY) {
							if (el.getProperty().getClassVar().equals(oldName))
								el.getProperty().setClassVar(newName);

						} else if (el.getType() == Type.OBJECT_PROPERTY) {

							if (el.getProperty().getClassVar().equals(oldName))
								el.getProperty().setClassVar(newName);
							if (el.getProperty().getValue().equals(oldName))
								el.getProperty().setValue(newName);

						} else if (el.getType() == Type.BUILTIN_METHOD) {

							// TODO select the builtins that take
							// as parameter a class variable
						}
					}
				}
			}
		}

		// update the corresponding list
		if (cloneSelectedNode.getPanel().equals("conditions"))
			conditions = (ArrayList<PointElement>) clonedList.clone();
		else
			conclusions = (ArrayList<PointElement>) clonedList.clone();

		// remove old Node from diagramModels
		if (cloneSelectedNode.getPanel().equals("conditions")) {
			Element el = Utils.getElementFromID(conditionsModel,
					cloneSelectedNode.getId());
			index = conditionsModel.getElements().indexOf(el);
			conditionsModel.getElements().remove(index);
			el.setData(cloneSelectedNode);
			conditionsModel.getElements().add(index, el);
		} else {
			Element el = Utils.getElementFromID(conclusionsModel,
					cloneSelectedNode.getId());
			index = conclusionsModel.getElements().indexOf(el);
			conclusionsModel.getElements().remove(index);
			el.setData(cloneSelectedNode);
			conclusionsModel.getElements().add(index, el);
		}

		// sort the collections according to the order comparator
		Collections.sort(conditions, new ElementComparator());
		Collections.sort(conclusions, new ElementComparator());

	}

	public DefaultDiagramModel getConclusionsModel() {
		return conclusionsModel;
	}

	public void setConclusionsModel(DefaultDiagramModel conclusionsModel) {
		this.conclusionsModel = conclusionsModel;
	}

	public DefaultDiagramModel getConditionsModel() {
		return conditionsModel;
	}

	public void setConditionsModel(DefaultDiagramModel conditionsModel) {
		this.conditionsModel = conditionsModel;
	}

	public PointElement getCloneSelectedNode() {
		return cloneSelectedNode;
	}

	public void setCloneSelectedNode(PointElement cloneSelectedNode) {
		this.cloneSelectedNode = cloneSelectedNode;
	}

	public PointElement setPosition(PointElement el) {
		int x = 0;
		int y = 0;
		if (objectCounter == 0) {
			x = initialX;
			y = initialY;
			objectCounter++;
		} else if (objectCounter == 1) {
			x = initialX + 20;
			y = initialY;
			// objectCounter++;
			objectCounter = 0;
			initialY = initialY + 12;
		}
		// } else if (objectCounter == 2) {
		// x = initialX + 35;
		// y = initialY;
		// objectCounter = 0;
		// initialY = initialY + 12;
		// }

		el.setX(x);
		el.setY(y);

		return el;
	}

	public void removeNode() {

		String nodeForRemoveId = cloneSelectedNode.getId();
		String panel = cloneSelectedNode.getPanel();

		int index = -1;
		Element elementForRemove = null;
		PointElement elementToRemove = null;
		if (panel.equals("conditions")) {

			// find index of element to remove

			for (PointElement el : conditions) {
				if (el.getId().equals(nodeForRemoveId)) {
					index = conditions.indexOf(el);
					elementToRemove = el.clone();
					break;
				}
			}

			// remove elementToRemove from connections
			for (PointElement el : conditions) {
				ArrayList<PointElement> cloneList = (ArrayList<PointElement>) el
						.getConnections().clone();
				for (PointElement temp : cloneList) {
					if (temp.equals(elementToRemove))
						el.getConnections().remove(cloneList.indexOf(temp));
				}
			}

			// remove element from list
			conditions.remove(index);

			// remove element from model
			elementForRemove = Utils.getElementFromID(conditionsModel,
					nodeForRemoveId);
			conditionsModel.removeElement(elementForRemove);

		} else {

			// find index of element to remove
			for (PointElement el : conclusions) {
				if (el.getId().equals(nodeForRemoveId)) {
					index = conclusions.indexOf(el);
					elementToRemove = el.clone();
					break;
				}
			}

			// remove elementToRemove from connections
			for (PointElement el : conclusions) {
				ArrayList<PointElement> cloneList = (ArrayList<PointElement>) el
						.getConnections().clone();
				for (PointElement temp : cloneList) {
					if (temp.equals(elementToRemove))
						el.getConnections().remove(cloneList.indexOf(temp));
				}
			}

			// remove element from list
			conclusions.remove(index);

			// remove element from model
			elementForRemove = Utils.getElementFromID(conclusionsModel,
					nodeForRemoveId);
			conclusionsModel.removeElement(elementForRemove);

		}
	}

	public int connectClassWithProperty(PointElement targetElement,
			PointElement sourceElement) {
		// update the list with the connections for the target element
		for (PointElement el : conditions) {
			if (el.getId().equalsIgnoreCase(targetElement.getId())) {
				targetElement.setConnections(el.getConnections());
				break;
			}
		}

		for (PointElement el : conclusions) {
			if (el.getId().equalsIgnoreCase(targetElement.getId())) {
				targetElement.setConnections(el.getConnections());
				break;
			}
		}
		// clone the old target element in order to make the changes
		clonedTargetElement = targetElement.clone();
		int result = 0;
		if (clonedTargetElement.getProperty() instanceof DataProperty) {

			// add element to data property connections
			result = addElementForDataProperty(sourceElement);

		} else {

			// add element to object property connections
			result = addElementForObjectProperty(sourceElement);
		}

		if (result == 1) {
			int index = -1;
			if (sourceElement.getPanel().equals("conditions")) {
				// remove old object from the list
				// add the updated one

				index = this.conditions.indexOf(targetElement);
				if (index != -1) {
					this.conditions.remove(index);
					this.conditions.add(index, clonedTargetElement);
				}

			} else {
				index = this.conclusions.indexOf(targetElement);
				if (index != -1) {
					this.conclusions.remove(index);
					this.conclusions.add(index, clonedTargetElement);
				}
			}
			counterOfConnections++;
			return 1;

		} else {
			System.out.println("connection failed");
		}
		return 0;
	}

	public int addElementForDataProperty(PointElement sourceElement) {
		DataProperty property = (DataProperty) clonedTargetElement
				.getProperty();
		String className = property.getClassName();
		int maxConns = 1;

		if (clonedTargetElement.getConnections().size() < maxConns
				&& className.equalsIgnoreCase(sourceElement.getInstance()
						.getClassName())) {
			clonedTargetElement.getConnections().add(sourceElement);
			clonedTargetElement.getProperty().setClassVar(
					sourceElement.getInstance().getInstanceName());
			return 1;
		}

		return 0;
	}

	public int addElementForObjectProperty(PointElement sourceElement) {
		ObjectProperty property = (ObjectProperty) clonedTargetElement
				.getProperty();
		String className = property.getClassName();
		String rangeClass = property.getRangeOfClasses().get(0);
		int maxConns = 2;
		String name = sourceElement.getInstance().getClassName();

		if (clonedTargetElement.getConnections().size() < maxConns
				&& (className.equalsIgnoreCase(name) || rangeClass
						.equalsIgnoreCase(name))) {

			PointElement sourceEl = null; // class element
			PointElement targetEl = null; // target value element
			for (PointElement temp : clonedTargetElement.getConnections()) {
				if (temp.getInstance()
						.getClassName()
						.equalsIgnoreCase(
								clonedTargetElement.getProperty()
										.getClassName()))
					sourceEl = temp;
				else
					targetEl = temp;
			}

			if (className.equalsIgnoreCase(name) && sourceEl == null) {
				clonedTargetElement.getProperty().setClassVar(
						sourceElement.getInstance().getInstanceName());
				clonedTargetElement.getConnections().add(sourceElement);
				return 1;
			} else if (!className.equalsIgnoreCase(name) && targetEl == null) {
				clonedTargetElement.getProperty().setValue(
						sourceElement.getInstance().getInstanceName());
				clonedTargetElement.getConnections().add(sourceElement);
				return 1;
			}

		}

		return 0;
	}

	public int connectInstanceWithProperty(PointElement sourceElement,
			PointElement targetElement) {

		// update the list with the connections for the target element
		for (PointElement el : conditions) {
			if (el.getId().equalsIgnoreCase(targetElement.getId())) {
				targetElement.setConnections(el.getConnections());
				break;
			}
		}

		for (PointElement el : conclusions) {
			if (el.getId().equalsIgnoreCase(targetElement.getId())) {
				targetElement.setConnections(el.getConnections());
				break;
			}
		}
		// clone the old target element in order to make the changes
		clonedTargetElement = targetElement.clone();
		int result = 0;
		if (clonedTargetElement.getProperty() instanceof DataProperty) {
			// add element to data property connections
			result = addElementForDataProperty(sourceElement);
		} else if (clonedTargetElement.getProperty() instanceof ObjectProperty) {
			// add element to object property connections
			result = addElementForObjectProperty(sourceElement);
		}

		if (result == 1) {
			int index = -1;
			if (sourceElement.getPanel().equals("conditions")) {
				// remove old object from the list
				// add the updated one

				index = this.conditions.indexOf(targetElement);
				if (index != -1) {
					this.conditions.remove(index);
					this.conditions.add(index, clonedTargetElement);
				}

			} else {
				index = this.conclusions.indexOf(targetElement);
				if (index != -1) {
					this.conclusions.remove(index);
					this.conclusions.add(index, clonedTargetElement);
				}
			}
			counterOfConnections++;
			return 1;
		} else {
			System.out.println("connection failed");
		}

		return result;
	}

	public int connectBuiltinMethodWithProperty(PointElement sourceElement,
			PointElement targetElement) {
		int result = 0;
		// update the connections of the source element (built in method), and
		// the
		// target element
		boolean sourceFlag = false;
		boolean targetFlag = false;
		for (PointElement el : conditions) {
			if (el.getId().equalsIgnoreCase(sourceElement.getId())) {
				sourceElement.setConnections(el.getConnections());
				sourceFlag = true;
			}

			if (el.getId().equalsIgnoreCase(targetElement.getId())) {
				targetElement.setConnections(el.getConnections());
				targetFlag = true;
			}

			if (sourceFlag && targetFlag)
				break;
		}

		if (!sourceFlag && !targetFlag)
			for (PointElement el : conclusions) {
				if (el.getId().equalsIgnoreCase(sourceElement.getId())) {
					sourceElement.setConnections(el.getConnections());
					sourceFlag = true;
				}

				if (el.getId().equalsIgnoreCase(targetElement.getId())) {
					targetElement.setConnections(el.getConnections());
					targetFlag = true;
				}

				if (sourceFlag && targetFlag)
					break;
			}

		// check if the connection with the target element is valid
		PointElement clonedSourceElement = sourceElement.clone();
		int i = clonedSourceElement.getMethod().getNumberOfParams();
		// if (clonedSourceElement.getConnections().size() < i
		// && targetElement.getType().equals(
		// clonedSourceElement.getMethod().getTypeOfParam())) {
		// clonedSourceElement.getConnections().add(targetElement.clone());
		// counterOfConnections++;
		// result = 1;
		// }

		int index = -1;
		if (result == 1)
			if (sourceElement.getPanel().equals("conditions")) {
				// remove old object from the list
				// add the updated one

				index = this.conditions.indexOf(sourceElement);
				if (index != -1) {
					this.conditions.remove(index);
					this.conditions.add(index, clonedSourceElement);
				}

			} else {
				index = this.conclusions.indexOf(sourceElement);
				if (index != -1) {
					this.conclusions.remove(index);
					this.conclusions.add(index, clonedSourceElement);
				}
			}

		return result;
	}

	public void onConnect(ConnectEvent event) {

		targetElement = (PointElement) event.getTargetElement().getData();
		sourceElement = (PointElement) event.getSourceElement().getData();
		int result = 0;
		// 1st case, connect a class with a property
		// if (sourceElement.getType() == Type.CLASS)
		// result = connectClassWithProperty(targetElement, sourceElement);
		// 2nd case, connect a built in method with a property
		if (sourceElement.getType() == Type.BUILTIN_METHOD)
			// result = connectBuiltinMethodWithProperty(sourceElement,
			// targetElement);
			result = 0;
		else if (sourceElement.getType() == Type.INSTANCE)
			result = connectInstanceWithProperty(sourceElement, targetElement);

		if (result == 0) {
			if (conditionsModel.getConnections().size() > 0)
				conditionsModel.getConnections().remove(
						conditionsModel.getConnections().size() - 1);
			System.out.println("incorrect connection");
		} else {

			// try to pass overlay to know the order of the connection
			// valid connection, add number (TODO dont know how to use it yet)
			Connection conn = conditionsModel.getConnections().get(
					conditionsModel.getConnections().size() - 1);
			conn.getOverlays().add(
					new LabelOverlay("" + counterOfConnections, "flow-label",
							0.5));
			conditionsModel.getConnections().remove(
					conditionsModel.getConnections().size() - 1);
			conditionsModel.getConnections().add(conn);
		}

	}

	public void onDisconnect(DisconnectEvent event) {
		boolean flag = false;

		targetElement = (PointElement) event.getTargetElement().getData();
		sourceElement = (PointElement) event.getSourceElement().getData();

		// update the list with the connections for the target element
		for (PointElement el : conditions) {
			if (el.getId().equalsIgnoreCase(targetElement.getId())) {
				targetElement.setConnections(el.getConnections());
				flag = true;
				break;
			}
		}

		if (!flag)
			for (PointElement el : conclusions) {
				if (el.getId().equalsIgnoreCase(targetElement.getId())) {
					targetElement.setConnections(el.getConnections());
					break;
				}
			}

		// remove the disconnected node from the connections of the property
		int indexOfNodeToRemove = -1;
		indexOfNodeToRemove = targetElement.getConnections().indexOf(
				sourceElement);
		if (indexOfNodeToRemove != -1)
			targetElement.getConnections().remove(indexOfNodeToRemove);

		// clone the old property
		clonedTargetElement = targetElement.clone();

		// add the new property node in the corresponding list
		int index = -1;
		if (flag) {
			index = this.conditions.indexOf(targetElement);
			if (index != -1) {
				this.conditions.remove(index);
				this.conditions.add(index, clonedTargetElement);
			}

		} else {
			index = this.conclusions.indexOf(targetElement);
			if (index != -1) {
				this.conclusions.remove(index);
				this.conclusions.add(index, clonedTargetElement);
			}
		}
	}

	public void onConnectionChange(ConnectionChangeEvent event) {

		ArrayList<PointElement> cloneList = new ArrayList<PointElement>();
		sourceElement = (PointElement) event.getNewSourceElement().getData();
		targetElement = (PointElement) event.getNewTargetElement().getData();
		originalTargetElement = (PointElement) event.getOriginalTargetElement()
				.getData();

		// boolean flag = Utils.findPanelOfElement(sourceElement.getVarName(),
		// conditions, conclusions);
		// if (flag)
		// cloneList = (ArrayList<PointElement>) conditions.clone();
		// else
		// cloneList = (ArrayList<PointElement>) conclusions.clone();
		//
		// for (PointElement el : cloneList) {
		//
		// if (el.getId().equalsIgnoreCase(originalTargetElement.getId()))
		// originalTargetElement.setConnections(el.getConnections());
		//
		// if (el.getVarName().equalsIgnoreCase(targetElement.getVarName()))
		// targetElement.setConnections(el.getConnections());
		//
		// }

		// remove sourceElement from the connections of the old target
		int index = -1;
		index = originalTargetElement.getConnections().indexOf(sourceElement);
		if (index != -1)
			originalTargetElement.getConnections().remove(index);

		clonedOriginalTargetElement = originalTargetElement.clone();

		// int result = 0;
		// if (sourceElement.getType() == Type.CLASS)
		// result = connectClassWithProperty(targetElement, sourceElement);
		// // 2nd case, connect a built in method with a property
		// else if (sourceElement.getType() == Type.BUILTIN_METHOD)
		// connectBuiltinMethodWithProperty(sourceElement, targetElement);

		clonedTargetElement = targetElement.clone();

		// remove old targets and add the updated targets
		index = cloneList.indexOf(originalTargetElement);
		cloneList.remove(index);
		cloneList.add(index, clonedOriginalTargetElement);

		index = cloneList.indexOf(targetElement);
		cloneList.remove(index);
		cloneList.add(index, clonedTargetElement);

		// update the lists
		if (flag)
			conditions = (ArrayList<PointElement>) cloneList.clone();
		else
			conclusions = (ArrayList<PointElement>) cloneList.clone();

		// if (result == 0) {
		// if (flag && conditionsModel.getConnections().size() > 0)
		// conditionsModel.getConnections().remove(
		// conditionsModel.getConnections().size() - 1);
		// else if (!flag && conclusionsModel.getConnections().size() > 0)
		// conclusionsModel.getConnections().remove(
		// conclusionsModel.getConnections().size() - 1);
		//
		// System.out.println("incorrect connection");
		//
		// }

	}

	private void createOntologyTree(Ontology ontology) {

		List<ArrayList<OntologyClass>> list = main.getAllClasses();
		OntologyClass cl = null;
		root = new DefaultTreeNode("Solutions Ontology", null);

		for (ArrayList<OntologyClass> temp : list) {
			cl = temp.get(0);
			getTreeNodeOfConcept(cl, root);
		}

	}

	private DefaultTreeNode getTreeNodeOfConcept(OntologyClass cl, TreeNode root) {

		DefaultTreeNode node = new DefaultTreeNode(cl.getClassName(), root);
		if (cl.getChildren().size() > 0)
			for (int i = 0; i < cl.getChildren().size(); i++) {
				getTreeNodeOfConcept(cl.getChildren().get(i), node);
			}
		return node;

	}

	public void onNodeSelect() {

		boolean flag = false;
		for (ArrayList<OntologyClass> temp : main.getAllClasses()) {
			flag = searchChildrenByName(temp.get(0), this.selectedNode
					.getData().toString());
			if (flag)
				break;

		}

	}

	public boolean searchChildrenByName(OntologyClass tempClass, String name) {
		boolean flag = false;
		if (tempClass.getClassName().equals(name)) {

			datatypes = tempClass.getDataProperties();
			objects = tempClass.getObjectProperties();
			instances = tempClass.getInstances();
			return true;

		} else {
			for (OntologyClass tempChild : tempClass.getChildren()) {
				flag = searchChildrenByName(tempChild, name);
				if (flag)
					break;
			}
		}

		return false;
	}

	public void findPropertiesForClass(String className) {

		boolean flag = false;
		for (ArrayList<OntologyClass> temp : main.getAllClasses()) {
			flag = searchChildren(temp.get(0), className);
			if (flag)
				break;

		}

	}

	public boolean searchChildren(OntologyClass tempClass, String className) {
		boolean flag = false;
		if (tempClass.getClassName().equals(className)) {

			propertiesList.addAll(tempClass.getDataProperties());
			propertiesList.addAll(tempClass.getObjectProperties());
			return true;

		} else {
			for (OntologyClass tempChild : tempClass.getChildren()) {
				flag = searchChildrenByName(tempChild, className);
				if (flag)
					break;
			}
		}

		return false;
	}

	public void getAllPropertiesForAllClasses() {
		for (ArrayList<OntologyClass> temp : main.getAllClasses()) {
			propertiesList.addAll(temp.get(0).getDataProperties());
			propertiesList.addAll(temp.get(0).getObjectProperties());
		}
	}

	public void createClassElement(String panelID) {
		PointElement classEl = new PointElement();
		classEl.setElementName(this.selectedNode.getData().toString());
		classEl.setType(Type.CLASS);
		classEl.setLabel("Class");
		classEl.setId(setVariableName());
		classEl = setPosition(classEl);
		// define the order
		int order = -1;
		if (panelID.equalsIgnoreCase("pan1")) {
			order = conditions.size() + 1;
		} else {
			order = conclusions.size() + 1;
		}
		classEl.setOrder(order);
		Element element = new Element(classEl, String.valueOf(classEl.getX()
				+ "em"), String.valueOf(classEl.getY() + "em"));
		EndPoint endPointCA = Utils
				.createRectangleEndPoint(EndPointAnchor.BOTTOM);
		endPointCA.setSource(true);
		element.addEndPoint(endPointCA);
		moveToPanel(panelID, classEl, element);
	}

	public void createMethodElement(String panelID) {
		PointElement methodEl = new PointElement();
		methodEl.setElementName(this.selectedMethod.getUsingName().toString());
		methodEl.setType(Type.BUILTIN_METHOD);
		methodEl.setId(setVariableName());
		methodEl = setPosition(methodEl);
		methodEl.setMethod(selectedMethod.clone());
		methodEl.setLabel("Method");
		// define the order
		int order = -1;
		if (panelID.equalsIgnoreCase("pan1")) {
			order = conditions.size() + 1;
		} else {
			order = conclusions.size() + 1;
		}

		methodEl.setOrder(order);
		Element element = new Element(methodEl, String.valueOf(methodEl.getX()
				+ "em"), String.valueOf(methodEl.getY() + "em"));

		// exclude methods that cannot be connected with other nodes
		// the user has to fill some field in order to use them

		EndPoint endPointCA = Utils
				.createRectangleEndPoint(EndPointAnchor.CONTINUOUS);
		// if (methodsWithoutConnections.contains(this.selectedMethod
		// .getOriginalName())) {
		//
		// endPointCA.setSource(false);
		// endPointCA.setTarget(false);
		// element.addEndPoint(endPointCA);
		//
		// } else {

		endPointCA.setSource(true);
		element.addEndPoint(endPointCA);
		// }

		moveToPanel(panelID, methodEl, element);
	}

	public void createInstanceElement(String panelID) {
		PointElement instanceEl = new PointElement();
		instanceEl.setElementName(this.selectedInstance.getInstanceName());
		instanceEl.setType(Type.INSTANCE);
		instanceEl.setInstance(this.selectedInstance);
		instanceEl.setLabel(this.selectedInstance.getClassName());
		instanceEl.setId(this.selectedInstance.getInstanceName());
		instanceEl = setPosition(instanceEl);
		Element element = new Element(instanceEl, String.valueOf(instanceEl
				.getX() + "em"), String.valueOf(instanceEl.getY() + "em"));
		EndPoint endPointCA = Utils
				.createRectangleEndPoint(EndPointAnchor.BOTTOM);
		endPointCA.setSource(true);
		element.addEndPoint(endPointCA);
		moveToPanel(panelID, instanceEl, element);

	}

	public void createPropertyElement(String panelID) {
		OntologyProperty property = new OntologyProperty("", "");
		Type type = Type.DATA_PROPERTY;

		if (this.getSelectedDataProperty() != null) {
			property = this.getSelectedDataProperty().clone();
		} else {
			property = this.getSelectedObjectProperty().clone();
			type = Type.OBJECT_PROPERTY;
		}

		// create the nodeElement for conditions list
		PointElement propElement = new PointElement();
		propElement.setElementName(property.getPropertyName());
		propElement.setId(setVariableName());
		propElement.setLabel(property.getClassName());

		// define the order
		int order = -1;
		if (panelID.equalsIgnoreCase("pan1")) {
			order = conditions.size() + 1;
		} else {
			order = conclusions.size() + 1;
		}

		propElement.setOrder(order);

		// define x, y
		propElement = setPosition(propElement);
		propElement.setType(type);
		propElement.setProperty(property);

		// create element for the model diagram
		Element element = new Element(propElement, String.valueOf(propElement
				.getX() + "em"), String.valueOf(propElement.getY() + "em"));
		EndPoint endPointCA = Utils
				.createDotEndPoint(EndPointAnchor.AUTO_DEFAULT);
		endPointCA.setTarget(true);
		element.addEndPoint(endPointCA);

		moveToPanel(panelID, propElement, element);

		this.selectedDataProperty = null;
		this.selectedObjectProperty = null;

	}

	public void moveToPanel(String panelID, PointElement networkElement,
			Element element) {

		if (panelID.contains("pan1")) {
			conditionsModel.addElement(element);
			networkElement.setPanel("conditions");
			conditions.add(networkElement);
			conditionsGraph.addVertex(networkElement);
		} else {
			conclusionsModel.addElement(element);
			networkElement.setPanel("conclusions");
			conclusions.add(networkElement);
			conclusionsGraph.addVertex(networkElement);
		}

	}

	public void clearPanel(String panelID) {

		// create a connector
		StraightConnector connector = new StraightConnector();
		connector.setPaintStyle("{strokeStyle:'#98AFC7', lineWidth:2}");
		connector.setHoverPaintStyle("{strokeStyle:'#5C738B'}");

		// clear and update the panels
		if (panelID.contains("pan1")) {
			conditionsModel = new DefaultDiagramModel();
			conditionsModel.setMaxConnections(-1);
			conditionsModel.getDefaultConnectionOverlays().add(
					new ArrowOverlay(20, 20, 1, 1));
			conditionsModel.setDefaultConnector(connector);
			conditions = new ArrayList<PointElement>();
		} else {
			conclusionsModel = new DefaultDiagramModel();
			conclusionsModel.setMaxConnections(-1);
			conclusionsModel.getDefaultConnectionOverlays().add(
					new ArrowOverlay(20, 20, 1, 1));
			conclusionsModel.setDefaultConnector(connector);
			conclusions = new ArrayList<PointElement>();
		}
	}

	public void handleChangeInArguments() {

		if (selectedClasses && selectedVariables && gridType1) {
			selectedVariables = false;
			gridType1 = false;
		} else if (selectedClasses && selectedVariables && !gridType1) {
			selectedClasses = false;
			gridType1 = true;
		}

		cloneSelectedNode.getMethod().setFlag(gridType1);
	}

	public String setVariableName() {
		return "X_" + counter++;
	}

	public void updatePropertiesList(AjaxBehaviorEvent event) {
		String selectedClassVar = cloneSelectedNode.getMethod().getValue1()
				.getValue();
		String className = getClassFromVar(selectedClassVar);
		findPropertiesForClass(className);
		System.out.println(propertiesList.size());

	}

	public String getClassFromVar(String var) {
		String className = "";

		// look for the class var firstly in conditions
		for (PointElement tmp : conditions) {
			if (tmp.getType() != PointElement.Type.BUILTIN_METHOD) {
				if (var.equals(tmp.getProperty().getClassVar())) {
					className = tmp.getProperty().getClassName();
					break;
				}
			}
		}

		// if the class name is empty look for it in the conclusions
		if (className.isEmpty())
			for (PointElement tmp : conclusions) {
				if (tmp.getType() != PointElement.Type.BUILTIN_METHOD) {
					if (var.equals(tmp.getProperty().getClassVar())) {
						className = tmp.getProperty().getClassName();
						break;
					}
				}
			}

		return className;
	}

	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public List<DataProperty> getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(List<DataProperty> datatypes) {
		this.datatypes = datatypes;
	}

	public List<ObjectProperty> getObjects() {
		return objects;
	}

	public void setObjects(List<ObjectProperty> objects) {
		this.objects = objects;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public ArrayList<PointElement> getConditions() {
		return conditions;
	}

	public void setConditions(ArrayList<PointElement> conditions) {
		this.conditions = conditions;
	}

	public String getNodeForRemove() {
		return nodeForRemove;
	}

	public void setNodeForRemove(String nodeForRemove) {
		this.nodeForRemove = nodeForRemove;
	}

	public OntologyProperty getSelectedDataProperty() {
		return selectedDataProperty;
	}

	public void setSelectedDataProperty(OntologyProperty selectedDataProperty) {
		this.selectedDataProperty = selectedDataProperty;
	}

	public OntologyProperty getSelectedObjectProperty() {
		return selectedObjectProperty;
	}

	public void setSelectedObjectProperty(
			OntologyProperty selectedObjectProperty) {
		this.selectedObjectProperty = selectedObjectProperty;
	}

	public BuiltinMethod getSelectedMethod() {
		return selectedMethod;
	}

	public void setSelectedMethod(BuiltinMethod selectedMethod) {
		this.selectedMethod = selectedMethod;
	}

	public Instance getSelectedInstance() {
		return selectedInstance;
	}

	public void setSelectedInstance(Instance selectedInstance) {
		this.selectedInstance = selectedInstance;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isFeedback() {
		return feedback;
	}

	public void setFeedback(boolean feedback) {
		this.feedback = feedback;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public ArrayList<Rule> getExistingRules() {
		return existingRules;
	}

	public void setExistingRules(ArrayList<Rule> existingRules) {
		this.existingRules = existingRules;
	}

	public ArrayList<PointElement> getConclusions() {
		return conclusions;
	}

	public void setConclusions(ArrayList<PointElement> conclusions) {
		this.conclusions = conclusions;
	}

	public String getOldFileName() {
		return oldFileName;
	}

	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}

	public ArrayList<String> getUsedVariablesForClasses() {
		return usedVariablesForClasses;
	}

	public void setUsedVariablesForClasses(
			ArrayList<String> usedVariablesForClasses) {
		this.usedVariablesForClasses = usedVariablesForClasses;
	}

	public ArrayList<String> getUsedVariablesForValues() {
		return usedVariablesForValues;
	}

	public void setUsedVariablesForValues(
			ArrayList<String> usedVariablesForValues) {
		this.usedVariablesForValues = usedVariablesForValues;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public ArrayList<String> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<String> variables) {
		this.variables = variables;
	}

	public boolean isSelectedClasses() {
		return selectedClasses;
	}

	public void setSelectedClasses(boolean selectedClasses) {
		this.selectedClasses = selectedClasses;
	}

	public boolean isSelectedVariables() {
		return selectedVariables;
	}

	public void setSelectedVariables(boolean selectedVariables) {
		this.selectedVariables = selectedVariables;
	}

	public boolean isGridType1() {
		return gridType1;
	}

	public void setGridType1(boolean gridType1) {
		this.gridType1 = gridType1;
	}

	public ArrayList<Message> getCorrelatedFiles() {
		return correlatedFiles;
	}

	public void setCorrelatedFiles(ArrayList<Message> correlatedFiles) {
		this.correlatedFiles = correlatedFiles;
	}

	public ArrayList<String> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<String> types) {
		this.types = types;
	}

	public ArrayList<OntologyProperty> getPropertiesList() {
		return propertiesList;
	}

	public void setPropertiesList(ArrayList<OntologyProperty> propertiesList) {
		this.propertiesList = propertiesList;
	}

	public ArrayList<String> getJenaLists() {
		return jenaLists;
	}

	public void setJenaLists(ArrayList<String> jenaLists) {
		this.jenaLists = jenaLists;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public ArrayList<String> getTempVariablesForClasses() {
		return tempVariablesForClasses;
	}

	public void setTempVariablesForClasses(
			ArrayList<String> tempVariablesForClasses) {
		this.tempVariablesForClasses = tempVariablesForClasses;
	}

	public ArrayList<String> getTempVariablesForValues() {
		return tempVariablesForValues;
	}

	public void setTempVariablesForValues(
			ArrayList<String> tempVariablesForValues) {
		this.tempVariablesForValues = tempVariablesForValues;
	}

	public ArrayList<String> getTempInstancesForClasses() {
		return tempInstancesForClasses;
	}

	public void setTempInstancesForClasses(
			ArrayList<String> tempInstancesForClasses) {
		this.tempInstancesForClasses = tempInstancesForClasses;
	}

	public ArrayList<String> getTempInstancesForValues() {
		return tempInstancesForValues;
	}

	public void setTempInstancesForValues(
			ArrayList<String> tempInstancesForValues) {
		this.tempInstancesForValues = tempInstancesForValues;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

}
