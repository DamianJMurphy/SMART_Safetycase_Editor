/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.projectuiframework.ui;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Point;
import org.w3c.dom.Document;
import uk.nhs.digital.safetycase.ui.bowtie.BowtieGraphEditor;

/**
 *
 * @author damian
 */
	/**
	 * 
	 */
//	public static class CustomGraphComponent extends mxGraphComponent
        public class CustomGraphComponent extends mxGraphComponent        
	{
                private int objectId = -1;
		/**
		 * 
		 */
		private static final long serialVersionUID = -6833603133512882012L;
                public int getObjectId() { return objectId; }
		/**
		 * 
		 * @param graph
		 */
		public CustomGraphComponent(mxGraph graph, int id)
		{
			super(graph);
                        objectId = id;
			// Sets switches typically used in an editor
			setPageVisible(true);
			setGridVisible(true);
			setToolTips(true);
			getConnectionHandler().setCreateTarget(true);

			// Loads the defalt stylesheet from an external file
			mxCodec codec = new mxCodec();
                        String s = BowtieGraphEditor.class.getResource(
					"/com/mxgraph/examples/swing/resources/default-style.xml")
					.toString();
			Document doc = mxUtils.loadDocument(s);
			codec.decode(doc.getDocumentElement(), graph.getStylesheet());

			// Sets the background to white
			getViewport().setOpaque(true);
			getViewport().setBackground(Color.WHITE);
		}
//                public int getHazardId() { return hazardId; }
		/**
		 * Overrides drop behaviour to set the cell style if the target
		 * is not a valid drop target and the cells are of the same
		 * type (eg. both vertices or both edges). 
		 */
		public Object[] importCells(Object[] cells, double dx, double dy,
				Object target, Point location)
		{
			if (target == null && cells.length == 1 && location != null)
			{
				target = getCellAt(location.x, location.y);

				if (target instanceof mxICell && cells[0] instanceof mxICell)
				{
					mxICell targetCell = (mxICell) target;
					mxICell dropCell = (mxICell) cells[0];

					if (targetCell.isVertex() == dropCell.isVertex()
							|| targetCell.isEdge() == dropCell.isEdge())
					{
						mxIGraphModel model = graph.getModel();
						model.setStyle(target, model.getStyle(cells[0]));
						graph.setSelectionCell(target);

						return null;
					}
				}
			}

			return super.importCells(cells, dx, dy, target, location);
		}

	}

