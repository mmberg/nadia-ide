package net.msoetebier.nadia.readsave;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import net.msoetebier.nadia.ExceptionHandler;
import net.msoetebier.nadia.Singleton;
import net.msoetebier.nadia.view.NavigationView;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class AddItemDetailView {

	 public void addItemToXmlFile(String parentItem, String item) {
		 	NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
			File xmlFile = new File(navigationView.getXmlPath());	 
			SAXBuilder builder = new SAXBuilder();
			Document document;
			try {
				document = (Document) builder.build(xmlFile);
				Element rootNode = document.getRootElement();
				if (rootNode.getName().equals(changeItem(parentItem))) {
					Element newElement = new Element(item);
					rootNode.addContent(newElement);
				} else {
					findAndAddItem(rootNode, parentItem, item);
				}
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(document, new FileWriter(navigationView.getXmlPath()));
				System.out.println("File Saved!");
			} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
	 }
	 
	 private void findAndAddItem(Element rootNode, String parentItem, String item) {
		 	List<Element> list = rootNode.getChildren();
		 	for (Element node : list) {
		 		if (node.getName().equals(changeItem(parentItem))) {
		 			if (node.getAttributeValue("name").equals(attributeValue(parentItem))) {
		 				Element newElement = new Element(item);
		 				node.addContent(newElement);
		 				break;		 				
		 			}
		 		} else {
		 			findAndAddItem(node, parentItem, item);
		 		}
		 	}
	 }
	 
	private String changeItem(String item) {
			String[] changeItem = item.split("=");
			return changeItem[0];
	}
		
	private String attributeValue(String item) {
		if (item.contains("=")) {
			String[] changeItem = item.split("=");
			return changeItem[1];	
		} else {
			return "";
		}
	}
}