[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/ffem3vg3)

# Inmomarket

Base URLs: https://web-production-06592e.up.railway.app/

> [!NOTE]
> Swagger de nuestra API: https://web-production-06592e.up.railway.app/swagger-ui/index.html#/

# Authentication

- HTTP Authentication, scheme: bearer

# Default

## POST SingIn v2

POST /localhost:8080/api/auth/signup

> Body Parameters

```json
{
  "name": "Juan Pérez",
  "email": "juan.perez@email.com",
  "phoneNumber": "+50312345678",
  "password": "miPassword123"
}
```

### Params

| Name          | Location | Type   | Required | Description |
| ------------- | -------- | ------ | -------- | ----------- |
| body          | body     | object | no       | none        |
| » name        | body     | string | yes      | none        |
| » email       | body     | string | yes      | none        |
| » phoneNumber | body     | string | yes      | none        |
| » password    | body     | string | yes      | none        |

> Response Examples

> 200 Response

```json
{
  "message": "User registered successfully!"
}
```

### Responses

| HTTP Status Code | Meaning                                                 | Description | Data schema |
| ---------------- | ------------------------------------------------------- | ----------- | ----------- |
| 200              | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1) | none        | Inline      |

### Responses Data Schema

HTTP Status Code **200**

| Name      | Type   | Required | Restrictions | Title | description |
| --------- | ------ | -------- | ------------ | ----- | ----------- |
| » message | string | true     | none         |       | none        |

## GET Publications filters

GET /api/publications

### Params

| Name          | Location | Type   | Required | Description |
| ------------- | -------- | ------ | -------- | ----------- |
| department    | query    | string | no       | none        |
| typeName      | query    | string | no       | none        |
| minPrice      | query    | string | no       | none        |
| maxPrice      | query    | string | no       | none        |
| minSize       | query    | string | no       | none        |
| maxSize       | query    | string | no       | none        |
| bedrooms      | query    | string | no       | none        |
| floors        | query    | string | no       | none        |
| parking       | query    | string | no       | none        |
| furnished     | query    | string | no       | none        |
| Authorization | header   | string | no       | none        |

> Response Examples

> 200 Response

```json
{}
```

### Responses

| HTTP Status Code | Meaning                                                 | Description | Data schema |
| ---------------- | ------------------------------------------------------- | ----------- | ----------- |
| 200              | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1) | none        | Inline      |

### Responses Data Schema

## GET All Publications

GET /api/publications/All

### Params

| Name          | Location | Type   | Required | Description |
| ------------- | -------- | ------ | -------- | ----------- |
| Authorization | header   | string | no       | none        |

> Response Examples

> 200 Response

```json
[
  {
    "propertyAddress": "string",
    "typeName": "string",
    "neighborhood": "string",
    "municipality": "string",
    "department": "string",
    "longitude": 0,
    "latitude": 0,
    "propertySize": 0,
    "propertyBedrooms": 0,
    "propertyFloors": 0,
    "propertyParking": 0,
    "propertyFurnished": true,
    "propertyImageUrl": "string",
    "userName": "string",
    "propertyDescription": "string",
    "propertyPrice": 0
  }
]
```

### Responses

| HTTP Status Code | Meaning                                                 | Description | Data schema |
| ---------------- | ------------------------------------------------------- | ----------- | ----------- |
| 200              | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1) | none        | Inline      |

### Responses Data Schema

HTTP Status Code **200**

| Name                  | Type    | Required | Restrictions | Title | description |
| --------------------- | ------- | -------- | ------------ | ----- | ----------- |
| » propertyAddress     | string  | true     | none         |       | none        |
| » typeName            | string  | true     | none         |       | none        |
| » neighborhood        | string  | true     | none         |       | none        |
| » municipality        | string  | true     | none         |       | none        |
| » department          | string  | true     | none         |       | none        |
| » longitude           | number  | true     | none         |       | none        |
| » latitude            | number  | true     | none         |       | none        |
| » propertySize        | number  | true     | none         |       | none        |
| » propertyBedrooms    | integer | true     | none         |       | none        |
| » propertyFloors      | integer | true     | none         |       | none        |
| » propertyParking     | integer | true     | none         |       | none        |
| » propertyFurnished   | boolean | true     | none         |       | none        |
| » propertyImageUrl    | string  | true     | none         |       | none        |
| » userName            | string  | true     | none         |       | none        |
| » propertyDescription | string  | true     | none         |       | none        |
| » propertyPrice       | integer | true     | none         |       | none        |

## POST CreatePublication

POST /api/publications/create

> Body Parameters

```yaml
propertyAddress: "Calle 123 #45-67"
typeName: Apartamento
neighborhood: El Poblado
municipality: Medellín
department: Antioquia
longitude: -75.5678
latitude: 6.2345
propertySize: 85
propertyBedrooms: 2
propertyFloors: 5
propertyParking: 1
propertyFurnished: 1
propertyPrice: 850000000
propertyDescription: Amplio apartamento en excelente ubicación, con acabados de
  lujo y vista panorámica.
files: ""
"availableTimes[0].startTime": 13:00
"availableTimes[0].endTime": 15:00
"availableTimes[0].dayOfWeek": "2"
```

### Params

| Name                          | Location | Type           | Required | Description |
| ----------------------------- | -------- | -------------- | -------- | ----------- |
| body                          | body     | object         | no       | none        |
| » propertyAddress             | body     | string         | yes      | none        |
| » typeName                    | body     | string         | yes      | none        |
| » neighborhood                | body     | string         | yes      | none        |
| » municipality                | body     | string         | yes      | none        |
| » department                  | body     | string         | yes      | none        |
| » longitude                   | body     | number         | yes      | none        |
| » latitude                    | body     | number         | yes      | none        |
| » propertySize                | body     | number         | yes      | none        |
| » propertyBedrooms            | body     | number         | yes      | none        |
| » propertyFloors              | body     | number         | yes      | none        |
| » propertyParking             | body     | number         | yes      | none        |
| » propertyFurnished           | body     | number         | yes      | none        |
| » propertyPrice               | body     | number         | yes      | none        |
| » propertyDescription         | body     | string         | yes      | none        |
| » files                       | body     | string(binary) | yes      | none        |
| » availableTimes[0].startTime | body     | string         | yes      | none        |
| » availableTimes[0].endTime   | body     | string         | yes      | none        |
| » availableTimes[0].dayOfWeek | body     | string         | yes      | none        |

> Response Examples

> 200 Response

```json
{}
```

### Responses

| HTTP Status Code | Meaning                                                 | Description | Data schema |
| ---------------- | ------------------------------------------------------- | ----------- | ----------- |
| 200              | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1) | none        | Inline      |

### Responses Data Schema

## PUT UpdateUser v2

PUT /localhost:8080/api/user/profile

> Body Parameters

```yaml
name: Josue Zelada
email: josuezelada412@gmail.com
phoneNumber: "+50361239659"
profilePicture: ""
removeProfilePicture: "false"
```

### Params

| Name                   | Location | Type           | Required | Description |
| ---------------------- | -------- | -------------- | -------- | ----------- |
| body                   | body     | object         | no       | none        |
| » name                 | body     | string         | yes      | none        |
| » email                | body     | string         | yes      | none        |
| » phoneNumber          | body     | string         | yes      | none        |
| » profilePicture       | body     | string(binary) | yes      | none        |
| » removeProfilePicture | body     | boolean        | yes      | none        |

> Response Examples

> 200 Response

```json
{
  "id": "fcd3ac07-d8fc-47e2-801a-fcb279bb6381",
  "name": "Josue Zelada",
  "email": "josuezelada412@gmail.com",
  "phoneNumber": "+50361239659",
  "profilePicture": "http://res.cloudinary.com/dx4mlsaxx/image/upload/v1749842123/profile_pictures/tnir8bvdutczhhtjb9nz.jpg",
  "role": "USER",
  "createdAt": "2025-06-13T13:11:30.161717",
  "updatedAt": "2025-06-13T17:31:03.336895"
}
```

### Responses

| HTTP Status Code | Meaning                                                 | Description | Data schema |
| ---------------- | ------------------------------------------------------- | ----------- | ----------- |
| 200              | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1) | none        | Inline      |

### Responses Data Schema

HTTP Status Code **200**

| Name             | Type   | Required | Restrictions | Title | description |
| ---------------- | ------ | -------- | ------------ | ----- | ----------- |
| » id             | string | true     | none         |       | none        |
| » name           | string | true     | none         |       | none        |
| » email          | string | true     | none         |       | none        |
| » phoneNumber    | string | true     | none         |       | none        |
| » profilePicture | string | true     | none         |       | none        |
| » role           | string | true     | none         |       | none        |
| » createdAt      | string | true     | none         |       | none        |
| » updatedAt      | string | true     | none         |       | none        |

# Data Schema
