package listener;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;

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
                        ArrayList<server.model.dto.Message> messages = (ArrayList) objectMessage.getObject();
                        createReport(messages);
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

    private void createReport(ArrayList<server.model.dto.Message> messages) {
        try {
            JasperDesign jd = createDesign();
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(messages);
            JasperPrint jp = JasperFillManager.fillReport(jr, new HashMap(), dataSource);
            JasperExportManager.exportReportToHtmlFile(jp, "jasperReport.html");
            JasperExportManager.exportReportToPdfFile(jp, "jasperReport.pdf");
            JasperExportManager.exportReportToXmlFile(jp, "jasperReport.xml", false);
        } catch (JRException e) {
            log.error(e.getMessage());
        }


    }

    private JasperDesign createDesign() throws JRException {
        JasperDesign jasDes = new JasperDesign();
        jasDes.setName("report");
        jasDes.setPageWidth(600);
        jasDes.setPageHeight(900);
        jasDes.setLeftMargin(20);
        jasDes.setRightMargin(20);
        jasDes.setTopMargin(20);
        jasDes.setBottomMargin(20);
        jasDes.setColumnWidth(550);

        JRDesignStyle mystyle = new JRDesignStyle();
        mystyle.setName("style");
        mystyle.setDefault(true);
        mystyle.setFontName("Arial");
        mystyle.setFontSize(12f);
        mystyle.setPdfFontName("Helvetica");
        mystyle.setPdfEncoding("UTF-8");
        jasDes.addStyle(mystyle);

        JRDesignField fieldId = new JRDesignField();
        fieldId.setName("id");
        fieldId.setValueClass(Integer.class);
        jasDes.addField(fieldId);

        JRDesignField fieldRoomName = new JRDesignField();
        fieldRoomName.setName("roomName");
        fieldRoomName.setValueClass(String.class);
        jasDes.addField(fieldRoomName);

        JRDesignField fieldTime = new JRDesignField();
        fieldTime.setName("time");
        fieldTime.setValueClass(OffsetDateTime.class);
        jasDes.addField(fieldTime);

        JRDesignField fieldNickname = new JRDesignField();
        fieldNickname.setName("nickname");
        fieldNickname.setValueClass(String.class);
        jasDes.addField(fieldNickname);

        JRDesignField fieldContent = new JRDesignField();
        fieldContent.setName("content");
        fieldContent.setValueClass(String.class);
        jasDes.addField(fieldContent);

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(50);

        JRDesignStaticText titleText = new JRDesignStaticText();
        titleText.setText("Messages");
        titleText.setX(0);
        titleText.setY(10);
        titleText.setWidth(515);
        titleText.setHeight(30);
        titleText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleText.setFontSize(22f);
        titleBand.addElement(titleText);
        jasDes.setTitle(titleBand);

        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(60);

        int x = 0;
        JRDesignTextField tfId = new JRDesignTextField();
        tfId.setBlankWhenNull(true);
        tfId.setX(x);
        tfId.setY(10);
        tfId.setWidth(60);
        tfId.setHeight(30);
        tfId.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        tfId.setStyle(mystyle);
        tfId.setExpression(new JRDesignExpression("$F{id}"));
        detailBand.addElement(tfId);

        x += 50;
        JRDesignTextField tfRoomName = new JRDesignTextField();
        tfRoomName.setBlankWhenNull(true);
        tfRoomName.setX(x);
        tfRoomName.setY(10);
        tfRoomName.setWidth(80);
        tfRoomName.setHeight(30);
        tfRoomName.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        tfRoomName.setStyle(mystyle);
        tfRoomName.setExpression(new JRDesignExpression("$F{roomName}"));
        detailBand.addElement(tfRoomName);

        x += 100;
        JRDesignTextField tfTime = new JRDesignTextField();
        tfTime.setBlankWhenNull(true);
        tfTime.setX(x);
        tfTime.setY(10);
        tfTime.setWidth(100);
        tfTime.setHeight(30);
        tfTime.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        tfTime.setStyle(mystyle);
        tfTime.setExpression(new JRDesignExpression("$F{time}"));
        detailBand.addElement(tfTime);

        x += 100;
        JRDesignTextField tfNickname = new JRDesignTextField();
        tfNickname.setBlankWhenNull(true);
        tfNickname.setX(x);
        tfNickname.setY(10);
        tfNickname.setWidth(80);
        tfNickname.setHeight(30);
        tfNickname.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        tfNickname.setStyle(mystyle);
        tfNickname.setExpression(new JRDesignExpression("$F{nickname}"));
        detailBand.addElement(tfNickname);


        x += 80;
        JRDesignTextField tfContent = new JRDesignTextField();
        tfContent.setBlankWhenNull(true);
        tfContent.setX(x);
        tfContent.setY(10);
        tfContent.setWidth(200);
        tfContent.setHeight(30);
        tfContent.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        tfContent.setStyle(mystyle);
        tfContent.setExpression(new JRDesignExpression("$F{content}"));
        detailBand.addElement(tfContent);


        ((JRDesignSection) jasDes.getDetailSection()).addBand(detailBand);

        return jasDes;
    }
}