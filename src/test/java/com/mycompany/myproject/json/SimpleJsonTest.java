/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.myproject.json;

import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.vertx.java.core.json.impl.Json;

/**
 *
 * @author david baldin
 */
public class SimpleJsonTest {

    @Test
    public void testMapEncode() {
        Object body = Json.decodeValue("\"foo\"", Object.class);
        Assert.assertTrue(body instanceof String);
        body = Json.decodeValue("10", Object.class);
        Assert.assertTrue(body instanceof Integer);
        body = Json.decodeValue("[1,2,3]", Object.class);
        Assert.assertTrue(body instanceof List);
        body = Json.decodeValue("{\"comp\": [\"inc\",\"sum\"], \"args\":[1,2,3]}", Object.class);
        Assert.assertTrue(body instanceof Map);
    }
}
