package io.gameofmovement.util;

import java.nio.charset.Charset;

import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;

import io.gameofmovement.model.GameEvent;

public class GametEventSerializer implements Serializer<GameEvent> {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    static private Gson gson = new Gson();

    @Override
    public byte[] serialize(String topic, GameEvent event) {
        return gson.toJson(event).getBytes(CHARSET);
    }

}
