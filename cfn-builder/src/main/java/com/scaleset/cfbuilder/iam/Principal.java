package com.scaleset.cfbuilder.iam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Constructs a {@code Principal} to specify the user (IAM user, federated user, or assumed-role user), AWS account, AWS
 * service, or other principal entity that is allowed or denied access to a resource.
 * <br>
 * Can be used as a IAM policy {@link Statement}.
 *
 * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">Documentation
 * Reference</a>
 */
public class Principal {

    @JsonIgnore
    private Map<String, List<String>> principalMap;

    public Principal() {
        this.principalMap = new HashMap<>();
    }

    @JsonAnyGetter
    public Map<String, List<String>> getPrincipalMap() {
        return principalMap;
    }

    public Principal principal(String arn, List<String> resources) {
        this.principalMap.put(arn, resources);
        return this;
    }
}
