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
package uk.nhs.digital.safetycase.data;

/**
 *
 * @author damian
 */
public class Hazard 
        extends Persistable
{
    private static final String[] TEXTFIELDS = {"Description", "Status", "Name", "ClinicalJustification", "GraphXml", "GroupingType"};
    
    private static final String[] INTEGERFIELDS = {"ProjectID","InitialSeverity", "InitialLikelihood","InitialRiskRating",
            "ResidualSeverity", "ResidualLikelihood", "ResidualRiskRating", "GraphCellId"};
    
    private static final String[] FIELDS = {"Description", "Status", "Summary", "ClinicalJustification", "ProjectID",
                                            "InitialSeverity", "InitialLikelihood","InitialRiskRating",
                                            "ResidualSeverity", "ResidualLikelihood", "ResidualRiskRating", "GroupingType"};
    
    public static final String[] SEVERITIES = {"Minor", "Significant", "Considerable", "Major", "Catastrophic"};
    public static final String[] LIKELIHOODS = {"Very Low", "Low", "Medium", "High", "Very High"};
    public static final int[][] RATINGS = {{1,1,2,2,3},{1,2,2,3,4},{2,2,3,3,4},{2,3,3,4,5},{3,4,4,5,5}};  
    
    public static int getRating(String likelihood, String severity) 
    {
        int l = getLikelihood(likelihood);
        int s = getSeverity(severity);
        int r = RATINGS[s][l];
        return r;
    }
    public static int getRating(int l, int s) {
        int r = RATINGS[s][l];
        return r;        
    }
    
    public static String translateSeverity(int s) {
        return SEVERITIES[s];
    }
    public static String translateLikelihood(int l) {
        return LIKELIHOODS[l];
    }
    public static int getSeverity(String s) {
        for (int i = 0; i < SEVERITIES.length; i++) {
            if (SEVERITIES[i].contentEquals(s))
                return i;
        }
        return -1;
    }
    public static int getLikelihood(String s) {
        for (int i = 0; i < LIKELIHOODS.length; i++) {
            if (LIKELIHOODS[i].contentEquals(s))
                return i;
        }
        return -1;
    }
    
    public Hazard() 
    {
        dbObjectName = "Hazard";
        referenceData = false;
        for (String s: TEXTFIELDS) {
            writableAttributes.put(s, new Attribute(""));
        }
        for (String s: INTEGERFIELDS) {
            writableAttributes.put(s, new Attribute(-1));                
        }
        for (String s : TRACKINGFIELDS) {
            readOnlyAttributes.put(s, Database.empty);
        }
        changed = true;
    }        

    @Override
    public String getTitle() {
        String t = writableAttributes.get("Name").toString();
        if ((t == null) || (t.trim().length() == 0))
            return "Not set";
        return t;
    }

    @Override
    public String[] getFields() {
        return FIELDS;
    }

}
