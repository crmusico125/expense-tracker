package com.github.crmusico125.expensetracker.common.error;

import com.github.crmusico125.expensetracker.user.exception.EmailAlreadyExistsException;
import com.github.crmusico125.expensetracker.auth.exception.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Invalid credentials");
        problem.setDetail("Email or password is incorrect");
        problem.setType(URI.create("https://api.expensetracker.dev/problems/invalid-credentials"));
        problem.setInstance(URI.create(request.getRequestURI()));

        Object traceId = request.getAttribute("traceId");
        if (traceId != null) problem.setProperty("traceId", traceId);

        return problem;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Email already exists");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://api.expensetracker.dev/problems/email-already-exists"));
        problem.setInstance(URI.create(request.getRequestURI()));

        Object traceId = request.getAttribute("traceId");
        if (traceId != null) problem.setProperty("traceId", traceId);

        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid request");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://api.expensetracker.dev/problems/invalid-request"));
        problem.setInstance(URI.create(request.getRequestURI()));

        Object traceId = request.getAttribute("traceId");
        if (traceId != null) problem.setProperty("traceId", traceId);

        return problem;
    }
}
