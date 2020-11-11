package io.github.edmm.plugins.puppet;

import java.util.Arrays;

import javax.xml.namespace.QName;

import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.transformation.TypeTransformer;

public class PuppetToscaTransformer extends TOSCATransformer {

    public PuppetToscaTransformer(TypeTransformer... transformTypePlugins) {
        super(Arrays.asList(transformTypePlugins));
    }

    @Override
    protected QName performTechnologySpecificMapping(String name, String version) {
        int index = name.lastIndexOf("::");
        if (index > 0) {
            return identifyType(name.substring(0, index), version);
        }
        return null;
    }
}
