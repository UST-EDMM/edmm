package io.github.edmm.exporter.dto;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.winery.common.version.WineryVersion;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypesDTO {
    private String id;
    private String name;
    private String namespace;
    private String qName;
    @JsonIgnore
    private QName xmlQName;
    @JsonIgnore
    private WineryVersion version;
    @JsonIgnore
    private String normalizedName;
}
