package io.github.edmm.core.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import io.github.edmm.exporter.OpenTOSCAConnector;
import io.github.edmm.exporter.dto.TypesDTO;
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
    private final List<TypesDTO> allNodeTypes;
    private DeploymentInstance deploymentInstance;

    public TOSCATransformer() {
        this.allNodeTypes = OpenTOSCAConnector.getAllNodeTypes();
        this.allNodeTypes.forEach(typesDTO -> {
            typesDTO.setVersion(VersionUtils.getVersion(typesDTO.getId()));
            typesDTO.setNormalizedName(
                VersionUtils.getNameWithoutVersion(typesDTO.getId())
                    .toLowerCase()
                    .replaceAll("\\s|-|_|\\.", "")
            );
            typesDTO.setXmlQName(QName.valueOf(typesDTO.getQName()));
        });
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
        QName nodeTypeQName = getNodeTypeQName(name, version);
        return OpenTOSCAConnector.getNodeType(nodeTypeQName);
    }

    public QName getNodeTypeQName(String name, String version) {
        List<TypesDTO> types = this.allNodeTypes.stream()
            .filter(nodeType -> nodeType.getNormalizedName().startsWith(
                name.toLowerCase()
                    .replaceAll("\\s|-|_|\\.", "")
            ))
            .peek(nodeType -> logger.info("Found matching NodeType {} for component {}", nodeType, name))
            .collect(Collectors.toList());

        if (types.size() > 0) {
            List<TypesDTO> filtered = types.stream()
                .filter(nodeType -> nodeType.getVersion().getComponentVersion().toLowerCase().equals(version.toLowerCase()))
                .peek(nodeType -> logger.info("Found matching NodeType after filtering for version {} for component {}", nodeType.getQName(), name))
                .collect(Collectors.toList());

            if (filtered.size() > 0) {
                types = filtered;
            }
        }

        if (types.size() > 1) {
            List<TypesDTO> filtered = types.stream()
                .filter(nodeType -> nodeType.getNamespace().startsWith(OPENTOSCA_BASE))
                .peek(nodeType -> logger.info("Found matching NodeType after filtering for OT namesapce {} for component {}", nodeType.getQName(), name))
                .collect(Collectors.toList());
            if (filtered.size() > 0) {
                types = filtered;
            }
        }

        if (types.size() > 0) {
            return types.get(0).getXmlQName();
        }

        return QName.valueOf("{" + OPENTOSCA_NORMATIVE_NODE_TYPES_NAMESPACE + "}SoftwareComponent");
    }
}
