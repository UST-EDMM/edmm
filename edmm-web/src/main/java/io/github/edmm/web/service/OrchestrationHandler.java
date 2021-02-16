package io.github.edmm.web.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationService;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.plugins.multi.MultiLifecycle;
import io.github.edmm.plugins.multi.model.ComponentProperties;
import io.github.edmm.plugins.multi.model.message.InitiateRequest;
import io.github.edmm.web.model.DeployRequest;
import io.github.edmm.web.model.DeployResult;
import io.github.edmm.web.model.TransformationRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OrchestrationHandler {

    private final TransformationService transformationService;

    @Value("${repository.path}")
    private String repositoryPath;

    public OrchestrationHandler(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @Async
    public void doTransform(TransformationRequest model) {

        TransformationContext context;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(model.getInput());
            String input = new String(decodedBytes);
            DeploymentModel deploymentModel = DeploymentModel.of(input);

            Path sourceDirectory = Paths.get(repositoryPath);
            Path newTargetDirectory = Paths.get(repositoryPath + "/multi-" + deploymentModel.getMultiId());

            context = transformationService.createContext(deploymentModel, model.getTarget(), sourceDirectory.toFile(),
                newTargetDirectory.toFile());
            context.setId(deploymentModel.getMultiId());

           MultiLifecycle multiLifecycle = new MultiLifecycle(context);
            multiLifecycle.transform();

        } catch (Exception e) {
            throw new IllegalStateException("Could not create transformation context", e);
        }

    }

    /**
     * Prepares the execution by retrieving the temporary saved transformation context
     * and passing the retrieved environment variables to the Multilifecycle
     * @param deployRequest Valid RequestBody
     */
    public DeployResult prepareExecution(DeployRequest deployRequest) {

        // Retrieves the saved transformation context
        MultiLifecycle multiLifecycle = new MultiLifecycle(deployRequest.getModelId());

        // If transformation context is available, then prepare execution in Multilifecycle
        if (multiLifecycle.isTransformationContextAvailable(deployRequest.getModelId())) {

            if (deployRequest.getCorrelationId() == null) {
                UUID uuid = UUID.randomUUID();
                deployRequest.setCorrelationId(uuid);
            }

            List<ComponentProperties> properties =
                multiLifecycle.assignRuntimeVariablesToLifecycles(deployRequest.getComponents(),
                    deployRequest.getInputs());

            return new DeployResult(deployRequest.getModelId(),
                deployRequest.getCorrelationId(),
                properties);
        }
        return null;
    }

    public String transferBPMNtoEngine(String url, String id) {

        final String bpmnFileName = "/bpmn/Workflow.bpmn";
        final String deploymentName = "multi-deployment";
        final String repositorySource = "repository";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        File file = new File(repositoryPath + "/multi-" + id + bpmnFileName);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.put("deployment-name", Collections.singletonList(deploymentName));
        map.put("deployment-source", Collections.singletonList(repositorySource));
        map.put("tenant-id", Collections.singletonList(id));
        map.put("bpmn-workflow.bpmn", Collections.singletonList(new FileSystemResource(file)));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        ResponseEntity<String> e = restTemplate.postForEntity(url + "/engine-rest/deployment/create",
            requestEntity, String.class);

        return e.getStatusCode().toString();
    }

    public String initiateDeployment(String url, String id) {

        HashMap<String, Object> variables = new HashMap<>();
        HashMap<String, String> value = new HashMap<>();

        value.put("value", "true");
        variables.put("initiator", value);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Sets InitiateRequest parameters
        InitiateRequest initiateRequest = new InitiateRequest();
        initiateRequest.setVariables(variables);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<InitiateRequest> entity = new HttpEntity<>(initiateRequest, headers);

        final String participantURI =
            url + "/engine-rest/process-definition/key/workflow/tenant-id/" + id + "/start";

        return restTemplate.exchange(participantURI, HttpMethod.POST, entity, String.class).getStatusCode().toString();
    }
}
