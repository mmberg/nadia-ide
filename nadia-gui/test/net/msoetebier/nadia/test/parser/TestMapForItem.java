package net.msoetebier.nadia.test.parser;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import net.msoetebier.nadia.parser.ParserForDetails;
import net.msoetebier.nadia.test.ressources.AbstractConverter;

public class TestMapForItem extends AbstractConverter {
	ParserForDetails parserForDetails = new ParserForDetails();

	@Test
	public void testMapForDialog() throws Exception {
		Map<Object, String> getMapForDialog = parserForDetails.getMapForItem("dialog", true, getNadiaPath());
		assertEquals("dialog", getMapForDialog.get("name"));
		assertEquals("dialog", getMapForDialog.get("type"));
		assertEquals("false", getMapForDialog.get("required"));
	}
	
	@Test
	public void testMapForTasks() throws Exception {
		Map<Object, String> getMapForTasks = parserForDetails.getMapForItem("tasks", false, getNadiaPath());
		assertEquals("tasks", getMapForTasks.get("name"));
	}
	
	@Test
	public void testMapForStartTaskName() throws Exception {
		Map<Object, String> getMapForStartTaskName = parserForDetails.getMapForItem("start_task_name", true, getNadiaPath());
		assertEquals("start_task_name", getMapForStartTaskName.get("name"));
		assertEquals("start_task_name", getMapForStartTaskName.get("type"));
		assertEquals("false", getMapForStartTaskName.get("required"));
	}
	
	@Test
	public void testMapForReturnAnswer() throws Exception {
		Map<Object, String> getMapForReturnAnswer = parserForDetails.getMapForItem("returnAnswer", false, getNadiaPath());
		assertEquals("returnAnswer", getMapForReturnAnswer.get("name"));
		assertEquals("boolean", getMapForReturnAnswer.get("type"));
		assertEquals("1", getMapForReturnAnswer.get("minOccurs"));
		assertEquals("1", getMapForReturnAnswer.get("maxOccurs"));
	}
	
	@Test
	public void testMapForDomain() throws Exception {
		Map<Object, String> getMapForDomain = parserForDetails.getMapForItem("domain", false, getNadiaPath());
		assertEquals("domain", getMapForDomain.get("name"));
		assertEquals("string", getMapForDomain.get("type"));
		assertEquals("0", getMapForDomain.get("minOccurs"));
		assertEquals("1", getMapForDomain.get("maxOccurs"));
	}
	
	@Test
	public void testMapForFallbackQuestion() throws Exception {
		Map<Object, String> getMapForFallbackQuestion = parserForDetails.getMapForItem("fallback_question", false, getNadiaPath());
		assertEquals("fallback_question", getMapForFallbackQuestion.get("name"));
		assertEquals("string", getMapForFallbackQuestion.get("type"));
		assertEquals("0", getMapForFallbackQuestion.get("minOccurs"));
		assertEquals("1", getMapForFallbackQuestion.get("maxOccurs"));
	}
	
	@Test
	public void testMapForReference() throws Exception {
		Map<Object, String> getMapForReference = parserForDetails.getMapForItem("reference", false, getNadiaPath());
		assertEquals("reference", getMapForReference.get("name"));
		assertEquals("string", getMapForReference.get("type"));
		assertEquals("0", getMapForReference.get("minOccurs"));
		assertEquals("1", getMapForReference.get("maxOccurs"));
	}
}