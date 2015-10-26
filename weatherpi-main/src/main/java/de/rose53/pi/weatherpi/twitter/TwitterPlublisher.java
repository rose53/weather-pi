package de.rose53.pi.weatherpi.twitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@ApplicationScoped
public class TwitterPlublisher {

    @Inject
    Logger logger;

    @Inject
    Twitter twitter;

    public void updateStatus(String status) throws TwitterException {

        StatusUpdate statusUpdate = new StatusUpdate(status);

        twitter.updateStatus(statusUpdate);
    }
}
