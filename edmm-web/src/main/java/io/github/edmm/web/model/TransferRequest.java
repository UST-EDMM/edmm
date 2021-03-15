package io.github.edmm.web.model;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotEmpty
    @Schema(description = "The target endpoint of the participant", required = true)
    private String endpoint;

    @NotEmpty
    @Schema(description = "The EDMM multi id", required = true)
    private String multiId;
}
