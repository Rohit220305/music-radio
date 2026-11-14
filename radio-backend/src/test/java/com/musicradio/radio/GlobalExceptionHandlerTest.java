package com.musicradio.radio;

import com.musicradio.radio.common.exception.BadRequestException;
import com.musicradio.radio.common.exception.DuplicateResourceException;
import com.musicradio.radio.common.exception.GlobalExceptionHandler;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException and return 404")
    void testHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Theme", "slug", "rainy-night");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("NOT_FOUND", response.getBody().getErrorCode());
        assertEquals("Theme not found with slug: 'rainy-night'", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException and return 409")
    void testHandleDuplicateResource() {
        DuplicateResourceException ex = new DuplicateResourceException("Theme", "slug", "coffee-shop");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleDuplicateResourceException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("DUPLICATE_RESOURCE", response.getBody().getErrorCode());
        assertEquals("Theme already exists with slug: 'coffee-shop'", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle BadRequestException and return 400")
    void testHandleBadRequest() {
        BadRequestException ex = new BadRequestException("Invalid playlist URL format");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleBadRequestException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("BAD_REQUEST", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException and return 409")
    void testHandleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("CONFLICT", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Should handle generic unexpected Exception and return 500 sanitized message")
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected SQL state leak");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
    }

    private void dummyMethod(String param) {}

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and preserve multiple error messages for the same field")
    void testHandleValidationExceptionPreservesMultipleMessagesPerField() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("dummyMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createStationRequest");

        bindingResult.addError(new FieldError("createStationRequest", "youtubePlaylistUrl", "Must not be blank"));
        bindingResult.addError(new FieldError("createStationRequest", "youtubePlaylistUrl", "Must be a valid YouTube playlist URL"));
        bindingResult.addError(new FieldError("createStationRequest", "name", "Station name is required"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals("Validation failed", response.getBody().getMessage());

        Map<String, List<String>> errors = response.getBody().getErrors();
        assertNotNull(errors);
        assertEquals(2, errors.size());

        List<String> urlErrors = errors.get("youtubePlaylistUrl");
        assertNotNull(urlErrors);
        assertEquals(2, urlErrors.size());
        assertTrue(urlErrors.contains("Must not be blank"));
        assertTrue(urlErrors.contains("Must be a valid YouTube playlist URL"));

        List<String> nameErrors = errors.get("name");
        assertNotNull(nameErrors);
        assertEquals(1, nameErrors.size());
        assertTrue(nameErrors.contains("Station name is required"));
    }
}

