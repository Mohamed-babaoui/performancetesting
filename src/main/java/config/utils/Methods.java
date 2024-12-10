package config.utils;

import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;


public class Methods {

    public static void sendMailWS(String graphWS, String newTableWS, String oldTableWS, String compareTableWS, Date startDate, Date endDate, String conclusion, String oldStartDate, String oldVersion) throws MessagingException, IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy '-' HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        SimpleDateFormat formatterHour = new SimpleDateFormat("HH:mm:ss");
        formatterHour.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        Date date = new Date(System.currentTimeMillis());

        Properties props = new Properties();
        props.load(Methods.class.getClassLoader().getResourceAsStream("mail.properties"));
        String userName = props.getProperty("userName");
        String password = props.getProperty("password");
        String smtpHost = props.getProperty("smtpHost");
        String smtpPort = props.getProperty("smtpPort");
        String fromAddress = props.getProperty("fromAddress");
        String destAddresses = System.getProperty("mail_list");

        String actualVersion = System.getProperty("actual_version");
        String env = System.getProperty("env");;
        String url = System.getProperty("url");;
        String duration = System.getProperty("duration");;
        String nbrUsers = "1";
        String resultString = "ne sont pas changés";

        System.out.println(conclusion);
        if (conclusion.charAt(0) == '+')
            resultString = "meilleures de l’ordre de " + conclusion;
        else if (conclusion.charAt(0) == '-')
            resultString = "degradées de l’ordre de " + conclusion;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.socketFactory.port", smtpPort);
        properties.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(fromAddress));
        String[] emailArray = destAddresses.split(",");
        InternetAddress[] toAddresses = new InternetAddress[emailArray.length];
        for (int i = 0; i < emailArray.length; i++) {
            toAddresses[i] = new InternetAddress(emailArray[i].trim());
        }
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(String.format("Rapport Tir de Performance POLO WS  [%s]  [%s]", actualVersion, env));
        msg.setSentDate(new Date());

        // Construct HTML content with specified text, tables, and image
        StringBuilder htmlContent = new StringBuilder();
        HTMLManager htmlManager = new HTMLManager(htmlContent);

        htmlContent.append("<p>Hello,</p>");
        htmlContent.append(String.format("<h2>Tir de performance de POLO WS %s sur %s au %s:</h2>", actualVersion, env, formatterDate.format(startDate)));

        htmlContent.append(String.format("<p>Les tests de perf du %s sont finalisés sur %s.</p>", formatterDate.format(startDate), env));
        htmlContent.append(String.format("<p>Voici les résultats des tir de perf, %s seconds pointant sur les urls :</p>", duration));

        htmlContent.append(String.format("<ul>"));
        htmlContent.append(String.format("<li>%s seconds POLO-WS, Url : ", duration))
                .append(String.format("%s %s user.</li>", url, nbrUsers));
        htmlContent.append(String.format("</ul>"));


        htmlContent.append("<h3>Conclusion :</h3>");

        htmlContent.append(String.format("<ul>"));
        System.out.println(resultString);
        if (resultString.contains("meilleur"))
            htmlContent.append("<li><h5>WS : ").append(String.format("Les performances par rapport à la version %s sont <span style=\"color: green;\">%s</span> .</h5></li>", oldVersion, resultString));
        else if (resultString.contains("degrad"))
            htmlContent.append("<li><h5>WS : ").append(String.format("Les performances par rapport à la version %s sont <span style=\"color: red;\">%s</span> .</h5></li>", oldVersion, resultString));

        htmlContent.append(String.format("</ul>"));


        htmlContent.append(String.format("<h3>[POLO WS] (%s) (%s >> %s):</h3>", formatterDate.format(startDate), formatterHour.format(startDate), formatterHour.format(endDate)));

        // Image reference after introductory text
        htmlContent.append("<img src='cid:image' /><br>");

        // New data table
        htmlContent.append(String.format("<h4>Les données du tir (%s): [%s]</h4>", formatterDate.format(startDate), actualVersion));
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Request Name</th><th>Count</th><th>Average (ms)</th><th>Min (ms)</th>")
                .append("<th>Max (ms)</th><th>Stdev (ms)</th><th>Error (%)</th><th>Throughput (rq/s)</th></tr>");
        appendTableData(htmlContent, newTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing

        // Text between new and old tables
        htmlContent.append(String.format("<h4>Les données du tir (%s) : [%s]</h4>", oldStartDate, oldVersion));

        // Old data table
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Request Name</th><th>Count</th><th>Average (ms)</th><th>Min (ms)</th>")
                .append("<th>Max (ms)</th><th>Stdev (ms)</th><th>Error (%)</th><th>Throughput (rq/s)</th></tr>");
        appendTableData(htmlContent, oldTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing

        // Comparaison table title and data
        htmlContent.append("<h4>Comparaison:</h4>");
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Request Name</th><th>Old Average (ms)</th><th>New Average (ms)</th><th>Difference</th></tr>");
        appendTableData(htmlContent, compareTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing

        // Conclusion and closing
        htmlContent.append("<h3>Conclusion:</h3>");
        if (resultString.contains("meilleur"))
            htmlContent.append("<p>").append(String.format("Les performances par rapport à la version %s sont <span style=\"color: green;\">%s</span> .</p>", oldVersion, resultString));
        else if (resultString.contains("degrad"))
            htmlContent.append("<p>").append(String.format("Les performances par rapport à la version %s sont <span style=\"color: red;\">%s</span> .</p>", oldVersion, resultString));

        htmlContent.append("<p>Cdt,<br>Automation Team.</p>");

        // HTML part with content, image reference, and tables
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlContent.toString(), "text/html; charset=utf-8");

        // Image part
        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.attachFile(graphWS);
        imagePart.setContentID("<image>");
        imagePart.setDisposition(MimeBodyPart.INLINE);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(imagePart);

        msg.setContent(multipart);

        Transport.send(msg);
    }

    public static void sendMailWeb(String graphWS, String newTableWS, String oldTableWS, String compareTableWS, Date startDate, Date endDate, String conclusion, String oldStartDate, String oldVersion) throws MessagingException, IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy '-' HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        SimpleDateFormat formatterHour = new SimpleDateFormat("HH:mm:ss");
        formatterHour.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        Date date = new Date(System.currentTimeMillis());

        Properties props = new Properties();
        props.load(Methods.class.getClassLoader().getResourceAsStream("mail.properties"));
        String userName = props.getProperty("userName");
        String password = props.getProperty("password");
        String smtpHost = props.getProperty("smtpHost");
        String smtpPort = props.getProperty("smtpPort");
        String fromAddress = props.getProperty("fromAddress");
        String destAddresses = System.getProperty("mail_list");

        String actualVersion = System.getProperty("actual_version");
        String env = System.getProperty("env");;
        String url = System.getProperty("url");;
        String duration = System.getProperty("duration");;
        String nbrUsers = "2";
        String resultString = "ne sont pas changés";

        System.out.println(conclusion);
        if (conclusion.charAt(0) == '+')
            resultString = "meilleures de l’ordre de " + conclusion;
        else if (conclusion.charAt(0) == '-')
            resultString = "degradées de l’ordre de " + conclusion;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.socketFactory.port", smtpPort);
        properties.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(fromAddress));
        String[] emailArray = destAddresses.split(",");
        InternetAddress[] toAddresses = new InternetAddress[emailArray.length];
        for (int i = 0; i < emailArray.length; i++) {
            toAddresses[i] = new InternetAddress(emailArray[i].trim());
        }
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(String.format("Rapport Tir de Performance POLO WEB  [%s]  [%s]", actualVersion, env));
        msg.setSentDate(new Date());

        // Construct HTML content with specified text, tables, and image
        StringBuilder htmlContent = new StringBuilder();
        HTMLManager htmlManager = new HTMLManager(htmlContent);

        htmlContent.append("<p>Hello,</p>");
        htmlContent.append(String.format("<h2>Tir de performance de POLO Web %s sur %s au %s:</h2>", actualVersion, env, formatterDate.format(startDate)));

        htmlContent.append(String.format("<p>Les tests de perf du %s sont finalisés sur %s.</p>", formatterDate.format(startDate), env));
        htmlContent.append(String.format("<p>Voici les résultats des tir de perf, %s seconds pointant sur les urls :</p>", duration));

        htmlContent.append(String.format("<ul>"));
        htmlContent.append(String.format("<li>%s seconds POLO-WS, Url : ", duration))
                .append(String.format("%s %s users; 2 scénarios.</li>", url, nbrUsers));
        htmlContent.append(String.format("</ul>"));


        htmlContent.append("<h3>Conclusion :</h3>");

        htmlContent.append(String.format("<ul>"));
        System.out.println(resultString);
        if (resultString.contains("meilleur"))
            htmlContent.append("<li><h5>WS : ").append(String.format("Les performances par rapport à la version %s sont <span style=\"color: green;\">%s</span> .</h5></li>", oldVersion, resultString));
        else if (resultString.contains("degrad"))
            htmlContent.append("<li><h5>WS : ").append(String.format("Les performances par rapport à la version %s sont <span style=\"color: red;\">%s</span> .</h5></li>", oldVersion, resultString));

        htmlContent.append(String.format("</ul>"));


        htmlContent.append(String.format("<h3>[POLO Web] (%s) (%s >> %s):</h3>", formatterDate.format(startDate), formatterHour.format(startDate), formatterHour.format(endDate)));

        // Image reference after introductory text
        htmlContent.append("<img src='cid:image' /><br>");

        // New data table
        htmlContent.append(String.format("<h4>Les données du tir (%s):</h4>", formatterDate.format(startDate)));
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Request Name</th><th>Count</th><th>Average (ms)</th><th>Min (ms)</th>")
                .append("<th>Max (ms)</th><th>Stdev (ms)</th><th>Error (%)</th><th>Throughput (rq/s)</th></tr>");
        appendTableData(htmlContent, newTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing

        // Text between new and old tables
        htmlContent.append(String.format("<h4>Les données du tir (%s) : [%s]</h4>", oldStartDate, oldVersion));

        // Old data table
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Request Name</th><th>Count</th><th>Average (ms)</th><th>Min (ms)</th>")
                .append("<th>Max (ms)</th><th>Stdev (ms)</th><th>Error (%)</th><th>Throughput (rq/s)</th></tr>");
        appendTableData(htmlContent, oldTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing

        // Comparaison table title and data
        htmlContent.append("<h4>Comparaison:</h4>");
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Request Name</th><th>Old Average (ms)</th><th>New Average (ms)</th><th>Difference</th></tr>");
        appendTableData(htmlContent, compareTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing

        // Conclusion and closing
        htmlContent.append("<h3>Conclusion:</h3>");
        if (resultString.contains("meilleur"))
            htmlContent.append("<p>").append(String.format("Les performances par rapport à la version %s sont <span style=\"color: green;\">%s</span> .</p>", oldVersion, resultString));
        else if (resultString.contains("degrad"))
            htmlContent.append("<p>").append(String.format("Les performances par rapport à la version %s sont <span style=\"color: red;\">%s</span> .</p>", oldVersion, resultString));

        htmlContent.append("<p>Cdt,<br>Automation Team.</p>");

        // HTML part with content, image reference, and tables
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlContent.toString(), "text/html; charset=utf-8");

        // Image part
        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.attachFile(graphWS);
        imagePart.setContentID("<image>");
        imagePart.setDisposition(MimeBodyPart.INLINE);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(imagePart);

        msg.setContent(multipart);

        Transport.send(msg);
    }

    // Helper method to read and append table data from file
    private static void appendTableData(StringBuilder htmlContent, String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                htmlContent.append("<tr>");
                for (String part : parts) {
                    if (part != null && !part.isBlank() && !part.isEmpty() && part.charAt(0) == '+')
                        htmlContent.append("<td style=\"color: green;\" >").append(part).append("</td>");
                    else if (part != null && !part.isBlank() && !part.isEmpty() && part.charAt(0) == '-')
                        htmlContent.append("<td style=\"color: red;\" >").append(part).append("</td>");
                    else
                        htmlContent.append("<td>").append(part).append("</td>");
                }
                htmlContent.append("</tr>");
            }
        }
    }



}
