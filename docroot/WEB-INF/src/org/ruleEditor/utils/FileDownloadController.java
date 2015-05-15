package org.ruleEditor.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.util.PortalUtil;

public class FileDownloadController {
	
	
	
	public static void writeGsonAndExportFile(String fileName, String json)
			throws IOException {

		// 1. write json to a new file
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write(json);
		out.close();

		File fil = new File(fileName);

		InputStream stream = new FileInputStream(fil);
		StreamedContent file = new DefaultStreamedContent(stream, "text/txt",
				fileName);

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
}
