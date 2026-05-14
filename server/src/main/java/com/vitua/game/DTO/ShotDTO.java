package com.vitua.game.DTO;

public record ShotDTO(
    int shooterId,
    double startX,
    double startY,
    int hittedObjectId,
    double endXHitted,
    double endYHitted,
    double endXRelative,
    double endYRelative
) {
}