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
	int counter = 0;

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
	}

	public void onFileUpload(FileUploadEvent event) throws IOException {

		this.fileName = event.getFile().getFileName();

		this.fileStream = event.getFile().getInputstream();

		System.out.println(fileName);

		this.formCompleted = false;
		
		List<List<PointElement>> list = Utils.convertRuleToDiagram(fileStream,
				main.getAllClasses(), main.getMethods());
		conditions = (ArrayList<PointElement>) list
				.get(0);
		conclusions = (ArrayList<PointElement>) list
				.get(1);

		rule = Utils.createRule(conditions, conclusions, "aaaa");
		System.out.println(rule);

	}

	public void submitOption() throws IOException {
		
		FacesContext context = FacesContext.getCurrentInstance();
		AddNewRuleBean addNewRuleBean = (AddNewRuleBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{addNewRuleBean}", AddNewRuleBean.class);

		boolean flag = false;
		if (rule.contains("message"))
			flag = true;

		if (counter == 0) {
			addNewRuleBean.editRule(flag, conditions, conclusions);
			counter++;
		}
		
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

}
