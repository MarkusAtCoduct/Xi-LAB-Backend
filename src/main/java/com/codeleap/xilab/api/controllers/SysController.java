package com.codeleap.xilab.api.controllers;

import com.codeleap.xilab.api.services.SysService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/sys", produces = MediaType.APPLICATION_JSON_VALUE)
public class SysController {

    @Autowired
    SysService sysService;

	@GetMapping("/heartbeat")
	public ResponseEntity<?> getHeartbeat() {
		log.info("Heartbeat: OK");
		return ResponseEntity.ok(Collections.singletonMap("heartbeat", "OK"));
	}

	@GetMapping("/version")
	public ResponseEntity<?> getVersion(HttpServletRequest request) {
        log.info("Check version from IP Address: " + sysService.getClientIpAddress(request));
        return ResponseEntity.ok(Collections.singletonMap("Running version", sysService.getAppVersion()));
	}

}
