package org.ruleEditor.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.util.PortalUtil;

public class FileDownloadController {

	public static void writeGsonAndExportInServer(String directoryName,
			String fileName, String json) throws IOException {

		File myFile = new File(fileName);
		File configFile;
		FileWriter fileWriter;
		FileInputStream fis;
		ArrayList<String> ruleSets;
		String inputString = "", newString = "";

		// true to append (if the file does not exist)
		// false to overwrite (if the file exists)
		if (!myFile.exists()) {

			fileName = fileName + ".rules";
			myFile = new File(fileName);
			System.out.println("file does not exist");
			// update the configuration file
			configFile = new File(RuleCreationUtilities.TEST_CONFIGFILE_PATH);
			fis = new FileInputStream(configFile);
			ruleSets = Utils.getRuleArray(fis);
			inputString = ruleSets.get(ruleSets.size() - 1);
			ruleSets.remove(ruleSets.size() - 1);
			ruleSets.add(myFile.getName());

			newString = Utils.createOrderOfFilesForConfigFile(inputString,
					ruleSets);

			fileWriter = new FileWriter(configFile, false);
			fileWriter.write(newString);
			fileWriter.close();

			System.out.println("update configuration file");

		}

		fileWriter = new FileWriter(myFile, false);
		fileWriter.write(json);
		fileWriter.close();

		System.out.println("rule file was saved successfully");

	}

	public static void writeGsonAndExportFile(String fileName, String json)
			throws IOException {

		// 1. write json to a new file
		BufferedWriter out = new BufferedWriter(new FileWriterWithEncoding(
				fileName, "UTF-8"));
		out.write(json);
		out.close();

		// File fil = new File(fileName);

		// InputStream stream = new FileInputStream(fil);
		// StreamedContent file = new DefaultStreamedContent(stream, "text/txt",
		// fileName);

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
		res.setCharacterEncoding("UTF-8");
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
}
