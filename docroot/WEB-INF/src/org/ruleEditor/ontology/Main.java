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
    private ArrayList<Solution> allSolutions= new ArrayList<Solution>();
    private ArrayList<Setting> allSettings = new ArrayList<Setting>();
	
	public Main() {
		System.gc();
		ontology = new Ontology();
		this.getOntology().loadOntology();

		// load solutions and settings
		allSolutions = this.getOntology().loadSolutionsAndSettingsInstances();
		for (Solution s : allSolutions) {
			allSettings.addAll(s.settings);
		}

		// load classes
		allClasses = new ArrayList<ArrayList<OntologyClass>>();
		allClasses = this.getOntology().getClassesStructured();
		
		Instance inst = null;
		int indexOfSolutions = -1;
		ArrayList<OntologyClass> solutions = null;
		for (ArrayList<OntologyClass> temp : allClasses) {
			if (temp.get(0).getClassName().equalsIgnoreCase("Solutions")) {
				indexOfSolutions = allClasses.indexOf(temp);
				solutions = temp;
				//findAndCreateSolutionsInstances(temp);
			}

			if (temp.get(0).getClassName().equalsIgnoreCase("Settings")) {
				for (Setting sett : allSettings) {
					if (!sett.ignoreSetting) {
						inst = new Instance();
						inst.setClassName("Settings");
						inst.setId(sett.instanceName);
						inst.setInstanceName(sett.instanceName);
						inst.setInstanceObj(sett);
						temp.get(0).getInstances().add(inst);
					}

				}
			}
		}
		
		ArrayList<OntologyClass> newSolutions = findAndCreateSolutionsInstances(solutions);
		allClasses.remove(solutions);
		allClasses.add(indexOfSolutions, newSolutions);
		

		// load built in methods
		// TODO complete the methods
		methods = getBuiltinMethods();

		// load all languages
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
	
	public ArrayList<OntologyClass> findAndCreateSolutionsInstances(
			ArrayList<OntologyClass> ontClass) {
		Instance inst = null;
		ArrayList<OntologyClass> toBeRturnedList = new ArrayList<OntologyClass>();
		for (Solution sol : allSolutions) {

			inst = new Instance();
			inst.setInstanceObj(sol);
			inst.setInstanceName(sol.name);
			inst.setId(sol.id);
			if (sol.className.equalsIgnoreCase("Solutions"))
				inst.setClassName(sol.className);

			if (ontClass.get(0).getClassName().equalsIgnoreCase(sol.className)) {

				ontClass.get(0).getInstances().add(inst);
			} else {
				toBeRturnedList = (ArrayList<OntologyClass>) checkChildrenForSolutions(
						(ArrayList<OntologyClass>) ontClass.get(0)
								.getChildren(), inst);
				ontClass.get(0).setChildren(toBeRturnedList);
			}
		}

		return ontClass;

	}
	
	public ArrayList<OntologyClass> checkChildrenForSolutions(
			ArrayList<OntologyClass> ontClass, Instance inst) {
		ArrayList<OntologyClass> toBeRturnedList = new ArrayList<OntologyClass>();
		for (OntologyClass temp : ontClass) {
			if (temp.getClassName().equalsIgnoreCase(inst.getClassName())) {
				temp.getInstances().add(inst);
				break;
			} else {

				toBeRturnedList = checkChildrenForSolutions(
						(ArrayList<OntologyClass>) temp.getChildren(), inst);
				temp.setChildren(toBeRturnedList);

			}
		}

		return ontClass;
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

		BuiltinMethod test1 = new BuiltinMethod(
				"equal",
				"equal",
				"Test if x=y. "
						+ "The equality test is semantic equality so that, for example,"
						+ " the xsd:int 1 and the xsd:decimal 1 would test equal.",
				2);
		test1.setWatermarkDescription("?X,?Y to be compared");
		
		BuiltinMethod test2 = new BuiltinMethod(
				"notEqual",
				"notEqual",
				"Test if x != y. "
						+ "The equality test is semantic equality so that, for example,"
						+ " the xsd:int 1 and the xsd:decimal 1 would test equal.",
				2);
		test2.setWatermarkDescription("?X,?Y to be compared");
		
		//-------------------------------------------------
		BuiltinMethod test4 = new BuiltinMethod("noValue", "noValue",
				"True if there is no known triple (x, p, ) or (x, p, v) in the model or the explicit forward deductions so far.", 1);
		//-----------------------------------------------
		BuiltinMethod test5 = new BuiltinMethod("isLiteral", "isLiteral",
				"Test whether the single argument is a literal", 1);
		test5.setWatermarkDescription("?X");
		BuiltinMethod test6 = new BuiltinMethod("notLiteral", "notLiteral",
				"Test whether the single argument is not a literal", 1);
		test6.setWatermarkDescription("?X");
		BuiltinMethod test7 = new BuiltinMethod("isFunctor", "isFunctor",
				"Test whether the single argument is a functor-valued literal",
				1);
		test7.setWatermarkDescription("?X");
		BuiltinMethod test8 = new BuiltinMethod(
				"notFunctor",
				"notFunctor",
				"Test whether the single argument is not a functor-valued literal",
				1);
		test8.setWatermarkDescription("?X");
		BuiltinMethod test9 = new BuiltinMethod("isBNode", "isBNode",
				"Test whether the single argument is a blank-node", 1);
		test9.setWatermarkDescription("?X");
		BuiltinMethod test10 = new BuiltinMethod("notBNode", "notBNode",
				"Test whether the single argument is not a blank-node", 1);
		test10.setWatermarkDescription("?X");
		//-------------------------------------------------------
		
		//test an h klassi exei antistoixithei h oxi se variable
		BuiltinMethod test11 = new BuiltinMethod("bound", "bound",
				"Test if all of the arguments are bound variables", 100);
		test11.setWatermarkDescription("?X,...?X10");
		BuiltinMethod test12 = new BuiltinMethod("unbound", "unbound",
				"Test if all of the arguments are not bound variables", 100);
		test12.setWatermarkDescription("?X,...?X10");
		//-----------------------------------------------------------
		BuiltinMethod test13 = new BuiltinMethod(
				"lessThan",
				"lessThan",
				"Test if x is < y."
						+ " Only passes if both x and y are numbers or time instants (can be integer or floating point or XSDDateTime).",
				2);
		test13.setWatermarkDescription("?X,?Y");
		BuiltinMethod test14 = new BuiltinMethod(
				"greaterThan",
				"greaterThan",
				"Test if x is > y."
						+ " Only passes if both x and y are numbers or time instants (can be integer or floating point or XSDDateTime).",
				2);
		test14.setWatermarkDescription("?X,?Y");
		BuiltinMethod test15 = new BuiltinMethod(
				"le",
				"le",
				"Test if x is <= y."
						+ " Only passes if both x and y are numbers or time instants (can be integer or floating point or XSDDateTime).",
				2);
		test15.setWatermarkDescription("?X,?Y");
		BuiltinMethod test16 = new BuiltinMethod(
				"ge",
				"ge",
				"Test if x is >= y."
						+ " Only passes if both x and y are numbers or time instants (can be integer or floating point or XSDDateTime).",
				2);
		test16.setWatermarkDescription("?X,?Y");
		
		//------------------------------------------------------------
		BuiltinMethod test17 = new BuiltinMethod("sum", "sum",
				"Sets c to be (a+b).", 3);
		test17.setWatermarkDescription("?a, ?b, ?c");
		BuiltinMethod test18 = new BuiltinMethod("addOne", "addOne",
				"Sets c to be (a+1).", 2);
		test18.setWatermarkDescription("?a, ?b, ?c");
		BuiltinMethod test19 = new BuiltinMethod("difference", "difference",
				"Sets c to be (a-b).", 3);
		test19.setWatermarkDescription("?a, ?b, ?c");
		BuiltinMethod test20 = new BuiltinMethod("min", "min",
				"Sets c to be min(a,b).", 3);
		test20.setWatermarkDescription("?a, ?b, ?c");
		BuiltinMethod test21 = new BuiltinMethod("max", "max",
				"Sets c to be max(a,b)", 3);
		test21.setWatermarkDescription("?a, ?b, ?c");
		BuiltinMethod test22 = new BuiltinMethod("product", "product",
				"Sets c to be (ab).", 3);
		test22.setWatermarkDescription("?a, ?b, ?c");
		BuiltinMethod test23 = new BuiltinMethod("quotient", "quotient",
				"Sets c to be (a/b).", 3);
		test23.setWatermarkDescription("?a, ?b, ?c");
		
		//---------------------------------------------------------------
		BuiltinMethod test24 = new BuiltinMethod("strConcat", "strConcat",
				"Concatenates the lexical form of all the arguments except the last,"
						+ " then binds the last argument to a plain literal "
						+ " with that lexical form.", 100);
		test24.setWatermarkDescription("?a1, .. ?an, ?t");
		BuiltinMethod test25 = new BuiltinMethod("uriConcat", "uriConcat",
				"Concatenates the lexical form of all the arguments except the last,"
						+ " then binds the last argument to"
						+ " a URI node with that lexical form.", 100);
		test25.setWatermarkDescription("?a1, .. ?an, ?t");
		//---------------------------------------------------------------
		//TODO
		BuiltinMethod test26 = new BuiltinMethod("regex", "regex",
				"regex", 1);
		
		test26.setWatermarkDescription("?t, ?p");		
		
		//--------------------------------------------------------------
		BuiltinMethod test27 = new BuiltinMethod(
				"now",
				"now",
				"Binds ?x to an xsd:dateTime value corresponding to the current time.",
				1);
		test27.setWatermarkDescription("?x");
		//--------------------------------------------------------------
		BuiltinMethod test28 = new BuiltinMethod("makeTemp", "makeTemp",
				"Binds ?x to a newly created blank node.", 1);
		test28.setWatermarkDescription("?x");
		//--------------------------------------------------------------
		BuiltinMethod test29 = new BuiltinMethod(
				"makeInstance",
				"makeInstance",
				"Binds ?v to be a blank node which is asserted as the value of the ?p property on resource ?x and optionally has type ?t.",
				2);
		test28.setWatermarkDescription("?x, ?p, ?v");
		//--------------------------------------------------------------
		
		BuiltinMethod test30 = new BuiltinMethod("makeSkolem", "makeSkolem",
				"Binds ?x to be a blank node."
				+ " The blank node is generated based on the values of the"
				+ " remain ?vi arguments, so the same combination of arguments"
				+ " will generate the same bNode.",2);
		test30.setWatermarkDescription("Parameters : ?variable_name_of_new_Node,"
				+ "?variable_name_of_classA,?variable_name_of_ClassB,...");
		//----------------------------------------------------------------
		
		//TODO: they remove statements, how can we handle it?
		BuiltinMethod test31 = new BuiltinMethod("remove", "remove",
				"Remove the statement (triple) which caused the"
				+ " n'th body term of this (forward-only) rule to match."
				+ " Remove will propagate the change to other consequent "
				+ "rules including the firing rule (which must thus be guarded"
				+ " by some other clauses).", 100);
		
		test31.setWatermarkDescription("List the number of connections to be removed e.g. n");
		BuiltinMethod test32 = new BuiltinMethod("drop", "drop",
				"Drop will silently remove the triple(s) "
				+ "from the graph but not fire any rules as a consequence",
				100);
		
		test32.setWatermarkDescription("List the number of connections to be removed e.g. n");
		//------------------------------------------------------------------
		
		BuiltinMethod test35 = new BuiltinMethod("print", "print",
				"Print (to standard out) a representation of each argument.", 1);
		test35.setWatermarkDescription("Text to be printed");
		
		BuiltinMethod test33 = new BuiltinMethod("isDType", "isDType",
				"Tests if literal ?l is an instance of the datatype defined by resource ?t.", 1);
		test33.setWatermarkDescription("?l, ?t");
		
		BuiltinMethod test34 = new BuiltinMethod("notDType", "notDType",
				"Tests if literal ?l is not an instance of the datatype defined by resource ?t.", 1);
		test34.setWatermarkDescription("?l, ?t");
		
		//-------------------------------------------------------------------
		BuiltinMethod test36 = new BuiltinMethod("listContains", "listContains",
				"Passes if ?l is a list which contains the element ?x,"
				+ " both arguments must be ground, can not be used as a generator.", 1);
		test36.setWatermarkDescription("?l, ?x");
		
		BuiltinMethod test37 = new BuiltinMethod("listNotContains", "listNotContains",
				"Passes if ?l is a list which does not contain the element ?x,"
				+ " both arguments must be ground, can not be used as a generator.", 1);
		test37.setWatermarkDescription("?l, ?x");
		
		//--------------------------------------------------------------------
		BuiltinMethod test38 = new BuiltinMethod("listEntry", "listEntry",
				"Binds ?val to the ?index'th entry in the RDF list ?list."
				+ " If there is no such entry the variable will be unbound and the call will fail."
				+ " Only useable in rule bodies.", 1);
		
		test38.setWatermarkDescription("?list, ?index, ?val");
		//---------------------------------------------------------------------
		BuiltinMethod test39 = new BuiltinMethod("listLength", "listLength",
				"Binds ?len to the length of the list ?l.", 1);
		
		test39.setWatermarkDescription("?l, ?len");
		//---------------------------------------------------------------------
		BuiltinMethod test40 = new BuiltinMethod("listEqual", "listEqual",
				"listEqual tests if the two arguments are both lists and contain the same elements."
				+ " The equality test is semantic equality on literals (sameValueAs) "
				+ "but will not take into account owl:sameAs aliases.", 1);
		test40.setWatermarkDescription("?la, ?lb");
		BuiltinMethod test41 = new BuiltinMethod("listNotEqual", "listNotEqual",
				"listNotEqual tests if the two arguments are not lists and do not contain the"
				+ " same elements.", 1);
		test41.setWatermarkDescription("?la, ?lb");
		//-----------------------------------------------------------------------
		BuiltinMethod test42 = new BuiltinMethod("listMapAsObject", "listMapAsObject",
				"listMapAsObject", 1);
		test42.setWatermarkDescription("?s, ?p ?l");
		BuiltinMethod test43 = new BuiltinMethod("listMapAsSubject", "listMapAsSubject",
				"listMapAsSubject", 1);
		test43.setWatermarkDescription("?l, ?p, ?o");
		//-------------------------------------------------------------------------
		BuiltinMethod test44 = new BuiltinMethod("table", "table",
				"Declare that all goals involving property ?p"
				+ " (or all goals) should be tabled by the backward engine.", 1);
		test44.setWatermarkDescription("?p");
		BuiltinMethod test45 = new BuiltinMethod("tableAll", "tableAll",
				"Declare that all goals involving property ?p (or all goals)"
				+ " should be tabled by the backward engine.", 1);
		test45.setFlag(false);
		//-------------------------------------------------------------------------
		BuiltinMethod test46 = new BuiltinMethod("hide", "hide",
				"Declares that statements involving the predicate p should be hidden."
				+ " Queries to the model will not report such statements."
				+ " This is useful to enable non-monotonic forward rules to define flag"
				+ " predicates which are only used for inference control and do not \"pollute\""
				+ " the inference results.", 1);
		test46.setWatermarkDescription("p");
		
		list.add(test1);list.add(test2);list.add(test4);list.add(test5);list.add(test6);
		list.add(test7);list.add(test8);list.add(test9);list.add(test10);list.add(test11);list.add(test12);
		list.add(test13);list.add(test14);list.add(test15);list.add(test16);list.add(test17);list.add(test18);
		list.add(test19);list.add(test20);list.add(test21);list.add(test22);list.add(test23);list.add(test24);list.add(test25);list.add(test26);list.add(test27);
		list.add(test28);list.add(test29);list.add(test30);list.add(test31);list.add(test32);list.add(test33);
		list.add(test34);list.add(test35);list.add(test36);list.add(test37);list.add(test38);list.add(test39);
		list.add(test40);list.add(test41);list.add(test42);list.add(test43);list.add(test44);list.add(test45);list.add(test46);
		
	
		
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

	public ArrayList<Solution> getAllSolutions() {
		return allSolutions;
	}

	public void setAllSolutions(ArrayList<Solution> allSolutions) {
		this.allSolutions = allSolutions;
	}
	
	
	
}
