// src/main/java/com/yourusername/redditanalytics/RedditAnalyticsApplication.java
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class RedditAnalyticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedditAnalyticsApplication.class, args);
    }
}