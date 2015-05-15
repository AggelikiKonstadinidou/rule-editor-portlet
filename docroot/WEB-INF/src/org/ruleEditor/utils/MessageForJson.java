package org.ruleEditor.utils;

import java.util.ArrayList;

public class MessageForJson {
	
		private Context context;
		private ArrayList<Group> graph;
		
		public MessageForJson(Context context, ArrayList<Group> graph) {
			super();
			this.context = context;
			this.graph = graph;
		}

		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public ArrayList<Group> getGraph() {
			return graph;
		}

		public void setGraph(ArrayList<Group> graph) {
			this.graph = graph;
		}

		
	
	
	public class Context{
		private String c4a;
		private String rdfs;
		public String getC4a() {
			return c4a;
		}
		public void setC4a(String c4a) {
			this.c4a = c4a;
		}
		public String getRdfs() {
			return rdfs;
		}
		public void setRdfs(String rdfs) {
			this.rdfs = rdfs;
		}
		public Context(String c4a, String rdfs) {
			super();
			this.c4a = c4a;
			this.rdfs = rdfs;
		}
	}
	
	public class Group{
		private String id;
		private String type;
		private ArrayList<TextMessage> messages;
		
		public Group(String id, String type, ArrayList<TextMessage> messages) {
			super();
			this.id = id;
			this.type = type;
			this.messages = new ArrayList<TextMessage>();
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public ArrayList<TextMessage> getMessages() {
			return messages;
		}

		public void setMessages(ArrayList<TextMessage> messages) {
			this.messages = messages;
		}

		public class TextMessage{
			private String language;
			private String text;
			public TextMessage(String language, String text) {
				super();
				this.language = language;
				this.text = text;
			}
			public String getLanguage() {
				return language;
			}
			public void setLanguage(String language) {
				this.language = language;
			}
			public String getText() {
				return text;
			}
			public void setText(String text) {
				this.text = text;
			}
			
		}
	}

}
