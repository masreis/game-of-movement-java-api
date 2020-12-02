package io.gameofmovement.streaming;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.gameofmovement.model.GameEvent;
import io.gameofmovement.model.GameStatus;
import io.gameofmovement.model.Play;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Backend class to consume events from Kafka.
 * 
 * @author Marco Reis
 *
 */

@Slf4j
@Component
public class GameServer {
    private boolean player1Ready;
    private boolean player2Ready;
    private GameEvent gameEvent = GameEvent.builder().status(GameStatus.WAITING_PLAYERS).build();

    @KafkaListener(topics = "${game.topic}")
    public void consumeGame(Play play) {
        int newValue = play.getValue();
        if (gameEvent.getStatus() == GameStatus.READY) {
            gameEvent.setMessage("First play with value [" + play.getValue() + "].");
            gameEvent.setStatus(GameStatus.STARTED);
        } else if (isDivisibleBy3(newValue)) {
            newValue = processNewValue(play);
        } else {
            String message = "The value [" + newValue
                    + "] is invalid. The resulting operation is not divisible by 3. Try another value.";
            log.info(message);
            gameEvent.setMessage(message);
            return;
        }

        gameEvent.setLastPlayerId(play.getPlayerId());
        gameEvent.setCurrentValue(newValue);
        log.info(gameEvent.toString());
    }

    private boolean isDivisibleBy3(int newValue) {
        return (this.gameEvent.getCurrentValue() + newValue) % 3 == 0;
    }

    private int processNewValue(Play play) {
        int newValue;
        newValue = (this.gameEvent.getCurrentValue() + play.getValue()) / 3;
        if (newValue == 1) {
            gameEvent.setMessage("Player " + play.getPlayerId() + " is the winner.");
            gameEvent.setStatus(GameStatus.FINISHED);
            gameEvent.setCurrentValue(newValue);
        } else if (newValue < 1) {
            gameEvent.setMessage("The game does not have a winner.");
            gameEvent.setStatus(GameStatus.FINISHED);
        } else {
            gameEvent.setMessage("Player #" + play.getPlayerId() + " sent value [" + play.getValue() + "]");
        }
        return newValue;
    }

    @KafkaListener(topics = "${ready.topic}")
    public void consumeReadiness(Play play) {
        if (play.getPlayerId() == 1) {
            player1Ready = true;
        } else if (play.getPlayerId() == 2) {
            player2Ready = true;
        }
        if (player1Ready && player2Ready) {
            this.gameEvent.setStatus(GameStatus.READY);
            this.gameEvent.setCurrentValue(0);
        }
    }

    public GameEvent getLastEvent() {
        return this.gameEvent;
    }

    public void restart() {
        player1Ready = false;
        player2Ready = false;
        this.gameEvent = GameEvent.builder().status(GameStatus.WAITING_PLAYERS).build();
    }

}
