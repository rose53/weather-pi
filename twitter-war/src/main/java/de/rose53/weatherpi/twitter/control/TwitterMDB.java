package de.rose53.weatherpi.twitter.control;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;

@MessageDriven(name = "TwitterMDB", activationConfig = {
         @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
         @ActivationConfigProperty(propertyName = "clientId",        propertyValue = "TwitterMDB"),
         @ActivationConfigProperty(propertyName = "destination",     propertyValue = "topic/DayStatisticTopic"),
         @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class TwitterMDB implements MessageListener {

     @Inject
     Logger logger;

    public void onMessage(Message message) {

        TextMessage tm = (TextMessage) message;
        try {
            logger.debug("onMessage: {}",tm.getText());
        } catch (JMSException e) {
            logger.error("onMessage",e);
        }


    }
}
