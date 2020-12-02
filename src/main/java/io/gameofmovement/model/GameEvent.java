package io.gameofmovement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameEvent {

    private Integer lastPlayerId;
    private Integer currentValue;
    private String message;
    private GameStatus status;

}
