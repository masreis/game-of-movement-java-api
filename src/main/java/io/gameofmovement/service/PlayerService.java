package io.gameofmovement.service;

import io.gameofmovement.model.GameEvent;
import io.gameofmovement.model.Play;

/**
 * 
 * The interface for the Service
 * 
 * @author Marco Reis
 *
 */

public interface PlayerService {

    void play(Play event);

    GameEvent getLastEvent();

    void restart();

    void ready(Play lastPlay);

}