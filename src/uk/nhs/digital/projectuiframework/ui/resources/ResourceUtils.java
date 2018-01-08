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
package uk.nhs.digital.projectuiframework.ui.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.swing.ImageIcon;

/**
 *
 * @author murff
 */
public class ResourceUtils {
    
    private static final int BUFFERSIZE = 1024;
    
    public static byte[] getByteArray(String n)
            throws Exception
    {
        ByteArrayOutputStream baos;
        try (InputStream is = ResourceUtils.class.getResourceAsStream(n)) {
            baos = new ByteArrayOutputStream();
            byte[] b = new byte[BUFFERSIZE];
            int r = -1;
            while ((r = is.read(b)) != -1) {
                baos.write(b);
            }
        }
        byte[] out = baos.toByteArray();
        baos.close();
        return out;
    }
    
    public static ImageIcon getImageIcon(String n)
            throws Exception
    {
        byte[] b = ResourceUtils.getByteArray(n);
        return new ImageIcon(b);
    }
    
}
