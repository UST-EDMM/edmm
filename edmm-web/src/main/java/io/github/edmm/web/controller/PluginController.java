package io.github.edmm.web.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.edmm.utils.Consts;
import io.github.edmm.web.model.PluginSupportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/plugins")
@Tag(name = "plugin", description = "The plugin API")
public class PluginController {

    @Operation(summary = "Checks if the plugins support the used components of an EDMM model.")
    @PostMapping(value = "/check-model-support",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PluginSupportResult>> checkModelSupport(@RequestBody String yaml) {
        log.info("======================================={}{}", Consts.NL, yaml);
        log.info("=======================================");
        List<PluginSupportResult> result = Stream.of(
                PluginSupportResult.builder()
                        .id("cfn")
                        .name("AWS CloudFormation")
                        .supports(0.87)
                        .unsupportedComponents(Stream.of("comp1", "comp2").collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
