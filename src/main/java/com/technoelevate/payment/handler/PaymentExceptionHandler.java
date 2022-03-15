package com.technoelevate.payment.handler;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.technoelevate.payment.exception.PaymentException;
import com.technoelevate.payment.response.ErrorMessage;


@RestControllerAdvice
public class PaymentExceptionHandler extends ResponseEntityExceptionHandler {

	

	@ExceptionHandler(PaymentException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorMessage> fileNotFound(PaymentException exception, WebRequest request) {
		return new ResponseEntity<>(new ErrorMessage(true, exception.getMessage(), null),
				HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(new ErrorMessage(true, exception.getMessage(), null), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorMessage> handleValidationException(ConstraintViolationException exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(true);
		List<String> error=new ArrayList<>();
		for (ConstraintViolation<?> constraintViolation : exception.getConstraintViolations()) {
			error.add(constraintViolation.getMessageTemplate());
		}
		errorMessage.setMessage(error);
		errorMessage.setData(null);
		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorMessage> internalServerError(RuntimeException exception, WebRequest request) {
		return new ResponseEntity<>(new ErrorMessage(true, exception.getMessage(), null),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
