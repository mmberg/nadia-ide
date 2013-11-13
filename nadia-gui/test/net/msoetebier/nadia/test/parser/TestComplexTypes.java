package net.msoetebier.nadia.test.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.test.ressources.AbstractConverter;

public class TestComplexTypes extends AbstractConverter {
	Parser parser = new Parser();
	
	@Test
	public void testGetComplexTypesForParentsInOtherFile() throws Exception {
		List<String> getComplexTypesForComputer = parser.getTypesForElement("computer", true, getOtherPath1(), true);
		List<String> complexTypesForComputer = new ArrayList<String>();
		assertEquals(complexTypesForComputer, getComplexTypesForComputer);
	}
	
	@Test 
	public void testGetComplexTypesForParentsInOldNadiaFile() throws Exception {
		List<String> getComplexTypesForDialog = parser.getTypesForElement("dialog", true, getOldNadiaPath(), true);
		List<String> complexTypesForDialog = new ArrayList<String>();
		complexTypesForDialog.add("tasks");
		assertEquals(complexTypesForDialog, getComplexTypesForDialog);
		
		List<String> getComplexTypesForGroovyAction = parser.getTypesForElement("groovyAction", true, getOldNadiaPath(), true);
		List<String> complexTypesForGroovyAction = new ArrayList<String>();
		assertEquals(complexTypesForGroovyAction, getComplexTypesForGroovyAction);
	}
	
	@Test
	public void testGetComplexTypesForParentsInNadiaFile() throws Exception {
		List<String> getComplexTypesForDialog = parser.getTypesForElement("dialog", true, getNadiaPath(), true);
		List<String> complexTypesForDialog = new ArrayList<String>();
		complexTypesForDialog.add("tasks");
		assertEquals(complexTypesForDialog, getComplexTypesForDialog);
		
		List<String> getComplexTypesForGroovyAction = parser.getTypesForElement("groovyAction", true, getNadiaPath(), true);
		List<String> complexTypesForGroovyAction = new ArrayList<String>();
		complexTypesForGroovyAction.add("resultMappings");
		assertEquals(complexTypesForGroovyAction, getComplexTypesForGroovyAction);
		
		List<String> getComplexTypesForHttpAction = parser.getTypesForElement("httpAction", true, getNadiaPath(), true);
		List<String> complexTypesForHttpAction = new ArrayList<String>();
		complexTypesForHttpAction.add("resultMappings");
		assertEquals(complexTypesForHttpAction, getComplexTypesForHttpAction);
	}
	
	@Test
	public void testGetComplexTypesForTasksInNadiaFile() throws Exception {
		List<String> getComplexTypesForTasks = parser.getTypesForElement("tasks", false, getNadiaPath(), true);
		List<String> complexTypesForTasks = new ArrayList<String>();
		complexTypesForTasks.add("task");
		assertEquals(complexTypesForTasks, getComplexTypesForTasks);
	}
	
	@Test
	public void testGetComplexTypesForTaskInNadiaFile() throws Exception {
		List<String> getComplexTypesForTask = parser.getTypesForElement("task", false, getNadiaPath(), true);
		List<String> complexTypesForTask = new ArrayList<String>();
		complexTypesForTask.add("selector");
		complexTypesForTask.add("itos");
		complexTypesForTask.add("action");
		complexTypesForTask.add("followup");
		assertEquals(complexTypesForTask, getComplexTypesForTask);
	}
	
	@Test
	public void testGetComplexTypesForSelectorInNadiaFile() throws Exception {
		List<String> getComplexTypesForSelector = parser.getTypesForElement("selector", false, getNadiaPath(), true);
		List<String> complexTypesForSelector = new ArrayList<String>();
		complexTypesForSelector.add("bagOfWordsTaskSelector");
		assertEquals(complexTypesForSelector, getComplexTypesForSelector);
	}
	
	@Test
	public void testGetComplexTypesForItosInNadiaFile() throws Exception {
		List<String> getComplexTypesForItos = parser.getTypesForElement("itos", false, getNadiaPath(), true);
		List<String> complexTypesForItos = new ArrayList<String>();
		complexTypesForItos.add("ito");
		assertEquals(complexTypesForItos, getComplexTypesForItos);
	}
	
	@Test
	public void testGetComplexTypesForItoInNadiaFile() throws Exception {
		List<String> getComplexTypesForIto = parser.getTypesForElement("ito", false, getNadiaPath(), true);
		List<String> complexTypesForIto = new ArrayList<String>();
		complexTypesForIto.add("AQD");
		assertEquals(complexTypesForIto, getComplexTypesForIto);
	}
	
	@Test
	public void testGetComplexTypesForAQDInNadiaFile() throws Exception {
		List<String> getComplexTypesForAQD = parser.getTypesForElement("AQD", false, getNadiaPath(), true);
		List<String> complexTypesForAQD = new ArrayList<String>();
		complexTypesForAQD.add("context");
		complexTypesForAQD.add("form");
		complexTypesForAQD.add("type");
		assertEquals(complexTypesForAQD, getComplexTypesForAQD);
	}
	
	@Test
	public void testGetComplexTypesForFollowupInNadiaFile() throws Exception {
		List<String> getComplexTypesForFollowup = parser.getTypesForElement("followup", false, getNadiaPath(), true);
		List<String> complexTypesForFollowup = new ArrayList<String>();
		complexTypesForFollowup.add("ito");
		complexTypesForFollowup.add("answerMapping");
		assertEquals(complexTypesForFollowup, getComplexTypesForFollowup);
	}
	
	@Test
	public void testGetComplexTypesForAnswerMappingInNadiaFile() throws Exception {
		List<String> getComplexTypesForAnswerMapping = parser.getTypesForElement("answerMapping", false, getNadiaPath(), true);
		List<String> complexTypesForAnswerMapping = new ArrayList<String>();
		complexTypesForAnswerMapping.add("item");
		assertEquals(complexTypesForAnswerMapping, getComplexTypesForAnswerMapping);
	}
}
