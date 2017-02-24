package com.softjourn.eris;

import com.softjourn.eris.block.pojo.ErisBlock;
import com.softjourn.eris.rpc.ErisRPCResponseEntity;

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
    public static ErisBlock getBlockFromFile(String path) throws FileNotFoundException {
        String blockString = getStringFromFile(path);
        ErisRPCResponseEntity<ErisBlock> entity;
        entity = new ErisRPCResponseEntity<>(blockString, ErisBlock.class);
        return entity.getResult();
    }
}
