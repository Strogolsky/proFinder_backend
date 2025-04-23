package fit.biejk.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Service for indexing, deleting, and searching specialists in Elasticsearch.
 */
@Slf4j
@ApplicationScoped
public class SpecialistSearchService {

    /**
     * Elasticsearch client used for communication with the search index.
     */
    @Inject
    private ElasticsearchClient elasticsearchClient;

    /**
     * Indexes a specialist document in Elasticsearch.
     *
     * @param dto the specialist data to be indexed
     */
    @Transactional
    public void save(final SpecialistSearchDto dto) {
        try {
            elasticsearchClient.index(i -> i
                    .index("specialists")
                    .id(String.valueOf(dto.getId()))
                    .document(dto)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a specialist document from Elasticsearch by ID.
     *
     * @param id the ID of the specialist to delete
     */
    @Transactional
    public void delete(final Long id) {
        try {
            elasticsearchClient.delete(d -> d
                    .index("specialists")
                    .id(String.valueOf(id))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches for specialists using keyword and location.
     * The results are sorted by averageRating in descending order.
     * Supports fuzzy matching for keyword.
     *
     * @param keyword  the search keyword (e.g., service name or description)
     * @param location the city to filter specialists by
     * @return a list of matching specialists
     */
    public List<SpecialistSearchDto> search(final String keyword, final String location) {
        try {
            SearchResponse<SpecialistSearchDto> response = elasticsearchClient.search(s -> s
                            .index("specialists")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .multiMatch(mm -> mm
                                                            .fields("firstName", "lastName",
                                                                    "description", "services")
                                                            .query(keyword)
                                                            .fuzziness("AUTO")
                                                    )
                                            )
                                            .filter(f -> f
                                                    .term(t -> t
                                                            .field("location.keyword")
                                                            .value(location)
                                                    )
                                            )
                                    )
                            )
                            .sort(so -> so
                                    .field(f -> f
                                            .field("averageRating")
                                            .order(SortOrder.Desc)
                                    )
                            ),
                    SpecialistSearchDto.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
