package com.mycompany.myproject;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;

public class RangeHandler implements Handler<Message<Buffer>>  {

	@Override
	public void handle(Message<Buffer> event) {
		Integer num = Integer.parseInt(event.body().toString());
		JsonArray result = new JsonArray();
		for(int i=0; i<num; i++) {
			result.add(i);
		}
		event.reply(new Buffer().appendString(result.encode()));		
	}

}
