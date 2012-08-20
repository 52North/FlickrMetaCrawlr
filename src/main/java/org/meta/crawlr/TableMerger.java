package org.meta.crawlr;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;

import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


public class TableMerger {
    
    //NYC TS 
//    private static double targetLongitude = -73.9863; 
//    private static double targetLatitude  = 40.7566;  
//    private static String[] tags = new String[]{"New York","Times Square"};
    
//    //NYC TS 2 
      private static double targetLongitude = -73.9863; 
      private static double targetLatitude  = 40.7566;  
      private static String[] tags = new String[]{"New York","Times Square"};
//      
//    //Eiffeltower     
//    private static double targetLongitude = 2.2945;   
//    private static double targetLatitude  = 48.8583;      
//    private static String[] tags = new String[]{"Eiffeltower"};
    
//    //Moscow K   
//    private static double targetLongitude = 37.617778;
//    private static double targetLatitude  = 55.751667;    
//    private static String[] tags = new String[]{"Moscow", "Kremlin"};
    
//    //Berlin BT
//    private static double targetLongitude = 13.377722;
//    private static double targetLatitude  = 52.516272;    
//    private static String[] tags = new String[]{"Berlin", "Brandenburger Tor"};
    
//    //Paris CE    
//    private static double targetLongitude = 2.3075;
//    private static double targetLatitude  = 48.8697;
//    private static String[] tags = new String[]{"Paris", "Champs Élysées"};
    
//    //Angkor, Cambodia 
//    private static double targetLongitude = 103.833333;
//    private static double targetLatitude  = 13.433333;  
//    private static String[] tags = new String[]{"Angkor","Cambodia"};
    
//  //Stonehenge
//    private static double targetLongitude = -1.826189;
//    private static double targetLatitude  = 51.178844; 
//    private static String[] tags = new String[]{"Stonehenge"};
    
    private static int excludeThreshold = 7000;
    private static int userContactCountLimit = Integer.MAX_VALUE;
    
    /**
     * This is the main method, automatically called when starting this application.
     * 
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        System.out.println("start program");
        
        //for (excludeThreshold = 2000; excludeThreshold <= 2000; excludeThreshold = excludeThreshold + 2000) {
        
            //// ------------------------ prepare output file writer:
            String outputFileName = "c:/temp/flickrPhotoData-" + concatenate(tags);
            if (excludeThreshold != Integer.MAX_VALUE) {
                outputFileName = outputFileName + "-Within_" + excludeThreshold  + "m";
            }
            if (userContactCountLimit != Integer.MAX_VALUE) {
                outputFileName = outputFileName + "-UserContactCountLimit_" + userContactCountLimit;
            }
            outputFileName = outputFileName + ".csv";
            
            System.out.println("Writing to file " + outputFileName);
            
            char delimiter = ';';
            CsvWriter writer = new CsvWriter(new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFileName),Charset.forName("UTF-8"))), delimiter);

            // ------------------------ start reading input file:
            
            for (int i=0; i<55; i++) {
                String inputFileName = "c:/temp/flickrPhotoData-" + concatenate(tags) + "-" + i + ".csv";
                
                if(new File(inputFileName).exists()) { 
                    System.out.println("Reading file '" + inputFileName + "'");
                    
                    CsvReader reader = new CsvReader(inputFileName);
                    reader.setDelimiter('|');
                    
                    // read header from input file:
                    reader.readRecord();
                    
                    if (i == 0) {
                        
                        // write header to output file:
                        
                        String[] headerRow = reader.getValues();
                        String[] headerRowPlusDistanceAndUserExperience = new String[headerRow.length + 2];
                        
                        for (int j = 0; j < headerRow.length; j++) {
                            headerRowPlusDistanceAndUserExperience[j] = headerRow[j];
                        }
                        
                        headerRowPlusDistanceAndUserExperience[headerRow.length] = "distanceToTarget";
                        headerRowPlusDistanceAndUserExperience[headerRow.length + 1] = "userExperience";
                        
                        writer.writeRecord(headerRowPlusDistanceAndUserExperience);
                    }
                    
                    // read data rows from input file:
                    while (reader.readRecord()) {
                        String[] dataRow = reader.getValues();
                        
                        //
                        // also compute and include distance to target
                        //
                        Double longitude = Double.parseDouble(dataRow[18]);
//                        if (longitude > 1000 && longitude < 9900) {
//                            longitude = Double.parseDouble(includeDecimalSign(dataRow[18], 1));
//                        }
//                        if (longitude > 100000) {
//                            longitude = Double.parseDouble(includeDecimalSign(dataRow[18], 3));
//                        }
//                        if (longitude < -70000 && longitude > -99000) {
//                            longitude = Double.parseDouble(includeDecimalSign(dataRow[18], 2));
//                        }
//                        if (longitude < -100000) {
//                            longitude = Double.parseDouble(includeDecimalSign(dataRow[18], 3));
//                        }
                        
                        Double latitude  = Double.parseDouble(dataRow[19]);
//                        if (latitude > 10000 && latitude < 99000) {
//                            latitude = Double.parseDouble(includeDecimalSign(dataRow[19], 2));
//                        }
                        
                        double distance = -1; 
                        
                        try {
                            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
                            
                            GeodeticCalculator calc = new GeodeticCalculator(sourceCRS);
                            calc.setStartingGeographicPoint(longitude, latitude);
                            calc.setDestinationGeographicPoint(targetLongitude, targetLatitude);
                            
                            distance = calc.getOrthodromicDistance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        //
                        // calculating user experience:
                        //
                        double userExperience = 0;
                        
                        boolean isPro = Boolean.parseBoolean(dataRow[28]); // 28
                        int contactCounts  = Integer.parseInt(dataRow[30]); // 30
                        int userPhotoCount = Integer.parseInt(dataRow[23]); // 23;
                        int userSetsCount  = Integer.parseInt(dataRow[31]); // 31
                        //Date userFirstPhoto = new Date(Date.parse(dataRow[24])); // 24;
                        
                        if (isPro) {
                            userExperience = userExperience + 10;
                        }
                        if (contactCounts > 20) {
                            userExperience = userExperience + 2;
                        }
                        if (userPhotoCount > 500) {
                            userExperience = userExperience + 5;
                        }
                        if (userSetsCount > 10) {
                            userExperience = userExperience + 0.5;
                        }
    //                    if (userFirstPhoto.before(new Date(111, 8, 1))) {
    //                        userExperience = userExperience + 20;
    //                    }
                        
                        //
                        // read number of user contacts to allow excluding some groups 
                        //
                        int userContactCount = Integer.parseInt(dataRow[30]);
                                
                        //
                        // writing to file:
                        //
                        if (distance != -1 && distance <= excludeThreshold && userContactCount < userContactCountLimit) { 
                            String[] dataRowPlusDistanceAndUserExperience = new String[dataRow.length + 2];
                            
                            for (int j = 0; j < dataRow.length; j++) {
                                dataRowPlusDistanceAndUserExperience[j] = dataRow[j];
                            }
                            
                            dataRowPlusDistanceAndUserExperience[dataRow.length] = "" + distance;
                            dataRowPlusDistanceAndUserExperience[dataRow.length + 1] = "" + userExperience;
                            
                            writer.writeRecord(dataRowPlusDistanceAndUserExperience);
                        }
                        
                    }
                }
            }
        
            // finish up the output file writing by 'flushing' and 'closing':
            writer.flush();
            writer.close();
        
        
        System.out.println("program finished");
        
    }
    
    /** 
     * concatenates the specified String array to one String and separates by a "-" sign.
     */
    public static String concatenate(String[] sArray)
    {
        String result = "";
        for (int i = 0; i < sArray.length - 1; i++) {
            result += sArray[i] + "-";
        }
        result += sArray[sArray.length - 1];
        return result;
    }
    
    /**
     * This is a helper method that includes a decimal point '.' at the defined position of an inputString.
     */
    private static String includeDecimalSign(String inputString, int decimalPointPosition){
    
        String outputString = "";
        
        int startIndex = 0;
        
                // handle case, where inputString starts with a '-':
        if (inputString.substring(0, 1).equals("-")) {
            startIndex = 1;
            decimalPointPosition = decimalPointPosition + 1;
            outputString = "-";
        }
    
                // now, create a new outputString, by coyping substrings from the inputString and in the middle a ".":
        outputString += inputString.substring(startIndex, decimalPointPosition);
        outputString += ".";
        outputString += inputString.substring(decimalPointPosition);
        
        return outputString;
    }
}
