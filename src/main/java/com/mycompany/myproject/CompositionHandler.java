package com.mycompany.myproject;

import com.mycompany.myproject.util.event.ChainableProcess;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.impl.Json;

/**
 * A Composition-Handler that is able to compose (in a mathematical sense) several handlers (which return and
 * argument formats must be compatible of course). Composition here means, that the results from handler A are
 * passed as arguments to handler B that returns the result eventual. The handler can be configured in a
 * static way by passing a string array containing handler-eventbus-keys to its constructor, or in a
 * configurable way by passing the configuration as argument for every call to the handle-method. In following
 * are some exemplaric json-calls, expecting the handler to be configured 'static' like
 * {@code new CompositionHandler(eventBus, new String[]{“square”, “inc”, “sum”})}:
 *
 * <ul>
 * <li>{@code "[1,2,3]"}</li>
 * <li>{@code "10"}</li>
 * <li>{@code "10"}</li>
 * </ul>
 *
 * Now some 'configurable' examples ({@code new CompositionHandler(eventBus)}):
 *
 * <ul>
 * <li>{@code {\"composition\" : [\"range\",\"square\",\"inc\",\"sum\"], \"argument\" : 10}}</li>
 * <li>{@code {\"composition\" : [\"range\",\"square\",\"inc\",\"sum\"], \"argument\" : 10}}</li>
 * <li>{@code {\"composition\" : [\"square\",\"inc\",\"sum\"], \"argument\" : [1,2,3]}}</li>
 * </ul>
 *
 * @author david baldin
 */
public class CompositionHandler implements Handler<Message<Buffer>> {

    /**
     * Event bus reference to be able to adress registered handlers.
     */
    private final EventBus eventBus;

    /**
     * String arrays (keeps natural order) to hold handlers to be composed.
     */
    private final String[] eventComposition;

    Logger LOG = Logger.getLogger(CompositionHandler.class.getName());

    /**
     *
     * @param eventBus - the EventBus this COmpositionHandler should take the configured handlers from,.
     * @param eventComposition - array of strings referencing the keys of the registered handlers.
     */
    CompositionHandler(EventBus eventBus, String... eventComposition) {
        this.eventBus = eventBus;
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
        /*
         * In the first place, we check if the givven buffer can be json-encoded and holds a
         * handler-composition-configuration in the form {"composition" : ["handler1", "handler2"], "argument"
         * : 1} or expects to find a default composition-configuration by passing somethinmg like [1,2,3]
         * directly.
         */
        String[] localEventComposition = eventComposition;
        Buffer eventArgument = rootEvent.body();
        /* in case there is no composition given, we try to parse the buffer as 
         json-object to see if it  contains a configuration */
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

        /*
         * initialize the chain of processes.
         */
        ChainableProcess<Buffer> chainHead = new ChainableProcess<Buffer>() {
            @Override
            public void process(Buffer argument) {
                getNext().process(argument);
            }
        };

        /* for every given handler-key, send a appropriated message to the eventBus, containing the initial 
         * argument or the argument from the last handlers result */
        for (final String eventKey : localEventComposition) {
            final ChainableProcess<Buffer> next = new ChainableProcess<Buffer>() {
                @Override
                public void process(Buffer argument) {
                    eventBus.send(eventKey, argument, new Handler<Message<Buffer>>() {
                        @Override
                        public void handle(Message<Buffer> event) {

                            ChainableProcess<Buffer> next = getNext();
                            if (next != null) {
                                next.process(event.body());
                            }
                        }
                    });
                }
            };
            chainHead.add(next);
        }

        /* finally add the reply chain-link to finish the request*/
        chainHead.add(new ChainableProcess<Buffer>() {
            @Override
            public void process(Buffer argument) {
                rootEvent.reply(argument);
            }
        });

        /* run everything */
        chainHead.process(eventArgument);

    }
}
