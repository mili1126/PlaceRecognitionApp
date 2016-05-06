package com.mili.placerecognitionapp.filters;

/**
 * Created by mili on 5/5/16.
 */
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import net.sourceforge.yamlbeans.YamlException;
import net.sourceforge.yamlbeans.YamlReader;

public class YamlMatLoader {

    private final static String TAG = "YamlMatLoader";
    // This nested class specifies the expected variables in the file
    // Mat cannot be used directly because it lacks rows and cols variables
    protected static class MatStorage {
        public int rows;
        public int cols;
        public String dt;
        public List<String> data;

        // The empty constructor is required by YamlReader
        public MatStorage() {
        }

        public double[] getData() {
            double[] dataOut = new double[data.size()];
            for (int i = 0; i < dataOut.length; i++) {
                dataOut[i] = Double.parseDouble(data.get(i));
            }

            return dataOut;
        }
    }

    // Loading function
    public Mat getMatYml(String path, int index) {
        try {
            YamlReader reader = new YamlReader(new FileReader(path));

            // Set the tag "opencv-matrix" to process as MatStorage
            // I'm not sure why the tag is parsed as
            // "tag:yaml.org,2002:opencv-matrix"
            // rather than "opencv-matrix", but I determined this value by
            // debugging
            reader.getConfig().setClassTag("tag:yaml.org,2002:opencv-matrix", MatStorage.class);

            // Read the string
            Map map = (Map) reader.read();

            // In file, the variable name for the Mat is "M"
            MatStorage data = (MatStorage) map.get("Mat"+index);

            // Create a new Mat to hold the extracted data
            Mat m = new Mat(data.rows, data.cols, CvType.CV_32FC1);
            m.put(0, 0, data.getData());
            return m;
        } catch (FileNotFoundException | YamlException e) {
            e.printStackTrace();
        }
        return null;
    }
}