package com.scaleset.cfbuilder.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FnTest {

    @Test
    public void testFn() {
        Fn fn = Fn.fnGetAtt("mydb", "Endpoint.Address");
        String yaml = fn.toString(true);
        // System.out.println(yaml);
        String expected = "Fn::GetAtt:\n" +
                "- \"mydb\"\n" +
                "- \"Endpoint.Address\"";
        assertEquals(yaml, expected);
    }
}
