package com.mycompany.myproject;

import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.impl.Json;

public class IncHandler implements Handler<Message<Buffer>> {

	@Override
	public void handle(Message<Buffer> event) {
		Object body = Json.decodeValue(event.body().toString(), Object.class);
		if(body instanceof List) {
			List<?> input = (List<?>) body;
			JsonArray result = new JsonArray();
		
			for(int i=0; i<input.size(); i++) {
				if(input.get(i) instanceof Integer) {
					result.add((Integer) input.get(i) + 1);
				}
			}
			event.reply(new Buffer(result.encode()));
		} else if(body instanceof Number) {
			int arg = ((Number) body).intValue();
			event.reply(new Buffer().appendString("" + (arg + 1)));
		}
		
		
	}

}
