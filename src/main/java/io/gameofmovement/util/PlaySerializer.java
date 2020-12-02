package io.gameofmovement.util;

import java.nio.charset.Charset;

import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;

import io.gameofmovement.model.Play;

/**
 * 
 * Serializer for the Kafka
 * 
 * @author Marco Reis
 *
 */
public class PlaySerializer implements Serializer<Play> {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    static private Gson gson = new Gson();

    @Override
    public byte[] serialize(String topic, Play obj) {
        return gson.toJson(obj).getBytes(CHARSET);
    }

}
