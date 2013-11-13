package net.msoetebier.nadia.language;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.msoetebier.nadia.ExceptionHandler;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class LanguageManagement {
	public final Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>();
	
	public Map<String, Map<String, String>> setMap(String language) {
		map.clear();
		SAXBuilder builder = new SAXBuilder();
		Document document;
		try {
			if (language.equals("english")) {
				InputStream inputStream = getClass().getResourceAsStream("english.xml");
				document = (Document) builder.build(inputStream);
				Element rootNode = document.getRootElement();
				addElements(rootNode, map);		
			} else if (language.equals("german")) {
				InputStream inputStream = getClass().getResourceAsStream("german.xml");	 	
				document = (Document) builder.build(inputStream);
				Element rootNode = document.getRootElement();
				addElements(rootNode, map);					
			}	 
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return map;
	}
	
	private void addElements(Element rootNode, Map<String, Map<String, String>> map) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
				addChildrenElements(node, map);
		}
	}
	
	private void addChildrenElements(Element node, Map<String, Map<String, String>> map) {
		List<Element> children = node.getChildren();
		Map<String, String> childMap = new HashMap<String, String>();
		for (Element childNode : children) {
			childMap.put(childNode.getName(), childNode.getValue());
		}
		map.put(node.getName(), childMap);
	}
}