package io.github.edmm.exporter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.namespace.QName;

import io.github.edmm.exporter.dto.ServiceTemplateCreationDTO;
import io.github.edmm.exporter.dto.TopologyTemplateDTO;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class WineryExporter {
    private static String wineryEndpoint = "http://localhost:8080/winery/";
    private static String serviceTemplatesPath = "servicetemplates";
    private static String topologyTemplatePath = "topologytemplate";

    public static void exportServiceTemplateInstanceToWinery(ServiceTemplateInstance serviceTemplateInstance) {
        createServiceTemplateInWinery(serviceTemplateInstance.getServiceTemplateId());
        createTopologyTemplateInWinery(serviceTemplateInstance);
    }

    private static void createServiceTemplateInWinery(QName serviceTemplateId) {
        ServiceTemplateCreationDTO creationDTO = new ServiceTemplateCreationDTO(serviceTemplateId.getNamespaceURI(), serviceTemplateId.getLocalPart());
        postServiceTemplate(creationDTO);
    }

    private static void createTopologyTemplateInWinery(ServiceTemplateInstance serviceTemplateInstance) {
        TopologyTemplateDTO topologyTemplateDTO = new TopologyTemplateDTO(serviceTemplateInstance);
        //  putTopology(topologyTemplateDTO, serviceTemplateInstance.getServiceTemplateId());
    }

    private static void postServiceTemplate(ServiceTemplateCreationDTO creationDTO) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(wineryEndpoint + serviceTemplatesPath);
        try {
            post.setEntity(getObjectAsJson(creationDTO));
            post.setHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(post);
        } catch (IOException e) {
            System.out.println("Failed to create Service Template Instance in Winery. Continue with creation of EDIMM YAML file.");
        }

    }

    private static void putTopology(TopologyTemplateDTO topologyTemplateDTO, QName serviceTemplateId) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut put = new HttpPut(wineryEndpoint + serviceTemplatesPath + "/" + URLEncoder.encode(URLEncoder.encode(serviceTemplateId.getNamespaceURI())) + "/" + serviceTemplateId.getLocalPart() + "/" + topologyTemplatePath);
        try {
            put.setEntity(getObjectAsJson(topologyTemplateDTO));
            put.setHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(put);
            System.out.println("OK");
        } catch (IOException e) {
            System.out.println("Failed to post Topology Template Instance to Winery. Continue with creation of EDIMM YAML file.");
        }

    }

    private static StringEntity getObjectAsJson(Object entity) throws UnsupportedEncodingException {
        Gson gson = new Gson();
        return new StringEntity(gson.toJson(entity));
    }
}
