package io.github.edmm.core.parser.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.RelationInstance;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class ConfigurationModelRepresenter extends Representer {

    public ConfigurationModelRepresenter() {
        super();
    }

    public static ConfigurationModelRepresenter getRepresenter() {
        ConfigurationModelRepresenter representer = new ConfigurationModelRepresenter();

        // EDiMM custom class representation
        representer.addClassTag(InstanceProperty.class, Tag.MAP);
        representer.addClassTag(ComponentInstance.class, Tag.MAP);
        representer.addClassTag(RelationInstance.class, Tag.MAP);
        representer.addClassTag(Metadata.class, Tag.MAP);

        return representer;
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        if (propertyValue == null) {
            return null;
        } else {
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }
    }

    protected Set<Property> getProperties(Class<? extends Object> type) {
        Set<Property> properties = getPropertyUtils().getProperties(type);

        List<Property> propertyList = new ArrayList<>(properties);
        Collections.sort(propertyList, new BeanPropertyComparator());

        return new LinkedHashSet<>(propertyList);
    }

    class BeanPropertyComparator implements Comparator<Property> {
        public int compare(Property p1, Property p2) {
            if (YamlSupport.isPrioritized(p1, p2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
