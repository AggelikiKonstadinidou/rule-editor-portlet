package org.ruleEditor.ontology;

public class Instance {
	
	private String className;
	private String instanceName;
	private String id;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Instance(String className, String instanceName, String id) {
		super();
		this.className = className;
		this.instanceName = instanceName;
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getId().equals(((Instance) obj).getId())) {
			return true;
		} else
			return false;
	}

	public Instance clone() {
		Instance inst = new Instance("", "", "");
		inst.setClassName(this.getClassName());
		inst.setInstanceName(this.getInstanceName());
		inst.setId(this.getId());
		return inst;
	}

}
