package net.msoetebier.nadia.table;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TableLabelProvider extends BaseLabelProvider implements
ITableLabelProvider {
	private static final long serialVersionUID = 3871742104315736776L;

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		return element.toString();
	}
} 