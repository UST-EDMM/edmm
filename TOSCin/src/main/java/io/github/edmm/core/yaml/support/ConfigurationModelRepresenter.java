package io.github.edmm.core.yaml.support;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

    private ConfigurationModelRepresenter() {
        super();
    }

    public static ConfigurationModelRepresenter getConfiguredRepresenter() {
        return getRepresenterWithClassTags();
    }

    private static ConfigurationModelRepresenter getRepresenterWithClassTags() {
        ConfigurationModelRepresenter representer = new ConfigurationModelRepresenter();
        // add custom classes to representer for suitable visual representation in YAML file
        representer.addClassTag(InstanceProperty.class, Tag.MAP);
        representer.addClassTag(ComponentInstance.class, Tag.MAP);
        representer.addClassTag(RelationInstance.class, Tag.MAP);
        representer.addClassTag(Metadata.class, Tag.MAP);

        return representer;
    }

    @Override
    protected NodeTuple representJavaBeanProperty(
        Object javaBean,
        Property property,
        Object propertyValue,
        Tag customTag) {
        if (propertyValue == null || isPropertyValueListEmpty(propertyValue) || isPropertyValueMapEmpty(propertyValue)) {
            return null;
        } else {
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }
    }

    private boolean isPropertyValueListEmpty(Object propertyValue) {
        if (isListPropertyValue(propertyValue)) {
            return ((List) propertyValue).isEmpty();
        }
        return false;
    }

    private boolean isPropertyValueMapEmpty(Object propertyValue) {
        if (isMapPropertyValue(propertyValue)) {
            return ((Map) propertyValue).isEmpty();
        }
        return false;
    }

    private boolean isListPropertyValue(Object propertyValue) {
        return propertyValue instanceof List;
    }

    private boolean isMapPropertyValue(Object propertyValue) {
        return propertyValue instanceof Map;
    }

    protected Set<Property> getProperties(Class<?> type) {
        Set<Property> properties = getPropertyUtils().getProperties(type);

        List<Property> propertyList = new ArrayList<>(properties);
        propertyList.sort(new BeanPropertyComparator());

        return new LinkedHashSet<>(propertyList);
    }

    class BeanPropertyComparator implements Comparator<Property> {
        public int compare(Property p1, Property p2) {
            if (YamlSupport.isFirstPropertyPrioritized(p1, p2)) {
                return -1;
            } else if (p1.equals(p2)) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
