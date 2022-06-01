package com.kubetrade.marketdata.util;

import com.kubetrade.avro.marketdata.EventType;
import com.kubetrade.avro.marketdata.SettlementPriceEvent;
import com.kubetrade.avro.marketdata.SettlementPriceEventKey;
import org.apache.avro.Schema;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UploadAvroSchemas {

    HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        UploadAvroSchemas uploadAvroSchemas = new UploadAvroSchemas();
        List<Schema> schemas = List.of(EventType.getClassSchema(), SettlementPriceEventKey.getClassSchema(), SettlementPriceEvent.getClassSchema());
        for (Schema schema: schemas) {
            System.out.println(uploadAvroSchemas.upload(schema).body());
        }
    }

    public HttpResponse<String> upload(Schema schema) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("http://localhost:8091/apis/registry/v2/groups/%s/artifacts?ifExists=RETURN_OR_UPDATE&canonical=true", schema.getNamespace())))
                .header("Content-type", "application/json; artifactType=AVRO")
                .header("X-Registry-ArtifactType", "AVRO")
                .header("X-Registry-Name", schema.getName())
                .header("X-Registry-ArtifactId", schema.getName())
                .POST(HttpRequest.BodyPublishers.ofString(schema.toString()))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}