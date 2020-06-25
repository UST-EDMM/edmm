package io.github.edmm.exporter.dto;

import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import org.apache.http.entity.StringEntity;

public class ServiceTemplateCreationDTO {
    String namespace;
    String localname;

    public ServiceTemplateCreationDTO(String namespace, String localname) {
        this.namespace = namespace;
        this.localname = localname;
    }

    public StringEntity toJson() throws UnsupportedEncodingException {
        Gson gson = new Gson();
        return new StringEntity(gson.toJson(this));
    }
}
