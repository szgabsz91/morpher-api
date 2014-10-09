package com.github.szgabsz91.morpher.api.model;

import org.junit.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseTest {

    @Test
    public void testConstructorAndGetters() {
        String message = "message";
        ErrorResponse errorResponse = new ErrorResponse(message);
        assertThat(errorResponse.isError()).isTrue();
        assertThat(errorResponse.getMessage()).isEqualTo(message);
    }

    @Test
    public void testSetters() {
        String message = "message";
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(true);
        errorResponse.setMessage(message);
        assertThat(errorResponse.isError()).isTrue();
        assertThat(errorResponse.getMessage()).isEqualTo(message);
    }

    @Test
    public void testEquals() {
        ErrorResponse errorResponse1 = new ErrorResponse("message");
        ErrorResponse errorResponse2 = new ErrorResponse("message2");
        ErrorResponse errorResponse3 = new ErrorResponse("message");

        assertThat(errorResponse1).isEqualTo(errorResponse1);
        assertThat(errorResponse1).isNotEqualTo(null);
        assertThat(errorResponse1).isNotEqualTo("string");
        assertThat(errorResponse1).isNotEqualTo(errorResponse2);
        assertThat(errorResponse1).isEqualTo(errorResponse3);
    }

    @Test
    public void testHashCode() {
        ErrorResponse errorResponse = new ErrorResponse("message");
        int result = errorResponse.hashCode();
        assertThat(result).isEqualTo(Objects.hash(errorResponse.getMessage()));
    }

    @Test
    public void testToString() {
        ErrorResponse errorResponse = new ErrorResponse("message");
        assertThat(errorResponse).hasToString("ErrorResponse[message='" + errorResponse.getMessage() + "']");
    }

}
