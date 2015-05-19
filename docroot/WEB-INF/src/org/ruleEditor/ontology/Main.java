package org.ruleEditor.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.ruleEditor.ontology.OntologyProperty.DataProperty;
import org.ruleEditor.ontology.OntologyProperty.ObjectProperty;
import org.ruleEditor.ontology.PointElement.Type;

@ManagedBean(name = "main", eager = true)
@ApplicationScoped

public class Main {
	private Ontology ontology;
    private List<ArrayList<OntologyClass>> allClasses;
    private List<BuiltinMethod> methods = new ArrayList<BuiltinMethod>();
    private List<String> languages = new ArrayList<String>();
	
	public Main() {
		System.gc();
		ontology = new Ontology();
		this.getOntology().loadOntology();

		//load classes
		allClasses = new ArrayList<ArrayList<OntologyClass>>();
		allClasses = this.getOntology().getClassesStructured();

		//load built in methods
		//TODO complete the methods
		methods = getBuiltinMethods();

		//load all languages
		Locale l = Locale.ENGLISH;
		String[] locales = l.getISOLanguages();
		for (int i = 0; i < locales.length; i++) {
			String str = locales[i];
			Locale obj = new Locale(str, str);
			languages.add(obj.getDisplayLanguage());
		}
		String[] ar = languages.toArray(new String[languages.size()]);
		Arrays.sort(ar);

		languages = new ArrayList<String>();
		for (int i = 0; i < ar.length; i++) {
			languages.add(ar[i]);
		}

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
	
	
	public OntologyProperty findProperty(String value){
		
		OntologyProperty ontologyProp = new OntologyProperty("","");
		for(ArrayList<OntologyClass> temp : allClasses){
			for(DataProperty prop : temp.get(0).getDataProperties()){
				if(prop.getOntologyURI().equalsIgnoreCase(value)){
					ontologyProp = (DataProperty) temp.clone();
					break;
				}
			}
			
			for(ObjectProperty prop : temp.get(0).getObjectProperties()){
				if(prop.getOntologyURI().equalsIgnoreCase(value)){
					ontologyProp = (ObjectProperty) temp.clone();
					break;
				}
			}
			
			
		}
		
		return ontologyProp;
	}
	
	public List<BuiltinMethod> getBuiltinMethods(){
		List<BuiltinMethod> list = new ArrayList<BuiltinMethod>();

		BuiltinMethod test1 = new BuiltinMethod("equals", "equals",
				"check equality", 4, Type.DATA_PROPERTY);
		BuiltinMethod test2 = new BuiltinMethod("notEquals", "notEquals",
				"check not equality", 4, Type.DATA_PROPERTY);
		BuiltinMethod test3 = new BuiltinMethod("makeSkolem", "makeSkolem",
				"create a new instance", 4, Type.DATA_PROPERTY);
		BuiltinMethod test4 = new BuiltinMethod("noValue", "noValue",
				"no value", 1, Type.DATA_PROPERTY);
		list.add(test1);
		list.add(test2);
		list.add(test3);
		list.add(test4);

		return list;
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

	public List<BuiltinMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<BuiltinMethod> methods) {
		this.methods = methods;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	
	
	
}
