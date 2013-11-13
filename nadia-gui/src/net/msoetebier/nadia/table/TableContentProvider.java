package net.msoetebier.nadia.table;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

	public class TableContentProvider implements IStructuredContentProvider {
		private static final long serialVersionUID = 1788437726036554048L;

		@SuppressWarnings("unchecked")
		@Override
		  public Object[] getElements(Object inputElement) {
			List<String> list = (List<String>) inputElement;				
			return list.toArray();
		  }

		  @Override
		  public void dispose() {  
		  }

		  @Override
		  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		  }
}