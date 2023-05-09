package com.group35.motionguard.features.camera;

import com.group35.motionguard.features.PiRepository;
import com.group35.motionguard.features.RaspberryPi;
import com.group35.motionguard.features.account.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: Save the last minute of PI stream.

@RestController
@RequestMapping("/pi/frames")
@RequiredArgsConstructor
public class FramesController {
    private final PiRepository piRepository;
    private final FrameRepository frameRepository;
    private final AuthService authService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('PI_HOLDER')")
    public List<FrameView> getLastFrames() {
        return frameRepository.findLastByUsername(authService.getId());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PI_HOLDER')")
    public String getLastFrame(@RequestParam("id") String id) {
        // verify that user owns this device
        if (!piRepository.findIdsByHolder(authService.getId()).contains(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Such PI doesn't exist or not the owner");
        }
        // return the frame data in base64
        RaspberryPi pi = piRepository.getReferenceById(id);
        Frame lastFrame = frameRepository.findFirstByPiOrderByDateDesc(pi);
        return (lastFrame != null) ? lastFrame.getBase64() : null;
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAuthority('PI')")
    public void saveFrame(@RequestBody Frame frame) {
        String id = authService.getId();
        frame.setPi(piRepository.getReferenceById(id));
        // verify that the frame is under database limits
        if (frame.getBase64().length() > 65535) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Frame size should be under 65535 bytes");
        }
        // save the last frame
        if (frame.getDate() == null) frame.setDate(new Date());
        frameRepository.save(frame);
    }
}
