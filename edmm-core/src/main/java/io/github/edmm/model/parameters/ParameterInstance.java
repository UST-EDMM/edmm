package io.github.edmm.model.parameters;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ParameterInstance extends InputParameter {

    private String value;

    public ParameterInstance(String name, ParameterType type, String defaultValue, String description, String value) {
        super(name, type, defaultValue, description);
        this.value = value;
    }

    public static ParameterInstance of(InputParameter p, String value) {
        return new ParameterInstance(p.getName(), p.getType(), p.getDefaultValue(), p.getDescription(), value);
    }

    public static Set<ParameterInstance> of(@NonNull Set<UserInput> userInputs, @NonNull Set<InputParameter> inputParameters) {
        Map<String, ParameterInstance> parameterMap = inputParameters.stream()
            .map(p -> ParameterInstance.of(p, null))
            .collect(Collectors.toMap(InputParameter::getName, p -> p));
        userInputs.forEach(userInput -> {
            ParameterInstance p = parameterMap.get(userInput.getName());
            if (p != null) p.setValue(userInput.getValue());
        });
        return new HashSet<>(parameterMap.values());
    }

    public static boolean isValid(ParameterInstance p) {
        return p.getType().validate(p.getValue());
    }

    public String getValue() {
        if (value == null) {
            return getDefaultValue();
        }
        return value;
    }
}
