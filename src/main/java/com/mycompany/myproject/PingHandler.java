package com.mycompany.myproject;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

public class PingHandler implements Handler<Message<Object>>{

	@Override
	public void handle(Message<Object> event) {
		event.reply(event.body());
		
	}

}
