package config.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HTMLManager {

    public StringBuilder htmlContent = null;

    // WS
    Boolean ws = true;
    String actualVersionWS = "2.10.0";
    String oldVersionWS = "2.9.0";
    String envWS = "A5";
    String urlWS = "https://example.com";
    String durationWS = "3600";
    String nbrUsersWS = "1";
    String resultStringWS = "meilleurs de l’ordre de 9,48 %";
    Date startDateWS = new Date(System.currentTimeMillis());
    Date oldStartDateWS = new Date(System.currentTimeMillis());
    Date endDateWS = new Date(System.currentTimeMillis());

    // Front
    Boolean front = false;
    String actualVersionWeb = "2.10.0";
    String oldVersionWeb = "2.9.0";
    String envWeb = "A5";
    String urlWeb = "https://example.com";
    String durationWeb = "3600";
    String nbrUsersWeb = "1";
    String resultStringWeb = "meilleurs de l’ordre de 9,48 %";
    Date startDateWeb = new Date(System.currentTimeMillis());
    Date endDateWeb = new Date(System.currentTimeMillis());

    // Date/Time Formatters
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy '-' HH:mm:ss");
    SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatterHour = new SimpleDateFormat("HH:mm:ss");


    // Constructor
    HTMLManager(StringBuilder htmlContent) {
        this.htmlContent = htmlContent;
        this.formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        this.formatterDate.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        this.formatterHour.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
    }

    public void addTitle() {
        htmlContent.append("<p>Hello,</p>");

        if (ws && front)
            htmlContent.append(String.format("<h2>Tir de performance de POLO WS %s sur %s au %s:</h2>", actualVersionWS, envWS, formatterDate.format(startDateWS)));
        else if (ws)
            htmlContent.append(String.format("<h2>Tir de performance de POLO WS %s sur %s au %s:</h2>", actualVersionWS, envWS, formatterDate.format(startDateWS)));
        else if (front)
            htmlContent.append(String.format("<h2>Tir de performance de POLO WS %s sur %s au %s:</h2>", actualVersionWS, envWS, formatterDate.format(startDateWS)));
    }

    public void addSimulationsList() {
        htmlContent.append(String.format("<p>Les tests de perf du %s sont finalisés sur %s.</p>", formatterDate.format(startDateWS), envWS));
        htmlContent.append(String.format("<p>Voici les résultats des tir de perf, %s seconds pointant sur les urls :</p>", durationWS));

        htmlContent.append(String.format("<ul>"));
        if (ws)
            htmlContent.append(String.format("<li>%s seconds POLO-WS, Url : ", durationWS))
                    .append(String.format("%s %s user.</li>", urlWS, nbrUsersWS));
        if (front)
            htmlContent.append(String.format("<li>%s seconds POLO-WEB, Url : ", durationWS))
                    .append(String.format("%s %s user.</li>", urlWS, nbrUsersWS));
    }

    public void addConclusionsList() {
        htmlContent.append("<h3>Conclusion :</h3>");

        htmlContent.append(String.format("<ul>"));
        if (ws)
            htmlContent.append("<li><h5>WS : ").append(String.format("Les performances par rapport à la version %s sont %s .</h5></li>", oldVersionWS, resultStringWS));
        if (front)
            htmlContent.append("<li><h5>Front : ").append(String.format("Les performances par rapport à la version %s sont %s .</h5></li>", oldVersionWS, resultStringWS));
        htmlContent.append(String.format("</ul>"));
    }

    public void addSimulationDataWS(String newTableWS, String oldTableWS, String compareTableWS) throws IOException {
        htmlContent.append(String.format("<h3>[POLO WS] (%s) (%s >> %s):</h3>", formatterDate.format(startDateWS), formatterHour.format(startDateWS), formatterHour.format(endDateWS)));

        // Image reference after introductory text
        htmlContent.append("<img src='cid:image' /><br>");

        // New data table
        htmlContent.append(String.format("<h4>Les données du tir (%s):</h4>", formatterDate.format(startDateWS)));
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Nom de Requête</th><th>Nbr de requêts lancées</th><th>Temps moyen de réponse (ms)</th><th>Min (ms)</th>")
                .append("<th>Max (ms)</th><th>Ecart type (ms)</th><th>Erreurs (%)</th><th>Throughput (rq/s)</th></tr>");
        appendTableData(htmlContent, newTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing

        // Text between new and old tables
        htmlContent.append(String.format("<h4>Les données du tir (%s) : [%s]</h4>", oldStartDateWS, oldVersionWS));

        // Old data table
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Nom de Requête</th><th>Nbr de requêts lancées</th><th>Temps moyen de réponse (ms)</th><th>Min (ms)</th>")
                .append("<th>Max (ms)</th><th>Ecart type (ms)</th><th>Erreurs (%)</th><th>Throughput (rq/s)</th></tr>");
        appendTableData(htmlContent, oldTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing

        // Comparaison table title and data
        htmlContent.append("<h4>Comparaison:</h4>");
        htmlContent.append("<table border='1' cellpadding='5' cellspacing='0'>")
                .append("<tr><th>Nom de Requête</th><th>Temps moyen de réponse (ms) %s</th><th>Temps moyen de réponse (ms) %s</th><th>Différence (%)</th></tr>");
        appendTableData(htmlContent, compareTableWS);
        htmlContent.append("</table><br>");  // Close table and add spacing
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
