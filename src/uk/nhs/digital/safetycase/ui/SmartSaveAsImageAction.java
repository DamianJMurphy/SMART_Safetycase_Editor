/*
 * 
 *   Copyright 2017  NHS Digital
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *  
 */
package uk.nhs.digital.safetycase.ui;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.DefaultFileFilter;
import static com.mxgraph.examples.swing.editor.EditorActions.getEditor;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author damian
 */
public class SmartSaveAsImageAction 
    extends AbstractAction
{
    private static String currentFile = null;
    private static String lastDir = null;
    
    public SmartSaveAsImageAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String filename = null;
        BasicGraphEditor editor = getEditor(e);

	if (editor != null) {
            mxGraphComponent graphComponent = editor.getGraphComponent();
            mxGraph graph = graphComponent.getGraph();
            FileFilter selectedFilter = null;
            DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png", "PNG+XML " + mxResources.get("file") + " (.png)");
            FileFilter vmlFileFilter = new DefaultFileFilter(".html", "VML " + mxResources.get("file") + " (.html)");
            boolean dialogShown = false;

            String wd;

            if (lastDir != null) {
		wd = lastDir;
            } else {
		wd = System.getProperty("user.dir");
            }

            JFileChooser fc = new JFileChooser(wd);

            // Adds the default file format
            FileFilter defaultFilter = xmlPngFilter;
            fc.addChoosableFileFilter(defaultFilter);

            // Adds special vector graphics formats and HTML
            fc.addChoosableFileFilter(new DefaultFileFilter(".mxe", "mxGraph Editor " + mxResources.get("file") + " (.mxe)"));
            fc.addChoosableFileFilter(new DefaultFileFilter(".svg", "SVG " + mxResources.get("file") + " (.svg)"));
            fc.addChoosableFileFilter(vmlFileFilter);
            fc.addChoosableFileFilter(new DefaultFileFilter(".html", "HTML " + mxResources.get("file") + " (.html)"));

            // Adds a filter for each supported image format
            Object[] imageFormats = ImageIO.getReaderFormatNames();

            // Finds all distinct extensions
            HashSet<String> formats = new HashSet<>();

            for (int i = 0; i < imageFormats.length; i++) {
		String ext = imageFormats[i].toString().toLowerCase();
		formats.add(ext);
            }

            imageFormats = formats.toArray();

            for (int i = 0; i < imageFormats.length; i++) {
		String ext = imageFormats[i].toString();
		fc.addChoosableFileFilter(new DefaultFileFilter("." + ext, ext.toUpperCase() + " " + mxResources.get("file") + " (." + ext + ")"));
            }

            // Adds filter that accepts all supported image formats
            fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(mxResources.get("allImages")));
            fc.setFileFilter(defaultFilter);
            int rc = fc.showDialog(null, mxResources.get("save"));
            dialogShown = true;

            if (rc != JFileChooser.APPROVE_OPTION) {
		return;
            } else {
                lastDir = fc.getSelectedFile().getParent();
            }

            filename = fc.getSelectedFile().getAbsolutePath();
            selectedFilter = fc.getFileFilter();

            if (selectedFilter instanceof DefaultFileFilter) {
		String ext = ((DefaultFileFilter) selectedFilter).getExtension();

		if (!filename.toLowerCase().endsWith(ext)) {
                    filename += ext;
		}
            }

            if (new File(filename).exists() && JOptionPane.showConfirmDialog(graphComponent, mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
		return;
            }
            try {
		String ext = filename.substring(filename.lastIndexOf('.') + 1);

		if (ext.equalsIgnoreCase("svg")) {
                    mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null,
					new mxCellRenderer.CanvasFactory() {
						public mxICanvas createCanvas(int width, int height) {
							mxSvgCanvas canvas = new mxSvgCanvas(mxDomUtils.createSvgDocument(width, height));
							canvas.setEmbedded(true);
							return canvas;
						}
					});

                    mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()),filename);
		} else if (selectedFilter == vmlFileFilter) {
                    mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer.createVmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("html"))
					{
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createHtmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("mxe")
							|| ext.equalsIgnoreCase("xml"))
					{
						mxCodec codec = new mxCodec();
						String xml = mxXmlUtils.getXml(codec.encode(graph
								.getModel()));

						mxUtils.writeFile(xml, filename);

						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
					}
					else if (ext.equalsIgnoreCase("txt"))
					{
						String content = mxGdCodec.encode(graph);

						mxUtils.writeFile(content, filename);
					}
					else
					{
						Color bg = null;

						if ((!ext.equalsIgnoreCase("gif") && !ext
								.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
										graphComponent, mxResources
												.get("transparentBackground")) != JOptionPane.YES_OPTION)
						{
							bg = graphComponent.getBackground();
						}

						if (selectedFilter == xmlPngFilter
								|| (editor.getCurrentFile() != null
										&& ext.equalsIgnoreCase("png") && !dialogShown))
						{
							saveXmlPng(editor, filename, bg);
						}
						else
						{
							BufferedImage image = mxCellRenderer
									.createBufferedImage(graph, null, 1, bg,
											graphComponent.isAntiAlias(), null,
											graphComponent.getCanvas());

							if (image != null)
							{
								ImageIO.write(image, ext, new File(filename));
							}
							else
							{
								JOptionPane.showMessageDialog(graphComponent,
										mxResources.get("noImageData"));
							}
						}
					}
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
        }
    }
		/**
		 * Saves XML+PNG format.
		 */
		protected void saveXmlPng(BasicGraphEditor editor, String filename,
				Color bg) throws IOException
		{
			mxGraphComponent graphComponent = editor.getGraphComponent();
			mxGraph graph = graphComponent.getGraph();

			// Creates the image for the PNG file
			BufferedImage image = mxCellRenderer.createBufferedImage(graph,
					null, 1, bg, graphComponent.isAntiAlias(), null,
					graphComponent.getCanvas());

			// Creates the URL-encoded XML data
			mxCodec codec = new mxCodec();
			String xml = URLEncoder.encode(
					mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
			mxPngEncodeParam param = mxPngEncodeParam
					.getDefaultEncodeParam(image);
			param.setCompressedText(new String[] { "mxGraphModel", xml });

			// Saves as a PNG file
			FileOutputStream outputStream = new FileOutputStream(new File(
					filename));
			try
			{
				mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
						param);

				if (image != null)
				{
					encoder.encode(image);

					editor.setModified(false);
					editor.setCurrentFile(new File(filename));
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noImageData"));
				}
			}
			finally
			{
				outputStream.close();
			}
		}    
    
}
