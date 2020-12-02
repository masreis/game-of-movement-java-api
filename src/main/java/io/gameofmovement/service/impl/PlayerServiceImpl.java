package io.gameofmovement.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import io.gameofmovement.model.GameEvent;
import io.gameofmovement.model.GameStatus;
import io.gameofmovement.model.Play;
import io.gameofmovement.service.PlayerService;
import io.gameofmovement.streaming.GameServer;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlayerServiceImpl implements PlayerService {
    private KafkaTemplate<String, Play> kafkaTemplate;
    private GameServer gameConsumer;

    @Value("${game.topic}")
    private String gameTopic;

    @Value("${ready.topic}")
    private String readyTopic;

    public PlayerServiceImpl(KafkaTemplate<String, Play> kafkaTemplate, GameServer gameConsumer) {
        this.kafkaTemplate = kafkaTemplate;
        this.gameConsumer = gameConsumer;
    }

    public void play(Play play) {
        validatePlayerReadiness(play);
        validateFinished();
        validateInput(play);
        validatePlayer(play);
        this.kafkaTemplate.send(gameTopic, play);
        log.info(play.toString());
    }

    private void validatePlayerReadiness(Play play) {
        if (this.gameConsumer.getLastEvent().getStatus() == GameStatus.WAITING_PLAYERS) {
            throw new IllegalArgumentException("We are waiting for the players to join the game.");
        }
    }

    private void validatePlayer(Play play) {
        if (play.getPlayerId() == this.gameConsumer.getLastEvent().getLastPlayerId()) {
            throw new IllegalArgumentException(
                    "Invalid player (#" + play.getPlayerId() + "). It is the other's player turn.");
        }
    }

    private void validateFinished() {
        if (this.gameConsumer.getLastEvent().getStatus() == GameStatus.FINISHED) {
            throw new IllegalArgumentException("This is finished. Start a new one.");
        }
    }

    private void validateInput(Play play) {
        if (this.gameConsumer.getLastEvent().getStatus() == GameStatus.READY) {
            return;
        }
        if (play.getValue() != -1 && play.getValue() != 0 && play.getValue() != 1) {
            throw new IllegalArgumentException("Invalid input. Valid values are -1, 0, 1.");
        }
    }

    @Override
    public GameEvent getLastEvent() {
        return this.gameConsumer.getLastEvent();
    }

    @Override
    public void restart() {
        this.gameConsumer.restart();
        log.info("Restarted");
    }

    @Override
    public void ready(Play play) {
        this.kafkaTemplate.send(readyTopic, play);
        log.info("Ready");
    }

}
