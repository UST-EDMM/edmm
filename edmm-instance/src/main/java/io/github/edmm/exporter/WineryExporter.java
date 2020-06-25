package io.github.edmm.exporter;

import java.io.IOException;

import javax.xml.namespace.QName;

import io.github.edmm.exporter.dto.ServiceTemplateCreationDTO;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

public class WineryExporter {
    private static String wineryEndpoint = "http://localhost:8080/winery/";
    private static String serviceTemplatesPath = "servicetemplates";
    private static String topologyTemplate = "topologytemplate";

    public static void exportServiceTemplateInstanceToWinery(ServiceTemplateInstance serviceTemplateInstance) {
        createServiceTemplateInWinery(serviceTemplateInstance.getServiceTemplateId());
        // TODO
    }

    private static void createServiceTemplateInWinery(QName serviceTemplateId) {
        ServiceTemplateCreationDTO creationDTO = new ServiceTemplateCreationDTO(serviceTemplateId.getNamespaceURI(), serviceTemplateId.getLocalPart());
        postToServiceTemplate(creationDTO);
    }

    private static void postToServiceTemplate(ServiceTemplateCreationDTO creationDTO) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(wineryEndpoint + serviceTemplatesPath);
        try {
            post.setEntity(creationDTO.toJson());
            post.setHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(post);
        } catch (IOException e) {
            System.out.println("Failed to post Service Template Instance to Winery. Continue with creation of EDIMM YAML file.");
        }

    }
}
