package io.github.edmm.model.parameters;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputParameter {

    private String name;
    private ParameterType type;
    private String defaultValue;
    private String description;

    public static InputParameter of(@NonNull String name, @NonNull ParameterType type, String defaultValue, String description) {
        return new InputParameter(name, type, defaultValue, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputParameter that = (InputParameter) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
