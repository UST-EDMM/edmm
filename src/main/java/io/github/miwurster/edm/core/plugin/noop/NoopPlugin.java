package io.github.miwurster.edm.core.plugin.noop;

import io.github.miwurster.edm.core.plugin.AbstractLifecycle;
import io.github.miwurster.edm.core.plugin.Plugin;
import io.github.miwurster.edm.core.transformation.Platform;
import io.github.miwurster.edm.core.transformation.TransformationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoopPlugin extends Plugin<NoopPlugin.NoopLifecycle> {

    public NoopPlugin() {
        super(Platform.NOOP);
    }

    @Override
    public NoopLifecycle getLifecycle(TransformationContext context) {
        return new NoopLifecycle();
    }

    public static class NoopLifecycle extends AbstractLifecycle {

        private static final Logger logger = LoggerFactory.getLogger(NoopLifecycle.class);

        @Override
        public void prepare() {
            logger.info("NOOP");
        }

        @Override
        public void transform() {
            logger.info("NOOP");
        }

        @Override
        public void cleanup() {
            logger.info("NOOP");
        }
    }
}
