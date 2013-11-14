package net.msoetebier.nadia.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.msoetebier.nadia.function.DragListener;
import net.msoetebier.nadia.function.Versionsmanagement;
import net.msoetebier.nadia.language.LanguageDialog;
import net.msoetebier.nadia.language.LanguageManagement;
import net.msoetebier.nadia.table.ChooseNewOrExistingFileDialog;
import net.msoetebier.nadia.table.FileDialog;
import net.msoetebier.nadia.table.TableContentProvider;
import net.msoetebier.nadia.table.TableLabelProvider;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

/**
 * TableView with a list. This class is contributed through the plugin.xml.
 */
public class NavigationView extends ViewPart {
	public static final String ID = "nadia-gui.navigationView";	
	private TableViewer viewer;
	private String schemaPath = "", xmlPath = "", restrictionPath = "", nadiaUrlPath = "", fileSpecification = "";
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
		chooseFileSpecificationDialog();
		chooseFileDialog();
	}
	
	private Shell getCenterShell() {
		Display display = Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent();
	    Shell shell = new Shell(display);
	    shell.setSize(200, 200);
	    
	    Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    shell.setLocation(x, y);
	    return shell;
	}
	
	private void chooseLanguage() {
		LanguageDialog dialog = new LanguageDialog(getCenterShell());
		dialog.create();
		if (dialog.open() == Window.OK) {
			updateLanguageManagement(dialog.getLanguage());
		}
	}
	
	private void chooseFileSpecificationDialog() {
		ChooseNewOrExistingFileDialog dialog = new ChooseNewOrExistingFileDialog(getCenterShell(), getLanguageManagement().get("chooseFileDialog"));
		dialog.create();
		if (dialog.open() == Window.OK) {
			setFileSpecifiation(dialog.getFileSpecification());
		}
	}
	
	private void chooseFileDialog() {
		FileDialog dialog = new FileDialog(getCenterShell(), getLanguageManagement().get("fileDialog"), getFileSpecification());
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
	
	private void setFileSpecifiation(String specification) {
		fileSpecification = specification;
	}
	
	private String getFileSpecification() {
		return fileSpecification;
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