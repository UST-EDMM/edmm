package io.github.edmm.plugins.salt.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ComponentEnvVar {
    final String name;
    final String value;
    final int insertIndex;
}
