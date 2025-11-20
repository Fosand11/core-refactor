CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Extensión necesaria para UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabla USERS (renombrada de USER para evitar palabra reservada)
CREATE TABLE USERS (
                       user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       profile_picture VARCHAR(255),
                       role VARCHAR(50) NOT NULL DEFAULT 'user',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla PROPERTY_TYPES (normalización)
CREATE TABLE PROPERTY_TYPES (
                                property_type_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                type_name VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla LOCATIONS (normalización de ubicaciones)
CREATE TABLE LOCATIONS (
                           location_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           neighborhood VARCHAR(100) NOT NULL,
                           municipality VARCHAR(100) NOT NULL,
                           department VARCHAR(100) NOT NULL,
    -- Creamos un índice combinado para búsquedas
                           UNIQUE (neighborhood, municipality, department)
);

-- Tabla PUBLICATIONS (normalizada)
CREATE TABLE PUBLICATIONS (
                              publication_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              user_id UUID NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
                              property_type_id UUID NOT NULL REFERENCES PROPERTY_TYPES(property_type_id),
                              location_id UUID NOT NULL REFERENCES LOCATIONS(location_id),
                              property_address VARCHAR(200) NOT NULL,
                              longitude DECIMAL(10, 8),
                              latitude DECIMAL(10, 8),
                              property_size DECIMAL(10, 2) NOT NULL,
                              property_bedrooms INTEGER NOT NULL,
                              property_floors INTEGER NOT NULL,
                              property_parking INTEGER DEFAULT 0,
                              property_furnished BOOLEAN DEFAULT FALSE,
                              property_description TEXT,
                              property_price DECIMAL(12, 2) NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'pending', 'sold', 'rented', 'inactive')),
                              CONSTRAINT valid_coordinates CHECK
                                  (longitude BETWEEN -180 AND 180 AND latitude BETWEEN -90 AND 90)
);

-- Tabla PROPERTY_IMAGES (normalización de imágenes)
CREATE TABLE PROPERTY_IMAGES (
                                 image_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 publication_id UUID NOT NULL REFERENCES PUBLICATIONS(publication_id) ON DELETE CASCADE,
                                 image_url VARCHAR(255) NOT NULL,
                                 is_main BOOLEAN DEFAULT FALSE,
                                 upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla AVAILABLE_TIMES (normalización de horarios disponibles)
CREATE TABLE AVAILABLE_TIMES (
                                 time_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 publication_id UUID NOT NULL REFERENCES PUBLICATIONS(publication_id) ON DELETE CASCADE,
                                 day_of_week INTEGER NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
                                 start_time TIME NOT NULL,
                                 end_time TIME NOT NULL,
                                 CONSTRAINT valid_time_range CHECK (start_time < end_time)
);

-- Tabla REPORTS (normalizada)
CREATE TABLE REPORTS (
                         report_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         publication_id UUID NOT NULL REFERENCES PUBLICATIONS(publication_id),
                         reporter_user_id UUID NOT NULL REFERENCES USERS(user_id),
                         reason VARCHAR(100) NOT NULL,
                         description TEXT,
                         report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'reviewed', 'resolved', 'dismissed'))
);

-- Tabla FAVORITES (para que los usuarios puedan guardar propiedades favoritas)
CREATE TABLE FAVORITES (
                           user_id UUID NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
                           publication_id UUID NOT NULL REFERENCES PUBLICATIONS(publication_id) ON DELETE CASCADE,
                           saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (user_id, publication_id)
);

-- Tabla VISITS (para agendar visitas a las propiedades)
CREATE TABLE VISITS (
                        visit_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        publication_id UUID NOT NULL REFERENCES PUBLICATIONS(publication_id) ON DELETE CASCADE,
                        visitor_user_id UUID NOT NULL REFERENCES USERS(user_id),
                        visit_date DATE NOT NULL,
                        visit_time TIME NOT NULL,
                        status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'completed', 'cancelled')),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Índices para mejorar el rendimiento de búsquedas comunes
CREATE INDEX idx_publications_price ON PUBLICATIONS(property_price);
CREATE INDEX idx_publications_bedrooms ON PUBLICATIONS(property_bedrooms);
CREATE INDEX idx_publications_size ON PUBLICATIONS(property_size);
CREATE INDEX idx_publications_location ON PUBLICATIONS(location_id);
CREATE INDEX idx_publications_type ON PUBLICATIONS(property_type_id);
CREATE INDEX idx_publications_user ON PUBLICATIONS(user_id);
CREATE INDEX idx_publications_status ON PUBLICATIONS(status);
CREATE INDEX idx_reports_status ON REPORTS(status);
CREATE INDEX idx_visits_date ON VISITS(visit_date);

-- Índices GIN para búsquedas de texto completo en descripciones
CREATE INDEX idx_publication_description_gin ON PUBLICATIONS USING GIN (to_tsvector('spanish', property_description));

-- Función de trigger para actualizar automáticamente el campo updated_at
CREATE OR REPLACE FUNCTION update_modified_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';


-- Triggers para actualizar automáticamente updated_at
CREATE TRIGGER update_users_modtime
    BEFORE UPDATE ON USERS
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_publications_modtime
    BEFORE UPDATE ON PUBLICATIONS
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();
