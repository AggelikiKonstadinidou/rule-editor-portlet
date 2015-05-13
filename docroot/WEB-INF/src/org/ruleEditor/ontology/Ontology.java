package org.ruleEditor.ontology;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.ruleEditor.ontology.OntologyProperty.DataProperty;
import org.ruleEditor.ontology.OntologyProperty.ObjectProperty;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.OntTools;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class Ontology implements Serializable {

	private OntModel ontologyModel;
	String ontologyFileName = "semanticFrameworkOfContentAndSolutions.owl";
	String ontologyPath = "";
	String SOURCE = "http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl";
	String NS = SOURCE + "#";
	private List<String> classes = Arrays.asList("Solutions",
			"InstalledSolution", "Preference", "Metadata", "Setting",
			"InferredConfiguration", "Configuration", "Conflict",
			"ConflictResolution", "PreferenceSet", "OperatingSystem",
			"MultipleATConflict", "MultipleSolutionConflict", "Environment",
			"Devices", "Platforms");

	// "AssistiveTechnology","AccessibilitySolutions"

	public void loadOntology() {

		FacesContext context = FacesContext.getCurrentInstance();

		String path = ((ServletContext) context.getExternalContext()
				.getContext()).getRealPath(ontologyFileName);
		System.out.println(path);
		try {
			ontologyPath = path;
			ontologyModel = ModelFactory.createOntologyModel();
			InputStream in = FileManager.get().open(ontologyPath);
			if (in == null) {
				throw new IllegalArgumentException("File: " + ontologyPath
						+ " not found");
			}

			ontologyModel.read(in, "");

		} catch (Exception e) {
			System.out.println("myexception");
			e.printStackTrace();
		}

	}

	public List<ArrayList<OntologyClass>> getClassesStructured() {

		List<ArrayList<OntologyClass>> list = new ArrayList<ArrayList<OntologyClass>>();
		ArrayList<OntologyClass> tempList = new ArrayList<OntologyClass>();

		for (String s : classes) {

			OntClass essaClasse = ontologyModel.getOntClass(NS + s);

			String vClasse = essaClasse.getLocalName().toString();
			List<OntologyClass> l = new ArrayList<OntologyClass>();

			// get children classes
			if (essaClasse.hasSubClass()) {
				for (Iterator i = essaClasse.listSubClasses(true); i.hasNext();) {
					OntClass c = (OntClass) i.next();
					l.add(getConceptChildrenStructured(c));
				}
			}

			// create a new class
			OntologyClass on = new OntologyClass();
			on.setClassName(vClasse);
			on.setChildren(l);

			// TODO
			fillClassData(on);

			tempList.add(on);
			list.add(tempList);
			tempList = new ArrayList<OntologyClass>();
		}

		return list;
	}

	public void fillClassData(OntologyClass myClass) {

		ArrayList<DataProperty> dataProperties = new ArrayList<DataProperty>();
		ArrayList<ObjectProperty> objectProperties = new ArrayList<ObjectProperty>();
		ArrayList<String> instances = new ArrayList<String>();

		String className = myClass.getClassName();

		List<String> dataPropertiesNames = setDataPropertiesToClass(className);
		List<String> objectPropertiesNames = setObjectPropertiesToClass(className);

		OntClass cl = ontologyModel.getOntClass(NS + myClass.getClassName());
		// get instances
		// TODO load instances and their properties
		List<IndividualImpl> myinstances = new ArrayList<IndividualImpl>();
		if (cl != null) {
			Iterator it = cl.listInstances();
			while (it.hasNext()) {
				IndividualImpl in = (IndividualImpl) it.next();
				instances.add(in.getURI());
				myinstances.add(in);
			}
		}

		// prepare properties
		// TODO load properties
		// data properties
		OntologyProperty prop = new OntologyProperty("", "");
		OntologyProperty.DataProperty dataProp = null;
		for (String s : dataPropertiesNames) {
			dataProp = prop.new DataProperty(s, className);
			dataProp.setOntologyURI(NS+className+"_"+s);
			dataProp.setDataRange("string");
			dataProp.setValue("empty");
			dataProperties.add(dataProp);
		}

		// object properties
		OntologyProperty.ObjectProperty objectProp = null;
		for (String s : objectPropertiesNames) {
			objectProp = prop.new ObjectProperty(s, className);
			objectProp.setOntologyURI(NS+className+"_"+s);
			objectProp.setRangeOfClasses(new ArrayList<String>());
			objectProperties.add(objectProp);
		}
		
		myClass.setDataProperties(dataProperties);
		myClass.setObjectProperties(objectProperties);
		myClass.setInstances(instances);
	}

	public List<OntologyClass> getSolutionsClassesStructured() {
		List<OntologyClass> list = new ArrayList<OntologyClass>();

		OntClass essaClasse = ontologyModel.getOntClass(NS + "Solutions");

		String vClasse = essaClasse.getLocalName().toString();
		List<OntologyClass> l = new ArrayList<OntologyClass>();
		if (essaClasse.hasSubClass()) {
			for (Iterator i = essaClasse.listSubClasses(true); i.hasNext();) {
				OntClass c = (OntClass) i.next();
				l.add(getConceptChildrenStructured(c));
			}
		}
		OntologyClass on = new OntologyClass();
		on.setClassName(vClasse);
		on.setChildren(l);
		list.add(on);

		return list;
	}

	public OntologyClass getConceptChildrenStructured(OntClass c) {
		List<OntologyClass> l = new ArrayList<OntologyClass>();
		if (c.hasSubClass()) {
			for (Iterator i = c.listSubClasses(true); i.hasNext();) {
				OntClass cc = (OntClass) i.next();
				l.add(getConceptChildrenStructured(cc));
			}
		}
		OntologyClass on = new OntologyClass();
		on.setClassName(c.getLocalName());
		on.setChildren(l);

		return on;
	}

	public List<String> setDataPropertiesToClass(String name) {
		List<String> solutions = Arrays.asList("name", "Id", "description");
		List<String> installedSolution = Arrays.asList("name", "id");
		List<String> preference = Arrays.asList("id", "name", "type", "value");
		List<String> metadata = Arrays.asList("value", "type");
		List<String> setting = Arrays.asList("name", "id", "description",
				"refersTo", "value");
		List<String> inferredConfiguration = Arrays.asList("id", "name");
		List<String> configuration = Arrays.asList("id", "name", "isActive",
				"solutionIsPreferred");
		List<String> conflict = Arrays.asList("id", "name");
		List<String> conflictResolution = Arrays.asList("id", "name");
		List<String> preferenceSet = new ArrayList<String>();

		List<String> operatingSystem = Arrays.asList("name", "version");
		List<String> multipleATConflict = new ArrayList<String>();
		List<String> multipleSolutionConflict = new ArrayList<String>();
		List<String> environment = new ArrayList<String>();
		List<String> devices = Arrays.asList("name", "description");
		List<String> platforms = Arrays.asList("name", "description",
				"version", "subType", "type");

		if (name.equals("Solutions"))
			return solutions;
		else if (name.equals("InstalledSolution"))
			return installedSolution;
		else if (name.equals("Preference"))
			return preference;
		else if (name.equals("Metadata"))
			return metadata;
		else if (name.equals("Setting"))
			return setting;
		else if (name.equals("InferredConfiguration"))
			return inferredConfiguration;
		else if (name.equals("Configuration"))
			return configuration;
		else if (name.equals("Conflict") || name.equals("ConflictResolution"))
			return conflict;
		else if (name.equals("OperatingSystem"))
			return operatingSystem;
		else if (name.equals("Devices"))
			return devices;
		else if (name.equals("platforms"))
			return platforms;
		else
			return new ArrayList<String>();

	}

	public List<String> setObjectPropertiesToClass(String name) {
		List<String> solutions = Arrays.asList("refersTo", "InstalledSolution",
				"Preference", "Metadata", "Setting", "InferredConfiguration",
				"Configuration", "Conflict", "ConflictResolution",
				"PreferenceSet", "OperatingSystem", "MultipleATConflict",
				"MultipleSolutionConflict", "Environment", "Devices",
				"Platforms");

		List<String> setting = Arrays.asList("Solutions", "InstalledSolution",
				"Preference", "Metadata", "Setting", "InferredConfiguration",
				"Configuration", "Conflict", "ConflictResolution",
				"PreferenceSet", "OperatingSystem", "MultipleATConflict",
				"MultipleSolutionConflict", "Environment", "Devices",
				"Platforms");
		List<String> metadata = Arrays.asList("scope");
		List<String> conflict = Arrays.asList("hasResolution");
		List<String> preferenceSet = Arrays.asList("hasMetadata",
				"hasPreference");
		List<String> inferredConfiguration = Arrays.asList("hasMetadata",
				"hasPreference", "refersTo");
		List<String> devices = Arrays.asList("specific Setting",
				"supporting Device");
		List<String> platforms = Arrays.asList("specific Setting",
				"supporting Platform");
		List<String> installedSolution = Arrays.asList("settings");

		if (name.equals("Solutions"))
			return solutions;
		else if (name.equals("Setting"))
			return setting;
		else if (name.equals("Conflict"))
			return conflict;
		else if (name.equals("PreferenceSet"))
			return preferenceSet;
		else if (name.equals("InferredConfiguration"))
			return inferredConfiguration;
		else if (name.equals("Devices"))
			return devices;
		else if (name.equals("Platforms"))
			return platforms;
		else if (name.equals("Metadata"))
			return metadata;
		else if (name.equals("InstalledSolution"))
			return installedSolution;
		else
			return new ArrayList<String>();

	}

}
