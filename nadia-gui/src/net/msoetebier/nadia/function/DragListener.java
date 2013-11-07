package net.msoetebier.nadia.function;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

public class DragListener implements DragSourceListener {
	private static final long serialVersionUID = -3051180492791097709L;
	private final TableViewer viewer;

	  public DragListener(TableViewer viewer) {
	    this.viewer = viewer;
	  }
	  
	  @Override
	  public void dragStart(DragSourceEvent event) {
	  }

	  @Override
	  public void dragFinished(DragSourceEvent event) {
	  }
	  
	  @Override
	  public void dragSetData(DragSourceEvent event) {
	    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
	    String firstElement = (String) selection.getFirstElement();
	    if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
	    	event.data = firstElement;
	    }
	  }
	} 