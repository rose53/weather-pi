package de.rose53.pi.weatherpi.common.junit;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class WeldJUnit4Runner extends BlockJUnit4ClassRunner {

    private final Class<?> clazz;
    private final Weld weld;
    private final WeldContainer container;

    public WeldJUnit4Runner(final Class<Object> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
        this.weld = new Weld();
        this.container = weld.initialize();
    }

    @Override
    protected Object createTest() throws Exception {
        return container.instance().select(clazz).get();
    }
}
