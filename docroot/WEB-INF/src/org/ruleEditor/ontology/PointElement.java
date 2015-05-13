package org.ruleEditor.ontology;

import java.util.ArrayList;


public class PointElement {
	private String elementName;
	private Type type;
	private String varName;
	private double x;
	private double y;
	private boolean renderEditText;
	private OntologyProperty property;
	private ArrayList<PointElement> connections;
	private String panel;
	private String id;

	public PointElement() {
		super();
		this.elementName = "";
		this.type = Type.CLASS;
		this.varName = "";
		this.x = 0;
		this.y = 0;
		this.renderEditText = false;
		this.property = new OntologyProperty("", "");
		this.connections = new ArrayList<PointElement>();
		this.panel = "";
		this.id = "";
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

	public void setType(Type type) {
		this.type = type;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
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

	public boolean isRenderEditText() {
		return renderEditText;
	}

	public void setRenderEditText(boolean renderEditText) {
		this.renderEditText = renderEditText;
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
		el.setVarName(this.getVarName());
		el.setX(this.getX());
		el.setY(this.getY());
		el.setRenderEditText(this.isRenderEditText());
		el.setProperty(this.getProperty().clone());
		for (int i = 0; i < this.getConnections().size(); i++) {
			el.getConnections().add(this.getConnections().get(i).clone());
		}
		el.setPanel(this.getPanel());
		el.setId(this.getId());

		return el;
	}



}
