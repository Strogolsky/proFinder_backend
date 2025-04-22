package fit.biejk.service;

import fit.biejk.entity.ServiceOffering;
import fit.biejk.repository.ServiceOfferingRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service class for managing {@link ServiceOffering} entities.
 * <p>
 * Provides business logic for retrieving available service offerings.
 * </p>
 */
@ApplicationScoped
@Slf4j
public class ServiceOfferingService {

    /**
     * Repository for accessing service offering data.
     */
    @Inject
    private ServiceOfferingRepository serviceOfferingRepository;

    /**
     * Retrieves all service offerings from the database.
     *
     * @return list of all available service offerings
     */
    public List<ServiceOffering> getAll() {
        log.info("get all service offerings");
        return serviceOfferingRepository.listAll();
    }
}
