package com.group35.motionguard.features;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pi")
public class PiController {
    private final PiHolderRepository piHolderRepository;
    private final PiRepository piRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('PI_HOLDER')")
    public List<PiDto> displayPis() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RaspberryPi> raspberryPis = piRepository.findPisByHolder(username);
        List<PiDto> dtos = new ArrayList<>();
        for (RaspberryPi raspberryPi : raspberryPis) {
            dtos.add(new PiDto(raspberryPi));
        }
        return dtos;
    }

    @Transactional
    @PostMapping(path = "/register")
    @PreAuthorize("hasAuthority('PI_HOLDER')")
    public void registerPi(@RequestBody MultiValueMap<String, String> data) {
        List<String> code = data.get("code");
        List<String> location = data.get("location");
        if (code.isEmpty() || location.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing location or activation code");
        }
        RaspberryPi pi = piRepository.findByActivationCode(code.get(0));
        if (pi == null || pi.getHolder() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid activation code");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        piHolderRepository.findById(username).ifPresent(holder -> {
            pi.setLocation(location.get(0));
            pi.setHolder(holder);
            piRepository.save(pi);
        });
    }
}
