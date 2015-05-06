package org.ruleEditor.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.ruleEditor.ontology.NetworkElement;
import org.ruleEditor.ontology.NetworkElement.Type;

import com.liferay.portal.util.PortalUtil;
import com.sun.faces.component.visit.FullVisitContext;

public class Utils {
	
	private static String prefix_c4a = "@prefix c4a: <http://rbmm.org/schemas/cloud4all/0.1/>.";
	private static String prefix_rdfs = "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.";

	public static String createRule(ArrayList<NetworkElement> conditions,
			ArrayList<NetworkElement> conclusions, String ruleName) {

		String rule = prefix_c4a + "\n" + prefix_rdfs + "\n\n"+"[ruleName \n";

		rule = rule + convertListToRule(conditions);

		rule = rule + "->\n";

		rule = rule + convertListToRule(conclusions);

		rule = rule + "]";

		return rule;
	}
	
	public static String convertListToRule(ArrayList<NetworkElement> list) {
		String rule = "";

		// work on conditions
		for (NetworkElement el : list) {
			if (el.getType() == Type.CLASS) {

				rule = rule + "(" + el.getVarName() + " rdf:type " + "c4a:"
						+ el.getElementName() + ")\n";

			} else if (el.getType() == Type.DATA_PROPERTY) {

				rule = rule + "(" + el.getConnections().get(0).getVarName()
						+ " c4a:" + el.getElementName() + " "
						+ el.getValueOfProperty() + ")\n";

			} else if (el.getType() == Type.OBJECT_PROPERTY) {

				// TODO: check that there are two connections
				// and define the correct order
				// in case that something is missing, throw away
				// the property node
				rule = rule + "(" + el.getConnections().get(0).getVarName()
						+ " c4a:" + el.getElementName() + " "
						+ el.getConnections().get(1).getVarName() + ")\n";

			}

		}

		return rule;
	}

	public static void writeGsonAndExportFile(String fileName, String json)
			throws IOException {

		// 1. write json to a new file
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write(json);
		out.close();

		File fil = new File(fileName);

		InputStream stream = new FileInputStream(fil);
		StreamedContent file = new DefaultStreamedContent(stream, "text/txt", fileName);

		File localfile = new File(fileName);
		FileInputStream fis = new FileInputStream(localfile);

		// 2. get Liferay's ServletResponse
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");//
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();

		// 3. write the file into the outputStream
		OutputStream outputStream = res.getOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = fis.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
			buffer = new byte[4096];
		}

		outputStream.close();
		fis.close();

	}
	
	public UIComponent findComponent(final String id) {

		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot root = context.getViewRoot();
		final UIComponent[] found = new UIComponent[1];

		root.visitTree(new FullVisitContext(context), new VisitCallback() {
			@Override
			public VisitResult visit(VisitContext context, UIComponent component) {
				if (component.getId().equals(id)) {
					found[0] = component;
					return VisitResult.COMPLETE;
				}
				return VisitResult.ACCEPT;
			}
		});

		return found[0];

	}

}
