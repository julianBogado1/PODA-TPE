package ar.edu.itba.io.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

public record JSONDataSource(JsonNode root) implements DataSource<JsonNode> {
    @Override
    public Iterator<JsonNode> iterator() {
        return root.elements();
    }
}
