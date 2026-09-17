package com.petcare.controller;

import com.petcare.dto.CallRequest;
import com.petcare.dto.CallResponse;
import com.petcare.service.CallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** User Story: Video call with Veterinarian (signaling REST đơn giản). */
@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallController {

    private final CallService callService;

    @PostMapping
    public ResponseEntity<CallResponse> start(@Valid @RequestBody CallRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(callService.start(req));
    }

    @PutMapping("/{id}/accept")
    public CallResponse accept(@PathVariable Long id) {
        return callService.accept(id);
    }

    @PutMapping("/{id}/end")
    public CallResponse end(@PathVariable Long id) {
        return callService.end(id);
    }
}
