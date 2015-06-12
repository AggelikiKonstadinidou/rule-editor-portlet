package org.ruleEditor.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.ontology.Ontology;
import org.ruleEditor.ontology.OntologyClass;
import org.ruleEditor.utils.RecommendationForJson.Term;
import org.ruleEditor.utils.RecommendationForJson.Term.Recommendation;
import org.ruleEditor.utils.Utils;

@ManagedBean(name = "editKnowledgeBean")
@SessionScoped
public class EditKnowledgeBean {
	private Main main;
	private String fileName;
	private InputStream inputStream;
	private ArrayList<Term> terms;
	private DefaultTreeNode root;

	public EditKnowledgeBean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
	}

	public void init() {

		fileName = "";
		inputStream = null;
		terms = new ArrayList<Term>();
		root = new DefaultTreeNode("Json-ld", null);
		
	}

	public void onFileUpload(FileUploadEvent event) throws IOException {
		fileName = event.getFile().getFileName();
		inputStream = event.getFile().getInputstream();
		//parse json and get terms as objects
		terms = Utils.getObjectsFromJsonLd(inputStream);
		createJsonTree();
	}
	
	private void createJsonTree() {

		new DefaultTreeNode("Json-ld", null);
		TreeNode node1 = null;
		TreeNode node2 = null;
		for (Term temp : terms) {
			node1 = new DefaultTreeNode(temp, root);
			for (Recommendation temp2 : temp.getHasRecommendation()) {
				node2 = new DefaultTreeNode(temp2, node1);
			}
		}

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public ArrayList<Term> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<Term> terms) {
		this.terms = terms;
	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}
	
	

}
