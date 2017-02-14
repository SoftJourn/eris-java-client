package com.softjourn.eris;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Additional class that implements wide used method for tests
 */
public class TestUtil {
    public static String getStringFromFile(String pathFormResource) throws FileNotFoundException {
        String root = "src/test/resources/";
        File file = new File(root + pathFormResource);
        return new Scanner(file).useDelimiter("\\Z").next();
    }
}
