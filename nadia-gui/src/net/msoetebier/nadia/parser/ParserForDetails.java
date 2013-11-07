package net.msoetebier.nadia.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;

public class ParserForDetails {
	private Parser parser = new Parser();
	private XSParticle childParticle;
	
	public Map<Object, String> getMapForItem(String item, boolean firstLevel, String fileLocation) throws SAXException, IOException {
		Map<Object, String> map = new HashMap<Object, String>();
		XSSchemaSet schemaSet = parser.parseXMLSchemaWithXSOM(fileLocation);
		Iterator <XSElementDecl> itrElements = schemaSet.iterateElementDecls();

		while(itrElements.hasNext()) {
		XSElementDecl xsElementDecl = (XSElementDecl) itrElements.next();
			if (xsElementDecl.getType().getName().equals(item) && firstLevel == true) {
				XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
				Collection<? extends XSAttributeUse> attributes = xsComplexType.getAttributeUses();
				Iterator<? extends XSAttributeUse> i = attributes.iterator();
				while (i.hasNext()) {
					XSAttributeUse attribute = i.next();
					if (attribute != null && attribute.isRequired()) {
						map.put("required", "true");
					} else if (attribute != null && !attribute.isRequired()) {
						map.put("required", "false");
					}
				}
				map.put("name", xsElementDecl.getName());
				
				if (xsComplexType != null) {
		    	  map.put("type", xsElementDecl.getType().getName());
		    	  }
			} else if (xsElementDecl.getName().equals(item) && firstLevel == true) {
				map.put("name", xsElementDecl.getName());
				map.put("type", xsElementDecl.getType().getName());
			} else if (firstLevel == false) {
				XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
				if (xsComplexType != null) {
					XSContentType xsContentType = xsComplexType.getContentType();
				    XSParticle particle = xsContentType.asParticle();
				    childParticle = null;
				    findMap(particle, item);
				    
					if (childParticle != null) {
						if (childParticle.getTerm().asElementDecl() != null) {
							map.put("name", childParticle.getTerm().asElementDecl().getName());
							map.put("type", childParticle.getTerm().asElementDecl().getType().getName());
							map.put("minOccurs", intToString(childParticle.getMinOccurs()));
							map.put("maxOccurs", intToString(childParticle.getMaxOccurs()));
						}
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * Find the map, depending of the item
	 */
	private void findMap(XSParticle xsParticle, String item) {
		//find XSParticle, if Name = Item - else recursion
		 if(xsParticle != null) {
			 XSTerm pterm = xsParticle.getTerm();
			 
			 if(pterm.isElementDecl()) {
				 if (pterm.asElementDecl().getName().equals(item)) {
					 childParticle = xsParticle;			        	 
				 }
		         
		         XSComplexType xsComplexType =  (pterm.asElementDecl()).getType().asComplexType();
		             if (xsComplexType != null && !(pterm.asElementDecl().getType()).toString().contains("Enumeration"))
		             {
		                XSContentType xsContentType = xsComplexType.getContentType();		              
		                XSParticle xsParticleInside = xsContentType.asParticle();
		                	findMap(xsParticleInside, item);		                			                	
		             }
		      
		        } else if(pterm.isModelGroup()){
		             XSModelGroup xsModelGroup2 = pterm.asModelGroup();
		             XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
		            	 for(XSParticle xsParticleTemp : xsParticleArray){ 
			            	  findMap(xsParticleTemp, item);		            		  		            		  
			            }
		        }
		    }
	}
	
	private String intToString(int item) {
		Integer integer = new Integer(item);
		return integer.toString();
	}
}
