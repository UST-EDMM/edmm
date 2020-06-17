package io.github.edmm.plugins.puppet.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.PropertyKey;
import io.github.edmm.plugins.puppet.model.Fact;
import io.github.edmm.plugins.puppet.model.FactType;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.Node;
import io.github.edmm.plugins.puppet.model.PuppetState;

public class NodesHandler {
    private Master master;

    public NodesHandler(Master master) {
        this.master = master;
    }

    public void handleNodes() {
        this.setNodes();
        this.setNodeFacts();
        this.setNodeStates();
    }

    private void setNodes() {
        this.master.setNodes(this.buildNodesFromString(this.master.executeCommandAndHandleResult(Commands.GET_NODES)));
    }

    private void setNodeFacts() {
        this.master.getNodes().forEach(node -> node.setFacts(this.getFactsForNodeByCertName(node.getCertname())));
    }

    private List<Fact> getFactsForNodeByCertName(String certName) {
        List<Fact> facts = new ArrayList<>();

        facts.add(this.getFact(certName, FactType.IPAddress));
        facts.add(this.getFact(certName, FactType.OperatingSystem));
        facts.add(this.getFact(certName, FactType.OperatingSystemRelease));
        facts.add(new Fact(certName, "private_key", this.master.getGeneratedPrivateKey()));
        facts.add(new Fact(certName, String.valueOf(PropertyKey.Compute.public_key), this.master.getGeneratedPublicKey()));

        return facts;
    }

    private Fact getFact(String certName, FactType factType) {
        return checkAndReplaceIPAddressKey(buildFactFromString(this.master.executeCommandAndHandleResult(Commands.getFactCommandByFactType(certName, factType))));
    }

    private Fact checkAndReplaceIPAddressKey(Fact fact) {
        if (fact.getName().equals(String.valueOf(FactType.IPAddress).toLowerCase())) {
            fact.setName(String.valueOf(PropertyKey.Compute.public_address));
        }
        return fact;
    }

    private void setNodeStates() {
        this.master.getNodes().forEach(node -> node.setState(PuppetState.NodeState.valueOf(node.getLatest_report_status())));
    }

    private List<Node> buildNodesFromString(String jsonString) {
        return GsonHelper.parseJsonStringToParameterizedList(jsonString, Node.class);
    }

    private Fact buildFactFromString(String jsonString) {
        return GsonHelper.parseJsonStringToObjectType(jsonString.substring(1, jsonString.length() - 1), Fact.class);
    }

}
