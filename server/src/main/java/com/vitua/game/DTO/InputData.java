package com.vitua.game.DTO;

import com.vitua.game.Engine.InputRecord;

public record InputData(
    String nickName,
    InputRecord data
) {
}
