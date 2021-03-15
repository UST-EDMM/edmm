package io.github.edmm.plugins.multi.model.message;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamundaMessage {

    private String messageName;
    private String tenantId;
    private HashMap<String, Object> processVariables;

}
