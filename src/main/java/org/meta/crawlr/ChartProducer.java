package org.meta.crawlr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.ui.RectangleEdge;

import com.csvreader.CsvReader;


public class ChartProducer  {

//    private static String[] tags = new String[]{"New York","Time Square"};

//    private static String[] tags = new String[]{"New York","Times Square"};
    
    //private static String[] tags = new String[]{"Moscow", "Kremlin"};
    
    private static String[] tags = new String[]{"Eiffeltower"};
    
    //private static String[] tags = new String[]{"Berlin", "Brandenburger Tor"};
    
//    private static String[] tags = new String[]{"Paris", "Champs Élysées"};
    
//    private static String[] tags = new String[]{"Angkor","Cambodia"};
    
    public static MyScatterPlot produceChart() throws IOException
    {
        String inputFileName = "c:/temp/flickrPhotoData-" + TableMerger.concatenate(tags) + ".csv"; //-Within_5000000m
        
        ArrayList<String> dataTupleList = new ArrayList<String>(); 
        
        System.out.println("Reading file '" + inputFileName + "'");
        
        CsvReader reader = new CsvReader(inputFileName);
        reader.setDelimiter(';');
        
        // read header from input file:
        reader.readRecord();
        
        // read data rows from input file:
        while (reader.readRecord()) {
            String[] dataRow = reader.getValues();
            
            //
            // also compute and include distance to target
            //
            String userCharacteristic = dataRow[30];  // 33 = userExperience
            String distanceToTarget = dataRow[32];
            
            dataTupleList.add(userCharacteristic + ";" + distanceToTarget);
        }
                
        float[][] data = new float[2][dataTupleList.size()];
        for (int j = 0; j < dataTupleList.size(); j++) {
            
            String userExperience = dataTupleList.get(j).split(";")[0];
            String distanceToTarget = dataTupleList.get(j).split(";")[1];
            
            data[0][j] = Float.parseFloat(userExperience);
            data[1][j] = Float.parseFloat(distanceToTarget);
            
        }
        
        String xAxisTitle = "User Contact Count";
        String yAxisTitle = "Distance to Target";

        final NumberAxis domainAxis = new NumberAxis(xAxisTitle);
        domainAxis.setAutoRangeIncludesZero(false);
        final NumberAxis rangeAxis = new NumberAxis(yAxisTitle);
        rangeAxis.setAutoRangeIncludesZero(false);

        return new MyScatterPlot(data, domainAxis, rangeAxis);
    }

    public static void main(String[] args) throws Exception
    {
        JFreeChart jF = new JFreeChart(produceChart());
        
        ChartFrame frame = new ChartFrame("Test", jF);
        frame.pack();
        frame.setVisible(true);
    }
}


class MyScatterPlot extends FastScatterPlot {
    
    public static final int dotSize = 3;
    
    public MyScatterPlot(float[][] data, ValueAxis domainAxis, ValueAxis rangeAxis) {
        super(data, domainAxis, rangeAxis);
    }

    @Override
    public void render(Graphics2D g2,
                       Rectangle2D dataArea,
                       PlotRenderingInfo info,
                       CrosshairState crosshairState) {
        float[][] data = getData();

        g2.setPaint(Color.red);

        if (super.getData() != null) {
            for (int i = 0; i < data[0].length; i++) {
                float x = data[0][i];
                float y = data[1][i];

                int transX = (int) getDomainAxis().valueToJava2D(x, dataArea, RectangleEdge.BOTTOM);
                int transY = (int) getRangeAxis().valueToJava2D(y, dataArea, RectangleEdge.LEFT);
                g2.fillRect(transX, transY, dotSize, dotSize);
            }
        }
    }
}

