package net.msoetebier.nadia.function;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.msoetebier.nadia.ExceptionHandler;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class RestrictionManagement {
	private Map<String, Map<String, Map<String,String>>> restrictionMap = new HashMap<String, Map<String, Map<String,String>>>();

	/**
	 * This method finds in the choosen language integer and strings attributes. 
	 * That means values are string elements and range are integer elements. 
	 */
	public void setRestrictionMap(String restrictionPath) {
		restrictionMap.clear();
		restrictionMap = new HashMap<String, Map<String, Map<String,String>>>();
		
		File xmlFile = new File(restrictionPath);	 
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			List<Element> attributes = rootNode.getChildren();
			for (Element attribute : attributes) { 
				Map<String, Map<String, String>> attributeMap = new HashMap<String, Map<String, String>>();
				List<Element> attributeChilds = attribute.getChildren();
				for (Element values : attributeChilds) {
					if (values.getName().equals("values")) { //string
						List<Element> value = values.getChildren();
						for (Element eachValue : value) {
							Map<String, String> valueMap = new HashMap<String, String>();
							Attribute displayName = eachValue.getAttribute("displayname");
							valueMap.put("displayname", displayName.getValue());
							Content content = eachValue.getContent(0);
							valueMap.put("value", content.getValue());
							attributeMap.put(content.getValue(), valueMap);	
						}
					} else if (values.getName().equals("range")) { //int
						List<Element> value = values.getChildren();
						Map<String, String> valueMap = new HashMap<String, String>();
						for (Element eachValue : value) {
							if (eachValue.getName().equals("min")) {
								valueMap.put("min", eachValue.getValue());
							} else if (eachValue.getName().equals("max")) {
								valueMap.put("max", eachValue.getValue());
							}
						}
						attributeMap.put("range", valueMap);
					}
				}
				Attribute attributeValue = attribute.getAttribute("name");
				restrictionMap.put(attributeValue.getValue(), attributeMap);				
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private Map<String, Map<String, Map<String,String>>> getRestrictionMap() {
		return restrictionMap;
	}
	
	public String[] getRestrictionValuesForItem(String item) {
		List<String> values = new ArrayList<String>();
		Map<String, Map<String, Map<String,String>>> restriction = getRestrictionMap();
		Map<String, Map<String, String>> restrictionForItem = restriction.get(item);
		for (Entry<String, Map<String, String>> entry : restrictionForItem.entrySet()) {
			    Map<String, String> value = entry.getValue();
			    values.add(value.get("displayname"));
		}
		String[] restrictionValues = new String[values.size()];
		values.toArray(restrictionValues);
		return restrictionValues;
	}
	
	public int getMaximum(String item) {
		String maximum = "";
		int max;
		Map<String, Map<String, Map<String,String>>> restriction = getRestrictionMap();
		Map<String, Map<String, String>> restrictionForItem = restriction.get(item);
		if (restrictionForItem != null) {
			Map<String, String> range = restrictionForItem.get("range");
			if (range != null) {
				if (range.get("max") != null) {
					maximum = range.get("max");				
				}				
			}
		}
		if (maximum.equals("")) {
			max = -1;
		} else {
			max = Integer.parseInt(maximum);			
		}
		return max;
	}
	
	public int getMinimum(String item) {
		String minimum = "";
		int min;
		Map<String, Map<String, Map<String,String>>> restriction = getRestrictionMap();
		Map<String, Map<String, String>> restrictionForItem = restriction.get(item);
		if (restrictionForItem != null) {
			Map<String, String> range = restrictionForItem.get("range");
			if (range != null) {
				if (range.get("min") != null) {
					minimum = range.get("min");
				}				
			}
		}
		if (minimum.equals("")) {
			min = -1;
		} else {
			min = Integer.parseInt(minimum);
		}
		return min;
	}
	
	public boolean isRestrictedItemWithValues(String item) {
		boolean isRestricted = false;
		Map<String, Map<String, Map<String,String>>> restriction = getRestrictionMap();
		Map<String, Map<String, String>> restrictionForItem = restriction.get(item);		
		if (restrictionForItem != null && !restrictionForItem.isEmpty()) {
			isRestricted = true;
		}
		return isRestricted;
	}
	
	public boolean isString(String item) {
		boolean isString = false;
		Map<String, Map<String, Map<String,String>>> restriction = getRestrictionMap();
		Map<String, Map<String, String>> restrictionForItem = restriction.get(item);	
		for (Map.Entry<String, Map<String, String>> entry : restrictionForItem.entrySet()) {
			Map<String, String> values = entry.getValue();
			if (values.containsKey("value")) {
				isString = true;
			}
		}
		return isString;
	}
	
	public boolean isInteger(String item) {
		boolean isInteger = false;
		Map<String, Map<String, Map<String,String>>> restriction = getRestrictionMap();
		Map<String, Map<String, String>> restrictionForItem = restriction.get(item);		
		if (restrictionForItem.containsKey("range")) {
			isInteger = true;
		}
		return isInteger;
	}
}