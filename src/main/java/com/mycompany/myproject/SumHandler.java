package com.mycompany.myproject;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.impl.Json;

public class SumHandler implements Handler<Message<Buffer>>{

	Logger LOG = Logger.getLogger(SumHandler.class.getName());
	
	@Override
	public void handle(Message<Buffer> event) {
		// parse body
		List<?> input = Json.decodeValue(event.body().toString(), List.class);
		int sum = 0;
		for(Object el : input) {
			if(el instanceof Number) {
				sum += ((Number) el).intValue();
			}
		}
		LOG.log(Level.FINE, "replying with sum: " + sum);
		event.reply(new Buffer().appendString("" + sum));
		
	}

}
