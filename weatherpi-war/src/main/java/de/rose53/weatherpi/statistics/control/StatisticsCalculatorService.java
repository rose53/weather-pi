package de.rose53.weatherpi.statistics.control;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;

import de.rose53.weatherpi.configuration.StringConfiguration;
import de.rose53.weatherpi.statistics.boundary.DayStatisticsService;
import de.rose53.weatherpi.statistics.entity.DayStatisticBean;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

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

    Configuration cfg;

    @PostConstruct
    public void init() {
        cfg = new Configuration(Configuration.VERSION_2_3_24);
        ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "/templates/");
        cfg.setTemplateLoader(ctl);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
    }

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
            LocalDate today = LocalDate.now();
            for (int year = 2015; year <= today.getYear(); year++) {
                String baseFileName = "data_" + year;
                List<DayStatisticBean> statistics = dayStatisticsService.getRangeStatistics(LocalDate.of(year, Month.JANUARY.getValue(), 1), LocalDate.of(year, Month.DECEMBER.getValue(), 31));

                List<MonthClimatologicClassification> months = asList(Month.values()).stream()
                                                                                     .map(m -> MonthClimatologicClassification.build(m, statistics))
                                                                                     .collect(toList());

                Map<String, Object> root = new HashMap<>();
                root.put("months", months);

                Template template = cfg.getTemplate(baseFileName + ".ftlh");

                StringWriter out = new StringWriter();
                template.process(root, out);

                logger.debug("statisticsCalculate: ftp server answered with: {}",ftpClient.getReplyString());
                if (!ftpClient.storeFile(baseFileName + ".html", new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8)))) {
                    logger.debug("statisticsCalculate: change directory failed.");
                    return;
                }
                logger.debug("statisticsCalculate: ftp server answered with: {}",ftpClient.getReplyString());
            }
            ftpClient.logout();
            logger.debug("statisticsCalculate: ftp server answered with: {}",ftpClient.getReplyString());
        } catch (IOException | TemplateException e) {
            logger.error("statisticsCalculate: error creating month statistics",e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("statisticsCalculate: error while closing ftp client",e);
            }
        }
    }
}
