package org.ruleEditor.ontology;

import java.util.Comparator;

public class ElementComparator implements Comparator<PointElement>{

	@Override
	public int compare(PointElement p1, PointElement p2) {
		// TODO Auto-generated method stub
		return p1.getOrder()-p2.getOrder();
	}

}
