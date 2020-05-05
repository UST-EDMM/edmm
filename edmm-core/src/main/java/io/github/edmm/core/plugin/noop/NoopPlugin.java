package io.github.edmm.core.plugin.noop;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopPlugin extends TransformationPlugin<NoopPlugin.NoopLifecycle> {

    public NoopPlugin() {
        super(TargetTechnology.NOOP);
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
