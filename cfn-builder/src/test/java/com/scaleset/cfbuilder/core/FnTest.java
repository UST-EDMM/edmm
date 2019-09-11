package com.scaleset.cfbuilder.core;

import org.junit.Assert;
import org.junit.Test;

public class FnTest {

    @Test
    public void testFn() {
        Fn fn = Fn.fnGetAtt("mydb", "Endpoint.Address");
        String yaml = fn.toString(true);
        System.out.println(yaml);
        String expected = "Fn::GetAtt:\n" +
                "- \"mydb\"\n" +
                "- \"Endpoint.Address\"";
        Assert.assertEquals(yaml, expected);
    }
}
