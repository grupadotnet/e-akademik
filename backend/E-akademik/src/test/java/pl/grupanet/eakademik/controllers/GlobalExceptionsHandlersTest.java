package pl.grupanet.eakademik.controllers;

import org.commons.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.grupanet.eakademik.authentication.UserAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionsHandlersTest {

    private GlobalExceptionsHandlers handler;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionsHandlers();
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void shouldReturnBadRequestForIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid email format");

        ResponseEntity<String> response = handler.handleBadRequest(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid email format");
    }

    @Test
    void shouldReturnBadRequestForIllegalStateException() {
        IllegalStateException exception = new IllegalStateException("Invalid state");

        ResponseEntity<String> response = handler.handleBadRequest(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid state");
    }

    @Test
    void shouldReturnConflictForUserAlreadyExistsException() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException(new Email("Example@wp.pl"));

        ResponseEntity<String> response = handler.handleConflict(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("User with email: example@wp.pl already exists");
    }

    @Test
    void shouldReturnUnauthorizedWithFixedMessageForAuthenticationException() {
        AuthenticationException exception = new BadCredentialsException("Detailed internal reason");

        ResponseEntity<String> response = handler.handleUnauthorized(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid credentials");
    }

    @Test
    void shouldInterceptExceptionsThroughSpringMvcPipeline() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad argument"));

        mockMvc.perform(get("/test/illegal-state"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad state"));

        mockMvc.perform(get("/test/user-exists"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with email: example@wp.pl already exists"));

        mockMvc.perform(get("/test/auth-error"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @RestController
    private static class DummyController {

        @GetMapping("/test/illegal-argument")
        void throwIllegalArgument() {
            throw new IllegalArgumentException("Bad argument");
        }

        @GetMapping("/test/illegal-state")
        void throwIllegalState() {
            throw new IllegalStateException("Bad state");
        }

        @GetMapping("/test/user-exists")
        void throwUserExists() {
            throw new UserAlreadyExistsException(new Email("Example@wp.pl"));
        }

        @GetMapping("/test/auth-error")
        void throwAuthError() {
            throw new BadCredentialsException("Secret details");
        }
    }
}