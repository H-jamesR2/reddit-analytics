// src/main/java/com/yourusername/redditanalytics/config/MetricsConfig.java
@Configuration
public class MetricsConfig {
    @Bean
    MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}

// Add metrics to services
@Service
public class RedditIngestionService {
    private final Counter postsIngested;
    private final Timer ingestionTimer;

    public RedditIngestionService(MeterRegistry registry) {
        this.postsIngested = registry.counter("reddit.posts.ingested");
        this.ingestionTimer = registry.timer("reddit.ingestion.time");
    }

    public void ingestSubredditData(String subreddit) {
        Timer.Sample sample = Timer.start();
        try {
            // existing ingestion logic
            postsIngested.increment();
        } finally {
            sample.stop(ingestionTimer);
        }
    }
}