package com.scaleset.cfbuilder.beanstalk;

/**
 * Constructs a {@code OptionSetting} that specifies a option for an ElasticBeanstalk {@link Environment} or an
 * ElasticBeanstalk {@link ConfigurationTemplate}.
 * <br>
 * The {@link Environment} or {@link ConfigurationTemplate} contain a list of {@code OptionSetting} property types.
 *
 * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/aws-properties-beanstalk-option-settings.html">Documentation
 * Reference</a>
 * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/aws-properties-elasticbeanstalk-configurationtemplate-configurationoptionsetting.html">Documentation
 * Reference</a>
 */
public class OptionSetting {
    private String namespace;
    private String optionName;
    private String resourceName;
    private Object value;

    public OptionSetting(String namespace, String optionName) {
        this.namespace = namespace;
        this.optionName = optionName;
    }

    public String getNamespace() {
        return namespace;
    }

    public OptionSetting setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getOptionName() {
        return optionName;
    }

    public OptionSetting setOptionName(String optionName) {
        this.optionName = optionName;
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public OptionSetting setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public OptionSetting setValue(Object value) {
        this.value = value;
        return this;
    }
}
