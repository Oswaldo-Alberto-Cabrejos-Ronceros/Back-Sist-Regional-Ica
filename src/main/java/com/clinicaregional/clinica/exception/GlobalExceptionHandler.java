// package com.clinicaregional.clinica.exception;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice
// public class GlobalExceptionHandler {

//     private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//     // Error genérico
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
//         logger.error("Error general: {}", ex.getMessage(), ex);
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                 .body(new ErrorResponse("Error interno del servidor"));
//     }

//     // Acceso denegado
//     @ExceptionHandler(AccessDeniedException.class)
//     public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
//         logger.warn("Acceso denegado: {}", ex.getMessage());
//         return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                 .body(new ErrorResponse("Acceso denegado"));
//     }

//     // Record de respuesta
//     public record ErrorResponse(String message) {}
// }
