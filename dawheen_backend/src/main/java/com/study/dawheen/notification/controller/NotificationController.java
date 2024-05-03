package com.study.dawheen.notification.controller;

import com.study.dawheen.notification.dto.NotificationResponseDto;
import com.study.dawheen.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "알림 API", description = "알림 관련 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "SSE 연결")
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return notificationService.subscribe(email, lastEventId);
    }

    @Operation(summary = "알림 내역")
    @GetMapping("/history")
    public ResponseEntity<List<NotificationResponseDto>> history() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<NotificationResponseDto> responseDtoList = notificationService.history(email);
        return ResponseEntity.ok(responseDtoList);
    }

    @Operation(summary = "읽음 표시", description = "해당 알림을 누르면 읽음 표시로 변경시킵니다.")
    @PostMapping("/read")
    public ResponseEntity<NotificationResponseDto> read(@RequestBody Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(notificationService.read(id, email));
    }

}
