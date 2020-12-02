package io.gameofmovement.service;

import io.gameofmovement.model.GameEvent;
import io.gameofmovement.model.Play;

public interface PlayerService {

    void play(Play event);

    GameEvent getLastEvent();

    void restart();

    void ready(Play lastPlay);

}