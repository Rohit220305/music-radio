package com.musicradio.radio;

import com.musicradio.radio.common.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    @DisplayName("Should create success ApiResponse with payload and message")
    void testSuccessResponse() {
        String testData = "Test Payload";
        ApiResponse<String> response = ApiResponse.success(testData, "Successfully fetched");

        assertTrue(response.isSuccess());
        assertEquals("Successfully fetched", response.getMessage());
        assertEquals("Test Payload", response.getData());
        assertNull(response.getErrorCode());
        assertNull(response.getErrors());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create error ApiResponse with errorCode and structured errors")
    void testErrorResponse() {
        Map<String, List<String>> fieldErrors = Map.of(
                "name", List.of("Name is required", "Name must be at least 3 characters")
        );
        ApiResponse<Void> response = ApiResponse.error("Validation failed", "VALIDATION_ERROR", fieldErrors);

        assertFalse(response.isSuccess());
        assertEquals("Validation failed", response.getMessage());
        assertNull(response.getData());
        assertEquals("VALIDATION_ERROR", response.getErrorCode());
        assertEquals(fieldErrors, response.getErrors());
        assertEquals(2, response.getErrors().get("name").size());
        assertNotNull(response.getTimestamp());
    }
}

