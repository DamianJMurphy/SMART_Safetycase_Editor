/**
 * Copyright (c) 2006-2012, JGraph Ltd */
package uk.nhs.digital.safetycase.ui.processeditor;

import java.awt.Point;
import java.net.URL;
import java.text.NumberFormat;
import javax.swing.ImageIcon;

import org.w3c.dom.Document;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import uk.nhs.digital.projectuiframework.DataNotificationSubscriber;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.CustomGraph;
import uk.nhs.digital.projectuiframework.ui.CustomGraphComponent;
import uk.nhs.digital.projectuiframework.ui.SaveRejectedException;
import uk.nhs.digital.safetycase.ui.DiagramEditorElement;
import uk.nhs.digital.safetycase.ui.GraphicalEditor;
import uk.nhs.digital.safetycase.ui.ProcessSaveHandler;

public class ProcessGraphEditor 
        extends BasicGraphEditor
        implements DataNotificationSubscriber, GraphicalEditor
{
    private int processId = -1;
    private HashMap<String,DiagramEditorElement> existingSteps = null;
    
    public void setExistingSteps(HashMap<String,DiagramEditorElement> ex) { existingSteps = ex; }
    public HashMap<String,DiagramEditorElement> getExistingSteps() { return existingSteps; }
    
    public int getProcessId() { return processId; }

    @Override
    public boolean notification(int evtype, Object o) 
            throws SaveRejectedException    
    {
        if (evtype == SmartProject.SAVE) {
            ProcessSaveHandler psh = new ProcessSaveHandler();
            try {
                psh.handle(this);
            }
            catch (SaveRejectedException sre) { throw sre; }
            catch (Exception e) {
                SmartProject.getProject().log("Failed to save process diagram from SaveAll request", e);
            }
        }
        return false;
    }
    @Override
    public void unsubscribe() {
        SmartProject.getProject().removeNotificationSubscriber(this);
    }

    @Override
    public JPanel getEditor(Object o) {
        try {
            uk.nhs.digital.safetycase.data.Process h = (uk.nhs.digital.safetycase.data.Process)o;
            if (h.getId() == processId)
                return this;
        }
        catch (Exception e) {}
        return null;
    }

    
    public void setProcessId(int i, String x) { 
        processId = i; 
        getGraphComponent().getGraph().setModel(new mxGraphModel());
        getGraphComponent().getGraph().refresh();
        
        if ((x == null) || (x.trim().length() == 0))
            return;
        Document document = mxXmlUtils.parseXml(x);
        mxCodec codec = new mxCodec(document);
	codec.decode(document.getDocumentElement(), getGraphComponent().getGraph().getModel());        
        setModified(false);
        getUndoManager().clear();
        getGraphComponent().zoomAndCenter();        
    }
    @Override
	protected void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		ProcessEditorPopupMenu menu = new ProcessEditorPopupMenu(this);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -4601740824088314699L;

	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Holds the URL for the icon to be used as a handle for creating new
	 * connections. This is currently unused.
	 */
	public static URL url = null;
	//GraphEditor.class.getResource("/com/mxgraph/examples/swing/images/connector.gif");

	public ProcessGraphEditor(int p)
	{
		this("mxGraph Editor", new CustomGraphComponent(new CustomGraph(), p));
                init(this.graphComponent);
                processId = p;
	}

	/**
	 * 
	 */
	public ProcessGraphEditor(String appTitle, mxGraphComponent component)
	{
		super(appTitle, component);
//                init(component);
        }
        private void init(mxGraphComponent component) 
        {
                PageFormat format = new PageFormat();
                format.setOrientation(PageFormat.LANDSCAPE);
                component.setPageFormat(format);
                component.setAutoExtend(true);
                
		final mxGraph graph = graphComponent.getGraph();
                SmartProject.getProject().addNotificationSubscriber(this);
		// Creates the shapes palette
//		EditorPalette shapesPalette = insertPalette(mxResources.get("shapes"));
//		EditorPalette imagesPalette = insertPalette(mxResources.get("images"));
//		EditorPalette symbolsPalette = insertPalette(mxResources.get("symbols"));
                EditorPalette processPalette = insertPalette("Process steps");
		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		processPalette.addListener(mxEvent.SELECT, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable)
				{
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell))
					{
						((CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		// Adds some template cells for dropping into the graph
		processPalette
				.addTemplate("Start",
						new ImageIcon(
								ProcessGraphEditor.class
										.getResource("/uk/nhs/digital/safetycase/ui/processeditor/start.png")),
						"roundImage;image=/uk/nhs/digital/safetycase/ui/processeditor/start.png",
						80, 80, "Start");

                processPalette
				.addTemplate("Stop",
						new ImageIcon(
								ProcessGraphEditor.class
										.getResource("/uk/nhs/digital/safetycase/ui/processeditor/stop.png")),
						"roundImage;image=/uk/nhs/digital/safetycase/ui/processeditor/stop.png",
						80, 80, "Stop");
		processPalette
				.addTemplate("Activity",
						new ImageIcon(
								ProcessGraphEditor.class
										.getResource("/uk/nhs/digital/safetycase/ui/processeditor/activity.png")),
						null, 160, 120, "activity");
		processPalette
				.addTemplate("Decision",
						new ImageIcon(
								ProcessGraphEditor.class
										.getResource("/uk/nhs/digital/safetycase/ui/processeditor/decision.png")),
						"rhombus", 160, 160, "");
		processPalette
				.addEdgeTemplate("Straight",
						new ImageIcon(
								ProcessGraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/straight.png")),
						"straight", 120, 120, "");
		processPalette
				.addEdgeTemplate("Horizontal Connector",
						new ImageIcon(
								ProcessGraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/connect.png")),
						null, 100, 100, "");
		processPalette
				.addEdgeTemplate("Vertical Connector",
						new ImageIcon(
								ProcessGraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/vertical.png")),
						"vertical", 100, 100, "");

	}
}
