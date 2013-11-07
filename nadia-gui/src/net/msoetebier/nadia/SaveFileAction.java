package net.msoetebier.nadia;

import java.util.Map;

import net.msoetebier.nadia.view.NavigationView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Opens a message dialog. Shows information about the save file action.
 */
public class SaveFileAction extends Action {

	private static final long serialVersionUID = 7528105144343527322L;
	private final IWorkbenchWindow window;

	public SaveFileAction(IWorkbenchWindow window) {
		super("How to save this");
		setId(this.getClass().getName());
		this.window = window;
	}

	public void run() {
		if(window != null) {	
			NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
  			Map<String, String> saveFileActionMap = navigationView.getLanguageManagement().get("saveFileAction");
			
			String title = saveFileActionMap.get("title");
			String msg = saveFileActionMap.get("message"); 
			MessageDialog.openInformation(window.getShell(), title, msg);
		}
	}
}
