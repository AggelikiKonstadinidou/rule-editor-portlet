package org.ruleEditor.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.ruleEditor.ontology.Main;
import org.ruleEditor.utils.Utils;

@ManagedBean(name = "configurateOrderBean")
@SessionScoped
public class ConfigurateOrderBean {

	private Main main;
	private String[] rules;
	private String propertiesFileName;
	private InputStream inputStream;

	public ConfigurateOrderBean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();
		main = (Main) context.getApplication().evaluateExpressionGet(context,
				"#{main}", Main.class);
	}

	public void init() {
//		Properties prop = new Properties();
//		InputStream configInputStream = null;
//		
//		File f = new File(
//				System.getProperty("user.dir")
//						+ "/../webapps/CLOUD4All_RBMM_Restful_WS/WEB-INF/config.properties");
//
//		try {
//			if (f.exists())
//				rules = prop.getProperty("rules").split(";");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (configInputStream != null) {
//				try {
//					configInputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}

	}
	
	public void onFileUpload(FileUploadEvent event) throws IOException{
		
		propertiesFileName = event.getFile().getFileName();
		inputStream = event.getFile().getInputstream();
		ArrayList<String> list = Utils.getRuleArray(inputStream);
		
	}

	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

}
