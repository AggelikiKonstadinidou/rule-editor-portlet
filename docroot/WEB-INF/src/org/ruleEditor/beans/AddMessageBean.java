package org.ruleEditor.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.ontology.Message;
import org.ruleEditor.ontology.PointElement;
import org.ruleEditor.utils.FileDownloadController;
import org.ruleEditor.utils.Rule;
import org.ruleEditor.utils.RuleCreationUtilities;
import org.ruleEditor.utils.Utils;

@ManagedBean(name = "addMessageBean")
@SessionScoped
public class AddMessageBean {

	private AddNewRuleBean addNewRuleBean;
	private Main main;
	private String jsonString = "";
	private String feedbackClass = "";
	private String feedbackScope = "";
	private String feedbackId = "";
	private List<Message> messages = null;
	private Message messageForRemove = null;
	private String feedbackFile="";
	private InputStream fileStream;
	private boolean isFeedback = false; //false if the bean is called for a default rule
	private String ruleName = "";       //true if the bean is called for a feedback rule
    private String oldFileName = "";
    private String newFileName = "";
	 
	public AddMessageBean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		addNewRuleBean = (AddNewRuleBean) context.getApplication()
				.evaluateExpressionGet(context, "#{addNewRuleBean}",
						AddNewRuleBean.class);
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);

	}

	public void init(boolean flag) {
		isFeedback = flag;
		jsonString = "";
		feedbackClass = "";
		feedbackScope = "";
		feedbackId = "";
		ruleName = "";
		oldFileName = "";
		newFileName = "";
		Message emptyMessage = new Message();
		emptyMessage.setLanguage("English");
		messages = new ArrayList<Message>();
		messages.add(emptyMessage);
		messageForRemove = new Message();
	}
	
	public void onFileUpload(FileUploadEvent event) throws IOException {

		feedbackFile = event.getFile().getFileName();

		fileStream = event.getFile().getInputstream();
		
		System.out.println(feedbackFile);

	}
	
	public void exportJsonLdFile() throws IOException{
		if (!feedbackFile.isEmpty())
			jsonString = Utils.writeMessagesInJsonLdFile(fileStream, messages);

		FileDownloadController.writeGsonAndExportFile(feedbackFile, jsonString);
	}
	
	public void uploadFileForSaveAs(FileUploadEvent event) throws IOException{
		oldFileName = event.getFile().getFileName();
		fileStream = event.getFile().getInputstream();
	}
	
	//TODO finish the part of saving the rule
	public void saveRule() throws IOException{
		
		//if it is a feedback rule
		if(isFeedback)
		RuleCreationUtilities.saveRule(ruleName, newFileName,
				oldFileName, 
				addNewRuleBean.getConditions(), new ArrayList<PointElement>(),
				true, feedbackClass, feedbackScope, 
				feedbackId, addNewRuleBean.getExistingRules(), fileStream);
		
		
	}
	
	public void removeMessageFromList() {
		messages.remove(messageForRemove);
	}

	public void addMessageToList() {
		Message newMessage = new Message();
		messages.add(newMessage);
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public String getFeedbackClass() {
		return feedbackClass;
	}

	public void setFeedbackClass(String feedbackClass) {
		this.feedbackClass = feedbackClass;
	}

	public String getFeedbackScope() {
		return feedbackScope;
	}

	public void setFeedbackScope(String feedbackScope) {
		this.feedbackScope = feedbackScope;
	}

	public String getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(String feedbackId) {
		this.feedbackId = feedbackId;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public Message getMessageForRemove() {
		return messageForRemove;
	}

	public void setMessageForRemove(Message messageForRemove) {
		this.messageForRemove = messageForRemove;
	}

	public String getFeedbackFile() {
		return feedbackFile;
	}

	public void setFeedbackFile(String feedbackFile) {
		this.feedbackFile = feedbackFile;
	}

	public boolean isFeedback() {
		return isFeedback;
	}

	public void setFeedback(boolean isFeedback) {
		this.isFeedback = isFeedback;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getOldFileName() {
		return oldFileName;
	}

	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}
	

}
