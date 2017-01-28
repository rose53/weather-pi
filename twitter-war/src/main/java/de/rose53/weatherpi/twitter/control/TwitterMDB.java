package de.rose53.weatherpi.twitter.control;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.EClimatologicClassificationDay;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@MessageDriven(name = "TwitterMDB", activationConfig = {
         @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
         @ActivationConfigProperty(propertyName = "clientId",        propertyValue = "TwitterMDB"),
         @ActivationConfigProperty(propertyName = "destination",     propertyValue = "topic/DayStatisticTopic"),
         @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class TwitterMDB implements MessageListener {

    @Inject
    Logger logger;

    @Inject
    Twitter twitter;

    public void onMessage(Message message) {

        TextMessage tm = (TextMessage) message;
        try {
            logger.debug("onMessage: {}",tm.getText());

            JsonObject object = Json.createReader(new StringReader(tm.getText()))
                                    .readObject();
            if (object == null) {
                return;
            }

            DecimalFormat tempFormat = new DecimalFormat("#.0");

            StringBuilder status = new StringBuilder();

            if (object.getJsonNumber("tMin") != null) {
                status.append("Tmin: ").append(tempFormat.format(object.getJsonNumber("tMin").doubleValue())).append("°C").append('\n');
            }

            if (object.getJsonNumber("tMax") != null) {
                status.append("Tmax: ").append(tempFormat.format(object.getJsonNumber("tMax").doubleValue())).append("°C").append('\n');
            }

            if (object.getJsonNumber("tMed") != null) {
                status.append("Tmed: ").append(tempFormat.format(object.getJsonNumber("tMed").doubleValue())).append("°C").append('\n');
            }

            JsonArray jsonArray = object.getJsonArray("classificationDay");

            List<EClimatologicClassificationDay> ccdList = new LinkedList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                ccdList.add(EClimatologicClassificationDay.valueOf(jsonArray.getString(i)));
            }
            status.append("CCD : ").append(EClimatologicClassificationDay.getTwitterFeed(ccdList)).append('\n');

            logger.debug("onMessage: status for twitter = >{}<",status);
            StatusUpdate statusUpdate = new StatusUpdate(status.toString());

            twitter.updateStatus(statusUpdate);

        } catch (JMSException | TwitterException e) {
            logger.error("onMessage",e);
        }
    }
}
