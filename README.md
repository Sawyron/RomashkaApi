# Romashka Co. API

## Products [/api/v1/products]

### GET /{id}

#### Responses

##### Status 200

###### Body

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "string",
  "description": "string",
  "price": 0,
  "isAvailable": true
}
```

##### Status 404

###### Body

```json
{
  "type": "about:blank",
  "title": "404 NOT_FOUND",
  "status": 404,
  "detail": "product with id 3fa85f64-5717-4562-b3fc-2c963f66afa6 is not found",
  "instance": "/api/v1/products/3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

### GET /

#### Query parameters

- name: type - string(256), optional
- price type - integer, optional
- isPriceBottom, type - boolean, optional
- isAvailable: type - boolean, optional
- size: type - integer, default - 20
- sort - type string[], format - {filedName},asc|desc

```json
{
  "name": "string",
  "price": 0,
  "isPriceBottom": true,
  "isAvailable": true
}
```

### POST /

#### Body

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "string",
  "description": "string",
  "price": 0,
  "isAvailable": true
}
````

#### Responses

##### Status 201

"3fa85f64-5717-4562-b3fc-2c963f66afa6"

##### Status 400

###### Body

```json
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "instance": "/api/v1/products/",
  "errors": {
    "price": "error description"
  }
}
```

### PUT /{id}

#### Body

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "string",
  "description": "string",
  "price": 0,
  "isAvailable": true
}
```

#### Responses

##### Body

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "string",
  "description": "string",
  "price": 0,
  "isAvailable": true
}
```

##### Status 204

##### Status 400, 404

###### Body

```json
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "instance": "/api/v1/products/3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "errors": {
    "price": "error description"
  }
}
```

### DELETE /{id}

#### Body

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "string",
  "description": "string",
  "price": 0,
  "isAvailable": true
}
```

#### Responses

##### Status 204

##### Status 400, 404

##### Body

```json
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "instance": "/api/v1/products/3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "errors": {
    "price": "error description"
  }
}
```

### Schemas

- Product
  - id: string(uuid)
  - name: string(255)
  - description: string(4096)
  - price: integer, positive, default: 0
  - isAvailable: boolean, default: false