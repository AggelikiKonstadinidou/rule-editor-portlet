package org.ruleEditor.ontology;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class NetworkElementConverter implements Converter{

	@Override
	public OntologyProperty getAsObject(FacesContext context, UIComponent component, String value) {
		Main main = (Main) context
				.getApplication().evaluateExpressionGet(context,
						"#{main}", Main.class);
		// Convert the unique String representation of Foo to the actual Foo
		// object.
		return main.find(value);
		
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		// TODO Auto-generated method stub
		Main main = (Main) context
				.getApplication().evaluateExpressionGet(context,
						"#{main}", Main.class);
		// Convert the unique String representation of Foo to the actual Foo
		// object.
		return ((OntologyProperty) value).toString();
	}

}
