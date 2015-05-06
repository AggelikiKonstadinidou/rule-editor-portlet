package org.ruleEditor.beans;

import java.awt.MenuItem;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

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
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.ontology.Ontology;
import org.ruleEditor.ontology.OntologyClass;
import org.ruleEditor.ontology.NetworkElement;
import org.ruleEditor.ontology.NetworkElement.Type;
import org.ruleEditor.utils.FileUploadController;
import org.ruleEditor.utils.Utils;

import com.sun.faces.component.visit.FullVisitContext;

@ManagedBean(name = "addConflictRuleBean")
@SessionScoped
public class AddConfictRuleBean {

	private Main main;
	private DefaultTreeNode root;
	private TreeNode selectedNode = null;
	private String ruleName = "", newFileName = "", oldFileName = "";
	private InputStream oldFileStream;
	private DefaultDiagramModel conditionsModel= new DefaultDiagramModel();;
	private DefaultDiagramModel conclusionsModel;
	private boolean suspendEvent;
	private List<String> datatypes = null;
	private List<String> objects = null;
	private List<String> instances = null;
	private String selectedRow;
	private String name;
	private ArrayList<NetworkElement> conditions;
	private ArrayList<NetworkElement> conclusions;
	private int counter;
	private int initialX =3;
	private int initialY = 3;
	private int objectCounter = 0;
	private Connector connector;
	private String nodeForRemove;

	public AddConfictRuleBean() {
		super();

		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
		createOntologyTree(main.getOntology());

	}

	public void init() {

		ruleName = "";
		newFileName = "";
		oldFileName = "";
		selectedRow = "";

		datatypes = new ArrayList<String>();
		objects = new ArrayList<String>();
		instances = new ArrayList<String>();
		conditions = new ArrayList<NetworkElement>();
		conclusions = new ArrayList<NetworkElement>();
		counter = 0;
		initialX =3;
		initialY = 3;
		objectCounter = 0;
		selectedNode = null;
		selectedRow = "";
		name = "";
		nodeForRemove = "";

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
	
	public NetworkElement setPosition(NetworkElement el) {
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
	
	public void removeNode(){
		
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String nodeForRemoveId = params.get("id");
		String panel = params.get("panel");
		
		int index = -1;
		Element elementForRemove = null;
		
		if(panel.equals("conditions")){
			
			for (NetworkElement el : conditions) {
				if (el.getVarName().equals(nodeForRemoveId)) {
					index = conditions.indexOf(el);
					break;
				}
			}

			conditions.remove(index);

			for (Element el : conditionsModel.getElements()) {
				NetworkElement tempEl = (NetworkElement) el.getData();
				if (tempEl.getVarName().equals(nodeForRemoveId)) {
					elementForRemove = el;
					break;

				}
			}
			conditionsModel.removeElement(elementForRemove);
			
		}else{
			
			for (NetworkElement el : conclusions) {
				if (el.getVarName().equals(nodeForRemoveId)) {
					index = conclusions.indexOf(el);
					break;
				}
			}

			conclusions.remove(index);

			for (Element el : conclusionsModel.getElements()) {
				NetworkElement tempEl = (NetworkElement) el.getData();
				if (tempEl.getVarName().equals(nodeForRemoveId)) {
					elementForRemove = el;
					break;

				}
			}
			conclusionsModel.removeElement(elementForRemove);
			
		}
	}

	public void onConnect(ConnectEvent event) {

		boolean flag = false;
		NetworkElement newTargetEl = new NetworkElement();
		NetworkElement oldTargetEl = (NetworkElement) event.getTargetElement()
				.getData();
		NetworkElement sourceEl = (NetworkElement) event.getSourceElement()
				.getData();
		
		//update the list with the connections for the target element
		for(NetworkElement el: conditions){
			if(el.getVarName().equalsIgnoreCase(oldTargetEl.getVarName())){
				oldTargetEl.setConnections(el.getConnections());
				break;
			}
		}
		
		for(NetworkElement el: conclusions){
			if(el.getVarName().equalsIgnoreCase(oldTargetEl.getVarName())){
				oldTargetEl.setConnections(el.getConnections());
				break;
			}
		}
		
		//clone the old target element in order to make the changes

		newTargetEl = oldTargetEl.clone();

		if (newTargetEl.getConnections().size() < 2) {
			newTargetEl.getConnections().add(sourceEl);
		}

		for (NetworkElement el : conditions) {
			if (el.getVarName().equalsIgnoreCase(sourceEl.getVarName())) {
				flag = true;
				break;
			}
		}

		int index = -1;
		if (flag) {
			// remove old object from the list
			// add the updated one

			index = this.conditions.indexOf(oldTargetEl);
			if (index != -1) {
				this.conditions.remove(index);
				this.conditions.add(index, newTargetEl);
			}

		} else {
			index = this.conclusions.indexOf(oldTargetEl);
			if (index != -1) {
				this.conclusions.remove(index);
				this.conclusions.add(index, newTargetEl);
			}
		}
			
		
	}

	public void onDisconnect(DisconnectEvent event) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Disconnected", "From " + event.getSourceElement().getData()
						+ " To " + event.getTargetElement().getData());

		FacesContext.getCurrentInstance().addMessage(null, msg);

		RequestContext.getCurrentInstance().update("form:msgs");
	}

	public void onConnectionChange(ConnectionChangeEvent event) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Connection Changed", "Original Source:"
						+ event.getOriginalSourceElement().getData()
						+ ", New Source: "
						+ event.getNewSourceElement().getData()
						+ ", Original Target: "
						+ event.getOriginalTargetElement().getData()
						+ ", New Target: "
						+ event.getNewTargetElement().getData());

		FacesContext.getCurrentInstance().addMessage(null, msg);

		RequestContext.getCurrentInstance().update("form:msgs");
		suspendEvent = true;
	}

	private EndPoint createDotEndPoint(EndPointAnchor anchor) {
		DotEndPoint endPoint = new DotEndPoint(anchor);
		endPoint.setScope("panel");
		endPoint.setTarget(true);
		endPoint.setStyle("{fillStyle:'#98AFC7'}");
		endPoint.setHoverStyle("{fillStyle:'#5C738B'}");
		endPoint.setRadius(5);

		return endPoint;
	}

	private EndPoint createRectangleEndPoint(EndPointAnchor anchor) {
		RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
		endPoint.setScope("panel");
		endPoint.setSource(true);
		endPoint.setStyle("{fillStyle:'#98AFC7'}");
		endPoint.setHoverStyle("{fillStyle:'#5C738B'}");
		endPoint.setWidth(10);
		endPoint.setHeight(10);

		return endPoint;
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
	
	public void saveRule() throws IOException{
		
//		if (ruleName.trim().equals("")) {
//		FacesContext.getCurrentInstance().addMessage(
//				null,
//				new FacesMessage(FacesMessage.SEVERITY_ERROR,
//						"Please provide a rule name", ""));
//
//		RequestContext.getCurrentInstance().update("form:msgs");
//
//		return;
//	}
//
//	String finalFileName = newFileName.trim();
//	if (newFileName.isEmpty() && !oldFileName.trim().isEmpty())
//		finalFileName = oldFileName;
//
//	if (finalFileName.trim().equals("")) {
//		FacesContext.getCurrentInstance().addMessage(
//				null,
//				new FacesMessage(FacesMessage.SEVERITY_ERROR,
//						"Please create a new file or select an existing",
//						""));
//		RequestContext.getCurrentInstance().update("form:msgs");
//		return;
//	}
		
		String rule = Utils.createRule(conditions, conclusions,"");
		
		Utils.writeGsonAndExportFile("aaa", rule);
		
	}

	/**
	 * <!--  	<h:panelGrid columns="3" columnClasses="label,value,name"
					styleClass="grid">
					<h:outputText value="Rule name: " id="ruleName" />
					<p:inputText label="Rule name" style="width: 300px;"
						id="newRuleName" autocomplete="off"
						value="#{addConflictRuleBean.ruleName}">
					</p:inputText>
					<h:outputText id="empty1" required="false" />

					<h:outputText value="Insert a new file name: " id="FileName"
						required="false" />
					<p:inputText label="File Name" required="false"
						style="width: 300px;" id="newFileName" autocomplete="off"
						value="#{addConflictRuleBean.newFileName}">
					</p:inputText>
					<h:outputText id="empty2" required="false" />

					<h:outputText value="or Select an existing rule file:  "
						id="existingFile" required="false" />
					<p:fileUpload mode="advanced" auto="false"
						value="#{addConflictRuleBean.oldFileName}"
						fileUploadListener="#{addConflictRuleBean.onFileUpload}"
						allowTypes="/(\.|\/)(rules)$/"
						description="Select an existing rule file">
					</p:fileUpload>
					<h:outputText value="#{addConfictRuleBean.oldFileName}"
						id="selectedFile" required="false" />
				</h:panelGrid> -->	
	 * @param event
	 */

	public void onFileUpload(FileUploadEvent event) {

		oldFileName = event.getFile().getFileName();
		try {
			oldFileStream = event.getFile().getInputstream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RequestContext rc = RequestContext.getCurrentInstance();
		rc.update("informationPanel");

	}

	public void onNodeSelect() {

		DefaultTreeNode node = null;

		// update data and object properties panel according to
		// the selected node

		for (ArrayList<OntologyClass> temp : main.getAllClasses()) {
			if (temp.get(0).getClassName().equals(this.selectedNode.getData())) {

				datatypes = temp.get(0).getDataProperties();
				objects = temp.get(0).getObjectProperties();

				break;
			}

		}

	}


	public void moveToPanel(String panelID) {

		// create the element for the diagram
		NetworkElement networkElement = new NetworkElement();
		networkElement.setElementName(this.selectedNode
				.getData().toString());
		networkElement.setType(Type.CLASS);
		networkElement.setRenderEditText(false);
		networkElement.setVarName(setVariableName());
		networkElement = setPosition(networkElement);
		Element element = new Element(networkElement,
				String.valueOf(networkElement.getX() + "em"),
				String.valueOf(networkElement.getY() + "em"));
		EndPoint endPointCA = createRectangleEndPoint(EndPointAnchor.BOTTOM);
		endPointCA.setSource(true);
		element.addEndPoint(endPointCA);


		if (panelID.contains("pan1")) {
			conditionsModel.addElement(element);
			conditions.add(networkElement);
		} else {
			conclusionsModel.addElement(element);
			conclusions.add(networkElement);
		}
		

	}

	public void moveToPanel2(String panelID) {
		
		OntologyClass selectedClass = new OntologyClass();
		//find the name of the selected class
		selectedClass = main.getOntologyClassByName(this.selectedNode.getData().toString());
		
		//find what type of property is the selected Property
		boolean isDataProperty = false;
		for(String s : selectedClass.getDataProperties()){
			if(s.equalsIgnoreCase(name)){
				isDataProperty = true;
				break;
			}
		}
		
       
		// create the element for the diagram
		NetworkElement networkElement = new NetworkElement();
		networkElement.setElementName(name);
		networkElement.setVarName(setVariableName());
		networkElement = setPosition(networkElement);
		networkElement.setOwnToClass(selectedClass.getClassName());
		if(isDataProperty){
			networkElement.setType(Type.DATA_PROPERTY);
			networkElement.setRenderEditText(true);
		}
		else
			networkElement.setType(Type.OBJECT_PROPERTY);
		
		Element element = new Element(networkElement,
				String.valueOf(networkElement.getX() + "em"),
				String.valueOf(networkElement.getY() + "em"));
		EndPoint endPointCA = createDotEndPoint(EndPointAnchor.AUTO_DEFAULT);
		endPointCA.setTarget(true);
	
		element.addEndPoint(endPointCA);

		if (panelID.contains("pan1")) {
			conditionsModel.addElement(element);
			conditions.add(networkElement);
		} else {
			conclusionsModel.addElement(element);
			conclusions.add(networkElement);
		}


	}

	public void clearPanel(String panelID) {

		if (panelID.contains("pan1"))
			conditionsModel = new DefaultDiagramModel();
		else
			conclusionsModel = new DefaultDiagramModel();
	}
	
//<p:ajax event="rowSelect"
//	listener="#{addConflictRuleBean.onRowSelect}" />
	public void onRowSelect(SelectEvent event) {
		this.selectedRow = event.getSource().toString();
		name = event.getObject().toString();
	}
	
	public String setVariableName(){
		return "X_"+counter++;
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

	public List<String> getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(List<String> datatypes) {
		this.datatypes = datatypes;
	}

	public List<String> getObjects() {
		return objects;
	}

	public void setObjects(List<String> objects) {
		this.objects = objects;
	}

	public List<String> getInstances() {
		return instances;
	}

	public void setInstances(List<String> instances) {
		this.instances = instances;
	}

	public String getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(String selectedRow) {
		this.selectedRow = selectedRow;
	}

	public ArrayList<NetworkElement> getConditions() {
		return conditions;
	}

	public void setConditions(ArrayList<NetworkElement> conditions) {
		this.conditions = conditions;
	}

	public String getNodeForRemove() {
		return nodeForRemove;
	}

	public void setNodeForRemove(String nodeForRemove) {
		this.nodeForRemove = nodeForRemove;
	}

	
	

}
