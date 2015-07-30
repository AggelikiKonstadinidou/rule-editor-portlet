package org.ruleEditor.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.event.FileUploadEvent;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.ontology.PointElement;
import org.ruleEditor.utils.Rule;
import org.ruleEditor.utils.RuleCreationUtilities;
import org.ruleEditor.utils.Utils;

@ManagedBean(name = "editRuleStep1Bean")
@SessionScoped
public class EditRuleStep1Bean {

	private String fileName = "";
	private CommandLink button;
	private boolean formCompleted = true;
	private Main main;
	private InputStream fileStream;
	private String rule;
	private ArrayList<PointElement> conditions;
	private ArrayList<PointElement> conclusions;
	private ArrayList<Rule> rulesList;
	int counter = 0;
	private Rule selectedRule = null;

	public EditRuleStep1Bean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
	}

	public void init() {
		fileName = "";
		formCompleted = true;
		fileStream = null;
		counter = 0;
		rulesList = new ArrayList<Rule>();
		selectedRule = new Rule("", "", null);
	}

	public void onFileUpload(FileUploadEvent event) throws IOException {

		this.fileName = event.getFile().getFileName();

		this.fileStream = event.getFile().getInputstream();

		System.out.println(fileName);

		this.formCompleted = false;

		rulesList = Utils.getRulesFromFile(fileStream);

	}

	public void reload() {
		selectedRule = null;
		formCompleted = true;
	}

	public void submitOption() throws IOException {

		if (counter == 0) {
			List<List<PointElement>> list = Utils.convertRuleToDiagram(
					selectedRule, main.getAllClasses(), main.getMethods());
			conditions = (ArrayList<PointElement>) list.get(0);
			conclusions = (ArrayList<PointElement>) list.get(1);

			rule = RuleCreationUtilities.createRule(conditions, conclusions,
					selectedRule.getName());
			System.out.println(rule);
			counter++;
		}

		FacesContext context = FacesContext.getCurrentInstance();
		AddNewRuleBean addNewRuleBean = (AddNewRuleBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{addNewRuleBean}", AddNewRuleBean.class);

		//get the lists with the variables for the rule that is
		//going to be edited
		addNewRuleBean.setUsedVariablesForClasses(Utils
				.getUsedVariablesForClassesList());
		addNewRuleBean.setUsedVariablesForValues(Utils
				.getUsedVariablesForValuesList());

		boolean flag = false;
		if (rule.contains("message"))
			flag = true;

		//make initializations
		addNewRuleBean.editRule(flag, conditions, conclusions, selectedRule,
				rulesList);

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isFormCompleted() {
		return formCompleted;
	}

	public void setFormCompleted(boolean formCompleted) {
		this.formCompleted = formCompleted;
	}

	public CommandLink getButton() {
		return button;
	}

	public void setButton(CommandLink button) {
		this.button = button;
	}

	public ArrayList<Rule> getRulesList() {
		return rulesList;
	}

	public void setRulesList(ArrayList<Rule> rulesList) {
		this.rulesList = rulesList;
	}

	public Rule getSelectedRule() {
		return selectedRule;
	}

	public void setSelectedRule(Rule selectedRule) {
		this.selectedRule = selectedRule;
		counter = 0;
	}

}
