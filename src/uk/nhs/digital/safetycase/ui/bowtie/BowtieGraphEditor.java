/**
 * Copyright (c) 2006-2012, JGraph Ltd */
package uk.nhs.digital.safetycase.ui.bowtie;

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
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.ui.DiagramEditorElement;
import uk.nhs.digital.safetycase.ui.GraphicalEditor;

public class BowtieGraphEditor 
        extends BasicGraphEditor
        implements DataNotificationSubscriber, GraphicalEditor
{
    private int hazardId = -1;
    private ProcessStep processStep = null;
    private HashMap<String,DiagramEditorElement> existingBowtie = null; 
//    private HazardListForm hazardListForm = null;
    
//    public void setHazardListForm(HazardListForm hlf) { hazardListForm = hlf; }
//    public HazardListForm getHazardListForm() { return hazardListForm; }
    public int getHazardId() { return hazardId; }
    public void setExistingBowtie(HashMap<String,DiagramEditorElement> ex) { existingBowtie = ex; }
    public HashMap<String,DiagramEditorElement> getExistingBowtie() { return existingBowtie; }
    
    public void setHazardId(int i, String x) { 
        hazardId = i; 
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
		BowtieEditorPopupMenu menu = new BowtieEditorPopupMenu(this);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}
 
    @Override
    public void unsubscribe() {
        SmartProject.getProject().removeNotificationSubscriber(this);
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

	public BowtieGraphEditor(int h)
	{
		super("mxGraph Editor", new CustomGraphComponent(new CustomGraph(), h));
                init(this.graphComponent);
                hazardId = h;
                
	}

        public void setProcessStep(ProcessStep p) {
            processStep = p;
        }
        
        public ProcessStep getProcessStep() { return processStep; }
	/**
	 * 
	 */
	public BowtieGraphEditor(String appTitle, mxGraphComponent component)
	{
		super(appTitle, component);
                init(component);
        }
        private void init(mxGraphComponent component) {
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
                EditorPalette bowtiePalette = insertPalette("Bowtie");
		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		bowtiePalette.addListener(mxEvent.SELECT, new mxIEventListener()
		{
                        @Override
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
                bowtiePalette
				.addTemplate("Hazard",
						new ImageIcon(
								BowtieGraphEditor.class
										.getResource("/uk/nhs/digital/safetycase/ui/bowtie/hazard.png")),
						"image;image=/uk/nhs/digital/safetycase/ui/bowtie/hazard.png;whiteSpace=wrap",
						50, 50, "Hazard");
		bowtiePalette
				.addTemplate("Cause",
						new ImageIcon(
								BowtieGraphEditor.class
										.getResource("/uk/nhs/digital/safetycase/ui/bowtie/cause.png")),
						"image;image=/uk/nhs/digital/safetycase/ui/bowtie/cause.png;whiteSpace=wrap",
						50, 50, "Cause");
		bowtiePalette
				.addTemplate("Control",
						new ImageIcon(
								BowtieGraphEditor.class
										.getResource("/uk/nhs/digital/safetycase/ui/bowtie/control.jpg")),
						"image;image=/uk/nhs/digital/safetycase/ui/bowtie/control.jpg;whiteSpace=wrap",
						50, 50, "Control");
		bowtiePalette
				.addTemplate("Effect",
						new ImageIcon(
								BowtieGraphEditor.class
										.getResource("/uk/nhs/digital/safetycase/ui/bowtie/effect.png")),
						"image;image=/uk/nhs/digital/safetycase/ui/bowtie/effect.png;whiteSpace=wrap",
						50, 50, "Effect");
		bowtiePalette
				.addEdgeTemplate("Straight",
						new ImageIcon(
								BowtieGraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/straight.png")),
						"straight", 120, 120, "");
		bowtiePalette
				.addEdgeTemplate("Horizontal Connector",
						new ImageIcon(
								BowtieGraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/connect.png")),
						null, 100, 100, "");
		bowtiePalette
				.addEdgeTemplate("Vertical Connector",
						new ImageIcon(
								BowtieGraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/vertical.png")),
						"vertical", 100, 100, "");

	}

    @Override
    public boolean notification(int evtype, Object o) {
        return false;
    }
    
    @Override
    public JPanel getEditor(Object o) {
        try {
            
            Hazard h = (Hazard)o;
            if (h.getId() == hazardId)
                return this;
        }
        catch (Exception e) {}
        return null;
    }


	/**
	 * 
	 * @param args
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

		BowtieGraphEditor editor = new BowtieGraphEditor();
		editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
	}
        * */
}
