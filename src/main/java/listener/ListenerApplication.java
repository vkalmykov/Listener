package listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;

@Slf4j
public class ListenerApplication implements Runnable {
    public static void main(String[] args) {
        Runnable listener = new ListenerApplication();
        Thread thread = new Thread(listener);
        thread.start();
    }

    @Override
    public void run() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = null;
        MessageConsumer consumer = null;
        try {
            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("messages");
            consumer = session.createConsumer(queue);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        while (true) {
            try {
                Message message = null;
                if (consumer != null) {
                    message = consumer.receive();
                }
                if (message instanceof ObjectMessage) {
                    ObjectMessage objectMessage = (ObjectMessage) message;
                    if (objectMessage.getObject() instanceof ArrayList) {
                        ArrayList<Message> messages = (ArrayList) objectMessage.getObject();
                        log.info(messages.toString());
                    }
                }
                if (message instanceof TextMessage) {
                    break;
                }
            } catch (JMSException e) {
                log.error(e.getMessage());
            }
        }

        try {
            connection.stop();
        } catch (JMSException e) {
            log.error(e.getMessage());
        }
    }
}
