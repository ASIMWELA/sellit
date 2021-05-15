package com.sellit.api.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler
{

    private ResponseEntity<Object> buildResponseEntity(ApiException apiException)
    {
        return new ResponseEntity<>(apiException, apiException.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex)
    {
        ApiException apiError = new ApiException(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    protected ResponseEntity<Object> handleEntityAlreadyExistException(EntityAlreadyExistException ex)
    {
        ApiException apiError = new ApiException(CONFLICT);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(SignatureException.class)
    protected ResponseEntity<Object> jwtSignatureError(SignatureException ex)
    {
        ApiException apiError = new ApiException(UNAUTHORIZED);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> emptyClaims(IllegalArgumentException ex)
    {
        ApiException apiError = new ApiException(UNAUTHORIZED);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthError(AuthenticationException ex)
    {
        ApiException apiError = new ApiException(UNAUTHORIZED);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }
    @ExceptionHandler(InsufficientAuthenticationException.class)
    protected ResponseEntity<Object> insufficientAuth(InsufficientAuthenticationException ex)
    {
        ApiException apiError = new ApiException(UNAUTHORIZED);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(MalformedJwtException.class)
    protected ResponseEntity<Object> malformedJwt(MalformedJwtException ex)
    {
        ApiException apiError = new ApiException(UNAUTHORIZED);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<Object> expiredJwt(ExpiredJwtException ex)
    {
        ApiException apiError = new ApiException(UNAUTHORIZED);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    protected ResponseEntity<Object> unsupportedJwt(UnsupportedJwtException ex)
    {
        ApiException apiError = new ApiException(UNAUTHORIZED);
        apiError.setMessage(ex.getMessage());
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<Object> sql(SQLException ex)
    {
        ApiException apiError = new ApiException(BAD_REQUEST);
        apiError.setMessage("Sql Exception occurred : " + ex.getMessage().substring(7, ex.getMessage().indexOf("Detail")));
        apiError.setCode(apiError.getStatus().value());
        return buildResponseEntity(apiError);

    }

}
