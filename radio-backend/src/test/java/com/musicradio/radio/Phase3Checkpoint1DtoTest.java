package com.musicradio.radio;

import com.musicradio.radio.common.config.InterimSecurityConfig;
import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.station.dto.UpdateStationRequest;
import com.musicradio.radio.theme.dto.UpdateThemeRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Phase3Checkpoint1DtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("StatusUpdateRequest should validate non-null isActive")
    void testStatusUpdateRequestValidation() {
        StatusUpdateRequest valid = StatusUpdateRequest.builder().isActive(true).build();
        Set<ConstraintViolation<StatusUpdateRequest>> violations = validator.validate(valid);
        assertTrue(violations.isEmpty(), "Valid StatusUpdateRequest should have zero violations");

        StatusUpdateRequest invalid = StatusUpdateRequest.builder().isActive(null).build();
        violations = validator.validate(invalid);
        assertFalse(violations.isEmpty(), "StatusUpdateRequest with null isActive must have violations");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("isActive")));
    }

    @Test
    @DisplayName("UpdateThemeRequest should enforce all required fields for full PUT semantics")
    void testUpdateThemeRequestValidation() {
        UpdateThemeRequest valid = UpdateThemeRequest.builder()
                .name("Rainy Evening")
                .slug("rainy-evening")
                .description("Cozy ambiance")
                .backgroundImage("https://example.com/bg.jpg")
                .thumbnailImage("https://example.com/thumb.jpg")
                .sortOrder(1)
                .isActive(true)
                .build();
        Set<ConstraintViolation<UpdateThemeRequest>> violations = validator.validate(valid);
        assertTrue(violations.isEmpty(), "Valid UpdateThemeRequest should have zero violations");

        UpdateThemeRequest invalid = UpdateThemeRequest.builder()
                .name("")
                .slug("INVALID SLUG!")
                .sortOrder(null)
                .isActive(null)
                .build();
        violations = validator.validate(invalid);
        assertEquals(4, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("slug")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sortOrder")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("isActive")));
    }

    @Test
    @DisplayName("UpdateStationRequest should enforce all required fields and YouTube URL for full PUT semantics")
    void testUpdateStationRequestValidation() {
        UpdateStationRequest valid = UpdateStationRequest.builder()
                .themeId(1)
                .name("Lo-Fi Study")
                .slug("lofi-study")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_STUDY_123")
                .description("Focus beats")
                .shuffleEnabled(false)
                .repeatEnabled(true)
                .isActive(true)
                .build();
        Set<ConstraintViolation<UpdateStationRequest>> violations = validator.validate(valid);
        assertTrue(violations.isEmpty(), "Valid UpdateStationRequest should have zero violations");

        UpdateStationRequest invalid = UpdateStationRequest.builder()
                .themeId(null)
                .name("   ")
                .slug("bad slug")
                .youtubePlaylistUrl("https://notyoutube.com/playlist?list=123")
                .shuffleEnabled(null)
                .repeatEnabled(null)
                .isActive(null)
                .build();
        violations = validator.validate(invalid);
        assertEquals(7, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("themeId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("slug")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("youtubePlaylistUrl")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shuffleEnabled")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("repeatEnabled")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("isActive")));
    }

    @Test
    @DisplayName("InterimSecurityConfig should be annotated with @Profile({\"dev\", \"test\"})")
    void testInterimSecurityConfigProfileAnnotation() {
        Profile profileAnnotation = InterimSecurityConfig.class.getAnnotation(Profile.class);
        assertNotNull(profileAnnotation, "InterimSecurityConfig must have @Profile annotation");
        String[] profiles = profileAnnotation.value();
        assertArrayEquals(new String[]{"dev", "test"}, profiles);
    }
}

