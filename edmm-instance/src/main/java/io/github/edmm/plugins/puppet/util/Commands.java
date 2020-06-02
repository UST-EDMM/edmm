package io.github.edmm.plugins.puppet.util;

public class Commands {
    private static final String BASE_COMMAND = "curl http://localhost:8080/pdb/query/v4/";
    public static final String GET_REPORTS = BASE_COMMAND + "reports";
    public static final String GET_NODES = BASE_COMMAND + "nodes";
    private static final String GET_FACTS = BASE_COMMAND + "facts";

    public static String getFacts(String certName) {
        return GET_FACTS + " 'query=[\"=\", \"certName\", \"" + certName + "\"]'";
    }
}
