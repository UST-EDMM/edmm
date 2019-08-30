package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

enum CachingEnum {
    None,
    ReadOnly,
    ReadWrite
}

enum CreateOptionsEnum {
    Attache,
    FromImage,
    Empty
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageProfile {
    private ImageReference imageReference;
    private OsDisk osDisk;
}

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class ImageReference {
    private String publisher;
    private String offer;
    private String sku;
    private String version;
}

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class OsDisk {
    private String name;
    private CachingEnum caching;
    private CreateOptionsEnum createOptions;
}
