/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.myproject.test.integration.java;

import static com.mycompany.myproject.test.integration.java.RxAssert.assertMessageThenComplete;
import io.vertx.rxcore.java.eventbus.RxEventBus;
import io.vertx.rxcore.java.eventbus.RxMessage;
import java.util.List;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.impl.Json;
import org.vertx.testtools.TestVerticle;
import rx.Observable;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

/**
 *
 * Module Test for the RxCompositionHandler.
 *
 * @author david baldin
 */
public class RxCompositionHandlerTest extends TestVerticle {

    @Test
    public void testSerialExecutionWithFakeHandlers() {

        final RxEventBus rxEventBus = new RxEventBus(vertx.eventBus());

        rxEventBus.<String>registerHandler("increment").subscribe(
                new Action1<RxMessage<String>>() {
                    @Override
                    public void call(RxMessage<String> message
                    ) {
                        Integer valueOf = Integer.valueOf(message.body());
                        message.reply((valueOf + 1) + "");
                    }
                }
        );
        Observable<RxMessage<String>> obs1 = rxEventBus.send("increment", "1");
        Observable<RxMessage<String>> obs2 = obs1.flatMap(new Func1<RxMessage<String>, Observable<RxMessage<String>>>() {
            @Override
            public Observable<RxMessage<String>> call(RxMessage<String> reply) {
                return rxEventBus.send("increment", reply.body());
            }
        });
        Observable<RxMessage<String>> obs3 = obs2.flatMap(new Func1<RxMessage<String>, Observable<RxMessage<String>>>() {
            @Override
            public Observable<RxMessage<String>> call(RxMessage<String> reply) {
                return rxEventBus.send("increment", reply.body());
            }
        });

        assertMessageThenComplete(obs3, "4");
    }

    @Test
    public void testSerialExecutionWithFakeHandlers2() {

        vertx.eventBus().registerHandler("inc", new Handler<Message<Buffer>>() {

            @Override
            public
                    void handle(Message<Buffer> event) {
                Object body = Json.decodeValue(event.body().toString(), Object.class
                );
                if (body instanceof List) {
                    List<?> input = (List<?>) body;
                    JsonArray result = new JsonArray();

                    for (int i = 0; i < input.size(); i++) {
                        if (input.get(i) instanceof Integer) {
                            result.add((Integer) input.get(i) + 1);
                        }
                    }
                    event.reply(new Buffer(result.encode()));
                } else if (body instanceof Number) {
                    int arg = ((Number) body).intValue();
                    event.reply(new Buffer().appendString("" + (arg + 1)));
                }
            }
        });

        final RxEventBus rxEventBus = new RxEventBus(vertx.eventBus());

        Observable<RxMessage<Buffer>> obs1 = rxEventBus.send("inc", new Buffer("1"));
        Observable<RxMessage<Buffer>> obs2 = obs1.flatMap(new Func1<RxMessage<Buffer>, Observable<RxMessage<Buffer>>>() {

            @Override
            public Observable<RxMessage<Buffer>> call(RxMessage<Buffer> reply) {
                return rxEventBus.send("inc", reply.body());
            }

        });
        Observable<RxMessage<Buffer>> obs3 = obs2.flatMap(new Func1<RxMessage<Buffer>, Observable<RxMessage<Buffer>>>() {

            @Override
            public Observable<RxMessage<Buffer>> call(RxMessage<Buffer> reply) {
                return rxEventBus.send("inc", reply.body());
            }

        });

        assertMessageThenComplete(obs3, new Buffer("4"));

    }
}
