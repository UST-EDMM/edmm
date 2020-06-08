package io.github.edmm.plugins.puppet.model;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.plugins.puppet.util.Commands;
import io.github.edmm.plugins.puppet.util.GsonHelper;

class MasterNodeHandler {
    private Master master;

    MasterNodeHandler(Master master) {
        this.master = master;
    }

    void handleNodes() {
        this.setNodes();
        this.setNodeFacts();
        this.setNodeState();
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
        facts.add(new Fact(certName, "privateKey", this.master.getGeneratedPrivateKey()));
        facts.add(new Fact(certName, "publicKey", this.master.getGeneratedPublicKey()));

        return facts;
    }

    private Fact getFact(String certName, FactType factType) {
        return buildFactFromString(this.master.executeCommandAndHandleResult(Commands.getFactCommandByFactType(certName, factType)));
    }

    private void setNodeState() {
        this.master.getNodes().forEach(node -> node.setState(PuppetState.NodeState.valueOf(node.getLatest_report_status())));
    }


    private List<Node> buildNodesFromString(String jsonString) {
        return GsonHelper.parseJsonStringToParameterizedList(jsonString, Node.class);
    }

    private Fact buildFactFromString(String jsonString) {
        return GsonHelper.parseJsonStringToObjectType(jsonString.substring(1, jsonString.length() - 1), Fact.class);
    }

}
