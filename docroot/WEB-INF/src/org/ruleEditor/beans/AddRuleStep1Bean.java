package org.ruleEditor.beans;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.context.RequestContext;
import org.ruleEditor.ontology.Main;

@ManagedBean(name = "addRuleStep1Bean")
@SessionScoped
public class AddRuleStep1Bean {
	private CommandLink button;
	private boolean formCompleted = true;
	private Main main;
	private AddNewRuleBean addNewRuleBean;
	private String selectedOption;
	private String description;
	
	public AddRuleStep1Bean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);

	}
	
	public void init(){
		selectedOption = "";
		description = "";
		formCompleted = true;
	}
	

	public CommandLink getButton() {
		return button;
	}

	public void setButton(CommandLink button) {
		this.button = button;
	}

	public boolean isFormCompleted() {
		return formCompleted;
	}

	public void setFormCompleted(boolean formCompleted) {
		this.formCompleted = formCompleted;
	}

	public String getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(String selectedOption) {
		this.selectedOption = selectedOption;
	}
	
	public void submitOption() throws IOException{
		
		if (description.trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide a description for the rule", ""));

			return;
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		addNewRuleBean = (AddNewRuleBean) context.getApplication()
				.evaluateExpressionGet(context, "#{addNewRuleBean}",
						AddNewRuleBean.class);
		boolean flag = false;
		if (selectedOption.contains("Feedback"))
			flag = true;

		addNewRuleBean.init(flag);
		addNewRuleBean.setRuleDescription(description);
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.redirect(selectedOption);
	
		
		
	}
    
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void handleChange(){
	     formCompleted = false;
	}
}
