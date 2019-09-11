package com.scaleset.cfbuilder.beanstalk;

import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Template;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class BeanstalkTest {

    @Test
    public void beanstalkTest() {
        Template beanstalkTemplate = new Template();
        new BeanstalkModule().id("").template(beanstalkTemplate).build();
        String beanstalkTemplateString = beanstalkTemplate.toString(true);

        assertNotNull(beanstalkTemplateString);
        System.err.println(beanstalkTemplateString);
    }

    class BeanstalkModule extends Module {
        public void build() {
            this.template.setDescription("Beanstalk Application");
            Application sampleApplication = resource(Application.class, "SampleApplication")
                    .description("AWS ElasticBeanstalk Sample Application");
            SourceBundle exampleSourceBundle = new SourceBundle("sample-bucket.s3.amazonaws.com", "sampleapp.jar");
            ApplicationVersion sampleApplicationVersion = resource(ApplicationVersion.class, "sampleApplicationVersion")
                    .applicationName(sampleApplication)
                    .description("AWS ElasticBeanstalk Sample Application Version")
                    .sourceBundle(exampleSourceBundle);
            OptionSetting loadBalanceOption = new OptionSetting("aws:elasticbeanstalk:environment", "EnvironmentType")
                    .setValue("LoadBalanced");
            ConfigurationTemplate sampleConfigurationTemplate = resource(ConfigurationTemplate.class,
                    "sampleConfigurationTemplate")
                    .applicationName(sampleApplication)
                    .description("AWS ElasticBeanstalk Sample Configuration Template")
                    .solutionStackName("64bit Amazon Linux 2017.09 v2.6.6 running Java 8")
                    .optionSettings(loadBalanceOption);
            resource(Environment.class, "sampleEnvironment")
                    .applicationName(sampleApplication)
                    .description("AWS ElasticBeanstalk Sample Environment")
                    .templateName(sampleConfigurationTemplate)
                    .versionLabel(sampleApplicationVersion);
        }
    }
}
