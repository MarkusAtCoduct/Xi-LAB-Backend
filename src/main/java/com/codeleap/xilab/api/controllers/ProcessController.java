package com.codeleap.xilab.api.controllers;

import com.codeleap.xilab.api.models.entities.auth.UserDetailsImpl;
import com.codeleap.xilab.api.payload.request.CreateMethodRequest;
import com.codeleap.xilab.api.payload.request.CreateOrEditMyProcessRequest;
import com.codeleap.xilab.api.payload.response.BaseResponse;
import com.codeleap.xilab.api.payload.response.ResponseDoc;
import com.codeleap.xilab.api.security.jwt.AuthTokenFilter;
import com.codeleap.xilab.api.services.MethodService;
import com.codeleap.xilab.api.services.ProcessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/process", consumes = MediaType.APPLICATION_JSON_VALUE)
public class ProcessController {

	@Autowired
    ProcessService processService;

    @Operation(summary = "Get method details")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return method details",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MethodDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@GetMapping("/details")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> geProcessDetails(HttpServletRequest request) {
        Object loginUserObject = request.getAttribute(AuthTokenFilter.CURRENT_VALIDATED_USER_ATTR);
        if (loginUserObject != null && loginUserObject instanceof UserDetailsImpl) {
            var userDetails = (UserDetailsImpl) loginUserObject;
            return processService.getProcessDetailsInfo(userDetails.getId());
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
	}

	@PostMapping("/save")
    @Operation(summary = "Create or edit login user's process")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Successful and return process details",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.ProcessDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> createOrEditMyProcess(@Valid @RequestBody CreateOrEditMyProcessRequest createOrEditMyProcessRequest, HttpServletRequest request) {
        Object loginUserObject = request.getAttribute(AuthTokenFilter.CURRENT_VALIDATED_USER_ATTR);
        if (loginUserObject != null && loginUserObject instanceof UserDetailsImpl) {
            var userDetails = (UserDetailsImpl) loginUserObject;
            return null;
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
	}

}
