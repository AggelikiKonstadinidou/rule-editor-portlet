package org.ruleEditor.ontology;

import java.util.ArrayList;

public class OntologyProperty {

	private String propertyName;
	private String className;
	private String ontologyURI;
	private String classVar;
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getClassVar() {
		return classVar;
	}

	public void setClassVar(String classVar) {
		this.classVar = classVar;
	}

	public String getOntologyURI() {
		return ontologyURI;
	}

	public void setOntologyURI(String ontologyURI) {
		this.ontologyURI = ontologyURI;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public OntologyProperty(String propertyName, String className) {
		super();
		this.propertyName = propertyName;
		this.className = className;
		this.ontologyURI = "";
		this.classVar = "empty";
		this.value = "empty";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getClassName().equals(
				((OntologyProperty) obj).getClassName())
				&& this.getPropertyName().equals(
						((OntologyProperty) obj).getPropertyName())) {
			return true;
		} else
			return false;
	}

	public OntologyProperty clone() {
		OntologyProperty prop = new OntologyProperty("","");
		
		prop.setClassName(this.getClassName());
		prop.setPropertyName(this.getPropertyName());
		prop.setOntologyURI(this.getOntologyURI());
		prop.setClassVar(this.getClassVar());
		prop.setValue(this.getValue());
		
		return prop;
	}

	public class DataProperty extends OntologyProperty {
		private String dataRange;

		public DataProperty(String propertyName, String className) {
			super(propertyName, className);
			this.dataRange = "";
		}

		public String getDataRange() {
			return dataRange;
		}

		public void setDataRange(String dataRange) {
			this.dataRange = dataRange;
		}

		public DataProperty clone() {
			DataProperty dataProp = new DataProperty("","");
			
			dataProp.setClassVar(this.getClassVar());
			dataProp.setClassName(this.getClassName());
			dataProp.setPropertyName(this.getPropertyName());
			dataProp.setDataRange(this.getDataRange());
			dataProp.setValue(this.getValue());
			dataProp.setOntologyURI(this.getOntologyURI());
			
			return dataProp;
		}

	}

	public class ObjectProperty extends OntologyProperty {
		private ArrayList<String> rangeOfClasses;

		public ObjectProperty(String propertyName,String className) {
			super(propertyName, className);
			rangeOfClasses = new ArrayList<String>();
		}

		public ArrayList<String> getRangeOfClasses() {
			return rangeOfClasses;
		}

		public void setRangeOfClasses(ArrayList<String> rangeOfClasses) {
			this.rangeOfClasses = rangeOfClasses;
		}
		

		public ObjectProperty clone() {
			ObjectProperty objectProp = new ObjectProperty("","");
			
			objectProp.setValue(this.getValue());
			objectProp.setClassVar(this.getClassVar());
			objectProp.setClassName(this.getClassName());
			objectProp.setPropertyName(this.getPropertyName());
			objectProp.setOntologyURI(this.getOntologyURI());
			for (int i = 0; i < this.getRangeOfClasses().size(); i++) {
				objectProp.getRangeOfClasses().add(
						this.getRangeOfClasses().get(i));
			}
				
			return objectProp;
		}

	}

}
