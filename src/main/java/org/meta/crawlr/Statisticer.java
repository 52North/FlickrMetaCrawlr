package org.meta.crawlr;

import java.io.IOException;
import java.util.ArrayList;

import com.csvreader.CsvReader;


public class Statisticer {

//  private static String[] tags = new String[]{"New York","Time Square"};

    private static String[] tags = new String[]{"New York","Times Square"};
    
//    private static String[] tags = new String[]{"Moscow", "Kremlin"};
    
//    private static String[] tags = new String[]{"Eiffeltower"};
    
//    private static String[] tags = new String[]{"Berlin", "Brandenburger Tor"};
    
//    private static String[] tags = new String[]{"Paris", "Champs Élysées"};
    
//    private static String[] tags = new String[]{"Angkor","Cambodia"};
    
    //private static String[] tags = new String[]{"Stonehenge"};
    
    private static int visibilityThreshold = 4500;
    
    private static int userCountThreshold1 = 0;
    private static int userCountThreshold2 = 5;
    private static int userCountThreshold3 = 20;
    private static int userCountThreshold4 = 200;
    private static int userCountThreshold5 = 1000;
    private static int userCountThreshold6 = 0;
    private static int userCountThreshold7 = 0;
    private static int userCountThreshold8 = 0;
    private static int userCountThreshold9 = 0;
    
    
    public static void main(String[] args) throws IOException
    {
        String inputFileName = "c:/temp/flickrPhotoData-" + TableMerger.concatenate(tags) + ".csv";
        
        System.out.println("Reading file '" + inputFileName + "'");
        
        CsvReader reader = new CsvReader(inputFileName);
        reader.setDelimiter(';');
        
        // read header from input file:
        reader.readRecord();
        
        
        ArrayList<String> dataTuple_Contacts_Group1 = new ArrayList<String>(); 
        ArrayList<String> dataTuple_Contacts_Group2 = new ArrayList<String>();
        ArrayList<String> dataTuple_Contacts_Group3 = new ArrayList<String>();
        ArrayList<String> dataTuple_Contacts_Group4 = new ArrayList<String>();
        ArrayList<String> dataTuple_Contacts_Group5 = new ArrayList<String>();
        ArrayList<String> dataTuple_Contacts_Group6 = new ArrayList<String>();
        ArrayList<String> dataTuple_Contacts_Group7 = new ArrayList<String>();
        ArrayList<String> dataTuple_Contacts_Group8 = new ArrayList<String>();
        ArrayList<String> dataTuple_Contacts_Group9 = new ArrayList<String>();
        ArrayList<String> dataTuple_Contacts_Group10 = new ArrayList<String>();
        
        // read data rows from input file:
        while (reader.readRecord()) {
            String[] dataRow = reader.getValues();
            
            //
            // also compute and include distance to target
            //
            int userContactCount = Integer.parseInt(dataRow[30]);  // 33 = userExperience
            String distanceToTarget = dataRow[32];
            
            if (userContactCount <= userCountThreshold1) {
                dataTuple_Contacts_Group1.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold1 && userContactCount <= userCountThreshold2) {
                dataTuple_Contacts_Group2.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold2 && userContactCount <= userCountThreshold3) {
                dataTuple_Contacts_Group3.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold3 && userContactCount <= userCountThreshold4) {
                dataTuple_Contacts_Group4.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold4 && userContactCount <= userCountThreshold5) {
                dataTuple_Contacts_Group5.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold5 && userContactCount <= userCountThreshold6) {
                dataTuple_Contacts_Group6.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold6 && userContactCount <= userCountThreshold7) {
                dataTuple_Contacts_Group7.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold7 && userContactCount <= userCountThreshold8) {
                dataTuple_Contacts_Group8.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold8 && userContactCount <= userCountThreshold9) {
                dataTuple_Contacts_Group9.add("" + userContactCount + ";" + distanceToTarget);
            }
            if (userContactCount > userCountThreshold9) {
                dataTuple_Contacts_Group10.add("" + userContactCount + ";" + distanceToTarget);
            }
        }
        
        
        System.out.println("Group 1 (" + dataTuple_Contacts_Group1.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts <= " + userCountThreshold1 + ":    "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group1));
        
        System.out.println("Group 2 (" + dataTuple_Contacts_Group2.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts "
                + userCountThreshold1 + " - " + userCountThreshold2 + ": "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group2));
        
        System.out.println("Group 3 (" + dataTuple_Contacts_Group3.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts "
                + userCountThreshold2 + " - " + userCountThreshold3 + ": "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group3));
        
        System.out.println("Group 4 (" + dataTuple_Contacts_Group4.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts "
                + userCountThreshold3 + " - " + userCountThreshold4 + ": "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group4));
        
        System.out.println("Group 5 (" + dataTuple_Contacts_Group5.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts "
                + userCountThreshold4 + " - " + userCountThreshold5 + ": "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group5));
        
        System.out.println("Group 6 (" + dataTuple_Contacts_Group6.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts "
                + userCountThreshold5 + " - " + userCountThreshold6 + ": "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group6));
        
        System.out.println("Group 7 (" + dataTuple_Contacts_Group7.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts "
                + userCountThreshold6 + " - " + userCountThreshold7 + ": "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group7));
        
        System.out.println("Group 8 (" + dataTuple_Contacts_Group8.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts "
                + userCountThreshold7 + " - " + userCountThreshold8 + ": "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group8));
        
        System.out.println("Group 9 (" + dataTuple_Contacts_Group9.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts "
                + userCountThreshold8 + " - " + userCountThreshold9 + ": "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group9));
        
        System.out.println("Group 10 (" + dataTuple_Contacts_Group10.size() + " photos): Percentage of data tuples above '" + visibilityThreshold + "m among contacts > " + userCountThreshold9 + ":    "
                + percentageOfDataTuplesAboveThreshold(dataTuple_Contacts_Group10));
    
        
    }
    
    private static double percentageOfDataTuplesAboveThreshold (ArrayList<String> dataTuples) {
        int countOfDataTuplesAboveThreshold = 0;
        
        for (int i = 0; i < dataTuples.size(); i++) {
            String dataTuple = dataTuples.get(i);
            double distanceToTarget = Double.parseDouble(dataTuple.split(";")[1]);
            if (distanceToTarget > visibilityThreshold) {
                countOfDataTuplesAboveThreshold = countOfDataTuplesAboveThreshold+1;
            }
        }
        
        int countOfDataTuples = dataTuples.size();
        
        double percent = new Double(countOfDataTuplesAboveThreshold) / new Double(countOfDataTuples) * 100.0;
        
        return percent;
    }
}
