/*
 * 
 *   Copyright 2018  NHS Digital
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
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author damian
 */
public abstract class AbstractSaveHandler {
    
    public abstract void handle(BasicGraphEditor bge) throws Exception;
    
    protected String getXml(BasicGraphEditor bge)
            throws Exception
    {
        mxGraphComponent graphComponent = bge.getGraphComponent();
        mxGraph graph = graphComponent.getGraph();            
        mxCodec codec = new mxCodec();
        return mxXmlUtils.getXml(codec.encode(graph.getModel()));        
    }
}
