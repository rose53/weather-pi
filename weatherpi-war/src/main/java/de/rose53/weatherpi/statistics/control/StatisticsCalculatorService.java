package de.rose53.weatherpi.statistics.control;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.commons.net.ftp.FTPClient;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import de.rose53.weatherpi.configuration.StringConfiguration;
import de.rose53.weatherpi.statistics.boundary.DayStatisticsService;
import de.rose53.weatherpi.statistics.entity.DayStatisticBean;

@Singleton
@Startup
public class StatisticsCalculatorService {

    @Inject
    Logger logger;

    @Inject
    @StringConfiguration(key="ftp.hostname")
    String hostname;

    @Inject
    @StringConfiguration(key="ftp.username")
    String username;

    @Inject
    @StringConfiguration(key="ftp.password")
    String password;

    @Inject
    @StringConfiguration(key="ftp.pathname")
    String pathname;

    @Inject
    DayStatisticsService dayStatisticsService;

    @Inject
    Event<DayStatisticEvent> dayStatisticEvent;

    @Schedule(second="0", minute="30",hour="1", persistent=false)
    public void statisticsCalculate(){

        // iterate over the years
        FTPClient       ftpClient = new FTPClient();
        try {
            ftpClient.connect(hostname);
            logger.debug("statisticsCalculate: ftp server answered with: {}",ftpClient.getReplyString());
            if (!ftpClient.login(username,password)) {
                logger.debug("statisticsCalculate: login failed.");
                return;
            }
            logger.debug("statisticsCalculate: ftp server answered with: {}",ftpClient.getReplyString());
            if (!ftpClient.changeWorkingDirectory(pathname)) {
                logger.debug("statisticsCalculate: change directory failed.");
                return;
            }
            logger.debug("statisticsCalculate: ftp server answered with: {}",ftpClient.getReplyString());
            LocalDate today = LocalDate.now();
            for (int year = 2015; year <= today.getYear(); year++) {
                String baseFileName = "data_" + year;

                String graph = graph(year);
                logger.debug("statisticsCalculate: graph = {}",graph);

                if (!ftpClient.storeFile(baseFileName + ".svg", new ByteArrayInputStream(graph.getBytes(StandardCharsets.UTF_8)))) {
                    logger.debug("statisticsCalculate: storeFile failed.");
                    return;
                }
                logger.debug("statisticsCalculate: ftp server answered with: {}",ftpClient.getReplyString());
            }
            ftpClient.logout();
            logger.debug("statisticsCalculate: ftp server answered with: {}",ftpClient.getReplyString());
        } catch (IOException e) {
            logger.error("statisticsCalculate: error creating month statistics",e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("statisticsCalculate: error while closing ftp client",e);
            }
        }
    }

    public String graph(int year) {

        List<DayStatisticBean> statistics = dayStatisticsService.getRangeStatistics(LocalDate.of(year, Month.JANUARY.getValue(), 1), LocalDate.of(2016, Month.DECEMBER.getValue(), 31));

        List<MonthClimatologicClassification> months = asList(Month.values()).stream()
                .map(m -> MonthClimatologicClassification.build(m, statistics))
                .collect(toList());



        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        months.forEach(m -> {
            dataset.addValue(m.getCountIcy(), "Eistag", m.getMonthName());
            dataset.addValue(m.getCountFrost(), "Frosttag", m.getMonthName());
            dataset.addValue(m.getCountVegetation(), "Vegetationstag", m.getMonthName());
            dataset.addValue(m.getCountHeating(), "Heiztag", m.getMonthName());
            dataset.addValue(m.getCountSummer(), "Sommertag", m.getMonthName());
            dataset.addValue(m.getCountTropical(), "Tropennacht", m.getMonthName());
            dataset.addValue(m.getCountHot(), "Heißer Tag", m.getMonthName());
            dataset.addValue(m.getCountDesert(), "Wüstentag", m.getMonthName());
        });

        JFreeChart chart = ChartFactory.createBarChart3D(null, "Monat", "Tage", dataset,PlotOrientation.VERTICAL, true, true, false);

        //final CategoryPlot plot = chart.getCategoryPlot();


        // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // draw the chart in the SVG generator
        chart.draw(svgGenerator, new Rectangle(0,0,1024,768));

        StringWriter out = new StringWriter();
        try {
            svgGenerator.stream(out, true /* use css */);
        } catch (SVGGraphics2DIOException e) {
            logger.error("graph:",e);
        }


        return out.toString();
    }


}
