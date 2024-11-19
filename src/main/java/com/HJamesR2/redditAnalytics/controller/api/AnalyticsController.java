// src/main/java/com/yourusername/redditanalytics/controller/api/AnalyticsController.java
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/subreddit/{name}")
    public ResponseEntity<SubredditAnalytics> getSubredditAnalytics(
            @PathVariable String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        SubredditAnalytics analytics = analyticsService.getSubredditAnalytics(name, start, end);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/subreddit/{name}/trending")
    public ResponseEntity<List<TrendingTopic>> getTrendingTopics(
            @PathVariable String name,
            @RequestParam(defaultValue = "10") int limit) {

        List<TrendingTopic> trends = analyticsService.getTrendingTopics(name, limit);
        return ResponseEntity.ok(trends);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Error processing request", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage()));
    }
}