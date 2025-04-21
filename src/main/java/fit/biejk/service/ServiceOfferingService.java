package fit.biejk.service;

import fit.biejk.entity.ServiceOffering;
import fit.biejk.repository.ServiceOfferingRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class ServiceOfferingService {

    @Inject
    private ServiceOfferingRepository serviceOfferingRepository;

    public List<ServiceOffering> getAll() {
        log.info("get all service offerings");
        return serviceOfferingRepository.listAll();
    }

}
