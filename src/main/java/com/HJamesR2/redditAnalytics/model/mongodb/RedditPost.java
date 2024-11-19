// src/main/java/com/yourusername/redditanalytics/model/mongodb/RedditPost.java
@Data
@Document(collection = "reddit_posts")
public class RedditPost {
    @Id
    private String id;
    private String subreddit;
    private String title;
    private String selfText;
    private String author;
    private Integer score;
    private Double upvoteRatio;
    private Date created;
    private List<Comment> comments;
    private Map<String, Object> metadata;

    @Version
    private Long version;
}

// src/main/java/com/yourusername/redditanalytics/model/mysql/PostAnalytics.java
@Entity
@Data
@Table(name = "post_analytics")
public class PostAnalytics {
    @Id
    private String postId;

    private String subreddit;
    private Integer commentCount;
    private Double sentimentScore;
    private Integer score;
    private Double upvoteRatio;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}