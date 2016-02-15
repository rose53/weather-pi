package de.rose53.weatherpi.twitter.control;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.weatherpi.configuration.StringConfiguration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterExposer {

    @Inject
    Logger logger;

    @Inject
    @StringConfiguration(key="twitter.consumerKey")
    String consumerKey;

    @Inject
    @StringConfiguration(key="twitter.consumerSecret")
    String consumerSecret;

    @Inject
    @StringConfiguration(key="twitter.accessToken")
    String accessToken;

    @Inject
    @StringConfiguration(key="twitter.accessTokenSecret")
    String accessTokenSecret;


    @Produces
    public Twitter expose() {
        //Instantiate a re-usable and thread-safe factory
        TwitterFactory twitterFactory = new TwitterFactory();

        //Instantiate a new Twitter instance
        Twitter twitter = twitterFactory.getInstance();

        //setup OAuth Consumer Credentials
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        //setup OAuth Access Token
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));

        return twitter;
    }
}
