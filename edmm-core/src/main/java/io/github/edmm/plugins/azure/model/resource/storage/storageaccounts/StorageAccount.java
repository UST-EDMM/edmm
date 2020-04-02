package io.github.edmm.plugins.azure.model.resource.storage.storageaccounts;

import java.util.Map;

import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Properties;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Default configuration assumes: - a parameter called 'location' - a parameter called 'storageAccountName'
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StorageAccount extends Resource {
    private StorageAccountKindEnum kind;
    private SkuTypeEnum sku;

    public StorageAccount() {
        super(ResourceTypeEnum.STORAGE_ACCOUNTS, "[parameters('storageAccountName')]");
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        this.setApiVersion("2019-04-01");
        this.setKind(StorageAccountKindEnum.Storage);
        this.setSku(SkuTypeEnum.Standard_LRS);
        this.setProperties(new Properties() {
        });
    }

    @Override
    public Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put("storageAccountName", Parameter.builder()
            .type(ParameterTypeEnum.STRING)
            .defaultValue("[concat(uniquestring(resourceGroup().id), 'myvmsa')]")
            .build());

        return params;
    }
}
