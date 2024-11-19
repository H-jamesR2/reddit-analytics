@Configuration
public class RedditConfig {
    @Value("${reddit.client.id}")
    private String clientId;

    @Value("${reddit.client.secret}")
    private String clientSecret;

    // Bean definitions and configurations
}