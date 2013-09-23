package com.mycompany.myproject;

import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;

public class EchoHandler implements Handler<Message<Buffer>>{

	public static final Logger LOG = Logger.getLogger(EchoHandler.class.getName());
	
	@Override
	public void handle(Message<Buffer> event) {
		LOG.fine("echoing to: " + event.body().toString());
		event.reply(event.body());		
	}

}
