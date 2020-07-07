package io.github.edmm.plugins.puppet.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import io.github.edmm.core.transformation.InstanceTransformationException;

import org.apache.commons.io.FileUtils;

class ModuleLoader {
    private static String baseURL = "https://github.com/mathonto/puppet_edimm_helper/blob/master/";
    private static String shellScript = "edimm_ssh.sh";
    private static String moduleZip = "edimm_ssh.zip";

    static void downloadPuppetArtifacts() {
        downloadPuppetArtifact(baseURL, shellScript);
        downloadPuppetArtifact(baseURL, moduleZip);
    }

    static void downloadPuppetArtifact(String baseURL, String fileName) {
        try {
            FileUtils.copyURLToFile(
                new URL(baseURL + fileName),
                new File(fileName));
        } catch (IOException e) {
            throw new InstanceTransformationException("Failed to download artifacts required for Puppet Plugin.", e.getCause());
        }
    }
}
