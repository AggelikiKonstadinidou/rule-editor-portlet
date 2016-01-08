package org.ruleEditor.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ReorderEvent;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.utils.FileDownloadController;
import org.ruleEditor.utils.Rule;
import org.ruleEditor.utils.RuleCreationUtilities;
import org.ruleEditor.utils.Utils;

@ManagedBean(name = "configurateOrderBean")
@SessionScoped
public class ConfigurateOrderBean {

	private Main main;
	private ArrayList<String> ruleSets;
	private String propertiesFileName;
	private InputStream inputStream;
	private InputStream rulesInputStream;
	private String inputString;
	private String ruleFileName;
	private ArrayList<Rule> objectRules;

	public ConfigurateOrderBean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
	}

	public void init() {

		ruleSets = new ArrayList<String>();
		objectRules = new ArrayList<Rule>();
		propertiesFileName = "";
		inputStream = null;
		ruleFileName = "";
		inputString = "";

	}

	public void onRowReorder(ReorderEvent event) {

	}

	public void clearRuleSet() {
		ruleSets = new ArrayList<String>();
	}

	public void clearRules() {
		objectRules = new ArrayList<Rule>();
	}

	public void exportRuleFile() throws IOException {

		if (objectRules.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please load a rule file", ""));

			return;
		}

		String allRuleString = Utils.prefix_c4a + "\n" + Utils.prefix_rdfs
				+ "\n\n";
		for (Rule temp : objectRules) {
			allRuleString = allRuleString.concat(temp.getBody()) + "\n\n";
		}

		FileDownloadController.writeGsonAndExportFile(ruleFileName,
				allRuleString);
	}

	public void exportRuleFileToServer() throws IOException {

		if (objectRules.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please load a rule file", ""));

			return;
		}

		String allRuleString = Utils.prefix_c4a + "\n" + Utils.prefix_rdfs
				+ "\n\n";
		for (Rule temp : objectRules) {
			allRuleString = allRuleString.concat(temp.getBody()) + "\n\n";
		}

		FileDownloadController.writeGsonForRuleAndExportToServer(
				RuleCreationUtilities.WORKING_RULES_DIRECTORY_PATH + "\\"
						+ ruleFileName + ".rules", allRuleString);

	}

	public void exportPropertyFile() throws IOException {

		if (ruleSets.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please load a configuration file", ""));

			return;
		}

		String s = Utils.createOrderOfFilesForConfigFile(inputString, ruleSets);

		FileDownloadController.writeGsonAndExportFile(propertiesFileName, s);
	}

	public void exportPropertyFileToServer() throws IOException {

		if (ruleSets.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please load a configuration file", ""));

			return;
		}

		String s = Utils.createOrderOfFilesForConfigFile(inputString, ruleSets);

		FileDownloadController.simpleWriteGsonAndExportToServer(
				RuleCreationUtilities.WORKING_CONFIGFILE_PATH, s);
	}

	public void onPropertiesFileUpload(FileUploadEvent event)
			throws IOException {

		propertiesFileName = event.getFile().getFileName();
		inputStream = event.getFile().getInputstream();
		ruleSets = Utils.getRuleArray(inputStream);
		inputString = ruleSets.get(ruleSets.size() - 1);
		ruleSets.remove(ruleSets.size() - 1);

	}

	public void onRuleFileUpload(FileUploadEvent event) throws IOException {
		this.ruleFileName = event.getFile().getFileName().replace(".rules", "");

		this.rulesInputStream = event.getFile().getInputstream();

		System.out.println(ruleFileName);

		objectRules = Utils.getRulesFromFile(rulesInputStream);
	}

	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	public ArrayList<String> getRuleSets() {
		return ruleSets;
	}

	public void setRuleSets(ArrayList<String> ruleSets) {
		this.ruleSets = ruleSets;
	}

	public String getRuleFileName() {
		return ruleFileName;
	}

	public void setRuleFileName(String ruleFileName) {
		this.ruleFileName = ruleFileName;
	}

	public ArrayList<Rule> getObjectRules() {
		return objectRules;
	}

	public void setObjectRules(ArrayList<Rule> objectRules) {
		this.objectRules = objectRules;
	}

}
