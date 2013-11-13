package net.msoetebier.nadia.parser;

import java.io.IOException;
import java.util.Iterator;

import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;

public class ParserForChildren {
	private Parser parser = new Parser();
	private XSParticle childParticle;

	/**
	 * Has the item children? Iterate over all elements
	 * @return true for yes, or false for no
	 */
	public boolean checkHasChildren(String item, boolean firstLevel, String fileLocation) throws SAXException, IOException {
		boolean children = false;
		XSSchemaSet schemaSet = parser.parseXMLSchemaWithXSOM(fileLocation);
		Iterator <XSElementDecl> itrElements = schemaSet.iterateElementDecls();
		
		while(itrElements.hasNext()) {
			XSElementDecl xsElementDecl = (XSElementDecl) itrElements.next();
				if (xsElementDecl.getType().getName().equals(item) && firstLevel == true) {
					//check if it's a complexType
					XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
					if (xsComplexType != null) {
			    	  XSContentType xsContentType = xsComplexType.getContentType();
			    	  XSParticle particle = xsContentType.asParticle();
			    	  //if it's a complexType, check if it has children
			    	  children = hasChildren(particle);
			    	  }
				} else if (xsElementDecl.getName().equals(item) && firstLevel == true) {
					children = false;
				} else if (firstLevel == false) {
					XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
					if (xsComplexType != null) {
						XSContentType xsContentType = xsComplexType.getContentType();
					    XSParticle particle = xsContentType.asParticle();
					    childParticle = null;
					    childParticle = parser.getParticle(particle, item); //new
					    //parser.findParticle(particle, item); //find particle
						if (childParticle != null) {
							children = hasChildren(childParticle);							
						}
					}
				}
		}
		return children;
	}

	private boolean hasChildren(XSParticle xsParticle) {
		 if(xsParticle != null){
			 XSTerm pterm = xsParticle.getTerm();
			 
			 if(pterm.isElementDecl()) {
				 if (pterm.asElementDecl().getName() != null) {
					 return true;				 
				 }
		        } else if(pterm.isModelGroup()){
		             XSModelGroup xsModelGroup2 = pterm.asModelGroup();
		             XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
		             if (xsParticleArray.length != 0) {
		            	 return true;
		             }
		        }
		    }
		return false;
	}
}
