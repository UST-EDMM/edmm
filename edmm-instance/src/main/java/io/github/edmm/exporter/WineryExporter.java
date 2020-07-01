package io.github.edmm.exporter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.exporter.dto.EnrichmentDTO;
import io.github.edmm.exporter.dto.ServiceTemplateCreationDTO;
import io.github.edmm.exporter.dto.TopologyTemplateDTO;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class WineryExporter {
    private static String wineryEndpoint = "http://localhost:8080/winery/";
    private static String containerEndpoint = "http://localhost:1337/csars";
    private static String serviceTemplatesPath = "servicetemplates";
    private static String topologyTemplatePath = "topologytemplate";
    private static String availableFeaturesPath = "availablefeatures";
    private static String csarDownloadPath = "?csar";

    public static void exportServiceTemplateInstanceToWinery(ServiceTemplateInstance serviceTemplateInstance, String outputPath) {
        createServiceTemplateInWinery(serviceTemplateInstance.getServiceTemplateId());
        createTopologyTemplateInWinery(serviceTemplateInstance);
        // TODO
        applyFeatures(getAvailableFeatures(serviceTemplateInstance.getServiceTemplateId()), serviceTemplateInstance.getServiceTemplateId());
        exportCSAR(serviceTemplateInstance.getServiceTemplateId(), outputPath);
        importCSARToContainer(outputPath);
    }

    private static void createServiceTemplateInWinery(QName serviceTemplateId) {
        ServiceTemplateCreationDTO creationDTO = new ServiceTemplateCreationDTO(serviceTemplateId.getNamespaceURI(), serviceTemplateId.getLocalPart());
        postServiceTemplate(creationDTO);
    }

    private static void createTopologyTemplateInWinery(ServiceTemplateInstance serviceTemplateInstance) {
        TopologyTemplateDTO topologyTemplateDTO = new TopologyTemplateDTO(serviceTemplateInstance);
        putTopology(topologyTemplateDTO, serviceTemplateInstance.getServiceTemplateId());
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
        HttpPut put = new HttpPut(wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + topologyTemplatePath);
        try {
            put.setEntity(getObjectAsJson(topologyTemplateDTO));
            put.setHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(put);
        } catch (IOException e) {
            System.out.println("Failed to post Topology Template Instance to Winery. Continue with creation of EDIMM YAML file.");
        }
    }

    private static List<EnrichmentDTO> getAvailableFeatures(QName serviceTemplateId) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + topologyTemplatePath + "/" + availableFeaturesPath);
        try {
            get.setHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(get);

            List<EnrichmentDTO> enrichmentDTOs = getJsonStringAsEnrichmentDTOs(EntityUtils.toString(response.getEntity()));
            return enrichmentDTOs;
        } catch (IOException e) {
            System.out.println("Failed to get available features from Winery. Continue with creation of EDIMM YAML file.");
        }
        return null;
    }

    private static void applyFeatures(List<EnrichmentDTO> selectedFeatures, QName serviceTemplateId) {
        if (selectedFeatures == null) {
            return;
        }
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut put = new HttpPut(wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + topologyTemplatePath + "/" + availableFeaturesPath);
        try {
            put.setHeader("content-type", "application/json");
            put.setEntity(getObjectAsJson(selectedFeatures));
            HttpResponse response = httpClient.execute(put);
        } catch (IOException e) {
            System.out.println("Failed to apply available features to Winery. Continue with creation of EDIMM YAML file.");
        }
    }

    private static void exportCSAR(QName serviceTemplateId, String outputPath) {
        try {
            FileUtils.copyURLToFile(new URL(wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + csarDownloadPath), new File(outputPath));
        } catch (IOException e) {
            System.out.println("Failed to export CSAR from Winery. Continue with creation of EDIMM YAML file.");
        }
    }

    private static void importCSARToContainer(String outputPath) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(containerEndpoint);
        try {
            File file = new File(outputPath);
            FileBody fileBody = new FileBody(file, ContentType.MULTIPART_FORM_DATA);
            StringBody stringBody = new StringBody("true", ContentType.MULTIPART_FORM_DATA);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", fileBody);
            builder.addPart("enrichment", stringBody);
            HttpEntity entity = builder.build();
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);
        } catch (IOException e) {
            System.out.println("Failed to import CSAR into Container. Continue with creation of EDIMM YAML file.");
        }

    }

    private static StringEntity getObjectAsJson(Object entity) throws UnsupportedEncodingException {
        Gson gson = new Gson();
        return new StringEntity(gson.toJson(entity));
    }

    private static List<EnrichmentDTO> getJsonStringAsEnrichmentDTOs(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, List.class);
    }

    private static String doubleEncodeNamespace(QName serviceTemplateId) {
        return "/" + URLEncoder.encode(URLEncoder.encode(serviceTemplateId.getNamespaceURI())) + "/" + serviceTemplateId.getLocalPart() + "/";
    }
}
