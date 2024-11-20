# Romashka Co. Api

## Paths

### /api/v1/supplies/{id}

#### GET

- **Tags:** supply-controller
- **OperationId:** getById
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** [SupplyResponse](#supplyresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### PUT

- **Tags:** supply-controller
- **OperationId:** update
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Request Body:**
  - **Content:** `application/json`
    - **Schema:** [UpdateSupplyRequest](#updatesupplyrequest)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** [SupplyResponse](#supplyresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### DELETE

- **Tags:** supply-controller
- **OperationId:** deleteById
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** string
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

### /api/v1/sales/{id}

#### GET

- **Tags:** sale-controller
- **OperationId:** findById
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** [SaleResponse](#saleresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### PUT

- **Tags:** sale-controller
- **OperationId:** update_1
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Request Body:**
  - **Content:** `application/json`
    - **Schema:** [UpdateSaleRequest](#updatesalerequest)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** [SaleResponse](#saleresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### DELETE

- **Tags:** sale-controller
- **OperationId:** delete
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** string
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

### /api/v1/products/{id}

#### GET

- **Tags:** product-controller
- **OperationId:** getById_1
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** [ProductResponse](#productresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### PUT

- **Tags:** product-controller
- **OperationId:** update_2
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Request Body:**
  - **Content:** `application/json`
    - **Schema:** [UpdateProductRequest](#updateproductrequest)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** [ProductResponse](#productresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### DELETE

- **Tags:** product-controller
- **OperationId:** delete_1
- **Parameters:**
  - `id` (path, required, string, format: uuid)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** string
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

### /api/v1/supplies

#### GET

- **Tags:** supply-controller
- **OperationId:** getAll
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** array of [SupplyResponse](#supplyresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### POST

- **Tags:** supply-controller
- **OperationId:** create
- **Request Body:**
  - **Content:** `application/json`
    - **Schema:** [CreateSupplyRequest](#createsupplyrequest)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** string (format: uuid)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

### /api/v1/sales

#### GET

- **Tags:** sale-controller
- **OperationId:** findAll
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** array of [SaleResponse](#saleresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### POST

- **Tags:** sale-controller
- **OperationId:** create_1
- **Request Body:**
  - **Content:** `application/json`
    - **Schema:** [CreateSaleRequest](#createsalerequest)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** string (format: uuid)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

### /api/v1/products

#### GET

- **Tags:** product-controller
- **OperationId:** getAll_1
- **Parameters:**
  - `filter` (query, required, [ProductFilter](#productfilter))
  - `size` (query, optional, integer, format: int32, default: 20)
  - `sort` (query, optional, array of string, description: Sorting criteria in the format: property,(asc|desc). Default
    sort order is ascending. Multiple sort criteria are supported.)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** array of [ProductResponse](#productresponse)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

#### POST

- **Tags:** product-controller
- **OperationId:** create_2
- **Request Body:**
  - **Content:** `application/json`
    - **Schema:** [CreateProductRequest](#createproductrequest)
- **Responses:**
  - **200:** OK
    - **Content:** `*/*`
      - **Schema:** string (format: uuid)
  - **100-511:** Problem details
    - **Content:** `application/json`
      - **Schema:** [ProblemDetail](#problemdetail)

## Components

### Schemas

#### ProblemDetail

- **Type:** object
- **Properties:**
  - `type` (string, format: uri)
  - `title` (string)
  - `status` (integer, format: int32)
  - `detail` (string)
  - `instance` (string, format: uri)
  - `properties` (object, additionalProperties: object)

#### UpdateSupplyRequest

- **Type:** object
- **Required:**
  - `document`
- **Properties:**
  - `document` (string, maxLength: 255, minLength: 0)
  - `productId` (string, format: uuid)
  - `quantity` (integer, format: int32)

#### SupplyResponse

- **Type:** object
- **Properties:**
  - `id` (string, format: uuid)
  - `document` (string)
  - `productId` (string, format: uuid)
  - `quantity` (integer, format: int32)

#### UpdateSaleRequest

- **Type:** object
- **Required:**
  - `document`
- **Properties:**
  - `document` (string, maxLength: 255, minLength: 0)
  - `quantity` (integer, format: int32)
  - `productId` (string, format: uuid)

#### SaleResponse

- **Type:** object
- **Properties:**
  - `id` (string, format: uuid)
  - `document` (string)
  - `quantity` (integer, format: int32)
  - `productId` (string, format: uuid)
  - `totalPrice` (integer, format: int64)

#### UpdateProductRequest

- **Type:** object
- **Required:**
  - `description`
  - `name`
- **Properties:**
  - `name` (string, maxLength: 255, minLength: 0)
  - `description` (string, maxLength: 4096, minLength: 0)
  - `price` (integer, format: int32)
  - `isAvailable` (boolean)

#### ProductResponse

- **Type:** object
- **Properties:**
  - `id` (string, format: uuid)
  - `name` (string)
  - `description` (string)
  - `price` (integer, format: int32)
  - `quantity` (integer, format: int32)

#### CreateSupplyRequest

- **Type:** object
- **Required:**
  - `document`
- **Properties:**
  - `document` (string, maxLength: 255, minLength: 0)
  - `productId` (string, format: uuid)
  - `quantity` (integer, format: int32)

#### CreateSaleRequest

- **Type:** object
- **Required:**
  - `document`
- **Properties:**
  - `document` (string, maxLength: 255, minLength: 0)
  - `quantity` (integer, format: int32)
  - `productId` (string, format: uuid)

#### CreateProductRequest

- **Type:** object
- **Required:**
  - `description`
  - `name`
- **Properties:**
  - `name` (string, maxLength: 255, minLength: 0)
  - `description` (string, maxLength: 4096, minLength: 0)
  - `price` (integer, format: int32)
  - `isAvailable` (boolean)

#### ProductFilter

- **Type:** object
- **Properties:**
  - `name` (string, maxLength: 255, minLength: 0)
  - `price` (integer, format: int32)
  - `isPriceBottom` (boolean)
  - `isAvailable` (boolean)
