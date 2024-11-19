// src/main/java/com/yourusername/redditanalytics/service/analytics/AnalyticsService.java
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {
    private final PostAnalyticsRepository analyticsRepository;
    private final RedditPostRepository postRepository;

    public SubredditAnalytics getSubredditAnalytics(String subreddit, LocalDateTime start, LocalDateTime end) {
        List<PostAnalytics> analytics = analyticsRepository
                .findBySubredditAndCreatedAtBetween(subreddit, start, end);

        return SubredditAnalytics.builder()
                .subreddit(subreddit)
                .postCount(analytics.size())
                .averageSentiment(calculateAverageSentiment(analytics))
                .averageScore(calculateAverageScore(analytics))
                .commentStats(calculateCommentStats(analytics))
                .topPosts(findTopPosts(analytics, 5))
                .timeRange(new TimeRange(start, end))
                .build();
    }

    public List<TrendingTopic> getTrendingTopics(String subreddit, int limit) {
        LocalDateTime recent = LocalDateTime.now().minusHours(24);
        List<RedditPost> recentPosts = postRepository
                .findBySubredditAndCreatedBetween(
                        subreddit,
                        Date.from(recent.atZone(ZoneId.systemDefault()).toInstant()),
                        Date.from(Instant.now()));

        return extractTrendingTopics(recentPosts, limit);
    }

    private List<TrendingTopic> extractTrendingTopics(List<RedditPost> posts, int limit) {
        // Use TF-IDF to extract trending topics
        Map<String, Double> topicScores = new HashMap<>();

        // Process posts to extract topics
        posts.forEach(post -> {
            String text = post.getTitle() + " " + post.getSelfText();
            List<String> topics = extractTopics(text);

            topics.forEach(topic -> {
                double score = calculateTopicScore(topic, post);
                topicScores.merge(topic, score, Double::sum);
            });
        });

        return topicScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> new TrendingTopic(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<String> extractTopics(String text) {
        // Implement topic extraction logic (e.g., using NLP)
        // This is a simplified version
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(word -> word.length() > 3)
                .filter(word -> !StopWords.contains(word))
                .collect(Collectors.toList());
    }
}

// src/main/java/com/yourusername/redditanalytics/model/analytics/SubredditAnalytics.java
@Data
@Builder
public class SubredditAnalytics {
    private String subreddit;
    private int postCount;
    private double averageSentiment;
    private double averageScore;
    private CommentStats commentStats;
    private List<TopPost> topPosts;
    private TimeRange timeRange;
}