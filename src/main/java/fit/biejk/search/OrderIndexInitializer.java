package fit.biejk.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import fit.biejk.service.OrderService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Startup
@Singleton
@Slf4j
public class OrderIndexInitializer {

    @Inject
    private OrderSearchMapper orderSearchMapper;
    @Inject
    private ElasticsearchClient elasticsearchClient;

    @Inject
    private OrderService orderService;

    @Inject
    private OrderSearchService orderSearchService;

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

    void load() {
        log.info("Loading orders from database");
        orderService.getAll()
                .forEach(order ->
                        orderSearchService.save(orderSearchMapper.toDto(order))
                );
    }
}
