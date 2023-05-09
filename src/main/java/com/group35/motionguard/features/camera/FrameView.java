package com.group35.motionguard.features.camera;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "frame", types = Frame.class)
public interface FrameView {

    @Value("#{target.id}")
    String getId();

    @Value("#{target.pi.id}")
    String getDeviceId();

    @Value("#{target.base64}")
    String getBase64();

    @Value("#{target.date}")
    Date getDate();
}
