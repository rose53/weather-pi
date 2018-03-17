package de.rose53.pi.weatherpi.common;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@RunWith(CdiRunner.class)
@AdditionalClasses(LoggerExposer.class)
public class MovingAverageTest {


    @Inject
    MovingAverage movingAverage;

    @Test
    public void test() throws IOException, URISyntaxException {

        Reader reader = Files.newBufferedReader(Paths.get(MovingAverageTest.class.getResource("/mean_test.csv").toURI()));

        CsvToBean<ValueBean> csvToBean = new CsvToBeanBuilder<ValueBean>(reader)
                                            .withIgnoreLeadingWhiteSpace(true)
                                            .withSeparator(',')
                                            .withType(ValueBean.class)
                                            .build();

        assertNotNull(csvToBean);

        List<ValueBean> parse = csvToBean.parse();
        assertNotNull(parse);

        List<? extends SensorData> ma = movingAverage.calculate(parse,5);
        assertNotNull(ma);
    }
}
