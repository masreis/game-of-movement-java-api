package io.gameofmovement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import io.gameofmovement.model.GameEvent;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlayerControllerITTest {

    @Autowired
    private TestRestTemplate template;

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    final void testReset() throws URISyntaxException {
        restartGame();
    }

    private void restartGame() throws URISyntaxException {
        URI uri = new URI("http://localhost:" + port + "/v1/players/restart");
        template.exchange(uri, HttpMethod.POST, null, Void.class);
    }

    @Test
    @Order(2)
    final void testPlay() throws URISyntaxException {

        // Ready
        URI uri = new URI("http://localhost:" + port + "/v1/players/1/ready");
        ResponseEntity<String> result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.OK.value());

        waitKafka();

        uri = new URI("http://localhost:" + port + "/v1/players/2/ready");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.OK.value());

        waitKafka();

        // Player #1
        uri = new URI("http://localhost:" + port + "/v1/players/1/56");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.ACCEPTED.value());

        waitKafka();
        
        uri = new URI("http://localhost:" + port + "/v1/players");
        ResponseEntity<GameEvent> resultGameEvent = template.getForEntity(uri, GameEvent.class);
        assertTrue(resultGameEvent.getStatusCodeValue() == HttpStatus.OK.value());
        GameEvent event = resultGameEvent.getBody();
        assertEquals(event.getCurrentValue(), 56);

        waitKafka();

        // Player #2
        uri = new URI("http://localhost:" + port + "/v1/players/2/1");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.ACCEPTED.value());

        waitKafka();

        uri = new URI("http://localhost:" + port + "/v1/players");
        resultGameEvent = template.getForEntity(uri, GameEvent.class);
        assertTrue(resultGameEvent.getStatusCodeValue() == HttpStatus.OK.value());
        event = resultGameEvent.getBody();
        assertEquals(event.getCurrentValue(), 19);

        waitKafka();

        // Player #1
        uri = new URI("http://localhost:" + port + "/v1/players/1/-1");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.ACCEPTED.value());

        waitKafka();

        uri = new URI("http://localhost:" + port + "/v1/players");
        resultGameEvent = template.getForEntity(uri, GameEvent.class);
        assertTrue(resultGameEvent.getStatusCodeValue() == HttpStatus.OK.value());
        event = resultGameEvent.getBody();
        assertEquals(event.getCurrentValue(), 6);

        waitKafka();

        // Player #2
        uri = new URI("http://localhost:" + port + "/v1/players/2/0");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.ACCEPTED.value());

        waitKafka();

        uri = new URI("http://localhost:" + port + "/v1/players");
        resultGameEvent = template.getForEntity(uri, GameEvent.class);
        assertTrue(resultGameEvent.getStatusCodeValue() == HttpStatus.OK.value());
        event = resultGameEvent.getBody();
        assertEquals(event.getCurrentValue(), 2);

        waitKafka();

        // Player #1
        uri = new URI("http://localhost:" + port + "/v1/players/1/1");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.ACCEPTED.value());

        waitKafka();

        uri = new URI("http://localhost:" + port + "/v1/players");
        resultGameEvent = template.getForEntity(uri, GameEvent.class);
        assertTrue(resultGameEvent.getStatusCodeValue() == HttpStatus.OK.value());
        event = resultGameEvent.getBody();
        assertEquals(event.getCurrentValue(), 1);
        assertEquals(event.getMessage(), "Player 1 is the winner.");

    }

    private void waitKafka() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    final void testPlayInvalidPlayer() throws URISyntaxException {
        restartGame();
        // Ready
        URI uri = new URI("http://localhost:" + port + "/v1/players/1/ready");
        ResponseEntity<String> result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.OK.value());

        waitKafka();

        uri = new URI("http://localhost:" + port + "/v1/players/2/ready");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.OK.value());

        waitKafka();

        // Player #1
        uri = new URI("http://localhost:" + port + "/v1/players/1/56");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.ACCEPTED.value());

        waitKafka();

        URI uriException = new URI("http://localhost:" + port + "/v1/players/1/1");
        ResponseEntity<String> response = template.postForEntity(uriException, null, String.class);

        assertEquals(response.getBody(), "Invalid player (#1). It is the other's player turn.");

    }

    @Test
    @Order(4)
    final void testPlayInvalidValue() throws URISyntaxException {

        restartGame();
        // Ready
        URI uri = new URI("http://localhost:" + port + "/v1/players/1/ready");
        ResponseEntity<String> result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.OK.value());

        uri = new URI("http://localhost:" + port + "/v1/players/2/ready");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.OK.value());

        waitKafka();

        // Player #1
        uri = new URI("http://localhost:" + port + "/v1/players/1/56");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.ACCEPTED.value());

        waitKafka();

//        BadRequest expected = assertThrows(HttpClientErrorException.BadRequest.class, () -> {
        URI uriException = new URI("http://localhost:" + port + "/v1/players/2/2");
        ResponseEntity<String> response = template.postForEntity(uriException, null, String.class);
//        });

        assertEquals(response.getBody(), "Invalid input. Valid values are -1, 0, 1.");

    }

    @Test
    @Order(5)
    final void testPlayNotDivisible() throws URISyntaxException {
        restartGame();
        // Ready
        URI uri = new URI("http://localhost:" + port + "/v1/players/1/ready");
        ResponseEntity<String> result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.OK.value());

        waitKafka();
        
        uri = new URI("http://localhost:" + port + "/v1/players/2/ready");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.OK.value());

        waitKafka();

        // Player #1
        uri = new URI("http://localhost:" + port + "/v1/players/1/56");
        result = template.postForEntity(uri, null, String.class);
        assertTrue(result.getStatusCodeValue() == HttpStatus.ACCEPTED.value());

        waitKafka();

        URI uriException = new URI("http://localhost:" + port + "/v1/players/2/0");
        template.postForEntity(uriException, null, String.class);

        waitKafka();

        uri = new URI("http://localhost:" + port + "/v1/players");
        ResponseEntity<GameEvent> resultGameEvent = template.getForEntity(uri, GameEvent.class);
        assertTrue(resultGameEvent.getStatusCodeValue() == HttpStatus.OK.value());
        GameEvent event = resultGameEvent.getBody();
        assertEquals(event.getCurrentValue(), 56);
        assertEquals(event.getMessage(),
                "The value [0] is invalid. The resulting operation is not divisible by 3. Try another value.");

    }

}
