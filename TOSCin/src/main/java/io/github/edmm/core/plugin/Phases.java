package io.github.edmm.core.plugin;

public enum Phases {
    PREPARE,
    GET_MODELS,
    TRANSFORM_EDMMi,
    TRANSFORM_TOSCA,
    STORE_TRANSFORMED_TOSCA,
    CREATE_YAML,
    CLEANUP,
}
