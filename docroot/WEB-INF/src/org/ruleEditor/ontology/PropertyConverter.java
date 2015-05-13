package org.ruleEditor.ontology;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class PropertyConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Main main = (Main) context
				.getApplication().evaluateExpressionGet(context,
						"#{main}", Main.class);
		// Convert the unique String representation of Foo to the actual Foo
		// object.
		return main.findProperty(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		Main main = (Main) context
				.getApplication().evaluateExpressionGet(context,
						"#{main}", Main.class);
		// Convert the Foo object to its unique String representation.
		return ((OntologyProperty) value).getOntologyURI();
		// return "aa";
	}
	

}
