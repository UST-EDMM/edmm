package io.github.edmm.plugins.puppet.model;

import java.util.Date;

import com.google.gson.Gson;

public class Report {
    private String host;
    private Date time;
    private String catalog_uuid;
    private String transactionUuid;
    // ... more properties to add

    public static Report ofString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Report.class);
    }
}
