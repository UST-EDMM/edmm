package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OsProfile {
    private String computerName;
    private String adminUsername;
    private String adminPassword;
    private LinuxConfiguration linuxConfiguration;
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class LinuxConfiguration {
    private boolean disablePasswordAuthentication;
    private SshConfiguratoin ssh;
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class SshConfiguratoin {
    private List<PublicKey> publicKeys;
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class PublicKey {
    private String path;
    private String keyData;
}
