package fit.biejk.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import fit.biejk.service.OrderService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Initializes the Elasticsearch index for storing order data.
 * <p>
 * This class is executed at application startup and ensures the "orders" index
 * is created in Elasticsearch with the correct mappings. It deletes the existing
 * index (if present), recreates it, and loads all current orders from the database
 * into the index.
 * </p>
 */
@Startup
@Singleton
@Slf4j
public class OrderIndexInitializer {

    /**
     * Mapper responsible for converting Order to OrderSearchDto.
     */
    @Inject
    private OrderSearchMapper orderSearchMapper;

    /**
     * Elasticsearch client used to interact with the Elasticsearch cluster.
     */
    @Inject
    private ElasticsearchClient elasticsearchClient;

    /**
     * Service for accessing orders from the relational database.
     */
    @Inject
    private OrderService orderService;

    /**
     * Service for indexing orders in Elasticsearch.
     */
    @Inject
    private OrderSearchService orderSearchService;

    /**
     * Initializes the index after application startup.
     * <p>
     * If the "orders" index already exists, it is deleted and recreated.
     * The index is created with mappings for ID, status, services, and location.
     * Then, all orders from the database are loaded and indexed.
     * </p>
     */
    @PostConstruct
    void init() {
        String indexName = "orders";

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
                            .properties("status", p -> p
                                    .text(t -> t
                                            .fields("keyword", k -> k.keyword(kk -> kk))
                                    )
                            )
                            .properties("services", p -> p
                                    .text(t -> t
                                            .fields("keyword", k -> k.keyword(kk -> kk))
                                    )
                            )
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
     * Loads all orders from the database and indexes them in Elasticsearch.
     */
    void load() {
        log.info("Loading orders from database");
        orderService.getAll()
                .forEach(order ->
                        orderSearchService.save(orderSearchMapper.toDto(order))
                );
    }
}
