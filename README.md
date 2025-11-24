
# InmoMarket – Backend (API REST)

Este módulo corresponde al backend de la plataforma InmoMarket, una aplicación diseñada para gestionar usuarios, publicaciones, reportes y autenticación mediante JWT.  
La API está desarrollada con **Spring Boot**, siguiendo buenas prácticas de organización, seguridad y persistencia en base de datos relacional.

Su función principal es proveer todos los servicios que utiliza el frontend, asegurando que los datos se almacenen, procesen y validen correctamente.

## Descripción del Proyecto

El backend proporciona toda la lógica de negocio y comunicación con PostgreSQL necesaria para que la plataforma funcione correctamente.  
Incluye:

- Registro, inicio de sesión y autenticación mediante JWT.  
- Creación, edición y listado de publicaciones.  
- Sistema de reportes generados por los usuarios.  
- Gestión de usuarios y publicaciones por parte del administrador.  
- Integración con Cloudinary para almacenar imágenes subidas desde el frontend.  
- Validación y seguridad mediante Spring Security.

Está diseñado para compradores, vendedores y administradores dentro del sistema.

## ¿Qué problema resuelve?

En entornos reales, la compraventa de propiedades suele manejarse por redes sociales o plataformas poco estructuradas, lo que genera:

- Información dispersa o poco confiable.  
- Perfiles falsos o imposibles de verificar.  
- Ausencia de un sistema de reportes.  
- Ningún control administrativo sobre publicaciones.

El backend de InmoMarket resuelve este problema proporcionando:

- Una API ordenada y segura.  
- Control de usuarios, roles y permisos.  
- Manejo centralizado de datos inmobiliarios.  
- Protección de rutas y recursos.  
- Moderación mediante reportes.  

# Funcionalidades Principales

## Autenticación y usuarios
- Registro de usuarios.  
- Inicio de sesión.  
- Token JWT para proteger rutas.  
- Actualización de información personal.  
- Visualización de perfiles.  

## Publicaciones
- Crear nuevas publicaciones.  
- Editar publicaciones propias.  
- Ver todas las publicaciones o filtrarlas.  
- Cargar imágenes mediante Cloudinary.  

## Sistema de reportes
- Reportar publicaciones que infrinjan reglas.  
- Administradores pueden revisar y cambiar el estado del reporte.  

## Roles
- **Usuario**: crea y gestiona sus publicaciones.  
- **Administrador**: supervisa usuarios, publicaciones y reportes.

# Requisitos Previos

## Software Necesario

- **JDK 22** o superior  
  Verificar instalación:  
  ```
  java -version
  ```

- **PostgreSQL 14+**  
  Verificar instalación:  
  ```
  psql --version
  ```

- **Gradle Wrapper**  
  No requiere instalación externa.

## Dependencias principales

- Spring Boot 3.5.0  
- Spring Web  
- Spring Data JPA  
- Spring Security  
- Spring Validation  
- PostgreSQL  
- JWT  
- Cloudinary SDK  
- Lombok  

# Instalación Paso a Paso

### 1. Clonar el repositorio

```
git clone <url-del-repositorio>
cd inmomarket-backend
```

### 2. Crear la base de datos

```
psql -U postgres
CREATE DATABASE inmomarket;
\q
```

### 3. Crear archivo de variables de entorno

```
cp .env.example .env
```

### 4. Editar valores reales en `.env`

- Credenciales de PostgreSQL  
- Clave JWT  
- Configuración de Cloudinary  

### 5. Construir el proyecto

```
./gradlew build
```

# Ejecución

### Modo desarrollo

Linux/macOS:
```
./gradlew bootRun
```

Windows:
```
gradlew.bat bootRun
```

### Modo producción (JAR)

```
./gradlew clean build
java -jar build/libs/InmoMarketBackEnd-0.0.1-SNAPSHOT.jar
```

La API estará disponible en:
```
http://localhost:8080
```

# Variables de Entorno (`.env.example`)

```
DATABASE_URL=jdbc:postgresql://localhost:5432/inmomarket
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=admin

JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
JWT_EXPIRATION_MS=86400000

CLOUDINARY_CLOUD_NAME=dstkr4nut
CLOUDINARY_API_KEY=528173669226754
CLOUDINARY_API_SECRET=y0E3oQFU1pEB8eL0dTmOjROeBJ8
```

## Explicación

- `DATABASE_URL`: dirección de PostgreSQL  
- `DATABASE_USERNAME` / `DATABASE_PASSWORD`: credenciales  
- `JWT_SECRET`: clave para firmar tokens  
- `JWT_EXPIRATION_MS`: tiempo de vigencia del token  
- `Cloudinary_*`: credenciales para almacenar imágenes  

# Endpoints Principales

## Autenticación
- POST `/api/auth/signup`  
- POST `/api/auth/signin`

## Publicaciones
- GET `/api/publications`  
- GET `/api/publications/all`  
- POST `/api/publications/create`  

## Usuario
- PUT `/api/user/profile`  

# Estructura del Proyecto

```
src/
├── main/
│   ├── java/org/milianz/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   └── resources/
│       └── application.properties
└── test/
```

# Solución de Problemas Comunes

### Puerto 8080 ocupado
```
lsof -i :8080   # Linux/macOS
netstat -ano | findstr :8080   # Windows
```

### Error de conexión a PostgreSQL
- Confirmar que PostgreSQL esté corriendo.  
- Verificar credenciales en `.env`.  

### Error "JWT Secret must be at least 256 bits"
Generar una nueva clave:
```
openssl rand -base64 32
```

# Contribución

1. Fork del repositorio  
2. Crear rama nueva  
3. Commits  
4. Push  
5. Pull Request  
