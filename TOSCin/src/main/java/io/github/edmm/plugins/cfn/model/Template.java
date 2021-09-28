package io.github.edmm.plugins.cfn.model;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;

@Getter
public class Template {
    private String AWSTemplateFormatVersion;
    private LinkedTreeMap<String, Resource> Resources;

    public static Template fromTemplateBodyString(String templateBody) {
        Gson gson = new Gson();
        return gson.fromJson(templateBody, Template.class);
    }
}
