package com.scaleset.cfbuilder.beanstalk;

/**
 * Constructs a {@code SourceConfiguration} that can be put into an ElasticBeanstalk {@link ConfigurationTemplate}.
 *
 * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/aws-properties-beanstalk-configurationtemplate-sourceconfiguration.html">Documentation
 * Reference</a>
 */
public class SourceConfiguration {
    private String applicationName;
    private String templateName;

    public SourceConfiguration(String applicationName, String templateName) {
        this.applicationName = applicationName;
        this.templateName = templateName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public SourceConfiguration setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public String getTemplateName() {
        return templateName;
    }

    public SourceConfiguration setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }
}
