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
	private ArrayList<Recommendation> newRecommendations;
	private Recommendation testRec;

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
		newRecommendations = new ArrayList<Recommendation>();
		newRecommendations.add(recommendationForRemove);
		testRec = abstractTerm.new Recommendation("Recommendation", "", "", "");

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

	// save the terms that have been edited to the json ld
	public void saveEditedTerm() throws IOException {

		if (clonedSelectedTerm.getType().equalsIgnoreCase("AbstractTerm")) {

			if (!clonedSelectedTerm.equals(selectedTerm) || 
					(newRecommendations.size()>=1 && !newRecommendations.contains(testRec) )) {

			//make any updates in the original term
				for (RecommendationForJson temp : terms) {
					if (temp.getId().equalsIgnoreCase(selectedTerm.getId())
							&& temp.getRating() == Integer
									.parseInt(selectedTerm.getRating())
							&& temp.isValue() == Boolean
									.parseBoolean(selectedTerm.getValue())) {
						temp.setId(clonedSelectedTerm.getId());
						temp.setValue(Boolean.parseBoolean(clonedSelectedTerm.getValue()));
						temp.setRating(Integer.parseInt(clonedSelectedTerm
								.getRating()));

						//add new recommendation to the list of recommendations
						//of the abstract term
						if (newRecommendations.size() >= 1) {
							for (Recommendation rec : newRecommendations) {
								if (!rec.getId().isEmpty()
										&& !rec.getName().isEmpty()
										&& !rec.getValue().isEmpty()) {
									temp.getHasRecommendation().add(rec);
								}
							}
						}
						
						// clear list of new recommendations
						newRecommendations = new ArrayList<Recommendation>();
						newRecommendations.add(testRec);
						break;
					}
				}
			}

		//make any updates in the original recommendation term
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

	// remove the terms from the json ld
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
		FileDownloadController.writeGsonAndExportFile(
				"explodePreferenceTerms.jsonld", jsonString);
	}

	public void saveNewAbstractTerm() throws IOException {
		
		ArrayList<String> emptyRecomm = new ArrayList<String>();
		for (int i = 0; i < abstractTerm.getHasRecommendation().size(); i++) {

			Recommendation temp = abstractTerm.getHasRecommendation().get(i);
			if (!temp.getId().isEmpty() && !temp.getValue().isEmpty()
					&& !temp.getName().isEmpty())
				temp.setType("Recommendation");
			else
				emptyRecomm.add("" + i);
		}

		// remove empty recommendations from abstract term
		for (String s : emptyRecomm) {
			abstractTerm.getHasRecommendation().remove(Integer.parseInt(s));
		}

		terms.add(abstractTerm);
		createJsonTree();
		abstractTerm = new RecommendationForJson("AbstractTerm", "", true, 0,
				new ArrayList<Recommendation>());
		addRecommendation();

	}

	public void addNewRecommendation() {
		Recommendation recommendation = abstractTerm.new Recommendation(
				"Recommendation", "", "", "");
		newRecommendations.add(recommendation);
	}

	public void removeNewRecommendation() {
		newRecommendations.remove(recommendationForRemove);
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
		return selectedTerm;
	}

	public void setSelectedTerm(Term selectedTerm) {
		this.selectedTerm = selectedTerm;
		this.clonedSelectedTerm = selectedTerm.clone();
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
		return clonedSelectedTerm;
	}

	public void setClonedSelectedTerm(Term clonedSelectedTerm) {
		this.clonedSelectedTerm = clonedSelectedTerm;
	}

	public ArrayList<Recommendation> getNewRecommendations() {
		return newRecommendations;
	}

	public void setNewRecommendations(
			ArrayList<Recommendation> newRecommendations) {
		this.newRecommendations = newRecommendations;
	}

}
