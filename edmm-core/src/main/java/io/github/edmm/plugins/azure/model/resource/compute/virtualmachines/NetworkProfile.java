package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import java.util.List;

import io.github.edmm.plugins.azure.model.resource.network.networkinterfaces.NetworkInterface;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkProfile {
    private List<NetworkInterface> networkInterfaces;
}
