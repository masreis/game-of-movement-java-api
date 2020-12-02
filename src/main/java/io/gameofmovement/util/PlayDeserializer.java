package io.gameofmovement.util;

import java.nio.charset.Charset;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;

import io.gameofmovement.model.Play;

/**
 * 
 * Deserializer for the Kafka
 * 
 * @author Marco Reis
 *
 */

public class PlayDeserializer implements Deserializer<Play> {
    private static final Charset CHARSET = Charset.forName("UTF-8");
    static private Gson gson = new Gson();

    @Override
    public Play deserialize(String topic, byte[] data) {
        String obj = new String(data, CHARSET);
        return gson.fromJson(obj, Play.class);
    }

}
