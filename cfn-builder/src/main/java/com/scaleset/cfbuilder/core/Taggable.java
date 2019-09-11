package com.scaleset.cfbuilder.core;

public interface Taggable extends Resource {

    default Taggable tag(String key, String value) {
        tags(new Tag(key, value));
        return this;
    }

    Taggable tags(Tag... values);
}
