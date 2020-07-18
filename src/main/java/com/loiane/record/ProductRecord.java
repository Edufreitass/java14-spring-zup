package com.loiane.record;

import com.fasterxml.jackson.annotation.JsonProperty;

// Records are classes with immutable attributes, do not contain setters, 
// only getters and automatically generates toString, equals and hashcode
public record ProductRecord(
		@JsonProperty Integer id, 
		@JsonProperty String name, 
		@JsonProperty int status) {
}
