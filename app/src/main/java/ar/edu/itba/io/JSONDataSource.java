package ar.edu.itba.io;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.Iterator;

public record JSONDataSource(JsonNode root) implements DataSource<JsonNode> {
    @Override
    public Iterator<JsonNode> iterator() {
        return root.elements();
    }
}
