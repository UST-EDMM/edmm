package io.github.edmm.plugins.cfn.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.InstanceProperty;

import com.amazonaws.services.cloudformation.model.Output;
import com.amazonaws.services.cloudformation.model.Parameter;

public class CfnStackPropertiesHandler {

    private final List<InstanceProperty> instanceProperties = new ArrayList<>();

    public List<InstanceProperty> getInstanceProperties(List<Parameter> parameters, List<Output> outputs) {
        this.handleParameters(parameters);
        this.handleOutputs(outputs);

        return instanceProperties;
    }

    private void handleParameters(List<Parameter> parameters) {
        parameters.forEach(parameter -> this.instanceProperties.add(new InstanceProperty(
            parameter.getParameterKey(),
            parameter.getParameterValue().getClass().getSimpleName(),
            parameter.getParameterValue()
        )));
    }

    private void handleOutputs(List<Output> outputs) {
        outputs.forEach(output -> this.instanceProperties.add(new InstanceProperty(
            output.getOutputKey(),
            output.getClass().getSimpleName(),
            output.getOutputValue()
        )));
    }
}
