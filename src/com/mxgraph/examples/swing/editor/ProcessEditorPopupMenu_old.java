package com.mxgraph.examples.swing.editor;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;
import uk.nhs.digital.safetycase.ui.AddHazardAction;
import uk.nhs.digital.safetycase.ui.LinkAction;
import uk.nhs.digital.safetycase.ui.ProcessStepDetailsAction;

public class ProcessEditorPopupMenu_old extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3132749140550242191L;

        @SuppressWarnings("OverridableMethodCallInConstructor")
	public ProcessEditorPopupMenu_old(BasicGraphEditor editor)
	{
		boolean selected = !editor.getGraphComponent().getGraph()
				.isSelectionEmpty();
                if (selected) {
                    add(editor.bind("Details", new ProcessStepDetailsAction(editor.getGraphComponent().getGraph().getSelectionCell()),
                        "/com/mxgraph/examples/swing/images/collapse.gif"));
                    add(editor.bind("Hazards", new AddHazardAction(editor.getGraphComponent().getGraph().getSelectionCell()),
                        "/com/mxgraph/examples/swing/images/maximize.gif"));                    
                    add(editor.bind("Link", new LinkAction(editor.getGraphComponent().getGraph().getSelectionCell()),
				"/com/mxgraph/examples/swing/images/entity.gif"));
                } else {
                    add(editor.bind("Link", new LinkAction(),
				"/com/mxgraph/examples/swing/images/entity.gif"));
                }    
                
                addSeparator();
                
		add(editor.bind(mxResources.get("undo"), new HistoryAction(true),
				"/com/mxgraph/examples/swing/images/undo.gif"));

		addSeparator();

		add(
				editor.bind(mxResources.get("cut"), TransferHandler
						.getCutAction(),
						"/com/mxgraph/examples/swing/images/cut.gif"))
				.setEnabled(selected);
		add(
				editor.bind(mxResources.get("copy"), TransferHandler
						.getCopyAction(),
						"/com/mxgraph/examples/swing/images/copy.gif"))
				.setEnabled(selected);
		add(editor.bind(mxResources.get("paste"), TransferHandler
				.getPasteAction(),
				"/com/mxgraph/examples/swing/images/paste.gif"));

		addSeparator();

		add(
				editor.bind(mxResources.get("delete"), mxGraphActions
						.getDeleteAction(),
						"/com/mxgraph/examples/swing/images/delete.gif"))
				.setEnabled(selected);

		addSeparator();

		// Creates the format menu
		JMenu menu = (JMenu) add(new JMenu(mxResources.get("format")));

		EditorMenuBar.populateFormatMenu(menu, editor);

		// Creates the shape menu
		menu = (JMenu) add(new JMenu(mxResources.get("shape")));

		EditorMenuBar.populateShapeMenu(menu, editor);

		addSeparator();

		add(
				editor.bind(mxResources.get("edit"), mxGraphActions
						.getEditAction())).setEnabled(selected);

		addSeparator();

		add(editor.bind(mxResources.get("selectVertices"), mxGraphActions
				.getSelectVerticesAction()));
		add(editor.bind(mxResources.get("selectEdges"), mxGraphActions
				.getSelectEdgesAction()));

		addSeparator();

		add(editor.bind(mxResources.get("selectAll"), mxGraphActions
				.getSelectAllAction()));
	}

}
