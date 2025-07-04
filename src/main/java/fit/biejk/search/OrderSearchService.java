package fit.biejk.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Service for managing the indexing, deletion, and searching of {@link OrderSearchDto}
 * documents in the Elasticsearch "orders" index.
 */
@ApplicationScoped
@Slf4j
public class OrderSearchService {

    /**
     * Elasticsearch client used to perform operations on the search index.
     */
    @Inject
    private ElasticsearchClient elasticsearchClient;

    /**
     * Saves or updates an {@link OrderSearchDto} document in the Elasticsearch "orders" index.
     *
     * @param dto the order data to be indexed
     */
    public void save(final OrderSearchDto dto) {
        try {
            log.info("Saving Order with id {} for search", dto.getId());
            elasticsearchClient.index(i -> i
                    .index("orders")
                    .id(String.valueOf(dto.getId()))
                    .document(dto)
            );
        } catch (IOException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes an order document from the Elasticsearch "orders" index by its ID.
     *
     * @param id the ID of the order to delete
     */
    public void delete(final Long id) {
        try {
            log.info("Deleting Order with id {} for search", id);
            elasticsearchClient.delete(d -> d
                    .index("orders")
                    .id(String.valueOf(id))
            );
        } catch (IOException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Searches for orders that match the specified service names and location.
     * Only orders with status "CREATED" or "CLIENT_PENDING" are returned.
     *
     * @param services a list of service names to search for
     * @param location the city/location to filter orders by
     * @return a list of matching {@link OrderSearchDto} objects
     */
    public List<OrderSearchDto> search(final List<String> services, final String location) {
        try {
            log.info("Searching for Orders with services {} and location {}", services, location);

            SearchResponse<OrderSearchDto> response = elasticsearchClient.search(s -> s
                            .index("orders")
                            .query(q -> q
                                    .bool(b -> b
                                        .must(m -> m
                                                .terms(t -> t
                                                    .field("services.keyword")
                                                        .terms(ts -> ts
                                                            .value(services.stream()
                                                                    .map(v -> co.elastic.clients.
                                                                            elasticsearch._types.FieldValue.of(v))
                                                                    .toList())
                                                            )
                                                    )
                                            )
                                            .filter(f -> f
                                                .term(t -> t
                                                    .field("location.keyword")
                                                        .value(co.elastic.clients.elasticsearch._types.
                                                                    FieldValue.of(location))
                                                    )
                                            )
                                            .filter(f -> f
                                                .terms(t -> t
                                                    .field("status.keyword")
                                                        .terms(ts -> ts
                                                            .value(List.of("CREATED", "CLIENT_PENDING").stream()
                                                                .map(co.elastic.clients.
                                                                    elasticsearch._types.FieldValue::of)
                                                                    .toList())
                                                            )
                                                    )
                                            )
                                    )
                            ),
                    OrderSearchDto.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

        } catch (IOException e) {
            log.error("Failed to search orders", e);
            return List.of();
        }
    }
}
