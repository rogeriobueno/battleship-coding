package com.odigeo.interview.coding.domain.contract;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameFireCommand {

    private String playerId;
    private String coordinate;

}
