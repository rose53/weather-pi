package de.rose53.weatherpi.statistics.control;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.weatherpi.statistics.entity.DayStatisticBean;

/**
 *
 * http://www.dwd.de/DE/service/lexikon/Functions/glossar.html?nn=103346&lv2=101334&lv3=101452
 *
 */
@ApplicationScoped
public class ClimatologicClassificationDayCalculator {

    @Inject
    Logger logger;

    public List<EClimatologicClassificationDay> calculateClimatologicClassificationDay(DayStatisticBean bean) {

        if (bean == null) {
            logger.debug("calculateClimatologicClassificationDay: no bean given, returning empty list");
            return Collections.emptyList();
        }
        if (bean.gettMin() == null || bean.gettMax() == null || bean.gettMed() == null) {
            logger.debug("calculateClimatologicClassificationDay: at least one temperature is missing, can not calculate, returning empty list");
            return Collections.emptyList();
        }
        return calculateClimatologicClassificationDay(bean.gettMin(), bean.gettMax(), bean.gettMed());
    }

    public List<EClimatologicClassificationDay> calculateClimatologicClassificationDay(double tMin, double tMax, double tMed) {
        logger.debug("calculateClimatologicClassificationDay: calculating for tMin = {}, tMax = {}, tMed = {}",tMin,tMax,tMed);
        return EClimatologicClassificationDay.calculateClimatologicClassificationDay(tMin, tMax, tMed);
    }
}
