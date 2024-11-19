// src/main/java/com/yourusername/redditanalytics/service/ingestion/RedditIngestionService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class RedditIngestionService {
    private final RedditApiClient redditApiClient;
    private final RedditPostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${reddit.batch.size}")
    private int batchSize;

    @Scheduled(fixedRateString = "${reddit.batch.interval}")
    public void ingestSubredditData(String subreddit) {
        try {
            redditApiClient.streamSubredditPosts(subreddit, batchSize)
                    .buffer(batchSize)
                    .subscribe(posts -> {
                        List<RedditPost> savedPosts = postRepository.saveAll(posts);
                        savedPosts.forEach(post -> eventPublisher.publishEvent(new PostIngestedEvent(post)));
                        log.info("Ingested {} posts from r/{}", posts.size(), subreddit);
                    }, error -> log.error("Error during ingestion for r/{}: {}", subreddit, error.getMessage()));
        } catch (Exception e) {
            log.error("Failed to ingest data from r/{}", subreddit, e);
        }
    }
}