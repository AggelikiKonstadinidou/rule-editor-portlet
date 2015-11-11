package org.ruleEditor.ontology;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.model.User;

@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean {
	private boolean user = false;
	private boolean admin = false;
	private Main main;

	public UserBean() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			main = (Main) context.getApplication().evaluateExpressionGet(
					context, "#{main}", Main.class);
			User currentUser = LiferayFacesContext.getInstance().getUser();
			for (int i = 0; i < currentUser.getRoles().size(); i++) {
				if (currentUser.getRoles().get(i).getName()
						.equals("ApplicationUser")) {
					user = true;
					main.setTypeOfUser("user");

				} else if (currentUser.getRoles().get(i).getName()
						.equals("Administrator")) {
					admin = true;
					main.setTypeOfUser("admin");

				}
			}

			if (!user && !admin)
				main.setTypeOfUser("user");
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * @return the user
	 */
	public boolean isUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(boolean user) {
		this.user = user;
	}

}
