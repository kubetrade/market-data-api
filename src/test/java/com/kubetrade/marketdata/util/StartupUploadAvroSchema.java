package com.kubetrade.marketdata.util;

import com.kubetrade.avro.marketdata.EventType;
import com.kubetrade.avro.marketdata.SettlementPriceEvent;
import com.kubetrade.avro.marketdata.SettlementPriceEventKey;
import io.quarkus.runtime.StartupEvent;
import org.apache.avro.Schema;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@ApplicationScoped
public class StartupUploadAvroSchema {

    private static final Logger log = LoggerFactory.getLogger(StartupUploadAvroSchema.class);
    HttpClient httpClient = HttpClient.newHttpClient();

    @ConfigProperty(name="mp.messaging.connector.smallrye-kafka.apicurio.registry.url")
    String url;

    void onStart(@Observes StartupEvent ev) throws IOException, URISyntaxException, InterruptedException {
        List<Schema> schemas = List.of(EventType.getClassSchema(), SettlementPriceEventKey.getClassSchema(), SettlementPriceEvent.getClassSchema());
        for (Schema schema: schemas) {
            log.info(this.upload(schema).body());
        }
    }

    public HttpResponse<String> upload(Schema schema) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("%s/groups/%s/artifacts?ifExists=RETURN_OR_UPDATE&canonical=true", url, schema.getNamespace())))
                .header("Content-type", "application/json; artifactType=AVRO")
                .header("X-Registry-ArtifactType", "AVRO")
                .header("X-Registry-Name", schema.getName())
                .header("X-Registry-ArtifactId", schema.getName())
                .POST(HttpRequest.BodyPublishers.ofString(schema.toString()))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
