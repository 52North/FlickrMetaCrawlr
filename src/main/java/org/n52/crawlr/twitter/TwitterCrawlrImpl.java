package org.n52.crawlr.twitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.n52.crawlr.core.ICrawlr;
import org.n52.crawlr.core.PropertiesLoader;
import org.n52.crawlr.entities.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterCrawlrImpl implements ICrawlr {

    private static final Logger log = LoggerFactory.getLogger(TwitterCrawlrImpl.class);

    /**
     * access to the Twitter API.
     */
    private Twitter twitter;

    private String consumerKey, consumerSecret, token, tokenSecret;

    public TwitterCrawlrImpl() throws IOException, ParserConfigurationException {
        initTwitterConnector();
        // TwitterFactory twf = new TwitterFactory();
        // twitter = twf.getInstance();
        // twitter.setOAuthConsumer("owFVBLYN7UXj5gzHwg1A",
        // "tHWGwjrI5LZqmI8tLDJIJac83ln6dvY9Em3jjhGmM4");
        // AccessToken accessToken =
        // new AccessToken("2267982991-f1cX6BZCgbcMzFsyjxFCG8M8tbj4G1C4kXBzFwu",
        // "as4WqmxdosAqDCzKQrq0rZnClLQLAviZnbneCSMt502vG");
        // twitter.setOAuthAccessToken(accessToken);
    }

    /**
     * Helper method to initialize a {@link Flickr} object.
     */
    private void initTwitterConnector() throws IOException, ParserConfigurationException {
        Properties properties = PropertiesLoader.getInstance().getProperties();
        consumerKey = properties.getProperty(TWITTER_CONSUMER_KEY);
        consumerSecret = properties.getProperty(TWITTER_CONSUMER_SECRET);
        token = properties.getProperty(TWITTER_TOKEN);
        tokenSecret = properties.getProperty(TWITTER_TOKEN_SECRET);
        TwitterFactory twf = new TwitterFactory();
        twitter = twf.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        twitter.setOAuthAccessToken(accessToken);

    }

    @Override
    public List<Entry> crawlForEntities(double minLongitude, double minLatitude, double maxLongitude,
            double maxLatitude, Date minDate, Date maxDate, Collection<String> keywords) throws IOException,
            SAXException, TwitterException {
        StringBuilder queryString = new StringBuilder();
        for (String keyword : keywords) {
            queryString.append(keyword).append("+");
        }
        queryString.replace(queryString.lastIndexOf("+"), queryString.lastIndexOf("+") + 1, "");
        Query query = new Query(queryString.toString());

        GeoLocation location =
                new GeoLocation(minLatitude + (maxLatitude - minLatitude) / 2, minLongitude
                        + (maxLongitude - minLongitude) / 2);
        query.setGeoCode(location, 100, "mi");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(minDate);
        // query.setSince(calendar.YEAR+"-"+calendar.MONTH+1+"-"+calendar.DAY_OF_MONTH);
        query.setSince(minDate.getYear() + 1900 + "-" + (minDate.getMonth() + 1) + "-" + minDate.getDate());
        calendar.setTime(maxDate);
        // query.setUntil(calendar.YEAR+"-"+calendar.MONTH+1+"-"+calendar.DAY_OF_MONTH);
        query.setUntil(maxDate.getYear() + 1900 + "-" + (maxDate.getMonth() + 1) + "-" + maxDate.getDate());

        log.debug("queryString :" + query.toString());

        int numberOfTweetsToLoad = 10;
        long lastID = Long.MAX_VALUE;

        ArrayList<Entry> tweets = new ArrayList<Entry>();

        while (tweets.size() < numberOfTweetsToLoad) {
            log.debug("Progress: " + tweets.size() + "/" + numberOfTweetsToLoad);
            if (numberOfTweetsToLoad - tweets.size() > 100) {
                query.setCount(100);
            } else {
                query.setCount(numberOfTweetsToLoad - tweets.size());
            }
            try {
                QueryResult result = twitter.search(query);
                if (result.getTweets().isEmpty()) {
                    numberOfTweetsToLoad = tweets.size();
                    break;
                }
                Iterator<Status> tweetsIterator = result.getTweets().iterator();
                while (tweetsIterator.hasNext()) {
                    Status currentStatus = tweetsIterator.next();
                    if (currentStatus.getId() < lastID)
                        lastID = currentStatus.getId();
                    Entry currentTweet = null;
                    currentTweet = createTweet(currentStatus);
                    if (currentTweet != null) {
                        tweets.add(currentTweet);
                    }
                }
                query.setMaxId(lastID - 1);
            } catch (TwitterException e) {
                log.error(e.getErrorMessage(), e);
                throw e;
            }
        }

        log.debug("Result count :" + tweets.size());

        return tweets;
    }

    /**
     * Helper method to fill all data for a {@link Entry}.
     */
    private Entry createTweet(Status status) throws IOException, SAXException {
        if (isValidStatus(status)) {
            Entry tweet = new Entry();

            tweet.setID(Long.toString(status.getId()));
            tweet.setTitle("Unnamed place");
            tweet.setValue(status.getText());
            tweet.setDescription(status.getText());

            List<String> tags = new ArrayList<String>();
            for (int i = 0; i < status.getHashtagEntities().length; i++) {
                tags.add(status.getHashtagEntities()[i].getText());
            }

            tweet.setTags(tags);

            if (status.getCreatedAt() != null) {
                tweet.setDatePosted(status.getCreatedAt());
                tweet.setDateTaken(status.getCreatedAt());
            }

            User user = status.getUser();
            tweet.setUserId(Long.toString(user.getId()));
            tweet.setUserName(user.getName());

            if (status.getGeoLocation() != null) {
                tweet.setLongitude(status.getGeoLocation().getLongitude());
                tweet.setLatitude(status.getGeoLocation().getLatitude());
                tweet.setAccuracy(0);

            } else if (status.getPlace() != null) {
                if (status.getPlace().getName() != null) {
                    tweet.setTitle(status.getPlace().getName());
                    tags.add(status.getPlace().getName());
                }
                double latitude = 0.0;
                double longitude = 0.0;
                if (status.getPlace().getBoundingBoxCoordinates() != null) {
                    latitude =
                            (status.getPlace().getBoundingBoxCoordinates()[0][0].getLatitude() + status.getPlace()
                                    .getBoundingBoxCoordinates()[0][2].getLatitude()) / 2;
                    longitude =
                            (status.getPlace().getBoundingBoxCoordinates()[0][0].getLongitude() + status.getPlace()
                                    .getBoundingBoxCoordinates()[0][1].getLongitude()) / 2;
                } else if (status.getPlace().getGeometryCoordinates() != null) {
                    latitude =
                            (status.getPlace().getGeometryCoordinates()[0][0].getLatitude() + status.getPlace()
                                    .getGeometryCoordinates()[0][2].getLatitude()) / 2;
                    longitude =
                            (status.getPlace().getGeometryCoordinates()[0][0].getLongitude() + status.getPlace()
                                    .getGeometryCoordinates()[0][1].getLongitude()) / 2;
                }
                tweet.setLatitude(latitude);
                tweet.setLongitude(longitude);
                tweet.setAccuracy(0);
            }
            log.debug(tweet.toString());

            return tweet;
        }
        return null;
    }

    private boolean isValidStatus(Status status) {
        return status.getGeoLocation() != null
                && status.getPlace() != null
                && (status.getPlace().getBoundingBoxCoordinates() != null || status.getPlace()
                        .getGeometryCoordinates() != null);
    }

}
