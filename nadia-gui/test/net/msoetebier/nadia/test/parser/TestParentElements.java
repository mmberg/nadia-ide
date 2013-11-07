package net.msoetebier.nadia.test.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.test.ressources.AbstractConverter;

import org.junit.Test;

public class TestParentElements extends AbstractConverter {
	Parser parser = new Parser();

	@Test
	public void testFindParentElementsForSchemaFile() throws Exception {
		List<String> getParentElements = new ArrayList<String>();
			getParentElements = parser.getParentElements(getOtherPath1());
		List<String> parentElements = new ArrayList<String>();
		parentElements.add("computer");
		assertEquals(parentElements, getParentElements);
	}
	
	@Test 
	public void testFindParentsForOldNadiaFile() throws Exception {
		List<String> getParentElements = parser.getParentElements(getOldNadiaPath());
		List<String> parentElements = new ArrayList<String>();
		parentElements.add("groovyAction");
		parentElements.add("javaAction");
		parentElements.add("dialog");
		parentElements.add("bagOfWordsTaskSelector");
		assertEquals(parentElements, getParentElements);
	}
	
	@Test
	public void testFindParentsForNadiaFile() throws Exception {
		List<String> getParentElements = parser.getParentElements(getNadiaPath());
		List<String> parentElements = new ArrayList<String>();
		parentElements.add("dialog");
		parentElements.add("groovyAction");
		parentElements.add("javaAction");
		parentElements.add("httpAction");
		parentElements.add("bagOfWordsTaskSelector");
		parentElements.add("followUpModel");
		assertEquals(parentElements, getParentElements);
	}
}
