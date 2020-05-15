package io.github.edmm.plugins.kubernetes;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import io.github.edmm.core.execution.ExecutionContext;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import static io.github.edmm.plugins.kubernetes.KubernetesPlugin.STACKS_ENTRY;

public class KubernetesExecutor {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesExecutor.class);

    private final KubernetesClient client = new DefaultKubernetesClient().inNamespace("default");

    private final ExecutionContext context;
    private final List<String> stacks;

    public KubernetesExecutor(ExecutionContext context) {
        this.context = context;
        this.stacks = getStacks(context);
    }

    public void execute() {
        for (String stack : stacks) {
            buildDockerImages(stack, context);
            applyToKubernetes(stack, context);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getStacks(ExecutionContext context) {
        Map<String, Object> values = context.getTransformation().getValues();
        return (List<String>) values.get(STACKS_ENTRY);
    }

    private void buildDockerImages(String name, ExecutionContext context) {
        File directory = new File(context.getDirectory(), name);
        File dockerfile = new File(directory, "Dockerfile");
        if (!dockerfile.isFile() || !dockerfile.canRead()) {
            throw new IllegalArgumentException("Dockerfile not available for deployment");
        }
        try {
            ProcessResult pr = new ProcessExecutor()
                .directory(directory)
                .command("docker", "build", "-t", name + ":latest", ".")
                .redirectError(Slf4jStream.of(getClass()).asError())
                .redirectOutput(Slf4jStream.of(getClass()).asDebug())
                .readOutput(true)
                .execute();
            if (pr.getExitValue() > 0) {
                throw new IllegalStateException("Error while executing 'docker' executable");
            }
        } catch (Exception e) {
            logger.error("Error building Dockerfile: {}", e.getMessage(), e);
        }
    }

    private void applyToKubernetes(String name, ExecutionContext context) {
        File directory = new File(context.getDirectory(), name);
        if (!directory.isDirectory() || !directory.canRead()) {
            throw new IllegalArgumentException("Stack not available for deployment");
        }
        try {
            String filename = name.replace("_", "-");
            doApplyToKubernetes(new File(directory, filename + "-deployment.yaml"));
            doApplyToKubernetes(new File(directory, filename + "-service.yaml"));
            doApplyToKubernetes(new File(directory, filename + "-config.yaml"));
        } catch (Exception e) {
            logger.error("Error applying Kubernetes deployment: {}", e.getMessage(), e);
        }
    }

    private void doApplyToKubernetes(File file) throws Exception {
        List<HasMetadata> metadata = client.load(new FileInputStream(file)).createOrReplace();
        logger.info("Applied <{}> items", metadata.size());
        metadata.forEach(item -> logger.info(display(item)));
    }

    private static String display(HasMetadata item) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        if (Utils.isNotNullOrEmpty(item.getKind())) {
            sb.append("Kind:").append(item.getKind());
        }
        if (Utils.isNotNullOrEmpty(item.getMetadata().getName())) {
            sb.append("Name:").append(item.getMetadata().getName());
        }
        if (item.getMetadata().getLabels() != null && !item.getMetadata().getLabels().isEmpty()) {
            sb.append("Labels: [ ");
            for (Map.Entry<String, String> entry : item.getMetadata().getLabels().entrySet()) {
                sb.append(entry.getKey()).append(":").append(entry.getValue()).append(" ");
            }
            sb.append("]");
        }
        sb.append(" ]");
        return sb.toString();
    }
}
