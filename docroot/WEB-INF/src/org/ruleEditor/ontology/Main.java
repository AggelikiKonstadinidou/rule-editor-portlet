package org.ruleEditor.ontology;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "main", eager = true)
@ApplicationScoped

public class Main {
	private Ontology ontology;
    private List<ArrayList<OntologyClass>> allClasses;
	
	public Main(){
		 System.gc();
		 ontology = new Ontology();
		 this.getOntology().loadOntology();
		 
		 allClasses = new ArrayList<ArrayList<OntologyClass>>();
		 allClasses = this.getOntology().getClassesStructured();
		 
		 System.out.println("Ontology loaded successfully");
		 
	}
	
	public OntologyClass getOntologyClassByName(String name) {
		OntologyClass ontClass = new OntologyClass();

		for (ArrayList<OntologyClass> temp : allClasses) {

			if (temp.get(0).getClassName().equalsIgnoreCase(name))
				ontClass = temp.get(0);
			else
				checkChildrenOfOntologyClass(ontClass, name);
		}

		return ontClass;
	}

	public OntologyClass checkChildrenOfOntologyClass(OntologyClass cl,
			String name) {
		OntologyClass classToReturn = new OntologyClass();
		if (cl.getChildren().size() > 0)
			for (int i = 0; i < cl.getChildren().size(); i++) {
				classToReturn = cl.getChildren().get(i);
				if (cl.getChildren().get(i).getClassName().equals(name))
					return classToReturn;
				else
					checkChildrenOfOntologyClass(cl.getChildren().get(i), name);
			}
		return null;

	}
	
	//TODO
	public OntologyProperty find(String value){
		return null;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public List<ArrayList<OntologyClass>> getAllClasses() {
		return allClasses;
	}

	public void setAllClasses(List<ArrayList<OntologyClass>> allClasses) {
		this.allClasses = allClasses;
	}
	
}
