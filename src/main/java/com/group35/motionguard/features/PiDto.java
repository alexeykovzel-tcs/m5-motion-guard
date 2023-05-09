package com.group35.motionguard.features;

import lombok.Data;

@Data
public class PiDto {
    private String id;
    private String location;
    private String holderId;

    public PiDto(RaspberryPi entity) {
        this.id = entity.getId();
        this.location = entity.getLocation();
        this.holderId = entity.getHolder().getId();
    }
}
