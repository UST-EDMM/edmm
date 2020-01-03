package io.github.edmm.core.plugin.noop;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopPlugin extends Plugin<NoopPlugin.NoopLifecycle> {

    public NoopPlugin() {
        super(Platform.NOOP);
    }

    @Override
    public NoopLifecycle getLifecycle(TransformationContext context) {
        return new NoopLifecycle(context);
    }

    public static class NoopLifecycle extends AbstractLifecycle {

        private static final Logger logger = LoggerFactory.getLogger(NoopLifecycle.class);

        NoopLifecycle(TransformationContext context) {
            super(context);
        }

        @Override
        public void transform() {
            logger.info("NOOP");
        }
    }
}
