package io.github.edmm.plugins.terraform.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Provisioner {
    private List<String> operations;
}
