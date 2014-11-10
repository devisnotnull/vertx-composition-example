/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.myproject.test.integration.java;

import com.mycompany.myproject.MainVerticle;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.testtools.TestVerticle;
import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

/**
 *
 * @author david baldin
 */
public class RxCompositionHandlerIntegrationTest extends TestVerticle {

    private static final long TIMEOUT = 10000;

    @Before
    public void initVerticle() {
        container.deployVerticle(MainVerticle.class.getName());
    }

    /**
     * The handler does not exists anymore, so this test is still running but the request will wait forever,
     * timeout does nothing to this.
     */
    @Test(timeout = TIMEOUT)
    public void testPreconfigured() {
        vertx.eventBus().sendWithTimeout(
                "comp.rx",
                new Buffer("[1,2,3]"),
                TIMEOUT,
                new Handler<AsyncResult<Message<Buffer>>>() {
                    @Override
                    public void handle(AsyncResult<Message<Buffer>> reply) {
                        try {
                            assertEquals("17", reply.result().body());
                        } finally {
                            testComplete();
                            reply.result().reply(new Buffer("test finished"));
                        }
                    }
                });
    }

    @Test(timeout = TIMEOUT)
    public void testConfigurable1() {
        vertx.eventBus().sendWithTimeout(
                "comp.rx.configurable",
                new Buffer("{\"composition\" : [\"range\",\"square\",\"inc\",\"sum\"], \"argument\" : 10}"),
                TIMEOUT,
                new Handler<AsyncResult<Message<Buffer>>>() {
                    @Override
                    public void handle(AsyncResult<Message<Buffer>> reply) {
                        try {
                            assertEquals("295", reply.result().body());
                        } finally {
                            testComplete();
                            reply.result().reply(new Buffer("test finished"));
                        }
                    }
                });
    }

    @Test(timeout = TIMEOUT)
    public void testConfigurable2() {
        vertx.eventBus().sendWithTimeout(
                "comp.rx.configurable",
                new Buffer("{\"composition\" : [\"sum\",\"square\"], \"argument\" : [1,2,3]}"),
                TIMEOUT,
                new Handler<AsyncResult<Message<Buffer>>>() {
                    @Override
                    public void handle(AsyncResult<Message<Buffer>> reply) {
                        try {
                            assertEquals("36", reply.result().body());
                        } finally {
                            testComplete();
                            reply.result().reply(new Buffer("test finished"));
                        }
                    }
                });
    }

    @Test(timeout = TIMEOUT)
    public void testConfigurable3() {
        vertx.eventBus().sendWithTimeout(
                "comp.rx.configurable",
                new Buffer("{\"composition\" : [\"square\",\"inc\",\"sum\"], \"argument\" : [1,2,3]}"),
                TIMEOUT,
                new Handler<AsyncResult<Message<Buffer>>>() {
                    @Override
                    public void handle(AsyncResult<Message<Buffer>> reply) {
                        try {
                            assertEquals("17", reply.result().body());
                        } finally {
                            testComplete();
                            reply.result().reply(new Buffer("test finished"));
                        }
                    }
                });
    }
}
