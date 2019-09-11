package com.scaleset.cfbuilder.cloudformation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;

/**
 * Constructs an {@code Authentication} to specify authentication credentials for files or sources specified with the
 * {@link CFNInit} resource.
 * <br>
 * Type: {@code AWS::CloudFormation::Authentication}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-authentication.html>Documentation
 * Reference</a>
 */
@Type("AWS::CloudFormation::Authentication")
public class Authentication {

    @JsonProperty("accessKeyId")
    private String accessKeyId;

    @JsonProperty("buckets")
    private List<String> buckets;

    @JsonProperty("secretKey")
    private String secretKey;

    @JsonProperty("type")
    private String type;

    @JsonProperty("uris")
    private List<String> uris;

    @JsonProperty("username")
    private String username;

    @JsonProperty("roleName")
    private Object roleName;

    @JsonIgnore
    private String name;

    public Authentication(String name) {
        this.name = name;
        this.buckets = new ArrayList<>();
        this.uris = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public Authentication name(String name) {
        this.name = name;
        return this;
    }

    public String getAccessKeyId() {
        return this.accessKeyId;
    }

    public Authentication accessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public List getBuckets() {
        return this.buckets;
    }

    public Authentication buckets(List<String> buckets) {
        this.buckets = buckets;
        return this;
    }

    public Authentication addBucket(String bucket) {
        this.buckets.add(bucket);
        return this;
    }

    public Authentication deleteBucket(String bucket) {
        this.buckets.remove(bucket);
        return this;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public Authentication secretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getType() {
        return type;
    }

    public Authentication type(String type) {
        this.type = type;
        return this;
    }

    public List getUris() {
        return uris;
    }

    public Authentication uris(List<String> uris) {
        this.uris = uris;
        return this;
    }

    public Authentication addUri(String uri) {
        this.uris.add(uri);
        return this;
    }

    public Authentication deleteUri(String uri) {
        this.uris.remove(uri);
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Authentication username(String username) {
        this.username = username;
        return this;
    }

    public Object getRoleName() {
        return roleName;
    }

    public Authentication roleName(Object roleName) {
        this.roleName = roleName;
        return this;
    }
}


