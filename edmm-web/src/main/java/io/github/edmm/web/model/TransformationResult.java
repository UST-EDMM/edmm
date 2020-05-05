package io.github.edmm.web.model;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import io.github.edmm.core.transformation.TransformationContext;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class TransformationResult {

    private final String id;
    private final String target;
    private final String time;
    private final String state;

    public static TransformationResult of(@NonNull TransformationContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.GERMANY).withZone(ZoneId.systemDefault());

        return TransformationResult.builder()
            .id(context.getId())
            .target(context.getTargetTechnology().getId())
            .time(formatter.format(context.getTimestamp().toInstant()))
            .state(context.getState().toString().toLowerCase())
            .build();
    }
}
