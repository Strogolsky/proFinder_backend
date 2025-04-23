package fit.biejk.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import fit.biejk.service.SpecialistService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;

@Startup
@Singleton
public class SpecialistIndexInitializer {

    @Inject
    ElasticsearchClient elasticsearchClient;

    @Inject
    SpecialistService specialistService;

    @Inject
    SpecialistSearchService specialistSearchService;

    @Inject
    SpecialistSearchMapper specialistSearchMapper;

    @PostConstruct
    void init() {
        String indexName = "specialists";

        try {
            boolean exists = elasticsearchClient.indices().exists(e -> e.index(indexName)).value();
            if (exists) {
                elasticsearchClient.indices().delete(d -> d.index(indexName));
            }
            elasticsearchClient.indices().create(c -> c
                    .index(indexName)
                    .mappings(m -> m
                            .properties("id", p -> p.long_(x -> x))
                            .properties("firstName", p -> p.text(t -> t))
                            .properties("lastName", p -> p.text(t -> t))
                            .properties("description", p -> p.text(t -> t))
                            .properties("averageRating", p -> p.double_(d -> d))
                            .properties("services", p -> p.text(t -> t))
                            .properties("location", p -> p
                                    .text(t -> t
                                            .fields("keyword", k -> k.keyword(kk -> kk))
                                    )
                            )

                    )
            );
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void load() {
        specialistService.getAll()
                .forEach(specialist ->
                        specialistSearchService.save(specialistSearchMapper.toDto(specialist))
                );
    }
}

