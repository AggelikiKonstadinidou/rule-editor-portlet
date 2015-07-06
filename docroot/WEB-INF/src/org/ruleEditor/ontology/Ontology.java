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
import org.ruleEditor.utils.Utils;

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

			// loads all instances of the mother class
			List<IndividualImpl> instances = getAllIndividualsForClass(s);
			// get children classes
			if (!s.equalsIgnoreCase("Settings"))
				if (essaClasse.hasSubClass()) {
					for (Iterator i = essaClasse.listSubClasses(true); i
							.hasNext();) {
						OntClass c = (OntClass) i.next();
						l.add(getConceptChildrenStructured(c, vClasse,
								instances));
					}
				}

			// create a new class
			OntologyClass on = new OntologyClass();
			on.setClassName(vClasse);
			on.setChildren(l);

			// TODO
			fillClassData(on, vClasse, instances);

			tempList.add(on);
			list.add(tempList);
			tempList = new ArrayList<OntologyClass>();
		}

		return list;
	}

	public void fillClassData(OntologyClass myClass, String motherClassName,
			List<IndividualImpl> instances) {

		ArrayList<DataProperty> dataProperties = new ArrayList<DataProperty>();
		ArrayList<ObjectProperty> objectProperties = new ArrayList<ObjectProperty>();
		ArrayList<Instance> tempInstances = new ArrayList<Instance>();

		String className = myClass.getClassName();

		List<String> dataPropertiesNames = setDataPropertiesToClass(motherClassName);
		List<String> objectPropertiesNames = setObjectPropertiesToClass(motherClassName);

		OntClass cl = ontologyModel.getOntClass(NS + myClass.getClassName());

		// get instances
		for (IndividualImpl in : instances) {
			String categoryName = in.getOntClass().toString().split("#")[1];
			if (categoryName.equalsIgnoreCase(className)) {
				String instanceName = in.getURI().replace(NS, "");
				if (instanceName.contains("_"))
					instanceName = instanceName.split("_")[0];
				
				//TODO load the id for every instance
				Instance inst = new Instance(className,
						Utils.splitCamelCase(instanceName), in.getURI());
				tempInstances.add(inst);
			}
		}

		// prepare properties
		// TODO load properties
		// data properties
		OntologyProperty prop = new OntologyProperty("", "");
		OntologyProperty.DataProperty dataProp = null;
		for (String s : dataPropertiesNames) {

			dataProp = prop.new DataProperty(s, className);
			dataProp.setOntologyURI(NS + className + "_" + s);
			dataProp.setDataRange("string");

			if (s.equalsIgnoreCase("isActive")
					|| s.equalsIgnoreCase("solPreferred"))
				dataProp.setDataRange("boolean");

			dataProp.setValue("empty");
			dataProperties.add(dataProp);
		}

		// object properties
		OntologyProperty.ObjectProperty objectProp = null;
		for (String s : objectPropertiesNames) {

			String objName = s.split("_")[0];
			String range = s.split("_")[1];
			objectProp = prop.new ObjectProperty(objName, className);
			objectProp.setOntologyURI(NS + className + "_" + objName);
			objectProp.setRangeOfClasses(new ArrayList<String>());
			objectProp.getRangeOfClasses().add(range);
			objectProperties.add(objectProp);
		}

		myClass.setDataProperties(dataProperties);
		myClass.setObjectProperties(objectProperties);
		myClass.setInstances(tempInstances);
	}

	public List<IndividualImpl> getAllIndividualsForClass(String className) {

		OntClass cl = ontologyModel.getOntClass(NS + className);
		List<IndividualImpl> myinstances = new ArrayList<IndividualImpl>();
		if (cl != null) {
			Iterator it = cl.listInstances();
			while (it.hasNext()) {
				IndividualImpl in = (IndividualImpl) it.next();
				myinstances.add(in);
			}
		}

		return myinstances;

	}

	public OntologyClass getConceptChildrenStructured(OntClass c,
			String motherClassName, List<IndividualImpl> instances) {
		List<OntologyClass> l = new ArrayList<OntologyClass>();
		if (c.hasSubClass()) {
			for (Iterator i = c.listSubClasses(true); i.hasNext();) {
				OntClass cc = (OntClass) i.next();
				l.add(getConceptChildrenStructured(cc, motherClassName,
						instances));
			}
		}
		OntologyClass on = new OntologyClass();
		on.setClassName(c.getLocalName());
		on.setChildren(l);

		fillClassData(on, motherClassName, instances);

		return on;
	}

	public List<String> setDataPropertiesToClass(String name) {
		List<String> solutions = Arrays.asList("hasSolutionName", "id",
				"hasSolutionDescription", "hasSolutionVersion",
				"hasStartCommand", "hasStopCommand", "hasCapabilities",
				"hasCapabilitiesTransformations", "hasContraints");
		List<String> installedSolution = Arrays.asList("name", "id");
		List<String> preference = Arrays.asList("id", "name", "type", "value");
		List<String> metadata = Arrays.asList("value", "type","scope");
		List<String> setting = Arrays.asList("name", "id", "description",
				"refersTo", "value");
		List<String> inferredConfiguration = Arrays.asList("id", "name");
		List<String> configuration = Arrays.asList("id", "name", "isActive",
				"solPreferred","refersTo");
		List<String> conflict = Arrays.asList("id", "name", "class");
		List<String> conflictResolution = Arrays.asList("id", "name");
		List<String> preferenceSet = new ArrayList<String>();

		List<String> operatingSystem = Arrays.asList("name", "version");
		List<String> multipleATConflict = new ArrayList<String>();
		List<String> multipleSolutionConflict = new ArrayList<String>();
		List<String> environment = Arrays.asList("hasEnvironmentName");
		List<String> devices = Arrays.asList("hasDeviceName",
				"hasDeviceDescription");
		List<String> platforms = Arrays.asList("hasPlatformName",
				"hasPlatformDescription", "hasPlatformVersion",
				"hasPlatformSubType", "hasPlatfomType");

		if (name.equalsIgnoreCase("Solutions"))
			return solutions;
		else if (name.equalsIgnoreCase("InstalledSolution"))
			return installedSolution;
		else if (name.equalsIgnoreCase("Preference"))
			return preference;
		else if (name.equalsIgnoreCase("Metadata"))
			return metadata;
		else if (name.equalsIgnoreCase("Setting"))
			return setting;
		else if (name.equalsIgnoreCase("InferredConfiguration"))
			return inferredConfiguration;
		else if (name.equalsIgnoreCase("Configuration"))
			return configuration;
		else if (name.equalsIgnoreCase("Conflict"))
			return conflict;
		else if (name.equalsIgnoreCase("ConflictResolution"))
			return conflictResolution;
		else if (name.equalsIgnoreCase("OperatingSystem"))
			return operatingSystem;
		else if (name.equalsIgnoreCase("Devices"))
			return devices;
		else if (name.equalsIgnoreCase("platforms"))
			return platforms;
		else if (name.equalsIgnoreCase("environment"))
			return platforms;
		else
			return new ArrayList<String>();

	}

	public List<String> setObjectPropertiesToClass(String name) {
		List<String> solutions = Arrays.asList("settings_Settings",
				"runsOnDevice_Devices", "runsOnPlatform_Platforms");
		List<String> setting = new ArrayList<String>();
		List<String> metadata = new ArrayList<String>();
		List<String> conflict = Arrays.asList("hasResolution_?",
				"refersTo_Solutions");
		List<String> preferenceSet = Arrays.asList("hasMetadata_Metadata",
				"hasPreference_Preference");
		List<String> inferredConfiguration = Arrays.asList(
				"hasMetadata_Metadata", "hasPrefs_Preference",
				"refersTo_Configuration");
		List<String> devices = Arrays.asList("hasDeviceVendor_Vendor",
				"hasDeviceSpecificSetting_Setting",
				"isSupportingDeviceOf_Solutions");
		List<String> platforms = Arrays.asList("hasPlatformVendor_Vendor",
				"hasPlatformSpecificSetting_Setting",
				"platformSupports_Solutions");
		List<String> installedSolution = Arrays.asList("settings_Setting");
		List<String> configuration = Arrays.asList("refersTo_Solutions",
				"settings_Setting");

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
		else if (name.equals("Configuration"))
			return configuration;
		else
			return new ArrayList<String>();

	}

}
