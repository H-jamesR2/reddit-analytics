// src/main/java/com/yourusername/redditanalytics/exception/handler/GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RedditApiException.class)
    public ResponseEntity<ErrorResponse> handleRedditApiException(RedditApiException ex) {
        log.error("Reddit API error", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("Error fetching data from Reddit"));
    }

    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ErrorResponse> handleProcessingException(ProcessingException ex) {
        log.error("Processing error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error processing Reddit data"));
    }
}