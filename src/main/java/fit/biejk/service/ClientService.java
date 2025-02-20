package fit.biejk.service;

import fit.biejk.entity.Client;
import fit.biejk.repository.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;

import java.util.List;

@ApplicationScoped
@NoArgsConstructor
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    @Transactional
    public Client create(Client client) {
        clientRepository.persist(client);
        return client;
    }

    public List<Client> getAll() {
        return clientRepository.listAll();
    }

    public Client getById(Long id) {
        return clientRepository.findById(id);
    }

    public Client update(Client client) {
        Client old = clientRepository.findById(client.getId());
        if(old == null) {
            // todo
        }
        old.setFirstName(client.getFirstName());
        old.setLastName(client.getLastName());
        clientRepository.persist(old);
        return old;
    }

    public void delete(Long id) {
        Client client = clientRepository.findById(id);
        clientRepository.delete(client);
    }



}
