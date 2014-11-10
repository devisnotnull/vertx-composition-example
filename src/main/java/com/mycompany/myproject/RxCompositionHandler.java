/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.myproject;

import com.mycompany.myproject.util.event.ChainableProcess;
import io.vertx.rxcore.java.eventbus.RxEventBus;
import io.vertx.rxcore.java.eventbus.RxMessage;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.impl.Json;
import rx.Observable;
import rx.util.functions.Action0;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

/**
 * This is a exemplaric implementation of a composition handler and looks very similar to
 * {@link CompositionHandler} but instead of chaining the async-calls with {@link ChainableProcess} it uses
 * RxJava.
 *
 * @author david * baldin
 */
public class RxCompositionHandler implements Handler<Message<Buffer>> {

    private final RxEventBus eventBus;
    private final String[] eventComposition;

    Logger LOG = Logger.getLogger(CompositionHandler.class.getName());

    /**
     *
     * @param eventBus
     * @param eventComposition - a array of string in natural order
     */
    RxCompositionHandler(EventBus eventBus, String... eventComposition) {
        this.eventBus = new RxEventBus(eventBus);
        this.eventComposition = eventComposition;
    }

    /**
     *
     * Implementation of {@link org.vertx.java.core.Handler#handle(java.lang.Object)}.
     *
     * @param rootEvent
     */
    @Override
    public void handle(final Message<Buffer> rootEvent) {
        String[] localEventComposition = eventComposition;
        Buffer eventArgument = rootEvent.body();
        /* in case there is no composition given, we try to parse the buffer as 
         json-object to see if it 
         contains a configuration */
        if (localEventComposition == null || localEventComposition.length == 0) {
            LOG.fine("no default coposition");
            final String jsonString = rootEvent.body().toString();
            Object body = Json.decodeValue(jsonString, Object.class);
            LOG.fine("got json string " + jsonString);
            if (body instanceof Map) {
                Map<String, Object> compositionConfig = (Map<String, Object>) body;
                Object compositionChain = compositionConfig.get("composition");
                if (compositionChain instanceof String) {
                    LOG.fine("got composition config " + compositionChain);
                    localEventComposition = new String[1];
                    localEventComposition[0] = (String) compositionChain;
                } else if (compositionChain instanceof List) {
                    List<Object> compositionChainList = (List<Object>) compositionChain;
                    LOG.fine("got composition config " + compositionChainList);
                    localEventComposition = new String[compositionChainList.size()];
                    for (int i = 0; i < localEventComposition.length; i++) {

                        localEventComposition[i] = (String) compositionChainList.get(i);
                        LOG.fine("parsing composition config " + localEventComposition[i]);
                    }
                }
                eventArgument = new Buffer(Json.encode(compositionConfig.get("argument")));
                LOG.fine("got event argument " + eventArgument);
            } /* there should be a testable negative-return here */ else {
            }

        }

        /* mapMany takes a Function that takes a Observable<T1>*/
        Observable<RxMessage<Buffer>> currentObserver = null;
        for (final String eventKey : localEventComposition) {
            if (currentObserver == null) {
                currentObserver = eventBus.send(eventKey, eventArgument);
            } else {
                /* mapMany is aliaed by flatMap and reactivex synonym for SelectMany. What is does is to take
                 an Obersable, apply a function to every element emited by that Observable (in this case it 
                 is only the one call to eventBus.send(...), and returns a new Observable on that is again 
                 applied flatMap .... kind of recursive and does not make full use of flatMaps capabilities 
                 (because everything is executed in series, flattening is only applied to one element, oberservables
                 emmit only one element)*/
                currentObserver = currentObserver.mapMany(new Func1<RxMessage<Buffer>, Observable<RxMessage<Buffer>>>() {
                    @Override
                    public Observable<RxMessage<Buffer>> call(RxMessage<Buffer> reply) {
                        return eventBus.send(eventKey, reply.body());
                    }

                });
            }
        }

        currentObserver.subscribe(
                new Action1<RxMessage<Buffer>>() {
                    public void call(RxMessage<Buffer> argument) {
                        rootEvent.reply(argument.body());
                    }
                },
                new Action1<Throwable>() {
                    public void call(Throwable t) {
                        throw new RuntimeException("Failed to execute composition!", t);
                    }
                },
                new Action0() {
                    public void call() {

                    }
                });

    }
}
