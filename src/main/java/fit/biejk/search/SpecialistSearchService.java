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

@Slf4j
@ApplicationScoped
public class SpecialistSearchService {
    @Inject
    ElasticsearchClient elasticsearchClient;

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

    public List<SpecialistSearchDto> search(String keyword, String location) {
        try {
            SearchResponse<SpecialistSearchDto> response = elasticsearchClient.search(s -> s
                            .index("specialists")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .multiMatch(mm -> mm
                                                            .fields("firstName", "lastName", "description", "services")
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
