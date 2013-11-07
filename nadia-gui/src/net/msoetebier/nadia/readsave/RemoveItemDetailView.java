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

public class RemoveItemDetailView {

	 public void removeItemFromXmlFile(int countElements, int countItem, String item) {
		 	NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
			File xmlFile = new File(navigationView.getXmlPath());	 
			SAXBuilder builder = new SAXBuilder();
			try {
				Document document = (Document) builder.build(xmlFile);
				Element rootNode = document.getRootElement();
				if (rootNode.getName().equals(item)) {
					if (countItem == 1) {
						rootNode.detach();						
					}
					countItem = countItem - 1;
				} else {
					findAndRemoveItem(countItem, rootNode, item);
				}
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(document, new FileWriter(navigationView.getXmlPath()));
				System.out.println("File Saved!");
			 } catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
	 }
	 
	 private void findAndRemoveItem(int countItem, Element children, String item) {
			List<Element> list = children.getChildren();
			for (Element element : list) {
				if (element.getName().equals(item)) {
					if (countItem == 1) {
						element.detach();
						break;
					}
					countItem = countItem - 1;
				} else {
					findAndRemoveItem(countItem,element, item);
				}
			}
	 }
}
