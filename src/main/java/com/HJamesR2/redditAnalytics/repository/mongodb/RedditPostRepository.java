// src/main/java/com/yourusername/redditanalytics/repository/mongodb/RedditPostRepository.java
@Repository
public interface RedditPostRepository extends MongoRepository<RedditPost, String> {
    List<RedditPost> findBySubredditAndCreatedBetween(String subreddit, Date start, Date end);

    @Query("{'subreddit': ?0, 'created': {'$gt': ?1}}")
    Stream<RedditPost> streamBySubredditAndCreatedAfter(String subreddit, Date after);
}

// src/main/java/com/yourusername/redditanalytics/repository/mysql/PostAnalyticsRepository.java
@Repository
public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, String> {
    List<PostAnalytics> findBySubredditAndCreatedAtBetween(
            String subreddit,
            LocalDateTime start,
            LocalDateTime end);

    @Query(value = "SELECT subreddit, AVG(sentiment_score) as avgSentiment " +
            "FROM post_analytics " +
            "WHERE created_at >= ?1 " +
            "GROUP BY subreddit", nativeQuery = true)
    List<SubredditSentimentDTO> getAverageSentimentBySubreddit(LocalDateTime since);
}