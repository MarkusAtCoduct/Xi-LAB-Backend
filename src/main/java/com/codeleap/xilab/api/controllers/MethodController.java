package com.codeleap.xilab.api.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codeleap.xilab.api.filter.FilterPageable;
import com.codeleap.xilab.api.models.entities.auth.UserDetailsImpl;
import com.codeleap.xilab.api.payload.request.CommentMethodRequest;
import com.codeleap.xilab.api.payload.request.CreateMethodRequest;
import com.codeleap.xilab.api.payload.request.RateMethodRequest;
import com.codeleap.xilab.api.payload.response.BaseResponse;
import com.codeleap.xilab.api.payload.response.ResponseDoc;
import com.codeleap.xilab.api.security.jwt.AuthTokenFilter;
import com.codeleap.xilab.api.services.MethodService;
import com.codeleap.xilab.api.services.ResourceService;
import com.codeleap.xilab.api.utils.SysUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/method", consumes = MediaType.APPLICATION_JSON_VALUE)
public class MethodController extends BaseController {

	@Autowired
    MethodService methodService;

	@Autowired
    ResourceService resourceService;

	// Get Method details
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
	@GetMapping("/{methodId}")
	//@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> geMethodDetails(@Valid @PathVariable Long methodId, HttpServletRequest request) {
        //Object loginUserObject = request.getAttribute(AuthTokenFilter.CURRENT_VALIDATED_USER_ATTR);
        //if (loginUserObject != null && loginUserObject instanceof UserDetailsImpl) {
            //var userDetails = (UserDetailsImpl) loginUserObject;
            return methodService.getMethodInfo(methodId, 1);
        //}
        //else {
        //    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        //}
	}

	// Method search
	@Operation(summary = "Search for methods and method sets")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return list of methods and method sets with pagination",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.PagingMethodList.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@GetMapping("/search")
	public ResponseEntity<?> searchFoMethods(@RequestParam(required = false) String label,
                                             @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                             @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
                                             @Parameter(description = "Value should be [Name, Cost, Time, Rate, Phase]")
                                             @RequestParam(value = "sortBy", defaultValue = "NAME") String sortBy,
                                             @Parameter(description = "Input the number of phase that you want to filter. This will work when the 'sortBy' param is set to 'phase'")
                                             @RequestParam(value = "certainPhase", required = false, defaultValue = "0") Short certainPhase,
                                             @Parameter(description = "Value should be [Asc, Desc]")
                                             @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
                                             @RequestParam(required = false, defaultValue = "true") Boolean includeMethods,
                                             @RequestParam(required = false, defaultValue = "true") Boolean includeMethodSets, HttpServletRequest request) {
        var pagingInfo = FilterPageable.generateMethodPageable(pageIndex, pageSize, sortBy, sortDirection);
        if (pagingInfo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return methodService.methodSearch(0l, label, certainPhase, pagingInfo, includeMethods, includeMethodSets);
        }
        return methodService.methodSearch(userDetails.getId(), label, certainPhase, pagingInfo, includeMethods, includeMethodSets);
    }

    // Create new Method
	@PostMapping("/create")
    @Operation(summary = "Create new method")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Successful and return message",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> createMethod(@Valid @RequestBody CreateMethodRequest createMethodRequest, HttpServletRequest request) {
        Object loginUserObject = request.getAttribute(AuthTokenFilter.CURRENT_VALIDATED_USER_ATTR);
        if (loginUserObject != null && loginUserObject instanceof UserDetailsImpl) {
            var userDetails = (UserDetailsImpl) loginUserObject;
            return methodService.createOrUpdateMethod(createMethodRequest, userDetails.getId(), null);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
	}

	// Update existing Method
	@PutMapping("/{methodId}/update")
    @Operation(summary = "Update the existing method")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return message",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> updateMethod(@Valid @RequestBody CreateMethodRequest createMethodRequest, HttpServletRequest request, @PathVariable Long methodId) {
        Object loginUserObject = request.getAttribute(AuthTokenFilter.CURRENT_VALIDATED_USER_ATTR);
        if (loginUserObject != null && loginUserObject instanceof UserDetailsImpl) {
            var userDetails = (UserDetailsImpl) loginUserObject;
            return methodService.createOrUpdateMethod(createMethodRequest, userDetails.getId(), methodId);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
	}

	// Delete existing Method
	@DeleteMapping("/{methodId}")
    @Operation(summary = "Delete the existing method")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return message",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> deleteMethod(HttpServletRequest request, @PathVariable Long methodId) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
        return methodService.deleteMethod(userDetails.getId(), methodId);
	}

	// Publish a method
	@PatchMapping("/{methodId}/publish")
    @Operation(summary = "Publish a method, so that other users can search for it")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return message",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MethodDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> publishMethod(HttpServletRequest request, @PathVariable Long methodId) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
        return methodService.publishMethod(userDetails.getId(), methodId);
	}

	// Give rating to a method
	@PostMapping("/{methodId}/rate")
    @Operation(summary = "Rate and leave message on method")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Successful and return message",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.RatingDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> rateMethod(HttpServletRequest request, @Valid @RequestBody RateMethodRequest rateMethodRequest, @PathVariable Long methodId) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }

        return methodService.addMethodRating(userDetails.getId(), rateMethodRequest, methodId);
	}

	// Get all ratings
    @GetMapping("/{methodId}/ratings")
    @Operation(summary = "Get all rating items of method")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return list of rating items",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.RatingList.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getMethodRatings(@PathVariable Long methodId) {
        return methodService.getAllRatingsOfMethod(methodId);
    }

    // Leave a comment to method
	@PostMapping("/{methodId}/comment")
    @Operation(summary = "Comment on method or on a message thread of method")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Successful and return comment object",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.CommentDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> commentMethod(HttpServletRequest request, @Valid @RequestBody CommentMethodRequest commentMethodRequest, @PathVariable Long methodId) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }

        return methodService.addMethodComment(userDetails.getId(), commentMethodRequest, methodId);
	}

	// Get all comments of method
	@GetMapping("/{methodId}/comments")
    @Operation(summary = "Get all comments of method")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return list of comments",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.CommentList.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> getMethodComments(@PathVariable Long methodId) {
        return methodService.getAllCommentsOfMethod(methodId);
	}

    // Download Sample CSV for Importing Methods
    @Operation(summary = "Download sample CSV file for importing methods")
    @GetMapping("/download-import-sample-csv")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> downloadSampleCSVForImportingMethod(HttpServletRequest request) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }

        HttpHeaders headers = new HttpHeaders();
        var sampleResourceFile = resourceService.loadSampleCSVForImportingMethods();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"MethodsImportSample.csv\"");
        return new ResponseEntity<>(sampleResourceFile, headers, HttpStatus.OK);
    }

    // Upload Importing Methods
    @Operation(summary = "Import methods by uploading CSV file")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return message",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
    @PostMapping(value = "/import-methods", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUserAvatarByStream(HttpServletRequest request,
                                                      @RequestParam("importCsv") MultipartFile csvFile) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }

        return methodService.importMethodsFromUploadCsvFile(userDetails.getId(), csvFile);
    }
}
