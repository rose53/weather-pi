package de.rose53.weatherpi.statistics.control;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.weatherpi.statistics.boundary.DayStatisticsService;
import de.rose53.weatherpi.statistics.entity.DayStatisticBean;

@Singleton
@Startup
public class DayStatisticsCalculatorService {

    @Inject
    Logger logger;

    @Inject
    DayStatisticsService dayStatisticsService;

    @Inject
    Event<DayStatisticEvent> dayStatisticEvent;

    @Inject
    ClimatologicClassificationDayCalculator climatologicClassificationDayCalculator;

    @PostConstruct
    public void calculateMissing() {
        if (dayStatisticsService.count() == 0) {
            logger.debug("calculateMissing: the table is empty, we have to recalculate");
            LocalDate today = LocalDate.now();
            LocalDate calcDate;
            for (int year = 2015; year <= today.getYear(); year++) {
                for (int month = Month.JANUARY.getValue(); month <= Month.DECEMBER.getValue(); month++) {
                    for (int day = 1; day <= Month.of(month).maxLength(); day++) {
                        try {
                            calcDate = LocalDate.of(year, month, day);
                            if (calcDate.isBefore(today)) {
                                logger.debug("calculateMissing: calculating for {}",calcDate);
                                dayStatisticsService.create(calcDate);
                            }
                        } catch (DateTimeException e) {}
                    }
                }
            }
        }
    }

    @Schedule(second="0", minute="0",hour="1", persistent=false)
    public void statisticsCalculate(){

        LocalDate yesterday = LocalDate.now().minusDays(1);
        logger.debug("statisticsCalculate: calculating statistics for {}",yesterday);

        DayStatisticBean dayStatisticBean = dayStatisticsService.create(yesterday);
        if (dayStatisticBean != null) {
            dayStatisticEvent.fire(new DayStatisticEvent(yesterday,dayStatisticBean.gettMin(),
                                                         dayStatisticBean.gettMax(),dayStatisticBean.gettMed(),
                                                         climatologicClassificationDayCalculator.calculateClimatologicClassificationDay(dayStatisticBean)));
        }
    }
}
