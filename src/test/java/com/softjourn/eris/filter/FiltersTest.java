package com.softjourn.eris.filter;

import com.softjourn.eris.filter.type.FilterHeight;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.RPCMethod;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class FiltersTest {
    @Test
    public void getMap() throws Exception {
        Long from = 0L;
        Long to = 10L;
        Filters filters = new Filters();
        FilterData filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, from);
        FilterData filterTo = new FilterHeight(Operation.LESS_OR_EQUALS, to);
        filters.add(filterFrom);
        filters.add(filterTo);
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
        File file = new File("src/test/resources/json/filters.json");
        String expectedJSON = new Scanner(file).useDelimiter("\\Z").next().replaceAll("[\\n ]", "");
        assertEquals(expectedJSON, entity.toString());
    }

}