package org.milianz.inmomarketbackend.Services;

import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Domain.Entities.User;
import org.milianz.inmomarketbackend.Domain.Repositories.iUserRepository;
import org.milianz.inmomarketbackend.Payload.Request.UpdateProfileRequest;
import org.milianz.inmomarketbackend.Payload.Response.UserProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

/**
 * Servicio de gestión de perfiles de usuario.
 *
 * Contiene la lógica de negocio para consultar y actualizar
 * perfiles de usuario, incluyendo cambio de contraseña seguro
 * y manejo de fotos de perfil con Cloudinary.
 *
 * @author InmoMarket Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final iUserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtiene el usuario autenticado actualmente desde el contexto de Spring Security.
     *
     * Este método garantiza que siempre se trabaje con el usuario del JWT token,
     * evitando que se puedan modificar perfiles de otros usuarios.
     *
     * @return Usuario autenticado
     * @throws UsernameNotFoundException si el usuario del token no existe en la BD
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * Obtiene el perfil completo del usuario autenticado.
     *
     * @return UserProfileResponse con datos del perfil (sin contraseña)
     */
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return mapToUserProfileResponse(user);
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     *
     * Realiza las siguientes operaciones:
     * 1. Valida que el email sea único (si se cambió)
     * 2. Actualiza campos básicos (nombre, email, teléfono)
     * 3. Cambia la contraseña de forma segura (si se proporcionó)
     * 4. Gestiona la foto de perfil (subir/eliminar)
     * 5. Persiste cambios en la base de datos
     *
     * Validaciones de seguridad:
     * - Solo el usuario autenticado puede modificar su perfil
     * - La contraseña actual debe ser correcta
     * - La nueva contraseña debe ser diferente
     * - Email debe ser único en el sistema
     * - Imagen debe cumplir requisitos (tipo, tamaño)
     *
     * @param request DTO con los datos a actualizar
     * @return UserProfileResponse con datos actualizados
     * @throws RuntimeException si:
     *         - El email ya está en uso por otro usuario
     *         - La contraseña actual es incorrecta
     *         - La nueva contraseña es igual a la actual
     *         - Faltan campos requeridos para cambio de contraseña
     *         - La imagen no cumple validaciones
     * @throws IOException si hay error al subir/eliminar imagen de Cloudinary
     */
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) throws IOException {
        User currentUser = getCurrentUser();

        // ========== VALIDACIÓN DE EMAIL ÚNICO ==========
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUser.getId())) {
            throw new RuntimeException("El email ya está en uso por otro usuario");
        }

        // ========== ACTUALIZACIÓN DE CAMPOS BÁSICOS ==========
        currentUser.setName(request.getName());
        currentUser.setEmail(request.getEmail());
        currentUser.setPhoneNumber(request.getPhoneNumber());

        // ========== CAMBIO SEGURO DE CONTRASEÑA ==========
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            // Validar que la contraseña actual sea correcta
            if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
                throw new RuntimeException("La contraseña actual es incorrecta");
            }

            // Validar que la nueva contraseña sea diferente
            if (request.getCurrentPassword().equals(request.getNewPassword())) {
                throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
            }

            // Encriptar con BCrypt y guardar
            currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));

        } else if (request.getCurrentPassword() != null || request.getNewPassword() != null) {
            // Si se proporcionó solo uno de los dos campos
            throw new RuntimeException("Para cambiar la contraseña, debe proporcionar tanto la contraseña actual como la nueva");
        }

        // ========== GESTIÓN DE FOTO DE PERFIL ==========
        if (Boolean.TRUE.equals(request.getRemoveProfilePicture())) {
            // Eliminar foto de perfil actual
            if (currentUser.getProfilePicture() != null) {
                cloudinaryService.deleteImage(currentUser.getProfilePicture());
                currentUser.setProfilePicture(null);
            }
        } else if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
            // Subir nueva foto de perfil
            validateProfilePicture(request.getProfilePicture());

            // Eliminar foto anterior si existe
            if (currentUser.getProfilePicture() != null) {
                cloudinaryService.deleteImage(currentUser.getProfilePicture());
            }

            // Subir nueva imagen a Cloudinary
            String imageUrl = cloudinaryService.uploadImage(
                    request.getProfilePicture(),
                    "profile_pictures"
            );
            currentUser.setProfilePicture(imageUrl);
        }

        // ========== PERSISTENCIA ==========
        User updatedUser = userRepository.save(currentUser);

        return mapToUserProfileResponse(updatedUser);
    }

    /**
     * Valida que la imagen de perfil cumpla con los requisitos.
     *
     * Requisitos:
     * - No puede estar vacía
     * - Debe ser una imagen (content-type image/*)
     * - No puede exceder 5MB de tamaño
     *
     * @param file Archivo a validar
     * @throws RuntimeException si no cumple algún requisito
     */
    private void validateProfilePicture(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo de imagen no puede estar vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("El archivo debe ser una imagen");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("El tamaño de la imagen no puede exceder 5MB");
        }
    }

    /**
     * Mapea una entidad User a UserProfileResponse.
     *
     * IMPORTANTE: No incluye la contraseña por seguridad.
     *
     * @param user Entidad de usuario
     * @return DTO con datos del perfil
     */
    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
