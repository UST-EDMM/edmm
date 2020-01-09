package io.github.edmm.plugins.terraform.model;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.utils.Consts;
import lombok.Data;

@Data
public class Auth0 {

    private String name = Consts.EMPTY;
    private String clientId = Consts.EMPTY;
    private String clientSecret = Consts.EMPTY;
    private String identifier = Consts.EMPTY;
    private List<String> scopes = new ArrayList<>();
}
