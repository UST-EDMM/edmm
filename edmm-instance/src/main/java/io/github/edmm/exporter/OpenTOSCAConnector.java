package io.github.edmm.exporter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import io.github.edmm.exporter.dto.EnrichmentDTO;
import io.github.edmm.exporter.dto.InstanceDTO;
import io.github.edmm.exporter.dto.ServiceTemplateCreationDTO;
import io.github.edmm.exporter.dto.TagDTO;
import io.github.edmm.exporter.dto.TopologyTemplateDTO;
import io.github.edmm.exporter.dto.TypesDTO;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.plugins.puppet.util.GsonHelper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.winery.model.tosca.TNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OpenTOSCAConnector {

    private final static Logger LOGGER = LoggerFactory.getLogger(OpenTOSCAConnector.class.getName());

    // TODO: retrieve such info from\overline{} a config or sth like that
    private static final String wineryEndpoint = "http://localhost:8080/winery/";
    private static final String containerEndpoint = "http://192.168.2.108:1337/csars/";
    private static final String serviceTemplatesPath = "servicetemplates";
    private static final String topologyTemplatePath = "topologytemplate";
    private static final String availableFeaturesPath = "availablefeatures";
    private static final String csarPath = "csar";
    private static final String csarDownloadPath = "?" + csarPath;

    public static void processServiceTemplateInstanceToOpenTOSCA(String deploymentTechnology, ServiceTemplateInstance serviceTemplateInstance, String outputPath) {
        createServiceTemplateInWinery(serviceTemplateInstance.getServiceTemplateId());
        setServiceTemplateTag(deploymentTechnology, serviceTemplateInstance.getServiceTemplateId());
        createTopologyTemplateInWinery(serviceTemplateInstance);
        applyFeatures(getAvailableFeatures(serviceTemplateInstance.getServiceTemplateId()), serviceTemplateInstance.getServiceTemplateId());
        exportCSAR(serviceTemplateInstance.getServiceTemplateId(), outputPath);
        importCSARToContainerAndStartInstance(serviceTemplateInstance.getCsarId(), outputPath, serviceTemplateInstance.getServiceTemplateId());
    }

    public static List<TypesDTO> getAllNodeTypes() {
        return performGetList(wineryEndpoint + "nodetypes", TypesDTO.class);
    }

    public static TNodeType getNodeType(QName qName) {
        try {
            return performGet(wineryEndpoint + "nodeyptes" + doubleEncodeNamespace(qName), TNodeType.class);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error while encoding namespace.", e);
        }
        return null;
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
        try {
            performPostRequest(wineryEndpoint + serviceTemplatesPath, creationDTO);
        } catch (IOException e) {
            LOGGER.error("Failed to create Service Template Instance in Winery.", e);
        }
    }

    private static void putTopology(TopologyTemplateDTO topologyTemplateDTO, QName serviceTemplateId) {
        try {
            performPutRequest(
                wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + topologyTemplatePath,
                topologyTemplateDTO
            );
        } catch (IOException e) {
            LOGGER.debug("Failed to post Topology Template Instance to Winery.", e);
        }
    }

    private static void setServiceTemplateTag(String deploymentTechnology, QName serviceTemplateId) {
        try {
            performPostRequest(
                wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + "tags",
                new TagDTO("deploymentTechnology", deploymentTechnology)
            );
        } catch (IOException e) {
            LOGGER.error("Failed to set Tag for Service Template in Winery.", e);
        }
    }

    private static List<EnrichmentDTO> getAvailableFeatures(QName serviceTemplateId) {
        try {
            return performGetList(
                wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + topologyTemplatePath + "/" + availableFeaturesPath,
                EnrichmentDTO.class
            );
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error while encoding Namespace...", e);
        }

        return null;
    }

    private static void applyFeatures(List<EnrichmentDTO> selectedFeatures, QName serviceTemplateId) {
        if (selectedFeatures == null) {
            return;
        }
        try {
            performPutRequest(
                wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + topologyTemplatePath + "/" + availableFeaturesPath,
                selectedFeatures
            );
        } catch (IOException e) {
            LOGGER.error("Failed to apply available features to Winery.", e);
        }
    }

    private static void exportCSAR(QName serviceTemplateId, String outputPath) {
        try {
            FileUtils.copyURLToFile(new URL(wineryEndpoint + serviceTemplatesPath + doubleEncodeNamespace(serviceTemplateId) + csarDownloadPath), new File(outputPath));
        } catch (IOException e) {
            LOGGER.error("Failed to export CSAR from Winery.", e);
        }
    }

    private static void importCSARToContainerAndStartInstance(String csarId, String outputPath, QName serviceTemplateId) {
        if (importCSARToContainer(outputPath)) {
            createCSARInstance(csarId, serviceTemplateId);
        }
    }

    private static boolean importCSARToContainer(String outputPath) {
        try {
            FileBody fileBody = new FileBody(new File(outputPath), ContentType.MULTIPART_FORM_DATA);
            StringBody stringBody = new StringBody("false", ContentType.MULTIPART_FORM_DATA);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", fileBody);
            builder.addPart("enrichment", stringBody);

            HttpPost post = new HttpPost(containerEndpoint);
            post.setEntity(builder.build());

            return executeHttpRequest(post);
        } catch (IOException e) {
            LOGGER.error("Failed to import CSAR into Container.", e);
        }
        return false;
    }

    private static void createCSARInstance(String csarId, QName serviceTemplateId) {
        try {
            performPostRequest(
                containerEndpoint + csarId.toLowerCase() + "." + csarPath + "/"
                    + serviceTemplatesPath
                    + "/" + serviceTemplateId.getLocalPart() + "/"
                    + "buildplans/"
                    + serviceTemplateId.getLocalPart().replace(".", "_")
                    + "_buildPlan/instances",
                generateInstancePayload()
            );
        } catch (IOException e) {
            LOGGER.error("Failed to start CSAR instance in Container.", e);
        }
    }

    private static boolean performPostRequest(String url, Object payload) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(getObjectAsJson(payload));
        post.setHeader("content-type", "application/json");
        return executeHttpRequest(post);
    }

    private static boolean performPutRequest(String url, Object payload) throws IOException {
        HttpPut put = new HttpPut(url);
        put.setEntity(getObjectAsJson(payload));
        put.setHeader("content-type", "application/json");
        return executeHttpRequest(put);
    }

    private static boolean executeHttpRequest(HttpUriRequest request) throws IOException {
        LOGGER.info("Executing {} request {}", request.getMethod(), request.getURI());

        HttpResponse response = HttpClientBuilder.create()
            .build()
            .execute(request);

        LOGGER.info("Request returned Status {}({}) for {} request {}",
            response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode(),
            request.getMethod(), request.getURI());

        return Response.Status.Family.familyOf(response.getStatusLine().getStatusCode())
            == Response.Status.Family.SUCCESSFUL;
    }

    private static StringEntity getObjectAsJson(Object entity) throws UnsupportedEncodingException {
        Gson gson = new Gson();
        return new StringEntity(gson.toJson(entity));
    }

    private static StringEntity generateInstancePayload() throws UnsupportedEncodingException {
        InstanceDTO instanceDataAPIUrl = new InstanceDTO("instanceDataAPIUrl", "String", "YES");
        InstanceDTO correlationID = new InstanceDTO("CorrelationID", "String", "YES");
        List<InstanceDTO> instanceDTOs = new ArrayList<>();
        instanceDTOs.add(instanceDataAPIUrl);
        instanceDTOs.add(correlationID);

        return getObjectAsJson(instanceDTOs);
    }

    private static <T> List<T> performGetList(String url, Class<T> clazz) {
        return GsonHelper.parseJsonStringToParameterizedList(performGetList(url), clazz);
    }

    private static <T> T performGet(String url, Class<T> clazz) {
        return GsonHelper.parseJsonStringToObjectType(performGetList(url), clazz);
    }

    private static String performGetList(String url) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url);
            get.setHeader("accept", "application/json");
            return EntityUtils.toString(httpClient.execute(get).getEntity());
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to get available features from Winery.", e);
        }
        return null;
    }

    private static List<EnrichmentDTO> getJsonStringAsEnrichmentDTOs(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, List.class);
    }

    private static String doubleEncodeNamespace(QName qName) throws UnsupportedEncodingException {
        return "/" + URLEncoder.encode(URLEncoder.encode(qName.getNamespaceURI(), "UTF-8"), "UTF-8") + "/" + qName.getLocalPart() + "/";
    }
}
