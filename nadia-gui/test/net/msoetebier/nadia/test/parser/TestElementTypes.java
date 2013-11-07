package net.msoetebier.nadia.test.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.test.ressources.AbstractConverter;

public class TestElementTypes extends AbstractConverter {
	Parser parser = new Parser();
	
	@Test
	public void testGetElementTypesForParentsInOtherFile() throws Exception {
		List<String> getElementTypesForComputer = parser.getTypesForElement("computer", true, getOtherPath1(), false);
		List<String> elementTypesForComputer = new ArrayList<String>();
		assertEquals(elementTypesForComputer, getElementTypesForComputer);
	}
	
	@Test 
	public void testGetElementTypesForParentsInOldNadiaFile() throws Exception {
		List<String> getElementTypesForDialog = parser.getTypesForElement("dialog", true, getOldNadiaPath(), false);
		List<String> elementTypesForDialog = new ArrayList<String>();
		elementTypesForDialog.add("global_formality");
		elementTypesForDialog.add("global_language");
		elementTypesForDialog.add("global_politeness");
		elementTypesForDialog.add("start_task_name");
		elementTypesForDialog.add("strategy");
		assertEquals(elementTypesForDialog, getElementTypesForDialog);
		
		List<String> getElementTypesForGroovyAction = parser.getTypesForElement("groovyAction", true, getOldNadiaPath(), false);
		List<String> elementTypesForGroovyAction = new ArrayList<String>();
		elementTypesForGroovyAction.add("returnAnswer");
		elementTypesForGroovyAction.add("utteranceTemplate");
		elementTypesForGroovyAction.add("code");
		assertEquals(elementTypesForGroovyAction, getElementTypesForGroovyAction);
	}
	
	@Test
	public void testGetElementTypesForParentsInNadiaFile() throws Exception {
		List<String> getElementTypesForDialog = parser.getTypesForElement("dialog", true, getNadiaPath(), false);
		List<String> elementTypesForDialog = new ArrayList<String>();
		elementTypesForDialog.add("start_task_name");
		elementTypesForDialog.add("global_language");
		elementTypesForDialog.add("global_politeness");
		elementTypesForDialog.add("global_formality");
		elementTypesForDialog.add("useSODA");
		elementTypesForDialog.add("allowSwitchTasks");
		elementTypesForDialog.add("allowOverAnswering");
		elementTypesForDialog.add("allowDifferentQuestion");
		elementTypesForDialog.add("allowCorrection");
		assertEquals(elementTypesForDialog, getElementTypesForDialog);
		
		List<String> getElementTypesForGroovyAction = parser.getTypesForElement("groovyAction", true, getNadiaPath(), false);
		List<String> elementTypesForGroovyAction = new ArrayList<String>();
		elementTypesForGroovyAction.add("returnAnswer");
		elementTypesForGroovyAction.add("utteranceTemplate");
		elementTypesForGroovyAction.add("code");
		assertEquals(elementTypesForGroovyAction, getElementTypesForGroovyAction);
		
		List<String> getElementTypesForHttpAction = parser.getTypesForElement("httpAction", true, getNadiaPath(), false);
		List<String> elementTypesForHttpAction = new ArrayList<String>();
		elementTypesForHttpAction.add("returnAnswer");
		elementTypesForHttpAction.add("utteranceTemplate");
		elementTypesForHttpAction.add("method");
		elementTypesForHttpAction.add("params");
		elementTypesForHttpAction.add("url");
		elementTypesForHttpAction.add("xpath");
		assertEquals(elementTypesForHttpAction, getElementTypesForHttpAction);
	}

	@Test
	public void testGetElementTypesForTasksInNadiaFile() throws Exception {
		List<String> getElementTypesForTasks = parser.getTypesForElement("tasks", false, getNadiaPath(), false);
		List<String> elementTypesForTasks = new ArrayList<String>();
		assertEquals(elementTypesForTasks, getElementTypesForTasks);
	}
	
	@Test
	public void testGetElementTypesForTaskInNadiaFile() throws Exception {
		List<String> getElementTypesForTask = parser.getTypesForElement("task", false, getNadiaPath(), false);
		List<String> elementTypesForTask = new ArrayList<String>();
		elementTypesForTask.add("domain");
		elementTypesForTask.add("act");
		assertEquals(elementTypesForTask, getElementTypesForTask);
	}
	
	@Test
	public void testGetElementTypesForItoInNadiaFile() throws Exception {
		List<String> getElementTypesForIto = parser.getTypesForElement("ito", false, getNadiaPath(), false);
		List<String> elementTypesForIto = new ArrayList<String>();
		elementTypesForIto.add("fallback_question");
		elementTypesForIto.add("group");
		elementTypesForIto.add("index");
		elementTypesForIto.add("required");
		elementTypesForIto.add("useLG");
		assertEquals(elementTypesForIto, getElementTypesForIto);
	}
	
	@Test
	public void testGetElementTypesForContextInNadiaFile() throws Exception {
		List<String> getElementTypesForContext = parser.getTypesForElement("context", false, getNadiaPath(), false);
		List<String> elementTypesForContext = new ArrayList<String>();
		elementTypesForContext.add("reference");
		elementTypesForContext.add("specification");
		assertEquals(elementTypesForContext, getElementTypesForContext);
	}
	
	@Test
	public void testGetElementTypesForFormInNadiaFile() throws Exception {
		List<String> getElementTypesForForm = parser.getTypesForElement("form", false, getNadiaPath(), false);
		List<String> elementTypesForForm = new ArrayList<String>();
		elementTypesForForm.add("formality");
		elementTypesForForm.add("politeness");
		elementTypesForForm.add("temporalOpener");
		assertEquals(elementTypesForForm, getElementTypesForForm);
	}
	
	@Test
	public void testGetElementTypesForTypeInNadiaFile() throws Exception {
		List<String> getElementTypesForType = parser.getTypesForElement("type", false, getNadiaPath(), false);
		List<String> elementTypesForType = new ArrayList<String>();
		elementTypesForType.add("answerType");
		assertEquals(elementTypesForType, getElementTypesForType);
	}
}