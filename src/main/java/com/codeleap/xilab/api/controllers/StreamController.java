package com.codeleap.xilab.api.controllers;

import com.codeleap.xilab.api.services.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/stream")
public class StreamController extends BaseController {

	@Autowired
    ImageService imageService;

	// Stream avatar image
    @Operation(summary = "Stream Avatar Image By ID")
	@GetMapping("/avatar/{fileKey}")
	public ResponseEntity<?> streamImage(@PathVariable String fileKey) {
        HttpHeaders headers = new HttpHeaders();
        var imageBytes = imageService.loadImageFromDB(fileKey);
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        return responseEntity;

    }
}
