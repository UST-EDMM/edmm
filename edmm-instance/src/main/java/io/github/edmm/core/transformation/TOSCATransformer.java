package io.github.edmm.core.transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import io.github.edmm.exporter.WineryConnector;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.NodeTemplateInstance;
import io.github.edmm.model.opentosca.RelationshipTemplateInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import lombok.Getter;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.opentosca.OpenTOSCANamespaces.OPENTOSCA_BASE;
import static io.github.edmm.model.opentosca.OpenTOSCANamespaces.OPENTOSCA_NORMATIVE_NODE_TYPES_NAMESPACE;

public class TOSCATransformer {

    protected WineryConnector wineryConnector;
    @Getter
    protected final List<TypeTransformer> transformTypePlugins = new ArrayList<>();
    protected DeploymentInstance deploymentInstance;

    private final Logger logger = LoggerFactory.getLogger(TOSCATransformer.class);

    private final List<NodeTemplateInstance> nodeTemplateInstances = new ArrayList<>();
    private final List<RelationshipTemplateInstance> relationshipTemplateInstances = new ArrayList<>();

    public TOSCATransformer() {
        this.wineryConnector = WineryConnector.getInstance();
    }

    public TOSCATransformer(List<TypeTransformer> transformTypePlugins) {
        this();
        this.transformTypePlugins.addAll(transformTypePlugins);
    }

    public ServiceTemplateInstance transformEDiMMToServiceTemplateInstance(DeploymentInstance deploymentInstance) {
        this.deploymentInstance = deploymentInstance;
        ServiceTemplateInstance serviceTemplateInstance = ServiceTemplateInstance.ofDeploymentInstance(this.deploymentInstance);
        createNodeTemplateInstances();
        createRelationshipTemplateInstances();
        serviceTemplateInstance.setNodeTemplateInstances(this.nodeTemplateInstances);
        serviceTemplateInstance.setRelationshipTemplateInstances(this.relationshipTemplateInstances);
        return serviceTemplateInstance;
    }

    private void createNodeTemplateInstances() {
        if (!this.isComponentInstancesExisting()) {
            return;
        }
        this.deploymentInstance.getComponentInstances().forEach(componentInstance -> {
            NodeTemplateInstance nodeTemplateInstance = NodeTemplateInstance.ofComponentInstance(this.deploymentInstance.getId(), this.deploymentInstance.getName(), componentInstance);
            this.nodeTemplateInstances.add(nodeTemplateInstance);
        });
    }

    private boolean isComponentInstancesExisting() {
        return this.deploymentInstance.getComponentInstances() != null && !this.deploymentInstance.getComponentInstances().isEmpty();
    }

    private void createRelationshipTemplateInstances() {
        if (isComponentInstancesExisting()) {
            this.deploymentInstance.getComponentInstances().forEach(componentInstance -> {
                if (hasRelationshipInstances(componentInstance)) {
                    componentInstance.getRelationInstances().forEach(relationInstance -> {
                        RelationshipTemplateInstance relationshipTemplateInstance = RelationshipTemplateInstance.ofRelationInstance(this.deploymentInstance.getId(), relationInstance, componentInstance);
                        this.relationshipTemplateInstances.add(relationshipTemplateInstance);
                    });
                }
            });
        }
    }

    private boolean hasRelationshipInstances(ComponentInstance componentInstance) {
        return componentInstance.getRelationInstances() != null && !componentInstance.getRelationInstances().isEmpty();
    }

    public TNodeType getSoftwareNodeType(String name, String version) {
        return wineryConnector.getNodeType(identifySoftwareNodeTypeByNameAndVersion(name, version));
    }

    public TRelationshipType getRelationshipType(QName qName) {
        return wineryConnector.getRelationshipType(qName);
    }

    public TNodeType getComputeNodeType(String name, String version) {
        return wineryConnector.getNodeType(identifyComputeNodeType(name, version));
    }

    private QName identifyComputeNodeType(String name, String version) {
        String normalizedName = normalizeName(name);
        QName qName;

        if (normalizedName.contains("OpenStack".toLowerCase())) {
            normalizedName = normalizedName.replace("compute", "");
        }

        qName = identifyType(normalizedName, version);

        if (qName == null) {
            qName = QName.valueOf("{" + OPENTOSCA_NORMATIVE_NODE_TYPES_NAMESPACE + "}Compute");
            logger.info("Fallback to the generic Compute NodeType {}", qName);
        }

        return qName;
    }

    protected QName identifySoftwareNodeTypeByNameAndVersion(String name, String version) {
        QName softwareComponent = identifyType(name, version);

        if (softwareComponent == null) {
            softwareComponent = QName.valueOf("{" + OPENTOSCA_NORMATIVE_NODE_TYPES_NAMESPACE + "}SoftwareComponent");
            logger.info("Fallback to the generic SoftwareComponent NodeType {}", softwareComponent);
        }

        return softwareComponent;
    }

    protected QName identifyType(String name, String version) {
        String normalizeName = normalizeName(name);

        Optional<TypeTransformer> transformer = transformTypePlugins.stream()
            .filter(transformType -> transformType.canHandle(name, version))
            .findFirst();

        if (transformer.isPresent()) {
            return transformer.get().performTransformation(name, version);
        }

        List<QName> candidates = this.wineryConnector.getBaseNodeTypesQNames().stream()
            .filter(qName ->
                normalizeName(VersionUtils.getNameWithoutVersion(qName.getLocalPart())).startsWith(normalizeName)
            )
            .peek(nodeType -> logger.info("Found matching NodeType {} for component {}", nodeType, name))
            .collect(Collectors.toList());

        if (candidates.size() > 1) {
            List<QName> filteredCandidates = candidates.stream()
                .filter(qName -> VersionUtils.getVersion(qName.getLocalPart()).getComponentVersion().toLowerCase()
                    .startsWith(version.toLowerCase()))
                .peek(qName -> logger.info("Found matching NodeType after filtering for version {} for component {}", qName, name))
                .collect(Collectors.toList());

            if (filteredCandidates.size() > 0) {
                candidates = filteredCandidates;
            }
        }

        if (candidates.size() > 1) {
            List<QName> filteredCandidates = candidates.stream()
                .filter(qName -> qName.getNamespaceURI().startsWith(OPENTOSCA_BASE))
                .peek(qName -> logger.info("Found matching NodeType after filtering for OT namespace {} for component {}", qName, name))
                .collect(Collectors.toList());

            if (filteredCandidates.size() > 0) {
                candidates = filteredCandidates;
            }
        }

        if (candidates.size() > 0) {
            logger.info("Identified NodeType {}", candidates.get(0));
            return candidates.get(0);
        }

        return performTechnologySpecificMapping(name, version);
    }

    protected QName performTechnologySpecificMapping(String name, String version) {
        return null;
    }

    protected String normalizeName(String name) {
        return name.toLowerCase()
            .replaceAll("\\s|-|_|\\.", "");
    }

    public void save(TServiceTemplate serviceTemplate) {
        try {
            logger.info("Saving Service Template [{}]{}", serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
            wineryConnector.save(serviceTemplate);
        } catch (IOException e) {
            logger.error("Error while persisting Service Template", e);
        }
    }
}
