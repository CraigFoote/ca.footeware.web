package ca.footeware.web.controllers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
class ExceptionController {

	public static final String ERROR = "error";

	@ExceptionHandler(value = Throwable.class)
	public String defaultErrorHandler(HttpServletRequest req, Throwable t, Model model) {
		model.addAttribute("timestamp", LocalDateTime.now());
		model.addAttribute("path", req.getRequestURL());
		model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
		model.addAttribute(ERROR, t.getCause());
		model.addAttribute("message", t.getMessage());
		model.addAttribute("trace", extractTraces(t.getStackTrace()));
		return ERROR;
	}

	/**
	 * Creates a "\n"-delimited string from stacktrace.
	 *
	 * @param stackTrace {@link StackTraceElement}
	 * @return {@link String}
	 */
	private String extractTraces(StackTraceElement[] stackTrace) {
		StringBuilder buffer = new StringBuilder();
		for (StackTraceElement stackTraceElement : stackTrace) {
			buffer.append(stackTraceElement.toString());
			buffer.append("\n");
		}
		return buffer.toString();
	}
}