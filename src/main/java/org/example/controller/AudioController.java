package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.generic.GenericResult;
import org.example.service.AudioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/voice")
@Slf4j
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;
    @RequestMapping("/transferText")
    public GenericResult transferText(@RequestParam("text") String text,
                                      @RequestParam("speed") String speed) throws Exception {
        log.info("文字转语音text: {}", text);
        return audioService.transferText(text, speed);
    }
}
