package io.gameofmovement.util;

import java.nio.charset.Charset;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;

import io.gameofmovement.model.GameEvent;

public class GameEventDeserializer implements Deserializer<GameEvent> {
    private static final Charset CHARSET = Charset.forName("UTF-8");
    static private Gson gson = new Gson();

    @Override
    public GameEvent deserialize(String topic, byte[] data) {
        String movementEvent = new String(data, CHARSET);
        return gson.fromJson(movementEvent, GameEvent.class);
    }

}
