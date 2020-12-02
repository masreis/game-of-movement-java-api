package io.gameofmovement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Represents each movement of the game
 * 
 * @author Marco Reis
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Play {

    private Integer playerId;
    private Integer value;

}
