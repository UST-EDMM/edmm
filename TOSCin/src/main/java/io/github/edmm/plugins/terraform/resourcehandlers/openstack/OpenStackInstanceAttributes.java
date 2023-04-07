package io.github.edmm.plugins.terraform.resourcehandlers.openstack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OpenStackInstanceAttributes {

    public String access_ip_v4;
    public String access_ip_v6;
    public String key_pair;
    public String name;
    public String image_name;
    public String id;
    public String flavor_name;
}
