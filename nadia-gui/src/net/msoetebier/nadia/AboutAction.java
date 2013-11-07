package net.msoetebier.nadia;

import java.util.Map;

import net.msoetebier.nadia.view.NavigationView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Opens a message dialog. Shows information about the author.
 */
public class AboutAction extends Action {

	private static final long serialVersionUID = 7324782547637007654L;
	private final IWorkbenchWindow window;

	public AboutAction(IWorkbenchWindow window) {
		super("About");
		setId(this.getClass().getName());
		this.window = window;
	}

	public void run() {
		if(window != null) {
			NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
  			Map<String, String> aboutActionMap = navigationView.getLanguageManagement().get("aboutAction");
			String title = aboutActionMap.get("title");
			String msg = aboutActionMap.get("message");
			MessageDialog.openInformation(window.getShell(), title, msg);
		}
	}
}