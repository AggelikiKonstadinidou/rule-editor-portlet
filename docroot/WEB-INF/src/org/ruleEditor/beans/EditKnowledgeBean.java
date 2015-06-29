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
import org.ruleEditor.utils.FileDownloadController;
import org.ruleEditor.utils.Term;
import org.ruleEditor.utils.Utils;

import com.github.jsonldjava.core.JsonLdError;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@ManagedBean(name = "editKnowledgeBean")
@SessionScoped
public class EditKnowledgeBean {
	private Main main;
	private String fileName;
	private InputStream inputStream;
	private ArrayList<RecommendationForJson> terms;
	private DefaultTreeNode root;
	private Term selectedTerm;
	private Term clonedSelectedTerm;
	private RecommendationForJson abstractTerm;
	private Recommendation recommendationForRemove;

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
		clonedSelectedTerm = null;
		abstractTerm = new RecommendationForJson("AbstractTerm", "", true, 0,
				new ArrayList<Recommendation>());
		recommendationForRemove = abstractTerm.new Recommendation("", "", "",
				"");
		abstractTerm.getHasRecommendation().add(recommendationForRemove);

	}

	public void onFileUpload(FileUploadEvent event) throws IOException,
			JsonLdError {
		fileName = event.getFile().getFileName();
		inputStream = event.getFile().getInputstream();
		String result = Utils.readJsonLd(inputStream);
		terms = Utils.getObjectsFromJsonLd(inputStream, result);
		createJsonTree();
	}

	private void createJsonTree() throws IOException {

		selectedTerm = null;
		clonedSelectedTerm = null;
		root = new DefaultTreeNode("Json-ld", null);
		TreeNode node1 = null;
		TreeNode node2 = null;
		Term term = null;
		for (RecommendationForJson temp : terms) {

			term = new Term(temp.getType(), temp.getId(), "" + temp.isValue(),
					"" + temp.getRating(), "-");
			term.setAbstractTerm(true);

			node1 = new DefaultTreeNode(term, root);
			for (Recommendation temp2 : temp.getHasRecommendation()) {

				term = new Term("Recommendation", temp2.getId(),
						temp2.getValue(), "-", temp2.getName());

				node2 = new DefaultTreeNode(term, node1);
			}
		}

	}

	// TODO, save / remove the terms from the json ld
	public void saveEditedTerm() throws IOException {

		if (clonedSelectedTerm.getType().equalsIgnoreCase("AbstractTerm")) {

			if (!clonedSelectedTerm.equals(selectedTerm)) {

				for (RecommendationForJson temp : terms) {
					if (temp.getId().equalsIgnoreCase(selectedTerm.getId())
							&& temp.getRating() == Integer
									.parseInt(selectedTerm.getRating())
							&& temp.isValue() == Boolean
									.parseBoolean(selectedTerm.getValue())) {
						temp.setId(clonedSelectedTerm.getId());
						temp.setRating(Integer.parseInt(clonedSelectedTerm
								.getRating()));
						break;
					}
				}
			}

		} else if (clonedSelectedTerm.getType().equalsIgnoreCase(
				"Recommendation")) {

			boolean flag = false;
			if (!clonedSelectedTerm.equals(selectedTerm)) {

				for (RecommendationForJson temp : terms) {

					for (Recommendation temp2 : temp.getHasRecommendation()) {

						if (temp2.getId()
								.equalsIgnoreCase(selectedTerm.getId())
								&& temp2.getName().equals(
										selectedTerm.getName())
								&& temp2.getValue().equals(
										selectedTerm.getValue())) {
							temp2.setId(clonedSelectedTerm.getId());
							temp2.setName(clonedSelectedTerm.getName());
							temp2.setValue(clonedSelectedTerm.getValue());
							flag = true;
							break;
						}
					}

					if (flag)
						break;
				}
			}
		}

		createJsonTree();

	}

	// TODO, save / remove the terms from the json ld
	public void removeExistingTerm() throws IOException {

		int indexForRemove = -1;
		if (selectedTerm.getType().equalsIgnoreCase("AbstractTerm")) {

			for (RecommendationForJson temp : terms) {
				if (temp.getId().equalsIgnoreCase(selectedTerm.getId())
						&& temp.getRating() == Integer.parseInt(selectedTerm
								.getRating())
						&& temp.isValue() == Boolean.parseBoolean(selectedTerm
								.getValue())) {
					indexForRemove = terms.indexOf(temp);
					break;
				}
			}

			if (indexForRemove != -1)
				terms.remove(indexForRemove);

		} else if (selectedTerm.getType().equalsIgnoreCase("Recommendation")) {
			boolean flag = false;

			for (RecommendationForJson temp : terms) {

				for (Recommendation temp2 : temp.getHasRecommendation()) {

					if (temp2.getId().equalsIgnoreCase(selectedTerm.getId())
							&& temp2.getName().equals(selectedTerm.getName())
							&& temp2.getValue().equals(selectedTerm.getValue())) {
						indexForRemove = temp.getHasRecommendation().indexOf(
								temp2);
						flag = true;
						break;
					}
				}

				if (flag) {
					temp.getHasRecommendation().remove(indexForRemove);
					break;
				}
			}

		}
		
		createJsonTree();
	}

	public void exportJsonLdFile() throws IOException {

		String jsonString = Utils.createJsonLdKnowledge(terms);
		FileDownloadController.writeGsonAndExportFile("JsonLd", jsonString);
	}

	public void saveNewAbstractTerm() throws IOException {
		for (Recommendation temp : abstractTerm.getHasRecommendation()) {
			temp.setType("Recommendation");
		}
		terms.add(abstractTerm);
		createJsonTree();
		abstractTerm = new RecommendationForJson("AbstractTerm", "", true, 0,
				new ArrayList<Recommendation>());
		addRecommendation();

	}

	public void addRecommendation() {
		Recommendation recommendation = abstractTerm.new Recommendation(
				"Recommendation", "", "", "");
		abstractTerm.getHasRecommendation().add(recommendation);
	}

	public void removeRecommendation() {
		abstractTerm.getHasRecommendation().remove(recommendationForRemove);
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
		clonedSelectedTerm = selectedTerm.clone();
		return selectedTerm;
	}

	public void setSelectedTerm(Term selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public RecommendationForJson getAbstractTerm() {
		return abstractTerm;
	}

	public void setAbstractTerm(RecommendationForJson abstractTerm) {
		this.abstractTerm = abstractTerm;
	}

	public Recommendation getRecommendationForRemove() {
		return recommendationForRemove;
	}

	public void setRecommendationForRemove(
			Recommendation recommendationForRemove) {
		this.recommendationForRemove = recommendationForRemove;
	}

	public Term getClonedSelectedTerm() {
		if (selectedTerm != null && clonedSelectedTerm == null)
			this.clonedSelectedTerm = selectedTerm.clone();

		return clonedSelectedTerm;
	}

	public void setClonedSelectedTerm(Term clonedSelectedTerm) {
		this.clonedSelectedTerm = clonedSelectedTerm;
	}

}
