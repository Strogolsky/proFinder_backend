package fit.biejk.service;

import fit.biejk.entity.Client;
import fit.biejk.repository.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    @Inject
    UserService userService;

    @Transactional
    public Client create(Client client) {
        log.info("Create client: {}", client.getEmail());
        userService.checkUniqueEmail(client.getEmail());
        clientRepository.persist(client);
        log.debug("Client created with ID={}", client.getId());
        return client;
    }

    public List<Client> getAll() {
        log.info("Get all clients");
        List<Client> result = clientRepository.listAll();
        log.debug("Found {} clients", result.size());
        return result;
    }

    public Client getById(Long id) {
        log.info("Get client by ID={}", id);
        Client result = clientRepository.findById(id);
        if (result == null) {
            log.error("Client with ID={} not found", id);
            throw new NotFoundException("Client with id " + id + " not found");
        }
        return result;
    }

    @Transactional
    public Client update(Long id, Client client) {
        log.info("Update client: ID={}, email={}", id, client.getEmail());
        Client old = getById(id);
        userService.update(id, client);
        log.debug("Client updated with ID={}", id);
        return old;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Delete client: ID={}", id);
        userService.delete(id);
        log.debug("Client deleted with ID={}", id);
    }
}
