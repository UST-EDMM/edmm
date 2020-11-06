package io.github.edmm.core.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import io.github.edmm.exporter.WineryConnector;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.NodeTemplateInstance;
import io.github.edmm.model.opentosca.RelationshipTemplateInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.tosca.TNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.opentosca.OpenTOSCANamespaces.OPENTOSCA_BASE;
import static io.github.edmm.model.opentosca.OpenTOSCANamespaces.OPENTOSCA_NORMATIVE_NODE_TYPES_NAMESPACE;

public class TOSCATransformer {

    private final Logger logger = LoggerFactory.getLogger(TOSCATransformer.class);

    private final List<NodeTemplateInstance> nodeTemplateInstances = new ArrayList<>();
    private final List<RelationshipTemplateInstance> relationshipTemplateInstances = new ArrayList<>();
    private final WineryConnector wineryConnector;
    private DeploymentInstance deploymentInstance;

    public TOSCATransformer() {
        this.wineryConnector = new WineryConnector();
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

    public TNodeType getNodeType(String name, String version) {
        return wineryConnector.getNodeType(identifyNodeTypeByNameAndVersion(name, version));
    }

    private QName identifyNodeTypeByNameAndVersion(String name, String version) {
        String normalizeName = normalizeName(name);
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
                .peek(qName -> logger.info("Found matching NodeType after filtering for OT namesapce {} for component {}", qName, name))
                .collect(Collectors.toList());

            if (filteredCandidates.size() > 0) {
                candidates = filteredCandidates;
            }
        }

        if (candidates.size() > 0) {
            logger.info("Identified NodeType {}", candidates.get(0));
            return candidates.get(0);
        }

        QName softwareComponent = QName.valueOf("{" + OPENTOSCA_NORMATIVE_NODE_TYPES_NAMESPACE + "}SoftwareComponent");
        logger.info("Fallback to the generic SoftwareComponent NodeType {}", softwareComponent);

        return softwareComponent;
    }

    private String normalizeName(String name) {
        return name.toLowerCase()
            .replaceAll("\\s|-|_|\\.", "");
    }
}
