package org.ruleEditor.beans;

import java.awt.MenuItem;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
	private DefaultTreeNode root;
	private TreeNode selectedNode = null;
	private String ruleName = "", newFileName = "", oldFileName = "";
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
	private BuiltinMethod selectedMethod = null;
	private boolean flag = false;// false : for simple rule
									// true : for feedback rule
	private Instance selectedInstance = null;
	private Rule rule = null;
	private ArrayList<Rule> existingRules = new ArrayList<Rule>();
	private boolean feedback = true;
	private int counterOfConnections = 0;
	private int orderConditionsCounter = 1;
	private int orderConclusionsCounter = 1;
	private ArrayList<String> usedVariablesForClasses = new ArrayList<String>();
	
	private ArrayList<String> usedVariablesForValues = new ArrayList<String>();
	
	private ArrayList<String> variables = new ArrayList<String>();
	private String argument = "";
	private boolean gridType1= true;
	private boolean selectedClasses = true;
	private boolean selectedVariables = false;
//	private List<String> methodsWithoutConnections = Arrays.asList(
//			"makeSkolem", "print", "drop");
	private String previousStep = "";
	private HashMap<String,InputStream> filesToCompare;
	private ArrayList<Message> correlatedFiles;
	

	public AddNewRuleBean() {
		super();

		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
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
		ruleName = "";
		oldFileName = "";
		newFileName = "";
		counterOfConnections = 0;
		orderConditionsCounter = 1;
		orderConclusionsCounter = 1;
		filesToCompare = new HashMap<String, InputStream>();
		correlatedFiles = new ArrayList<Message>();
		Message msg = new Message();
		msg.setLanguage("fileName");
		msg.setText("rule");
		correlatedFiles.add(msg);
		
		usedVariablesForClasses = new ArrayList<String>();
		
		
		usedVariablesForValues = new ArrayList<String>();
	
		
		argument = "";
		variables = new ArrayList<String>();
		gridType1 = false;
		selectedClasses = true;
		selectedVariables = false;

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

		datatypes = new ArrayList<DataProperty>();
		objects = new ArrayList<ObjectProperty>();
		instances = new ArrayList<Instance>();
		conditions = (ArrayList<PointElement>) conditionsList.clone();
		conclusions = (ArrayList<PointElement>) conclusionsList.clone();
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

	public void createModels(String panelID, ArrayList<PointElement> list) {

		Element element = null;
		EndPoint endPointCA = null;

		// create the diagram models with the point elements
		for (PointElement el : list) {

			if (el.getType() == Type.DATA_PROPERTY
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

//		int sourceIndex = -1;
//		int targetIndex = -1;
//		// make connections between the elements of the diagram models
//		for (PointElement el : list) {
//			if (el.getType() == Type.OBJECT_PROPERTY
//					|| el.getType() == Type.DATA_PROPERTY
//					|| el.getType() == Type.BUILTIN_METHOD) {
//
//				targetIndex = findIndexOfElementByPointElement(el,
//						conditionsModel);
//
//				boolean flag = false;
//				for (PointElement temp : el.getConnections()) {
//
//					sourceIndex = findIndexOfElementByPointElement(temp,
//							conditionsModel);
//
//					if (sourceIndex == -1) {
//						sourceIndex = findIndexOfElementByPointElement(temp,
//								conclusionsModel);
//						flag = true;
//					}
//
//					if (panelID.equalsIgnoreCase("conclusions"))
//						targetIndex = findIndexOfElementByPointElement(el,
//								conclusionsModel);
//
//					if (panelID.equalsIgnoreCase("conditions")) {
//
//							conditionsModel.connect(new Connection(
//									conditionsModel.getElements()
//											.get(sourceIndex).getEndPoints()
//											.get(0), conditionsModel
//											.getElements().get(targetIndex)
//											.getEndPoints().get(0)));
//						
//					} else {
//
//						if (flag)
//							conclusionsModel.connect(new Connection(
//									conclusionsModel.getElements()
//											.get(sourceIndex).getEndPoints()
//											.get(0), conclusionsModel
//											.getElements().get(targetIndex)
//											.getEndPoints().get(0)));
//						else {
//							
//							//a conclusion method/property has to be connected with
//							// a class that exists in conditions
//							Element conclElement = new Element(temp,
//									String.valueOf(temp.getX() + "em"),
//									String.valueOf(temp.getY() + "em"));
//							EndPoint endPointCon = Utils
//									.createRectangleEndPoint(EndPointAnchor.BOTTOM);
//							endPointCon.setSource(true);
//							conclElement.addEndPoint(endPointCon);
//							conclusionsModel.addElement(conclElement);
//							
//							sourceIndex = conclusionsModel.getElements().size()-1;
//							
//							conclusionsModel.connect(new Connection(
//									conclusionsModel.getElements()
//											.get(sourceIndex).getEndPoints()
//											.get(0), conclusionsModel
//											.getElements().get(targetIndex)
//											.getEndPoints().get(0)));
//						}
//
//					}
//				}
//			}
//		}

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

		if (panel.equalsIgnoreCase("conditions")) {
			for (PointElement el : conditions) {
				if (el.getId().equals(nodeForRemoveId)) {
					cloneSelectedNode = el.clone();
					break;
				}
			}
		} else {

			for (PointElement el : conclusions) {
				if (el.getId().equals(nodeForRemoveId)) {
					cloneSelectedNode = el.clone();
					break;
				}
			}

		}
		
		if(cloneSelectedNode.getType()==Type.BUILTIN_METHOD)
			fillListsWithVariables();
		

	}
	
	//TODO it does not work
	public void spinnerValueChangeForClasses(AjaxBehaviorEvent event) {
		
//		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("index");
//		String indexOfList = (String)event.getComponent().getAttributes().get("index");
//		String newValue = (String)event.getComponent().getAttributes().get("newValue");
		
		for(int i=0; i<cloneSelectedNode.getMethod().getNumberOfParams();i++){
			
		}

	}

	public void fillListsWithVariables() {

		for (int i = 0; i < cloneSelectedNode.getMethod().getNumberOfParams(); i++) {
			cloneSelectedNode.getMethod().getListOfVarClassesLists()
					.set(i, usedVariablesForClasses);
			cloneSelectedNode.getMethod().getListOfVarValuesLists()
					.set(i, usedVariablesForValues);
			cloneSelectedNode
					.getMethod()
					.getSelectedValues()
					.add(cloneSelectedNode.getMethod().new HelpObject("" + i,
							"-"));

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
	
	public void findCorrelation() throws IOException{
		correlatedFiles = Utils.correlateRules(filesToCompare,conditions,conclusions,main);
	}
	
	public void goToPreviousStep() throws IOException{
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.redirect(previousStep);
	}
	
	public void getPreviousStep(String step){
		previousStep = step;
	}

	public void saveRule() throws IOException {

		if (ruleName.trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide a rule name", ""));

			return;
		}

		String finalFileName = newFileName.replace(" ", "_").trim();
		boolean createNewFile = true;
		if (newFileName.isEmpty() && !oldFileName.trim().isEmpty()) {
			createNewFile = false;
			finalFileName = oldFileName;
		}
		if (finalFileName.trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please create a new file or select an existing",
							""));
			return;
		}

		RuleCreationUtilities.saveRule(ruleName.replace(" ", "").trim(),
				finalFileName, conditions, conclusions, false, createNewFile,
				"", "", "", existingRules, fileStream);

	}

	public void saveEditOfNode() {

		if (cloneSelectedNode.getType() == Type.DATA_PROPERTY) {

			DataProperty property = (DataProperty) cloneSelectedNode
					.getProperty();
			if (property.getValue().trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(
						"msgs",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Please provide a value for the property", ""));

				return;
			}

			if (property.getDataRange().equalsIgnoreCase("boolean"))
				if (!property.getValue().trim().equals("true")
						&& !property.getValue().trim().equals("false")) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									"msgs",
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Please provide a boolean value for the property",
											""));

					return;
				}
		}
		
		// get the used variables for classes and generally for values
		String s = cloneSelectedNode.getProperty().getClassVar();
		String value = cloneSelectedNode.getProperty().getValue();
		if (!usedVariablesForClasses.contains(s) && s.contains("?"))
			usedVariablesForClasses.add(s);

		if (cloneSelectedNode.getType() == Type.DATA_PROPERTY)
			if (!usedVariablesForValues.contains(value) && value.contains("?"))
				usedVariablesForValues.add(value);

		if (cloneSelectedNode.getType() == Type.OBJECT_PROPERTY)
			if (!usedVariablesForClasses.contains(value) && value.contains("?"))
				usedVariablesForClasses.add(value);
		
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
		int pos = -1;
		if (cloneSelectedNode.getType() == Type.CLASS)
			for (PointElement el : clonedList) {
				if (el.getType() == Type.DATA_PROPERTY
						|| el.getType() == Type.OBJECT_PROPERTY) {
					for (PointElement connEl : el.getConnections()) {
						if (connEl.getId().equalsIgnoreCase(
								cloneSelectedNode.getId())) {
							pos = el.getConnections().indexOf(connEl);
							break;
						}
					}

					if (pos != -1) {
						el.getConnections().remove(pos);
						el.getConnections().add(pos, cloneSelectedNode);
					}

					pos = -1;
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
			//objectCounter++;
			objectCounter = 0;
			initialY = initialY + 12;
		}
//		} else if (objectCounter == 2) {
//			x = initialX + 35;
//			y = initialY;
//			objectCounter = 0;
//			initialY = initialY + 12;
//		}

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
			//add element to data property connections
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
//		if (clonedSourceElement.getConnections().size() < i
//				&& targetElement.getType().equals(
//						clonedSourceElement.getMethod().getTypeOfParam())) {
//			clonedSourceElement.getConnections().add(targetElement.clone());
//			counterOfConnections++;
//			result = 1;
//		}

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
//		if (sourceElement.getType() == Type.CLASS)
//			result = connectClassWithProperty(targetElement, sourceElement);
		// 2nd case, connect a built in method with a property
		 if (sourceElement.getType() == Type.BUILTIN_METHOD)
//			result = connectBuiltinMethodWithProperty(sourceElement,
//					targetElement);
			result = 0;
		else if (sourceElement.getType() == Type.INSTANCE)
			result = connectInstanceWithProperty(sourceElement,
					targetElement);

		if (result == 0) {
			if (conditionsModel.getConnections().size() > 0)
				conditionsModel.getConnections().remove(
						conditionsModel.getConnections().size() - 1);
			System.out.println("incorrect connection");
		} else {

			// try to pass overlay to know the order of the connection
            //valid connection, add number (TODO dont know how to use it yet)
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

//		boolean flag = Utils.findPanelOfElement(sourceElement.getVarName(),
//				conditions, conclusions);
//		if (flag)
//			cloneList = (ArrayList<PointElement>) conditions.clone();
//		else
//			cloneList = (ArrayList<PointElement>) conclusions.clone();
//
//		for (PointElement el : cloneList) {
//
//			if (el.getId().equalsIgnoreCase(originalTargetElement.getId()))
//				originalTargetElement.setConnections(el.getConnections());
//
//			if (el.getVarName().equalsIgnoreCase(targetElement.getVarName()))
//				targetElement.setConnections(el.getConnections());
//
//		}

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

		for (ArrayList<OntologyClass> temp : main.getAllClasses()) {
			searchChildrenByName(temp.get(0));

		}

	}

	public boolean searchChildrenByName(OntologyClass tempClass) {
		boolean flag = false;
		if (tempClass.getClassName().equals(this.selectedNode.getData())) {

			datatypes = tempClass.getDataProperties();
			objects = tempClass.getObjectProperties();
			instances = tempClass.getInstances();
			return true;

		} else {
			for (OntologyClass tempChild : tempClass.getChildren()) {
				flag = searchChildrenByName(tempChild);
				if (flag)
					break;
			}
		}

		return false;
	}

	public void createMethodElement(String panelID) {
		PointElement methodEl = new PointElement();
		methodEl.setElementName(this.selectedMethod.getUsingName()
				.toString());
		methodEl.setType(Type.BUILTIN_METHOD);
		methodEl.setId(setVariableName());
		methodEl = setPosition(methodEl);
		methodEl.setMethod(selectedMethod.clone());
		methodEl.setLabel("Method");
		// define the order
		int order = -1 ;
		if(panelID.equalsIgnoreCase("pan1")){
			order = orderConditionsCounter;
			orderConditionsCounter++;
		}
		else{
			order = orderConclusionsCounter;
			orderConclusionsCounter++;
		}

		methodEl.setOrder(order);
		Element element = new Element(methodEl,
				String.valueOf(methodEl.getX() + "em"),
				String.valueOf(methodEl.getY() + "em"));

		// exclude methods that cannot be connected with other nodes
		// the user has to fill some field in order to use them

		EndPoint endPointCA = Utils
				.createRectangleEndPoint(EndPointAnchor.CONTINUOUS);
//		if (methodsWithoutConnections.contains(this.selectedMethod
//				.getOriginalName())) {
//
//			endPointCA.setSource(false);
//			endPointCA.setTarget(false);
//			element.addEndPoint(endPointCA);
//
//		} else {

			endPointCA.setSource(true);
			element.addEndPoint(endPointCA);
	//	}

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
		Element element = new Element(instanceEl,
				String.valueOf(instanceEl.getX() + "em"),
				String.valueOf(instanceEl.getY() + "em"));
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
		
		//define the order
		int order = -1 ;
		if(panelID.equalsIgnoreCase("pan1")){
			order = orderConditionsCounter;
			orderConditionsCounter++;
		}
		else{
			order = orderConclusionsCounter;
			orderConclusionsCounter++;
		}
		
		propElement.setOrder(order);
		
		//define x, y
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
		} else {
			conclusionsModel.addElement(element);
			networkElement.setPanel("conclusions");
			conclusions.add(networkElement);
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
		} else if(selectedClasses && selectedVariables && !gridType1 ){
			selectedClasses = false;
			gridType1 = true;
		}
	}

	public String setVariableName() {
		return "X_" + counter++;
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

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
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
	public void setUsedVariablesForClasses(ArrayList<String> usedVariablesForClasses) {
		this.usedVariablesForClasses = usedVariablesForClasses;
	}

	public ArrayList<String> getUsedVariablesForValues() {
		return usedVariablesForValues;
	}
	public void setUsedVariablesForValues(ArrayList<String> usedVariablesForValues) {
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

	
	
	
}
