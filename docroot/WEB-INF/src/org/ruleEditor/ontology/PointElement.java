package org.ruleEditor.ontology;

import java.util.ArrayList;

public class PointElement {
	private String elementName;
	private Type type;
	private double x;
	private double y;
	private boolean editVar;
	private boolean editValue;
	private OntologyProperty property;
	private ArrayList<PointElement> connections;
	private String panel;
	private String id;
	private BuiltinMethod method;
	private Instance instance;
	private boolean enableEdit;
	private int order;
	private String label;
	private String classVariable;

	public PointElement() {
		super();
		this.elementName = "";
		this.type = Type.CLASS;
		this.x = 0;
		this.y = 0;
		this.editValue = false;
		this.editVar = false;
		this.property = new OntologyProperty("", "");
		this.connections = new ArrayList<PointElement>();
		this.panel = "";
		this.id = "";
		this.method = new BuiltinMethod("", "", "", 0);
		this.method.setFlag(false);
		this.instance = new Instance();
		this.enableEdit = false;
		this.order = 0;
		this.label = "";
		this.classVariable = "empty";
	}
	
	public String getClassVariable() {
		return classVariable;
	}

	public void setClassVariable(String classVariable) {
		this.classVariable = classVariable;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isEditVar() {
		return editVar;
	}

	public void setEditVar(boolean editVar) {
		this.editVar = editVar;
	}

	public boolean isEditValue() {
		return editValue;
	}

	public void setEditValue(boolean editValue) {
		this.editValue = editValue;
	}

	public boolean isEnableEdit() {
		return enableEdit;
	}

	public void setEnableEdit(boolean enableEdit) {
		this.enableEdit = enableEdit;
	}

	public BuiltinMethod getMethod() {
		return method;
	}

	public void setMethod(BuiltinMethod method) {
		this.method = method;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public Type getType() {
		return type;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public void setType(Type type) {
		this.type = type;
		// if(this.type == Type.CLASS){
		// this.setEnableEdit(true);
		// this.setEditVar(true);
		// }
		if (this.type == Type.DATA_PROPERTY
				|| this.type == Type.OBJECT_PROPERTY) {
			this.setEditVar(true);
			this.setEnableEdit(true);
			this.setEditValue(true);
		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public OntologyProperty getProperty() {
		return property;
	}

	public void setProperty(OntologyProperty property) {
		this.property = property;
	}

	public ArrayList<PointElement> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<PointElement> connections) {
		this.connections = connections;
	}

	public String getPanel() {
		return panel;
	}

	public void setPanel(String panel) {
		this.panel = panel;
	}

	public enum Type {

		CLASS, DATA_PROPERTY, OBJECT_PROPERTY, INSTANCE, BUILTIN_METHOD;

	}

	@Override
	public boolean equals(Object obj) {
		if (this.getElementName().equals(((PointElement) obj).getElementName())
				&& this.getId().equals(((PointElement) obj).getId())) {
			return true;
		} else
			return false;
	}

	public PointElement clone() {
		PointElement el = new PointElement();

		el.setElementName(this.getElementName());
		el.setType(this.getType());
		el.setX(this.getX());
		el.setY(this.getY());
		el.setEditValue(this.isEditValue());
		el.setEditVar(this.isEditVar());
		el.setEnableEdit(this.isEnableEdit());
		el.setProperty(this.getProperty().clone());
		for (int i = 0; i < this.getConnections().size(); i++) {
			el.getConnections().add(this.getConnections().get(i).clone());
		}
		el.setPanel(this.getPanel());
		el.setId(this.getId());
		el.setMethod(this.getMethod().clone());
		el.setInstance(this.getInstance().clone());
		el.setOrder(this.getOrder());
		el.setLabel(this.getLabel());
		el.setClassVariable(this.getClassVariable());

		return el;
	}

}
