package org.ruleEditor.ontology;

import java.util.ArrayList;

public class NetworkElement {

	private String elementName;
	private Type type;
	private String varName;
	private double x;
	private double y;
	private boolean renderEditText;
	private String valueOfProperty;
	private String ownToClass;
	private ArrayList<NetworkElement> connections;

	public NetworkElement() {
		super();
		this.elementName = "";
		this.type = Type.CLASS;
		this.varName = "";
		this.x = 0;
		this.y = 0;
		this.ownToClass = "";
		this.renderEditText = false;
		this.valueOfProperty = "empty";
		this.connections = new ArrayList<NetworkElement>();

	}

	public String getOwnToClass() {
		return ownToClass;
	}

	public void setOwnToClass(String ownToClass) {
		this.ownToClass = ownToClass;
	}

	public ArrayList<NetworkElement> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<NetworkElement> connections) {
		this.connections = connections;
	}

	public String getValueOfProperty() {
		return valueOfProperty;
	}

	public void setValueOfProperty(String valueOfProperty) {
		this.valueOfProperty = valueOfProperty;
	}

	public boolean isRenderEditText() {
		return renderEditText;
	}

	public void setRenderEditText(boolean renderEditText) {
		this.renderEditText = renderEditText;
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

	public enum Type {

		CLASS, DATA_PROPERTY, OBJECT_PROPERTY, INSTANCE;

	}
	
	

	@Override
	public String toString() {
		return "NetworkElement [elementName=" + elementName + ", type=" + type
				+ ", varName=" + varName + ", x=" + x + ", y=" + y
				+ ", renderEditText=" + renderEditText + ", valueOfProperty="
				+ valueOfProperty + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this.getElementName().equals(
				((NetworkElement) obj).getElementName())
				&& this.getVarName()
						.equals(((NetworkElement) obj).getVarName())) {
			return true;
		} else
			return false;
	}

	public NetworkElement clone() {
		NetworkElement networkEl = new NetworkElement();

		networkEl.setElementName(this.getElementName());
		networkEl.setType(this.getType());
		networkEl.setOwnToClass(this.getOwnToClass());
		networkEl.setVarName(this.getVarName());
		networkEl.setX(this.getX());
		networkEl.setY(this.getY());
		networkEl.setRenderEditText(this.isRenderEditText());
		networkEl.setValueOfProperty(this.getValueOfProperty());
		for (int i = 0; i < this.getConnections().size(); i++) {
			networkEl.getConnections()
					.add(this.getConnections().get(i).clone());

		}
		return networkEl;
	}

}
