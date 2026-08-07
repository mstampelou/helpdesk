package gr.aueb.cf.helpdesk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({TicketNotFoundException.class, CategoryNotFoundException.class, UserNotFoundException.class})
    public ModelAndView handleNotFound(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        ModelAndView mav = new ModelAndView("error/403");
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleBadRequest(IllegalArgumentException ex) {
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("message", ex.getMessage());
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }
}
