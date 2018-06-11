/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.safetycase.ui.systemeditor;

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
import uk.nhs.digital.safetycase.ui.DiagramEditorElement;
import uk.nhs.digital.safetycase.ui.GraphicalEditor;
import uk.nhs.digital.safetycase.ui.SystemSaveHandler;

/**
 *
 * @author SHUL1
 */

public class SystemGraphEditor 
        extends BasicGraphEditor 
        implements DataNotificationSubscriber, GraphicalEditor
{

    private int systemId = -1;
    
    private HashMap<String,DiagramEditorElement> existingSystem = null; 
     public void setExistingGraph(HashMap<String,DiagramEditorElement> ex) 
     {
         existingSystem = ex;
     }
    public HashMap<String,DiagramEditorElement> getExistingGraph() { return existingSystem; }
    
    public int getSystemId() {
        return systemId;
    }

    @Override
    public void unsubscribe() {
        SmartProject.getProject().removeNotificationSubscriber(this);
    }
    
    public void setSystemId(int i, String x) {
        systemId = i;
        getGraphComponent().getGraph().setModel(new mxGraphModel());
        getGraphComponent().getGraph().refresh();

        if ((x == null) || (x.trim().length() == 0)) {
            return;
        }
        Document document = mxXmlUtils.parseXml(x);
        mxCodec codec = new mxCodec(document);
        codec.decode(document.getDocumentElement(), getGraphComponent().getGraph().getModel());
        setModified(false);
        getUndoManager().clear();
        getGraphComponent().zoomAndCenter();
    }

    @Override
    protected void showGraphPopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                graphComponent);
        SystemEditorPopupMenu menu = new SystemEditorPopupMenu(this);
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

    public SystemGraphEditor(int s) {
        this("mxGraph Editor", new CustomGraphComponent(new CustomGraph(), s));
        init(this.graphComponent);
        systemId = s;
        
    }

    /**
     *
     */
    public SystemGraphEditor(String appTitle, mxGraphComponent component) {
        super(appTitle, component);
//        init(component);
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
        EditorPalette systemPalette = insertPalette("System/System Function");
        // Sets the edge template to be used for creating new edges if an edge
        // is clicked in the shape palette
        systemPalette.addListener(mxEvent.SELECT, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                Object tmp = evt.getProperty("transferable");

                if (tmp instanceof mxGraphTransferable) {
                    mxGraphTransferable t = (mxGraphTransferable) tmp;
                    Object cell = t.getCells()[0];

                    if (graph.getModel().isEdge(cell)) {
                        ((CustomGraph) graph).setEdgeTemplate(cell);
                    }
                }
            }

        });

        // Adds some template cells for dropping into the graph
        systemPalette
                .addTemplate(
                        "System",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/uk/nhs/digital/safetycase/ui/systemeditor/system.png")),
						"image;image=/uk/nhs/digital/safetycase/ui/systemeditor/system.png;whiteSpace=wrap",50, 50, "System");

//        systemPalette
//                .addTemplate(
//                        "System",
//                        new ImageIcon(
//                                GraphEditor.class
//                                        .getResource("/uk/nhs/digital/safetycase/ui/systemeditor/subsystem.png")),
//						"image;image=/uk/nhs/digital/safetycase/ui/systemeditor/subsystem.png",50, 50, "System");
        systemPalette
                .addTemplate(
                        "Function",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/uk/nhs/digital/safetycase/ui/systemeditor/SystemFunction.png")),
						"image;image=/uk/nhs/digital/safetycase/ui/systemeditor/SystemFunction.png;whiteSpace=wrap",50, 50, "Function");
//        systemPalette
//                .addTemplate(
//                        "SystemFunction",
//                        new ImageIcon(
//                                GraphEditor.class
//                                        .getResource("/uk/nhs/digital/safetycase/ui/systemeditor/subfunction.png")),
//						"image;image=/uk/nhs/digital/safetycase/ui/systemeditor/subfunction.png",50, 50, "SystemFunction");
        systemPalette
                .addEdgeTemplate("Straight",
                        new ImageIcon(
                                SystemGraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/straight.png")),
                        "straight", 120, 120, "");
        systemPalette
                .addEdgeTemplate("Horizontal Connector",
                        new ImageIcon(
                                SystemGraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/connect.png")),
                        null, 100, 100, "");
        systemPalette
                .addEdgeTemplate("Vertical Connector",
                        new ImageIcon(
                                SystemGraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/vertical.png")),
                        "vertical", 100, 100, "");

    }

    @Override
    public boolean notification(int evtype, Object o) {
        if (evtype == SmartProject.SAVE) {
            SystemSaveHandler ssh = new SystemSaveHandler();
            try {
                ssh.handle(this);
            }
            catch (Exception e) {
                SmartProject.getProject().log("Failed to save system diagram from SaveAll request", e);
            }
        }
        return false;
    }

    @Override
    public JPanel getEditor(Object o) {
        try {
            uk.nhs.digital.safetycase.data.System h = (uk.nhs.digital.safetycase.data.System)o;
            if (h.getId() == systemId)
                return this;
        }
        catch (Exception e) {}
        return null;
    }

}
