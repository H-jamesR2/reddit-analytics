// src/main/java/com/yourusername/redditanalytics/service/processing/DataProcessingService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class DataProcessingService {
    private final PostAnalyticsRepository analyticsRepository;
    private final SentimentAnalyzer sentimentAnalyzer;

    @EventListener
    @Async
    public void onPostIngested(PostIngestedEvent event) {
        try {
            RedditPost post = event.getPost();
            PostAnalytics analytics = processPost(post);
            analyticsRepository.save(analytics);
            log.debug("Processed post: {}", post.getId());
        } catch (Exception e) {
            log.error("Error processing post: {}", event.getPost().getId(), e);
        }
    }

    private PostAnalytics processPost(RedditPost post) {
        PostAnalytics analytics = new PostAnalytics();
        analytics.setPostId(post.getId());
        analytics.setSubreddit(post.getSubreddit());
        analytics.setCommentCount(post.getComments().size());
        analytics.setScore(post.getScore());
        analytics.setUpvoteRatio(post.getUpvoteRatio());

        // Calculate sentiment from title and self text
        String textContent = post.getTitle() + " " + post.getSelfText();
        Double sentimentScore = sentimentAnalyzer.analyzeSentiment(textContent);
        analytics.setSentimentScore(sentimentScore);

        return analytics;
    }
}

// src/main/java/com/yourusername/redditanalytics/service/processing/SentimentAnalyzer.java
@Service
@Slf4j
public class SentimentAnalyzer {
    private final Properties props;
    private final StanfordCoreNLP pipeline;

    public SentimentAnalyzer() {
        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public Double analyzeSentiment(String text) {
        if (StringUtils.isBlank(text)) {
            return 0.0;
        }

        try {
            Annotation annotation = new Annotation(text);
            pipeline.annotate(annotation);

            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            double totalSentiment = 0.0;

            for (CoreMap sentence : sentences) {
                String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                totalSentiment += convertSentimentToScore(sentiment);
            }

            return sentences.isEmpty() ? 0.0 : totalSentiment / sentences.size();
        } catch (Exception e) {
            log.error("Error analyzing sentiment: {}", e.getMessage());
            return 0.0;
        }
    }

    private double convertSentimentToScore(String sentiment) {
        return switch (sentiment.toLowerCase()) {
            case "very positive" -> 1.0;
            case "positive" -> 0.5;
            case "neutral" -> 0.0;
            case "negative" -> -0.5;
            case "very negative" -> -1.0;
            default -> 0.0;
        };
    }
}