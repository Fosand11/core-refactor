package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Datos para crear una nueva publicación de propiedad")
public class PublicationSaveDTO {
    @Schema(description = "Dirección completa de la propiedad", example = "Calle 123 #45-67")
    private String propertyAddress;

    @Schema(description = "Tipo de propiedad", example = "Casa")
    private String typeName;

    @Schema(description = "Barrio o vecindario", example = "El Poblado")
    private String neighborhood;

    @Schema(description = "Municipio", example = "Medellín")
    private String municipality;

    @Schema(description = "Departamento", example = "Antioquia")
    private String department;

    @Schema(description = "Título de la publicación", example = "Casa amplia en zona exclusiva")
    private String propertyTitle;

    @Schema(description = "Longitud de la ubicación", example = "-75.576")
    private BigDecimal longitude;

    @Schema(description = "Latitud de la ubicación", example = "6.244")
    private BigDecimal latitude;

    @Schema(description = "Tamaño de la propiedad en m²", example = "120.5")
    private BigDecimal propertySize;

    @Schema(description = "Número de habitaciones", example = "3")
    private Integer propertyBedrooms;

    @Schema(description = "Número de pisos", example = "2")
    private Integer propertyFloors;

    @Schema(description = "Número de parqueaderos", example = "1")
    private Integer propertyParking;

    @Schema(description = "Indica si la propiedad está amoblada", example = "true")
    private Boolean propertyFurnished;

    @Schema(description = "Descripción detallada de la propiedad", example = "Hermosa casa con amplio jardín y excelente iluminación")
    private String PropertyDescription;

    @Schema(description = "Precio de la propiedad", example = "450000000")
    private BigDecimal PropertyPrice;

    @Schema(description = "Horarios disponibles para visitas")
    private List<AvailableTimesSaveDTO> availableTimes;
}
