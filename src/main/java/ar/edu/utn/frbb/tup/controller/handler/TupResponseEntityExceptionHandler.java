package ar.edu.utn.frbb.tup.controller.handler;

import ar.edu.utn.frbb.tup.model.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TupResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    // Excepciones esperadas (negocio)
    @ExceptionHandler({
            TipoCuentaAlreadyExistsException.class,
            ClienteAlreadyExistsException.class,
            PrestamoNotAllowedException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
        return buildCustomErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({
            ClienteNotFoundException.class,
            CuentaNotFoundException.class,
            TipoCuentaNotSupportedException.class
    })
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        return buildCustomErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        if (body == null) {
            CustomApiError error = new CustomApiError();
            error.setErrorMessage(ex.getMessage());
            body = error;
        }

        return new ResponseEntity<>(body, headers, statusCode);
    }



    private ResponseEntity<Object> buildCustomErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        CustomApiError error = buildErrorObject(ex, status, request);
        return new ResponseEntity<>(error, status);
    }

    private CustomApiError buildErrorObject(Exception ex, HttpStatus status, WebRequest request) {
        CustomApiError error = new CustomApiError();
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setErrorMessage(ex.getMessage());

        if (request instanceof ServletWebRequest servletWebRequest) {
            error.setPath(servletWebRequest.getRequest().getRequestURI());
        }

        return error;
    }
}
