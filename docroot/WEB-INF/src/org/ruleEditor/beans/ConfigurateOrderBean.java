package org.ruleEditor.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ReorderEvent;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.utils.FileDownloadController;
import org.ruleEditor.utils.Rule;
import org.ruleEditor.utils.RuleFile;
import org.ruleEditor.utils.Utils;

@ManagedBean(name = "configurateOrderBean")
@SessionScoped
public class ConfigurateOrderBean {

	private Main main;
	private ArrayList<String> rules;
	private String propertiesFileName;
	private InputStream inputStream;
	private InputStream rulesInputStream;
	private String inputString;
	private String ruleFileName;
	private RuleFile selectedFile;
	private ArrayList<Rule> objectRules = new ArrayList<Rule>();
	private ArrayList<RuleFile> listOfFileRules = new ArrayList<RuleFile>();

	public ConfigurateOrderBean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
	}

	public void init() {
		
		rules = new ArrayList<String>();
		listOfFileRules = new ArrayList<RuleFile>();
		propertiesFileName = "";
		inputStream = null;
		ruleFileName = "";
		inputString = "";
		selectedFile = new RuleFile("", new ArrayList<Rule>());

	}
	
	public void onRowReorder(ReorderEvent event) {
		
	}
	
	public void saveChanges() throws IOException {
		String newRuleString = "rules=";
		for (String temp : rules) {
			newRuleString = newRuleString.concat("testData/rules/" + temp
					+ ".rules;");
		}

		String[] splitted = inputString.split("\n");
		int startPosOfRules = -1;

		for (int i = 0; i < splitted.length; i++) {
			if (splitted[i].contains("rules=")) {
				startPosOfRules = i;
				break;
			}
		}

		int j = -1;
		for (int i = startPosOfRules + 1; i < splitted.length; i++) {
			if (splitted[i].contains("queries="))
				break;
			else if (splitted[i].contains("testData/rules/"))
				j++;
		}

		if(j!=-1)
		for (int i = startPosOfRules; i < j; i++) {
			splitted[i] = "";
		}

		splitted[startPosOfRules] = newRuleString;

		String s = "";
		for (int i = 0; i < splitted.length; i++) {
			s = s.concat(splitted[i] + "\n");
		}

		FileDownloadController.writeGsonAndExportFile(propertiesFileName, s);
	}
	
	public void onFileUpload(FileUploadEvent event) throws IOException{
		
		propertiesFileName = event.getFile().getFileName();
		inputStream = event.getFile().getInputstream();
		rules = Utils.getRuleArray(inputStream);
		inputString = rules.get(rules.size()-1);
		rules.remove(rules.size()-1);
		for(String s : rules){
			listOfFileRules.add(new RuleFile(s, new ArrayList<Rule>()));
		}
		
	}
	
	public void onRuleFileUpload(FileUploadEvent event) throws IOException{
		this.ruleFileName = event.getFile().getFileName();

		this.rulesInputStream = event.getFile().getInputstream();

		System.out.println(ruleFileName);
		
		objectRules = Utils.getRulesFromFile(rulesInputStream);
		
		for(RuleFile file : listOfFileRules){
			if(file.getFileName().equalsIgnoreCase(ruleFileName)){
				file.setRules(objectRules);
				break;
			}
		}
	}

	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	public ArrayList<String> getRules() {
		return rules;
	}

	public void setRules(ArrayList<String> rules) {
		this.rules = rules;
	}

	public String getRuleFileName() {
		return ruleFileName;
	}

	public void setRuleFileName(String ruleFileName) {
		this.ruleFileName = ruleFileName;
	}

	public RuleFile getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(RuleFile selectedFile) {
		this.selectedFile = selectedFile;
	}

	public ArrayList<Rule> getObjectRules() {
		return objectRules;
	}

	public void setObjectRules(ArrayList<Rule> objectRules) {
		this.objectRules = objectRules;
	}

	public ArrayList<RuleFile> getListOfFileRules() {
		return listOfFileRules;
	}

	public void setListOfFileRules(ArrayList<RuleFile> listOfFileRules) {
		this.listOfFileRules = listOfFileRules;
	}
	

}
