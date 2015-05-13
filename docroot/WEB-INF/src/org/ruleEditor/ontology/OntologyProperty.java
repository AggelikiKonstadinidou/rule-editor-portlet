package org.ruleEditor.ontology;

import java.util.ArrayList;

public class OntologyProperty {

	private String propertyName;
	private String className;
	private String ontologyURI;

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
		
		return prop;
	}

	public class DataProperty extends OntologyProperty {
		private String dataRange;
		private String value;

		public DataProperty(String propertyName, String className) {
			super(propertyName, className);
			this.dataRange = "";
			this.value = "empty";
		}

		public String getDataRange() {
			return dataRange;
		}

		public void setDataRange(String dataRange) {
			this.dataRange = dataRange;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		

		public DataProperty clone() {
			DataProperty dataProp = new DataProperty("","");
			
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
