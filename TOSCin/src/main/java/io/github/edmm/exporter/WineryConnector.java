package io.github.edmm.exporter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WineryConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineryConnector.class);
    private static WineryConnector INSTANCE;

    private final IRepository repository;
    private Map<QName, TNodeType> nodeTypes;

    private WineryConnector() {
        repository = RepositoryFactory.getRepository();
    }

    public static synchronized WineryConnector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WineryConnector();
        }
        return INSTANCE;
    }

    public TNodeType getNodeType(QName qName) {
        return repository.getElement(new NodeTypeId(qName));
    }

    public List<QName> getBaseNodeTypesQNames() {
        getNodeTypes();
        return this.nodeTypes.entrySet().stream()
                .filter(entry -> entry.getValue().getTags() == null
                        || entry.getValue().getTags().getTag().stream().noneMatch(tag -> "feature".equals(tag.getName())))
                .filter(entry -> !repository.getNamespaceManager().isGeneratedNamespace(entry.getKey().getNamespaceURI()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Map<QName, TNodeType> getNodeTypes() {
        if (this.nodeTypes == null) {
            this.nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        }
        return nodeTypes;
    }

    public TRelationshipType getRelationshipType(QName qName) {
        return repository.getElement(new RelationshipTypeId(qName));
    }

    public void save(TServiceTemplate serviceTemplate) throws IOException {
        BackendUtils.persist(
                repository,
                new ServiceTemplateId(serviceTemplate.getTargetNamespace(), serviceTemplate.getId(), false),
                serviceTemplate
        );
    }
}
