package com.alibou.ecommerce.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody NotificationRequest request) {
        String id = this.service.createNotification(request);
        return ResponseEntity.created(URI.create("/api/v1/notifications/" + id)).body(id);
    }

    @GetMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(this.service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        return ResponseEntity.ok(this.service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> update(@PathVariable String id, @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(this.service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }
}