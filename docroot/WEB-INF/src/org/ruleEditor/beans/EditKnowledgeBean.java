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
import org.ruleEditor.utils.RecommendationForJson;
import org.ruleEditor.utils.RecommendationForJson.Recommendation;
import org.ruleEditor.utils.Term;
import org.ruleEditor.utils.Utils;

import com.github.jsonldjava.core.JsonLdError;

@ManagedBean(name = "editKnowledgeBean")
@SessionScoped
public class EditKnowledgeBean {
	private Main main;
	private String fileName;
	private InputStream inputStream;
	private ArrayList<RecommendationForJson> terms;
	private DefaultTreeNode root;
	private Term selectedTerm;

	public EditKnowledgeBean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
	}

	public void init() {

		fileName = "";
		inputStream = null;
		terms = new ArrayList<RecommendationForJson>();
		root = new DefaultTreeNode("Json-ld", null);
		selectedTerm = null;
		
	}

	public void onFileUpload(FileUploadEvent event) throws IOException, JsonLdError {
		fileName = event.getFile().getFileName();
		inputStream = event.getFile().getInputstream();
		String result = Utils.readJsonLd(inputStream);
		terms = Utils.getObjectsFromJsonLd(inputStream,result);
		createJsonTree();
	}
	
	private void createJsonTree() throws IOException {
		
		new DefaultTreeNode("Json-ld", null);
		TreeNode node1 = null;
		TreeNode node2 = null;
		Term term = null;
		for (RecommendationForJson temp : terms) {

			term = new Term(temp.getType(), temp.getId(), "" + temp.isValue(),
					"" + temp.getRating(), "-");

			node1 = new DefaultTreeNode(term, root);
			for (Recommendation temp2 : temp.getHasRecommendation()) {

				term = new Term(temp2.getType(), temp2.getId(),
						temp2.getValue(), "-", temp2.getName());

				node2 = new DefaultTreeNode(term, node1);
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

	public ArrayList<RecommendationForJson> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<RecommendationForJson> terms) {
		this.terms = terms;
	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}

	public Term getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(Term selectedTerm) {
		this.selectedTerm = selectedTerm;
	}
	
}
