package io.github.edmm.plugins.puppet.util;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.Report;
import io.github.edmm.plugins.puppet.model.ResourceEventEntry;

import org.apache.commons.lang3.StringUtils;

public class PuppetNodeHandler {

    public static List<Report> identifyRelevantReports(Master master, String certName) {
        List<Report> allReports = new Gson().fromJson(master.executeCommandAndHandleResult(Commands.GET_ALL_REPORTS),
            new TypeToken<List<Report>>() {
            }.getType());
        return allReports.stream()
            .filter(report -> report.getStatus() == Report.State.changed)
            .filter(report -> report.getResource_events().getData() != null && report.getCertname().equals(certName))
            .collect(Collectors.toList());
    }

    public static String extractComponentNameFromResourceEntry(ResourceEventEntry event) {
        // this may be extended by using a provider pattern...
        if (StringUtils.isNotBlank(event.getResource_type())) {
            switch (event.getResource_type()) {
                case "Service":
                    return event.getResource_title();
                case "Mysql_database":
                    return event.getResource_type();
            }
        }
        return null;
    }
}
