package org.ruleEditor.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.event.FileUploadEvent;
import org.ruleEditor.ontology.Main;

@ManagedBean(name = "editRuleStep1Bean")
@SessionScoped
public class EditRuleStep1Bean {
	
	private String fileName = "";
	private CommandLink button;
	private boolean formCompleted = true;
	private Main main;
	private InputStream fileStream;
	
	public EditRuleStep1Bean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
	}
	
	public void init(){
		fileName = "";
		formCompleted = true;
		fileStream = null;
	}
	
	public void onFileUpload(FileUploadEvent event) throws IOException{
		
		this.fileName = event.getFile().getFileName();

		this.fileStream = event.getFile().getInputstream();
		
		System.out.println(fileName);
		
		this.formCompleted = false;
		
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
