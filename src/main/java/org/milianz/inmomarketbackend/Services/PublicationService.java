package org.milianz.inmomarketbackend.Services;

import org.milianz.inmomarketbackend.Domain.Entities.*;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationSaveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationUpdateDTO;
import org.milianz.inmomarketbackend.Domain.Repositories.*;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.milianz.inmomarketbackend.Utils.PublicationsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PublicationService {

    @Autowired
    private iPublicationRepository publicationRepository;
    @Autowired
    private iUserRepository userRepository;
    @Autowired
    private PropertyTypeService propertyTypeService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private iPropertyTypeRepository propertyTypeRepository;
    @Autowired
    private iLocationRepository locationRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private AvailableTimeService availableTimeService;
    @Autowired
    private iFavoriteRepository favoriteRepository;

    public ResponseEntity<?> createPublication(@RequestBody PublicationSaveDTO publicationSaveDTO, String userName, MultipartFile[] files) {
        try {
            User user = userRepository.findByEmail(userName)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + userName));

            Publication publication = new Publication();
            publication.setUser(user);
            publication.setPropertyType(propertyTypeService.createPropertyType(publicationSaveDTO));
            publication.setLocation(locationService.createLocation(publicationSaveDTO));
            publication.setPropertyAddress(publicationSaveDTO.getPropertyAddress());
            publication.setPropertyTitle(publicationSaveDTO.getPropertyTitle());
            publication.setLongitude(publicationSaveDTO.getLongitude());
            publication.setLatitude(publicationSaveDTO.getLatitude());
            publication.setPropertySize(publicationSaveDTO.getPropertySize());
            publication.setPropertyBedrooms(publicationSaveDTO.getPropertyBedrooms());
            publication.setPropertyFloors(publicationSaveDTO.getPropertyFloors());
            publication.setPropertyParking(publicationSaveDTO.getPropertyParking());
            publication.setPropertyFurnished(publicationSaveDTO.getPropertyFurnished());
            publication.setPropertyDescription(publicationSaveDTO.getPropertyDescription());
            publication.setPropertyPrice(publicationSaveDTO.getPropertyPrice());
            publication.setCreatedAt(LocalDateTime.now());
            publication.setUpdatedAt(LocalDateTime.now());
            publication.setStatus(Publication.PublicationStatus.ACTIVE);

            publicationRepository.save(publication);

            List<PropertyImage> propertyImage = cloudinaryService.uploadImage(files, publication);
            publication.setPropertyImages(propertyImage);

            List<AvailableTime> availableTimes = availableTimeService.createAvailableTime(publicationSaveDTO, publication);
            publication.setAvailableTimes(availableTimes);

            publicationRepository.save(publication);

            PublicationsConstructor constructor = new PublicationsConstructor();

            return ResponseEntity.ok(constructor.PublicationUnique(publication));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating publication: " + e.getMessage());
        }
    }

    public List<PublicationDefaultDTO> getAllPublications() {
        List<Publication> publications = publicationRepository.findByStatus(Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByDepartment(String department) {
        List<Publication> publications = publicationRepository.findByLocation_DepartmentAndStatus(
                department, Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Publication> publications = publicationRepository.findByPropertyPriceBetweenAndStatus(
                minPrice, maxPrice, Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByType(String typeName) {
        List<Publication> publications = publicationRepository.findByPropertyType_TypeNameAndStatus(
                typeName, Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsBySize(BigDecimal minSize, BigDecimal maxSize) {
        List<Publication> publications = publicationRepository.findByPropertySizeBetweenAndStatus(
                minSize, maxSize, Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByBedrooms(Integer bedrooms) {
        List<Publication> publications = publicationRepository.findByPropertyBedroomsAndStatus(
                bedrooms, Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByFloors(Integer floors) {
        List<Publication> publications = publicationRepository.findByPropertyFloorsAndStatus(
                floors, Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByParking(Integer parking) {
        List<Publication> publications = publicationRepository.findByPropertyParkingAndStatus(
                parking, Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByFurnished(Boolean furnished) {
        List<Publication> publications = publicationRepository.findByPropertyFurnishedAndStatus(
                furnished, Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getAllActivePublications() {
        List<Publication> publications = publicationRepository.findByStatus(Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getAllPublicationsbyUserId(UUID userId) {
        List<Publication> publications = publicationRepository.findByUser_Id(userId);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public PublicationDefaultDTO getPublicationById(UUID publicationId) {
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationUnique(publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + publicationId)));
    }

    public List<PublicationDefaultDTO> getLastPublications() {
        List<Publication> publications = publicationRepository.findTop10ByStatusOrderByCreatedAtDesc(
                Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getTop10MostPopularPublications() {
        List<Favorite> favorites = favoriteRepository.findTop10ByOrderByPublicationIdDesc();
        List<Publication> publications = favorites.stream()
                .map(favorite -> favorite.getPublication())
                .filter(publication -> publication.getStatus() == Publication.PublicationStatus.ACTIVE)
                .toList();
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    // Actualiza una publicación existente. Solo el propietario puede editar sus publicaciones.
    // Los campos que no se envíen en el DTO permanecen sin cambios.
    public ResponseEntity<?> updatePublication(UUID publicationId, PublicationUpdateDTO publicationUpdateDTO, String userName, MultipartFile[] files) {
        try {
            // Obtener el usuario actual
            User currentUser = userRepository.findByEmail(userName)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + userName));

            // Buscar la publicación
            Publication publication = publicationRepository.findById(publicationId)
                    .orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + publicationId));

            // Verificar que el usuario sea el propietario de la publicación
            if (!publication.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403)
                        .body(new MessageResponse("No tienes permisos para editar esta publicación"));
            }

            // Actualizar solo los campos que se envían en la petición
            if (publicationUpdateDTO.getPropertyAddress() != null) {
                publication.setPropertyAddress(publicationUpdateDTO.getPropertyAddress());
            }
            if (publicationUpdateDTO.getPropertyTitle() != null) {
                publication.setPropertyTitle(publicationUpdateDTO.getPropertyTitle());
            }
            if (publicationUpdateDTO.getLongitude() != null) {
                publication.setLongitude(publicationUpdateDTO.getLongitude());
            }
            if (publicationUpdateDTO.getLatitude() != null) {
                publication.setLatitude(publicationUpdateDTO.getLatitude());
            }
            if (publicationUpdateDTO.getPropertySize() != null) {
                publication.setPropertySize(publicationUpdateDTO.getPropertySize());
            }
            if (publicationUpdateDTO.getPropertyBedrooms() != null) {
                publication.setPropertyBedrooms(publicationUpdateDTO.getPropertyBedrooms());
            }
            if (publicationUpdateDTO.getPropertyFloors() != null) {
                publication.setPropertyFloors(publicationUpdateDTO.getPropertyFloors());
            }
            if (publicationUpdateDTO.getPropertyParking() != null) {
                publication.setPropertyParking(publicationUpdateDTO.getPropertyParking());
            }
            if (publicationUpdateDTO.getPropertyFurnished() != null) {
                publication.setPropertyFurnished(publicationUpdateDTO.getPropertyFurnished());
            }
            if (publicationUpdateDTO.getPropertyDescription() != null) {
                publication.setPropertyDescription(publicationUpdateDTO.getPropertyDescription());
            }
            if (publicationUpdateDTO.getPropertyPrice() != null) {
                publication.setPropertyPrice(publicationUpdateDTO.getPropertyPrice());
            }

            // Actualizar tipo de propiedad si se especifica
            if (publicationUpdateDTO.getTypeName() != null) {
                PropertyType propertyType = propertyTypeRepository.findByTypeName(publicationUpdateDTO.getTypeName())
                        .orElseGet(() -> propertyTypeService.createPropertyType(publicationUpdateDTO));
                publication.setPropertyType(propertyType);
            }

            // Actualizar ubicación si se envía alguno de sus campos
            if (publicationUpdateDTO.getDepartment() != null ||
                publicationUpdateDTO.getMunicipality() != null ||
                publicationUpdateDTO.getNeighborhood() != null) {
                Location location = locationService.createLocation(publicationUpdateDTO);
                publication.setLocation(location);
            }

            // Agregar nuevas imágenes si se envían archivos
            if (files != null && files.length > 0) {
                List<PropertyImage> propertyImages = cloudinaryService.uploadImage(files, publication);
                publication.getPropertyImages().addAll(propertyImages);
            }

            // Actualizar horarios disponibles. Se reemplazan todos los existentes.
            if (publicationUpdateDTO.getAvailableTimes() != null && !publicationUpdateDTO.getAvailableTimes().isEmpty()) {
                publication.getAvailableTimes().clear();
                List<AvailableTime> availableTimes = availableTimeService.createAvailableTime(publicationUpdateDTO, publication);
                publication.setAvailableTimes(availableTimes);
            }

            publicationRepository.save(publication);

            PublicationsConstructor constructor = new PublicationsConstructor();
            return ResponseEntity.ok(constructor.PublicationUnique(publication));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error actualizando publicación: " + e.getMessage()));
        }
    }
}