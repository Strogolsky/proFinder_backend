package fit.biejk.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import fit.biejk.service.SpecialistService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Initializes the Elasticsearch index for storing specialist data.
 * <p>
 * This class deletes the existing index on startup, recreates it with the correct mappings,
 * and populates it from the database using the {@link SpecialistService}.
 * </p>
 */
@Startup
@Singleton
@Slf4j
public class SpecialistIndexInitializer {

    /**
     * Elasticsearch client used to interact with the Elasticsearch cluster.
     */
    @Inject
    private ElasticsearchClient elasticsearchClient;

    /**
     * Service used to retrieve specialist data from the PostgreSQL database.
     */
    @Inject
    private SpecialistService specialistService;

    /**
     * Service used to persist specialist data to the Elasticsearch index.
     */
    @Inject
    private SpecialistSearchService specialistSearchService;

    /**
     * Mapper for converting {@link fit.biejk.entity.Specialist} entities into {@link SpecialistSearchDto}.
     */
    @Inject
    private SpecialistSearchMapper specialistSearchMapper;

    /**
     * Called after the bean is constructed.
     * <p>
     * This method checks if the index already exists. If it does, it deletes the index,
     * then creates a new one with the necessary field mappings and loads data into it.
     * </p>
     */
    @PostConstruct
    void init() {
        String indexName = "specialists";

        try {
            boolean exists = elasticsearchClient.indices().exists(e -> e.index(indexName)).value();
            if (exists) {
                log.info("Deleting index " + indexName);
                elasticsearchClient.indices().delete(d -> d.index(indexName));
            }
            log.info("Creating index " + indexName);
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

    /**
     * Loads all specialists from the database and saves them into the Elasticsearch index.
     */
    void load() {
        log.info("Loading specialists from database");
        specialistService.getAll()
                .forEach(specialist ->
                        specialistSearchService.save(specialistSearchMapper.toDto(specialist))
                );
    }
}
