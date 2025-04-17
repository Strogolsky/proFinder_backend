package fit.biejk.service;

import fit.biejk.entity.Location;
import fit.biejk.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class LocationService {
    @Inject
    private LocationRepository locationRepository;

    public List<Location> getAll() {
        return locationRepository.listAll();
    }

    public Location getById(Long locationId) {
        Location result = locationRepository.findById(locationId);
        if(result == null) {
            throw new NotFoundException("Location not found");
        }
        return result;
    }

    public Location getByName(final String name) {
        Location result = locationRepository.findByName(name);
        if(result == null) {
            throw new NotFoundException("Location not found");
        }
        return result;
    }

    public Location getDefault() {
        return getByName("Prague");
    }


}
