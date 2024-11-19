// src/main/java/com/yourusername/redditanalytics/client/reddit/RedditApiClient.java
@Slf4j
@Service
@RequiredArgsConstructor
public class RedditApiClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${reddit.client.id}")
    private String clientId;

    @Value("${reddit.client.secret}")
    private String clientSecret;

    @Value("${reddit.user-agent}")
    private String userAgent;

    private String accessToken;
    private Instant tokenExpiration;

    @PostConstruct
    public void init() {
        refreshAccessToken();
    }

    private synchronized void refreshAccessToken() {
        if (tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
            return;
        }

        WebClient authClient = webClientBuilder.build();

        String credentials = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes());

        TokenResponse response = authClient.post()
                .uri("https://www.reddit.com/api/v1/access_token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        if (response != null) {
            this.accessToken = response.getAccessToken();
            this.tokenExpiration = Instant.now().plusSeconds(response.getExpiresIn());
        }
    }

    public Flux<RedditPost> streamSubredditPosts(String subreddit, int limit) {
        refreshAccessTokenIfNeeded();

        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/r/{subreddit}/new")
                        .queryParam("limit", limit)
                        .build(subreddit))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .retrieve()
                .bodyToFlux(RedditPost.class)
                .doOnError(error -> log.error("Error fetching posts: ", error));
    }
}

// src/main/java/com/yourusername/redditanalytics/client/reddit/dto/TokenResponse.java
@Data
@NoArgsConstructor
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;
}