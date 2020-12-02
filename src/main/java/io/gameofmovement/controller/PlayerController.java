package io.gameofmovement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.gameofmovement.model.GameEvent;
import io.gameofmovement.model.Play;
import io.gameofmovement.service.PlayerService;
import io.swagger.annotations.ApiOperation;


/**
 * 
 * The controller class
 * 
 * @author Marco Reis
 *
 */
@RestController
@RequestMapping("/v1/players")
public class PlayerController {

    private PlayerService playerService;

    private PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/{playerId}/{value}")
    @ApiOperation(value = "Route to play")
    public ResponseEntity<String> play(@PathVariable Integer playerId, @PathVariable Integer value) {
        Play lastPlay = Play.builder().playerId(playerId).value(value).build();
        playerService.play(lastPlay);
        String message = "Player #" + playerId;
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    @PostMapping("/{playerId}/ready")
    @ApiOperation(value = "Route to say that the player is ready")
    public ResponseEntity<String> ready(@PathVariable Integer playerId) {
        Play lastPlay = Play.builder().playerId(playerId).build();
        playerService.ready(lastPlay);
        String message = "Player " + playerId + " is ready.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "Route to query the game status")
    public ResponseEntity<GameEvent> query() {
        return new ResponseEntity<>(playerService.getLastEvent(), HttpStatus.OK);
    }

    @PostMapping("/restart")
    @ApiOperation(value = "Route to restart the game")
    public ResponseEntity<String> restart() {
        playerService.restart();
        return new ResponseEntity<>("The play was restarted.", HttpStatus.RESET_CONTENT);
    }

}
