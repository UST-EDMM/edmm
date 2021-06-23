package io.github.edmm.plugins.puppet.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.edmm.plugins.puppet.model.Fact;
import io.github.edmm.plugins.puppet.model.FactType;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.Node;
import io.github.edmm.plugins.puppet.model.PuppetState;
import io.github.edmm.util.Constants;

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
        List<Node> nodes = this.buildNodesFromString(this.master.executeCommandAndHandleResult(Commands.GET_NODES))
            .stream()
            .filter(node -> !node.getCertname().equals(this.master.getHostName()))
            .collect(Collectors.toList());
        this.master.setNodes(nodes);
    }

    private void setNodeFacts() {
        this.master.getNodes().forEach(node -> node.setFacts(this.getAllFactsForNode(node.getCertname())));
    }

    private List<Fact> getFactsForNodeByCertName(String certName) {
        List<Fact> facts = new ArrayList<>();

        facts.add(this.getFact(certName, FactType.IPAddress));
        facts.add(this.getFact(certName, FactType.OperatingSystem));
        facts.add(this.getFact(certName, FactType.OperatingSystemRelease));
        facts.add(new Fact(certName, Constants.VM_PRIVATE_KEY, this.master.getGeneratedPrivateKey()));
        facts.add(new Fact(certName, Constants.VM_PUBLIC_KEY, this.master.getGeneratedPublicKey()));

        return facts;
    }

    private List<Fact> getAllFactsForNode(String certName) {
        String s = this.master.executeCommandAndHandleResult(Commands.factQuery(certName));
        return GsonHelper.parseJsonStringToParameterizedList(s, Fact.class);
    }

    private Fact getFact(String certName, FactType factType) {
        return checkAndReplaceIPAddressKey(buildFactFromString(this.master.executeCommandAndHandleResult(Commands.getFactCommandByFactType(
            certName,
            factType))));
    }

    private Fact checkAndReplaceIPAddressKey(Fact fact) {
        if (fact.getName().equals(String.valueOf(FactType.IPAddress).toLowerCase())) {
            fact.setName(Constants.VMIP);
        }
        return fact;
    }

    private void setNodeStates() {
        this.master.getNodes()
            .forEach(node -> node.setState(PuppetState.NodeState.valueOf(node.getLatest_report_status())));
    }

    private List<Node> buildNodesFromString(String jsonString) {
        return GsonHelper.parseJsonStringToParameterizedList(jsonString, Node.class);
    }

    private Fact buildFactFromString(String jsonString) {
        return GsonHelper.parseJsonStringToObjectType(jsonString.substring(1, jsonString.length() - 1), Fact.class);
    }
}
