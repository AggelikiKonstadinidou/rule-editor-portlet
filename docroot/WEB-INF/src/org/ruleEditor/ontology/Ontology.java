package org.ruleEditor.ontology;

import java.io.FileInputStream;
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
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class Ontology implements Serializable {

	private OntModel ontologyModel;
	String ontologyFileName = "semanticFrameworkOfContentAndSolutions.owl";
	String ontologyPath = "";
	String SOURCE = "http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl";
	String NS = SOURCE + "#";
	
	private List<String> classes = Arrays.asList("PreferenceSet", "Preference",
			"Metadata", "Setting", "InstalledSolution",
			"InferredConfiguration", "Configuration", "Conflict",
			"OperatingSystem", "Solutions", "AccessibilitySolution",
			"AccessibilitySetting", "AssistiveTechnology",
			"SolutionConflict", "PreferenceSubstituteSet", "Transformation",
			"Range", "PreferenceSubstitute", "Recommendation", "Message",
			"Tutorial", "MetadataScope", "MultiUserPreferenceConflict",
			"ContextInference", "Service", "ServiceSetting");

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
	
	public ArrayList<Solution> loadSolutionsAndSettingsInstances() {

		ArrayList<Solution> allSolutions = new ArrayList<Solution>();
		// fill "allSolutions" ArrayList

		OntClass solutionsClass = ontologyModel.getOntClass(NS + "Solutions");
		List<IndividualImpl> myinstances = new ArrayList<IndividualImpl>();
		if (solutionsClass != null) {
			Iterator it = solutionsClass.listInstances();
			while (it.hasNext()) {
				IndividualImpl tmpInstance = (IndividualImpl) it.next();

				Solution tmpSolution = new Solution();
				tmpSolution.className = tmpInstance.getOntClass()
						.getLocalName();
				tmpSolution.name = tmpInstance.getLocalName();
				System.out.println(tmpSolution.className + "  "
						+ tmpSolution.name);

				tmpSolution.id = tmpInstance
						.getPropertyValue(ontologyModel.getProperty(NS, "id"))
						.asLiteral().getValue().toString();

				// get solution settings
				NodeIterator settingsInstances = tmpInstance
						.listPropertyValues(ontologyModel.getProperty(NS,
								"hasSolutionSpecificSettings"));
				while (settingsInstances.hasNext()) {
					RDFNode tmpSettingsInstanceRDFNode = (RDFNode) settingsInstances
							.next();
					// System.out.println("\t\tsettings instance: "
					// +
					// tmpSettingsInstanceRDFNode.toString());
					Individual tmpSettingsInstance = ontologyModel
							.getIndividual(tmpSettingsInstanceRDFNode
									.toString());
					StmtIterator allSettingsIntanceProperties = tmpSettingsInstance
							.listProperties();
					while (allSettingsIntanceProperties.hasNext()) {
						Statement tmpStatement = allSettingsIntanceProperties
								.next();

						Setting tmpSetting = new Setting();
						// System.out.println("\t\t\t\tproperty: " +
						// tmpStatement.toString());
						if (tmpStatement.getPredicate() != null) {
							// System.out.println("\t\t\t\t predicate: "
							// +
							// tmpStatement.getPredicate().getLocalName());
							tmpSetting.instanceName = tmpStatement
									.getPredicate().getLocalName();
						}
						if (tmpStatement.getObject() != null) {
							// System.out.println("object: " +
							// tmpStatement.getObject());
							if (tmpStatement.getObject().isLiteral()) {
								String valueStr = tmpStatement
										.getObject()
										.toString()
										.substring(
												0,
												tmpStatement.getObject()
														.toString()
														.indexOf("^^"));
								String typeStr = tmpStatement
										.getObject()
										.toString()
										.substring(
												tmpStatement.getObject()
														.toString()
														.indexOf("^^") + 2);
								// System.out.println("\t\t\t\t valueStr: "
								// + valueStr + ", type: " +
								// typeStr);

								tmpSetting.value = valueStr;
								if (typeStr
										.equals("http://www.w3.org/2001/XMLSchema#string"))
									tmpSetting.type = Setting.STRING;
								else if (typeStr
										.equals("http://www.w3.org/2001/XMLSchema#boolean"))
									tmpSetting.type = Setting.BOOLEAN;
								else if (typeStr
										.equals("http://www.w3.org/2001/XMLSchema#float")
										|| typeStr
												.equals("http://www.w3.org/2001/XMLSchema#double"))
									tmpSetting.type = Setting.FLOAT;
								else if (typeStr
										.equals("http://www.w3.org/2001/XMLSchema#int"))
									tmpSetting.type = Setting.INT;
								else if (typeStr
										.equals("http://www.w3.org/2001/XMLSchema#time"))
									tmpSetting.type = Setting.TIME;
								else if (typeStr
										.equals("http://www.w3.org/2001/XMLSchema#date"))
									tmpSetting.type = Setting.DATE;
								else if (typeStr
										.equals("http://www.w3.org/2001/XMLSchema#dateTime"))
									tmpSetting.type = Setting.DATETIME;
								else
									System.out
											.println("Exception! THIS TYPE IS NOT INCLUDED! -> "
													+ tmpStatement.getObject());

							} else // object property
							{

								if (tmpStatement.getPredicate().toString()
										.endsWith("_isMappedToRegTerm")) {
									// get mapped common term
									Individual tmpRegTermInstance = ontologyModel
											.getIndividual(tmpStatement
													.getObject().toString());
									tmpSetting.isMappedToRegTerm = tmpRegTermInstance
											.getPropertyValue(
													ontologyModel
															.getProperty(NS,
																	"RegistryTerm_hasID"))
											.asLiteral().getValue().toString();
								}
							}
						}

						if (tmpSetting.instanceName.equals("type") == false
								&& tmpSetting.instanceName.equals("adapting") == false) {
							// System.out.println("\t\t\tsetting -> "
							// +
							// tmpSetting.toString());
							// tmpSetting.process();
							tmpSolution.settings.add(tmpSetting);
						}

					}
				}

				allSolutions.add(tmpSolution);
			}
		}

		System.out.println(allSolutions.size());
		allSolutions = processSolutionSettings(allSolutions);
		return allSolutions;

	}
	
	public ArrayList<Solution> processSolutionSettings(ArrayList<Solution> allSolutions) {
		ArrayList<Solution> cloneAllSolutions = allSolutions;
		for (int i = 0; i < allSolutions.size(); i++) {
			Solution tmpSolution = allSolutions.get(i);
			// System.out.println("SOLUTION name: " + tmpSolution.name +
			// ", id: " + tmpSolution.id);

			ArrayList<Setting> allSettings = tmpSolution.settings;
			for (int j = 0; j < allSettings.size(); j++) {
				Setting tmpSetting = allSettings.get(j);

				//
				if (!tmpSolution.name.contains("Service_"))	
				if (tmpSetting.instanceName.endsWith("_hasID")
						|| tmpSetting.instanceName.endsWith("_hasName")
						|| tmpSetting.instanceName.endsWith("_hasDescription")
						|| tmpSetting.instanceName.endsWith("_hasValueSpace")
						|| tmpSetting.instanceName.endsWith("_hasConstraints")
						|| tmpSetting.instanceName
								.endsWith("_isMappedToRegTerm")
						|| tmpSetting.instanceName.endsWith("_isExactMatching")
						|| tmpSetting.instanceName
								.endsWith("_hasCommentsForMapping")) {
					tmpSetting.ignoreSetting = true;

					String originalSettingName = tmpSetting.instanceName
							.substring(0,
									tmpSetting.instanceName.lastIndexOf("_"));
					Setting originalSetting = getSetting(tmpSolution.name,
							originalSettingName,cloneAllSolutions);

					if (originalSetting != null) {
						if (tmpSetting.instanceName.endsWith("_hasID"))
							originalSetting.hasID = tmpSetting.value;
						if (tmpSetting.instanceName.endsWith("_hasName"))
							originalSetting.hasName = tmpSetting.value;
						if (tmpSetting.instanceName.endsWith("_hasDescription"))
							originalSetting.hasDescription = tmpSetting.value;
						if (tmpSetting.instanceName.endsWith("_hasValueSpace"))
							originalSetting.hasValueSpace = tmpSetting.value;
						if (tmpSetting.instanceName.endsWith("_hasConstraints"))
							originalSetting.hasConstraints = tmpSetting.value;
						if (tmpSetting.instanceName
								.endsWith("_isMappedToRegTerm"))
							originalSetting.isMappedToRegTerm = tmpSetting.isMappedToRegTerm;
						if (tmpSetting.instanceName
								.endsWith("_isExactMatching"))
							originalSetting.isExactMatching = Boolean
									.parseBoolean(tmpSetting.value);
						if (tmpSetting.instanceName
								.endsWith("_hasCommentsForMapping"))
							originalSetting.hasCommentsForMapping = tmpSetting.value;
					}
				}
				// System.out.println("\t" + tmpSetting.toString());
			}
		}
		
		return allSolutions;
	}

	public Setting getSetting(String solutionName, String settingName,
			ArrayList<Solution> allSolutions) {
		for (int i = 0; i < allSolutions.size(); i++) {
			Solution tmpSolution = allSolutions.get(i);
			if (tmpSolution.name.equals(solutionName)) {
				ArrayList<Setting> allSettings = tmpSolution.settings;
				for (int j = 0; j < allSettings.size(); j++) {
					Setting tmpSetting = allSettings.get(j);
					if (tmpSetting.instanceName.equals(settingName))
						return tmpSetting;
				}
			}
		}
		return null;
	}

	public List<ArrayList<OntologyClass>> getClassesStructured() {

		List<ArrayList<OntologyClass>> list = new ArrayList<ArrayList<OntologyClass>>();
		ArrayList<OntologyClass> tempList = new ArrayList<OntologyClass>();

		for (String s : classes) {

			OntClass essaClasse = ontologyModel.getOntClass(NS + s);

			String vClasse = essaClasse.getLocalName().toString();
			List<OntologyClass> l = new ArrayList<OntologyClass>();

			// // loads all instances of the mother class
			// List<IndividualImpl> instances = getAllIndividualsForClass(s);
			// // get children classes
			if (!s.equalsIgnoreCase("Settings"))
				if (essaClasse.hasSubClass()) {
					for (Iterator i = essaClasse.listSubClasses(true); i
							.hasNext();) {
						OntClass c = (OntClass) i.next();
						// l.add(getConceptChildrenStructured(c,
						// vClasse,instances));
						l.add(getConceptChildrenStructured(c, vClasse));
					}
				}

			// create a new class
			OntologyClass on = new OntologyClass();
			on.setClassName(vClasse);
			on.setChildren(l);

			// fillClassData(on, vClasse, instances);
			fillClassData(on, vClasse);

			tempList.add(on);
			list.add(tempList);
			tempList = new ArrayList<OntologyClass>();
		}

		return list;
	}

	public void fillClassData(OntologyClass myClass, String motherClassName) {

		ArrayList<DataProperty> dataProperties = new ArrayList<DataProperty>();
		ArrayList<ObjectProperty> objectProperties = new ArrayList<ObjectProperty>();
		ArrayList<Instance> tempInstances = new ArrayList<Instance>();

		String className = myClass.getClassName();

		List<String> dataPropertiesNames = setDataPropertiesToClass(motherClassName);
		List<String> objectPropertiesNames = setObjectPropertiesToClass(motherClassName);

		OntClass cl = ontologyModel.getOntClass(NS + myClass.getClassName());
		ArrayList<IndividualImpl> instances = (ArrayList<IndividualImpl>) cl
				.listInstances(true).toList();

		if (!motherClassName.equalsIgnoreCase("Settings")
				&& motherClassName.equalsIgnoreCase("Service")
				&& !motherClassName.equalsIgnoreCase("Solutions")
				&& !myClass.getClassName().equalsIgnoreCase("settings"))
			for (int i = 0; i < instances.size(); i++) {
				IndividualImpl tmpInstance = instances.get(i);
				String instanceName = tmpInstance.getURI().replace(NS, "");
				if (instanceName.contains("_"))
					instanceName = instanceName.split("_")[0];

				Instance inst = new Instance();
				inst.setClassName(className);
				inst.setInstanceName(Utils.splitCamelCase(instanceName));
				inst.setId(tmpInstance.getURI());
				tempInstances.add(inst);

			}


		// prepare properties
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
			String motherClassName) {
		List<OntologyClass> l = new ArrayList<OntologyClass>();
		if (c.hasSubClass()) {
			for (Iterator i = c.listSubClasses(true); i.hasNext();) {
				OntClass cc = (OntClass) i.next();
				l.add(getConceptChildrenStructured(cc, motherClassName));
			}
		}
		OntologyClass on = new OntologyClass();
		on.setClassName(c.getLocalName());
		on.setChildren(l);

		//fillClassData(on, motherClassName, instances);
		fillClassData(on, motherClassName);

		return on;
	}

	public List<String> setDataPropertiesToClass(String name) {

		List<String> solutions = Arrays.asList("name", "id");
		// properties from ontology
		// Arrays.asList("hasSolutionName", "id",
		// "hasSolutionDescription", "hasSolutionVersion",
		// "hasStartCommand", "hasStopCommand", "hasCapabilities",
		// "hasCapabilitiesTransformations", "hasContraints");
		List<String> serviceSetting  = Arrays.asList("serviceName","priority");
		List<String> service = Arrays.asList("id","priority");
		List<String> contextInference = Arrays.asList("id","condition","affects");
		List<String> multiUserPreferenceConflict = Arrays.asList("id","affects","resolution");
		List<String> metadataScope = Arrays.asList("id","name","addition");
		List<String> tutorial = Arrays.asList("language","link");
		List<String> preferenceSubstituteSet = Arrays.asList("id","scope");
		List<String> solutionConflict = Arrays.asList("refersTo");
		List<String> installedSolution = Arrays.asList("name", "id");
		List<String> preference = Arrays.asList("id", "name", "type", "value","status");
		List<String> metadata = Arrays.asList("value", "messageType", "type","refersTo"
				,"fromServiceName","fromVariableName","toServiceName","toVariableName");
		List<String> setting = Arrays.asList("name", "id", "value","type","default");
		List<String> inferredConfiguration = Arrays.asList("id", "name");
		List<String> configuration = Arrays.asList("id", "name", "isActive",
				"solPreferred");
		List<String> conflict = Arrays.asList("name", "class", "activated",
				"deactivated","affects");// id
		List<String> conflictResolution = Arrays.asList("id", "name");
		List<String> preferenceSet = Arrays.asList("id", "name","user","prefList","hasCondition");

		List<String> operatingSystem = Arrays.asList("name", "version");
		List<String> environment = Arrays.asList("hasEnvironmentName");
		List<String> devices = Arrays.asList("hasDeviceName",
				"hasDeviceDescription");
		List<String> platforms = Arrays.asList("hasPlatformName",
				"hasPlatformDescription", "hasPlatformVersion",
				"hasPlatformSubType", "hasPlatfomType");
		// use the same list for accessibilitySolution,accessibilitySetting and
		// assistiveTechnology
		// as the three of them have tha same property name
		List<String> accessibilitySolution = Arrays.asList("name");
		List<String> assistiveTechnology = Arrays.asList("name","ranking","refersTo");
		List<String> range = Arrays.asList("min","max");
		List<String> recommendation = Arrays.asList("id","value");
		List<String> preferenceSubstitute = Arrays.asList("rating");
		List<String> message = Arrays.asList("id","messageType","language","text","learnMore");
		

		if (name.equalsIgnoreCase("Solutions"))
			return solutions;
		else if(name.equalsIgnoreCase("serviceSetting"))
			return serviceSetting;
		else if(name.equalsIgnoreCase("service"))
			return service;
		else if(name.equalsIgnoreCase("contextInference"))
			return contextInference;
		else if(name.equalsIgnoreCase("multiUserPreferenceConflict"))
			return multiUserPreferenceConflict;
		else if(name.equalsIgnoreCase("MetadataScope"))
			return metadataScope;
		else if(name.equalsIgnoreCase("Tutorial"))
			return tutorial;
		else if(name.equalsIgnoreCase("Message"))
			return message;
		else if(name.equalsIgnoreCase("PreferenceSubstitute"))
			return preferenceSubstitute;
		else if(name.equalsIgnoreCase("recommendation"))
			return recommendation;
		else if(name.equalsIgnoreCase("range"))
			return range;
		else if(name.equalsIgnoreCase("preferenceSubstituteSet"))
			return preferenceSubstituteSet;
		else if(name.equalsIgnoreCase("solutionConflict"))
			return solutionConflict;
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
			return environment;
		else if (name.equalsIgnoreCase("PreferenceSet"))
			return preferenceSet;
		else if (name.equalsIgnoreCase("accessibilitySolution")
				|| name.equalsIgnoreCase("accessibilitySetting"))
			return accessibilitySolution;
		else if(name.equalsIgnoreCase("assistiveTechnology"))
			return assistiveTechnology;
		else
			return new ArrayList<String>();

	}

	public List<String> setObjectPropertiesToClass(String name) {
		List<String> solutions = Arrays.asList("settings_Settings","class_AssistiveTechnology",
				"tutorial_Tutorial");// runsOnDevice_Devices,runsOnPlatform_Platforms
		List<String> setting = Arrays.asList("refersTo_?"); // TODO refers to
		List<String> metadata = Arrays.asList("messages_Message","scope_MetadataScope");
		List<String> conflict = Arrays.asList("refersTo_Configuration");// hasResolution_?,
																	// TODO
																	// refersTo
																	// installedSolution, Solution, Configuration?
		List<String> preferenceSet = Arrays.asList("hasMetadata_Metadata",
				"hasPrefs_Preference");
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
		List<String> configuration = Arrays.asList("refersTo_Solutions", // TODO
																			// refersTo
																			// se
																			// installedSolution
				"settings_Setting", "hasConflict_Conflict","refersTo_Recommendation");
		List<String> preference = Arrays.asList("setting_Setting");
		List<String> preferenceSubstituteSet = Arrays.asList("transform_Transformation");
		List<String> Transformation = Arrays.asList("valueRange_Range","subsitute_Substitute");
		List<String> PreferenceSubstitute = Arrays.asList("recommend_Recommendation");
		List<String> recommendation = Arrays.asList("refersTo_PreferenceSubstituteSet",
				"refersTo_PreferenceSubstitute");
		List<String> message = Arrays.asList("messages_Message");
		List<String> service = Arrays.asList("settings_Setting");
		List<String> serviceSetting = Arrays.asList("serviceInput_Setting");

		if (name.equalsIgnoreCase("Solutions"))
			return solutions;
		else if(name.equalsIgnoreCase("serviceSetting"))
			return serviceSetting;
		else if(name.equalsIgnoreCase("Service"))
			return service;
		else if (name.equalsIgnoreCase("message"))
			return message;
		else if (name.equalsIgnoreCase("recommendation"))
			return recommendation;
		else if(name.equalsIgnoreCase("PreferenceSubstitute"))
			return PreferenceSubstitute;
		else if(name.equalsIgnoreCase("transformation"))
			return Transformation;
		else if(name.equalsIgnoreCase("preferenceSubstituteSet"))
			return preferenceSubstituteSet;
		else if (name.equalsIgnoreCase("Setting"))
			return setting;
		else if (name.equalsIgnoreCase("Conflict"))
			return conflict;
		else if (name.equalsIgnoreCase("PreferenceSet"))
			return preferenceSet;
		else if (name.equalsIgnoreCase("InferredConfiguration"))
			return inferredConfiguration;
		else if (name.equals("Devices"))
			return devices;
		else if (name.equalsIgnoreCase("Platforms"))
			return platforms;
		else if (name.equalsIgnoreCase("Metadata"))
			return metadata;
		else if (name.equalsIgnoreCase("InstalledSolution"))
			return installedSolution;
		else if (name.equalsIgnoreCase("Configuration"))
			return configuration;
		else if (name.equalsIgnoreCase("preference"))
			return preference;
		else
			return new ArrayList<String>();

	}

}
