package com.group35.motionguard.features.detection;

import com.group35.motionguard.features.camera.FrameView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "detection", types = Detection.class)
public interface DetectionView {

    @Value("#{target.id}")
    String getId();

    @Value("#{target.pi.id}")
    String getDeviceId();

    @Value("#{target.pi.location}")
    String getLocation();

    @Value("#{target.frame}")
    FrameView getImage();

    @Value("#{target.date}")
    Date getDate();

    @Value("#{target.videoBase64}")
    String getBase64();
}
