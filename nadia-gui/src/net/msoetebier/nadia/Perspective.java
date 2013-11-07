package net.msoetebier.nadia;

import net.msoetebier.nadia.view.DetailView;
import net.msoetebier.nadia.view.NavigationView;
import net.msoetebier.nadia.view.View;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Configures the perspective layout. This class is contributed through the
 * plugin.xml.
 */
public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView(NavigationView.ID,  false, IPageLayout.LEFT, 0.25f, editorArea);
		layout.addStandaloneView(View.ID, false, IPageLayout.TOP, 0.6f, editorArea);
		layout.addStandaloneView(DetailView.ID, false, IPageLayout.BOTTOM, 0.25f, editorArea);
		layout.getViewLayout(NavigationView.ID).setCloseable(false);
	}
	
	public static IViewPart getView(IWorkbenchWindow window, String viewId) {
		IViewReference[] refs = window.getActivePage().getViewReferences();
	    for (IViewReference viewReference : refs) {
	        if (viewReference.getId().equals(viewId)) {
	            return viewReference.getView(true);
	        }
	    }
	    return null;
	}
}
