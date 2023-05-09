package com.group35.motionguard.features.detection;

import com.group35.motionguard.features.PiRepository;
import com.group35.motionguard.features.RaspberryPi;
import com.group35.motionguard.features.account.AuthService;
import com.group35.motionguard.features.camera.Frame;
import com.group35.motionguard.features.camera.FrameRepository;
import com.group35.motionguard.utils.DateUtils;
import com.group35.motionguard.features.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/pi/motion")
@RequiredArgsConstructor
@Slf4j
public class DetectionsController {
    private final DetectionRepository detectionRepository;
    private final FrameRepository frameRepository;
    private final PiRepository piRepository;
    private final AuthService authService;
    private final EmailSender sender;
    private AWTSequenceEncoder encoder;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('PI_HOLDER')")
    public Collection<DetectionView> getDetections(@RequestParam(required = false) Optional<Date> from,
                                                   @RequestParam(required = false) Optional<Date> to) {
        String username = authService.getId();
        return (from.isPresent() || to.isPresent())
                ? detectionRepository.findByUserForPeriod(username, from.orElse(DateUtils.MIN_DATE), to.orElse(new Date()))
                : detectionRepository.findByHolder(username);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PI_HOLDER')")
    public Collection<DetectionView> getDetectionsByPi(@RequestParam("id") String id) {
        if (!piRepository.isHolder(id, authService.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such device");
        }
        return detectionRepository.findByPi(id);
    }

    @GetMapping("/recording")
    @PreAuthorize("hasAuthority('PI_HOLDER')")
    public String getRecording(@RequestParam("id") Long id) {
        return detectionRepository.getBase64ById(id);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('PI_HOLDER')")
    public void deleteDetection(@RequestParam("id") Long detection) {
        detectionRepository.deleteByLongId(detection);
    }

    @Transactional
    @PostMapping
    @PreAuthorize("hasAuthority('PI')")
    public void saveDetection() {
        piRepository.findById(authService.getId()).ifPresent(pi -> {

            // Set the last PI frame as detection and the rest for the corresponding recording
            Frame lastFrame = frameRepository.findFirstByPiOrderByDateDesc(pi);
            if (lastFrame != null) lastFrame.setDetection(true);

            // Save detection to the database
            Detection detection = new Detection(pi, lastFrame);

            //Get video
            File file = new File("src/main/resources/tmp/newVideo.mp4");
            try {
                encoder = AWTSequenceEncoder.createSequenceEncoder(file, 5);
                encodeVideo(encoder, detection, pi, file);
                encoder.finish();
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }


//            //Update video
//            TimerTask task = new TimerTask() {
//                public void run() {
//                    File file2 = new File("src/main/resources/tmp/newVideo2.mp4");
//                    try {
//                        AWTSequenceEncoder encoder2 = AWTSequenceEncoder.createSequenceEncoder(file2, 5);
//                        Detection detection1 = new Detection(pi);
//                        encodeVideo(encoder2, detection1, pi, file2);
//                        encoder2.finish();
//                        file2.delete();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            Timer timer = new Timer("Timer");
//            long delay = 20000L;
//            timer.schedule(task, delay);


            // Notify PI holder by email
            String email = pi.getUser().getEmail();
            if (email != null && !email.equals("")) {
                boolean success = sender.notifyDetection(pi.getLocation(), detection.getDate(), email);
                if (!success) log.error("Could not send an email to {}", email);
            }

        });
    }

    private void encodeVideo(AWTSequenceEncoder encoder, Detection detection, RaspberryPi pi, File file) {
        try {
            List<Frame> framesList = frameRepository.findFramesByPiOrderByDateDesc(pi);
            if (framesList.size() >= 100) {
                framesList = framesList.subList(0, 99);
            }
            for (int i = 0; i < framesList.size(); i++) {
                byte[] pic = Base64.getDecoder().decode(framesList.get(i).getBase64());
                InputStream in = new ByteArrayInputStream(pic);
                BufferedImage bImageFromConvert = ImageIO.read(in);
                encoder.encodeImage(bImageFromConvert);
            }
            FileInputStream is = new FileInputStream(file);
            byte[] byteArr = IOUtils.toByteArray(is);
            String base64video = Base64.getEncoder().encodeToString(byteArr);
            detection.setVideoBase64(base64video);
            detectionRepository.save(detection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
