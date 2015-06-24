package org.ruleEditor.utils;

import java.util.ArrayList;

import org.ruleEditor.utils.MessageForJson.Context;

public class RecommendationForJson {

		private String type;
		private String id;
		private boolean value;
		private int rating;
		private ArrayList<Recommendation> hasRecommendation;
 		
		public ArrayList<Recommendation> getHasRecommendation() {
			return hasRecommendation;
		}
		public void setHasRecommendation(ArrayList<Recommendation> hasRecommendation) {
			this.hasRecommendation = hasRecommendation;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public boolean isValue() {
			return value;
		}
		public void setValue(boolean value) {
			this.value = value;
		}
		public int getRating() {
			return rating;
		}
		public void setRating(int rating) {
			this.rating = rating;
		}
		
		public RecommendationForJson(String type, String id, boolean value,
				int rating, ArrayList<Recommendation> hasRecommendation) {
			super();
			this.type = type;
			this.id = id;
			this.value = value;
			this.rating = rating;
			this.hasRecommendation = hasRecommendation;
		}


		public class Recommendation{
			private String type;
			private String id;
			private String name;
			private String value;
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public Recommendation(String type, String id, String name,
					String value) {
				super();
				this.type = type;
				this.id = id;
				this.name = name;
				this.value = value;
			}
			
		}
		
	
	
}
