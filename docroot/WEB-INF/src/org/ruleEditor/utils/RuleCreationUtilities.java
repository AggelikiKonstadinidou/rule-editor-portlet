package org.ruleEditor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.ruleEditor.ontology.PointElement;

public class RuleCreationUtilities {
	
	public static void saveRule(String ruleName, String newFileName,
			String oldFileName, ArrayList<PointElement> conditions,
			ArrayList<PointElement> conclusions, boolean flag,
			String feedbackClass, String feedbackScope,String feedbackId,
			ArrayList<Rule> existingRules, InputStream fileStream) throws IOException{
		
		if (ruleName.trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide a rule name", ""));

			return;
		}

		String finalFileName = newFileName.trim();
		if (newFileName.isEmpty() && !oldFileName.trim().isEmpty())
			finalFileName = oldFileName;

		if (finalFileName.trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"msgs",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please create a new file or select an existing",
							""));
			return;
		}

		//create the rule string
		String rule = "";
		if (!flag)
			rule = Utils.createRule(conditions, conclusions, ruleName);
		else
			rule = Utils.createFeedBackRule(feedbackClass, feedbackScope,
					feedbackId, conditions,ruleName);
		
		// export the rule
		if (!rule.isEmpty() && !newFileName.isEmpty())
			FileDownloadController.writeGsonAndExportFile(finalFileName, rule);
		else {
			existingRules = Utils.getRulesFromFile(fileStream);
			String allRuleString = Utils.prefix_c4a + "\n" + Utils.prefix_rdfs
					+ "\n";
			for (Rule temp : existingRules) {
				allRuleString = allRuleString.concat(temp.getBody()) + "\n";
			}

			allRuleString = allRuleString.concat(rule
					.replace(Utils.prefix_c4a, "")
					.replace(Utils.prefix_rdfs, "").trim());
			FileDownloadController.writeGsonAndExportFile(finalFileName,
					allRuleString);
		}
		
	}

}
