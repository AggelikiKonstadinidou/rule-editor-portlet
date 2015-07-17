package org.ruleEditor.ontology;

import java.util.ArrayList;
import java.util.List;

import org.ruleEditor.ontology.OntologyProperty.DataProperty;
import org.ruleEditor.ontology.OntologyProperty.ObjectProperty;

public class OntologyClass {
	private String className;
	private List<OntologyClass> children;
	private List<DataProperty> dataProperties;
	private List<ObjectProperty> objectProperties;
	private List<Instance> instances;
	
	public OntologyClass() {
		super();
		this.className = "";
		this.children = new ArrayList<OntologyClass>();
		this.dataProperties = new ArrayList<DataProperty>();
		this.objectProperties = new ArrayList<ObjectProperty>();
		this.instances = new ArrayList<Instance>();
	}

	public List<OntologyClass> getChildren() {
		return children;
	}

	public void setChildren(List<OntologyClass> children) {
		this.children = children;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<DataProperty> getDataProperties() {
		return dataProperties;
	}

	public void setDataProperties(List<DataProperty> dataProperties) {
		this.dataProperties = dataProperties;
	}

	public List<ObjectProperty> getObjectProperties() {
		return objectProperties;
	}

	public void setObjectProperties(List<ObjectProperty> objectProperties) {
		this.objectProperties = objectProperties;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getClassName().equals(
				((OntologyClass) obj).getClassName())) {
			return true;
		} else
			return false;
	}
	
	public OntologyClass clone() {
		OntologyClass ontClass = new OntologyClass();
		
		ontClass.setClassName(this.getClassName());
		for (DataProperty temp : this.getDataProperties()) {
			ontClass.getDataProperties().add(temp.clone());
		}

		for (ObjectProperty temp : this.getObjectProperties()) {
			ontClass.getObjectProperties().add(temp.clone());
		}

		for (OntologyClass temp : this.getChildren()) {
			ontClass.getChildren().add(temp.clone());
		}
		
		for(Instance temp: this.getInstances()){
			ontClass.getInstances().add(temp.clone());
		}
		
		return ontClass;
	}


}
