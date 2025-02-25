package fit.biejk.service;

import fit.biejk.entity.Client;
import fit.biejk.repository.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.NoArgsConstructor;

import java.util.List;

@ApplicationScoped
@NoArgsConstructor
public class ClientService {

    @Inject
    ClientRepository clientRepository;
    @Inject
    UserService userService;

    @Transactional
    public Client create(Client client) {
        userService.checkUniqueEmail(client.getEmail());
        clientRepository.persist(client);
        return client;
    }

    public List<Client> getAll() {
        return clientRepository.listAll();
    }

    public Client getById(Long id) {
        return clientRepository.findById(id);
    }

    @Transactional
    public Client update(Long id, Client client) {
        Client old = clientRepository.findById(id);
        if(old == null) {
            throw new NotFoundException("Client with id " + id + " not found");
        }
        userService.updateCommonFields(old, client);
        return old;
    }

    @Transactional
    public void delete(Long id) {
        Client client = clientRepository.findById(id);
        clientRepository.delete(client);
    }



}
