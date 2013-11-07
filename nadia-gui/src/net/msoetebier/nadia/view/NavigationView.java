package net.msoetebier.nadia.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.msoetebier.nadia.function.DragListener;
import net.msoetebier.nadia.function.Versionsmanagement;
import net.msoetebier.nadia.language.LanguageDialog;
import net.msoetebier.nadia.language.LanguageManagement;
import net.msoetebier.nadia.table.FileDialog;
import net.msoetebier.nadia.table.TableContentProvider;
import net.msoetebier.nadia.table.TableLabelProvider;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

/**
 * TableView with a list. This class is contributed through the plugin.xml.
 */
public class NavigationView extends ViewPart {
	public static final String ID = "nadia-gui.navigationView";	
	private TableViewer viewer;
	private String schemaPath = "", xmlPath = "", restrictionPath = "", nadiaUrlPath = "";
	private Map<String, Map<String, String>> languageMap = new HashMap<String, Map<String, String>>();
	
	public void createPartControl(Composite parent) {
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
	    Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
		
	    viewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.addDragSupport(operations, transferTypes , new DragListener(viewer));
		viewer.setContentProvider(new TableContentProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		setViewerInput(null);
		chooseLanguage();
		chooseFileDialog();
	}
	
	private void chooseLanguage() {
		LanguageDialog dialog = new LanguageDialog(new Shell(Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent(), SWT.NO_TRIM ));
		dialog.create();
		if (dialog.open() == Window.OK) {
			updateLanguageManagement(dialog.getLanguage());
		}
	}
	
	private void chooseFileDialog() {
		FileDialog dialog = new FileDialog(new Shell(Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent(), SWT.NO_TRIM), getLanguageManagement().get("fileDialog"));
		dialog.create();
		if (dialog.open() == Window.OK) {
		  setSchemaPath(dialog.getSchemaPath());
		  setXmlPath(dialog.getXmlPath());
		  setRestrictionPath(dialog.getRestrictionPath());
		  setNadiaUrlPath(dialog.getNadiaUrlPath());
		  addVersionsManagement();
		} 
	}
	
	private void addVersionsManagement() {
		Versionsmanagement versionsManagement = new Versionsmanagement();
		versionsManagement.versionsManagement(getSchemaPath(), getXmlPath(), getLanguageManagement().get("versionsDialog"));
	}
	
	public void updateLanguageManagement(String language) {
		LanguageManagement languageManagement = new LanguageManagement();
		setLanguageManagement(languageManagement.setMap(language));
	}
	
	private void setLanguageManagement(Map<String, Map<String, String>> map) {
		languageMap = map;
	}
	
	public Map<String, Map<String, String>> getLanguageManagement() {
		return languageMap;
	}
	
	private void setSchemaPath(String path) {
		schemaPath = path; 
	}
	
	public String getSchemaPath() {
		return schemaPath;
	}
	
	public void setXmlPath(String path) {
		xmlPath = path;
	}
	
	public String getXmlPath() {
		return xmlPath;
	}
	
	private void setRestrictionPath(String path) {
		restrictionPath = path;
	}
	
	public String getRestrictionPath() {
		return restrictionPath;
	}
	
	private void setNadiaUrlPath(String path) {
		nadiaUrlPath = path;
	}
	
	public String getNadiaUrlPath() {
		return nadiaUrlPath;
	}

	public void setViewerInput(List<String> input) {
		viewer.setInput(input);
	}
	
	public void setFocus() { 
	}
}