# Implementation Plan: Franchise API

## Overview

This plan implements a reactive RESTful Franchise Management API using Spring WebFlux, Clean Architecture (Bancolombia scaffold), MongoDB, Resilience4j circuit breaker, Docker, and Terraform. Tasks are ordered to build from domain core outward to infrastructure, following the dependency rule.

## Tasks

- [ ] 1. Project scaffold and base configuration
  - [x] 1.1 Generate project using Bancolombia Clean Architecture plugin
    - Run `gradle cleanArchitecture --package=co.com.franchise --type=reactive --name=franchise-api --coverage=jacoco --lombok=false`
    - Generate entry point: `gradle generateEntryPoint --type=webflux`
    - Generate driven adapter: `gradle generateDrivenAdapter --type=mongo-reactive`
    - Verify multi-module Gradle structure (applications/app-service, domain/model, domain/usecase, infrastructure/entry-points/api-rest, infrastructure/driven-adapters/mongo-repository)
    - _Requirements: 13.1, 13.2_

  - [ ] 1.2 Configure application dependencies and properties
    - Add dependencies: spring-boot-starter-webflux, spring-boot-starter-data-mongodb-reactive, resilience4j-spring-boot3, resilience4j-reactor, springdoc-openapi-starter-webflux-ui, jqwik (test), reactor-test (test), de.flapdoodle.embed.mongo (test)
    - Configure application.yml with MongoDB connection, Resilience4j circuit breaker settings (failureRateThreshold: 50, waitDurationInOpenState: 30s, slidingWindowSize: 10, permittedNumberOfCallsInHalfOpenState: 3)
    - Configure OpenAPI/Swagger endpoint path
    - _Requirements: 14.1, 14.2, 15.1, 12.1_

- [ ] 2. Domain model — Entities and exceptions
  - [ ] 2.1 Implement domain entities (Franchise, Branch, Product)
    - Create `Franchise` entity with factory method `create(String name)` and `updateName(String newName)` with blank-name validation
    - Create `Branch` entity with factory method `create(String name, String franchiseId)` and `updateName(String newName)` with blank-name validation
    - Create `Product` entity with factory method `create(String name, int stock, String branchId)`, `updateStock(int newStock)`, and `updateName(String newName)` with blank-name and negative-stock validation
    - All entities include getters; no framework dependencies
    - _Requirements: 1.2, 1.3, 2.3, 3.3, 3.4, 5.2, 7.3, 8.3, 9.3_

  - [ ]* 2.2 Write property test: Blank name rejection (Property 1)
    - **Property 1: Blank name rejection**
    - Generate whitespace-only strings (empty, spaces, tabs, newlines) and verify `Franchise.create()`, `Branch.create()`, `Product.create()`, and all `updateName()` methods throw `InvalidInputException`
    - Use jqwik `@Property` with minimum 100 tries
    - **Validates: Requirements 1.2, 2.3, 3.3, 7.3, 8.3, 9.3**

  - [ ]* 2.3 Write property test: Valid name accepted on creation (Property 2)
    - **Property 2: Valid name accepted on creation**
    - Generate arbitrary non-blank strings and verify `Franchise.create()`, `Branch.create()`, `Product.create()` succeed, return trimmed name, and have non-null id placeholder
    - Use jqwik `@Property` with minimum 100 tries
    - **Validates: Requirements 1.1, 2.1, 3.1**

  - [ ]* 2.4 Write property test: Negative stock rejection (Property 4)
    - **Property 4: Negative stock rejection**
    - Generate negative integers (Integer.MIN_VALUE to -1) and verify `Product.create()` and `Product.updateStock()` throw `InvalidInputException`
    - Use jqwik `@Property` with minimum 100 tries
    - **Validates: Requirements 3.4, 5.2**

  - [ ] 2.5 Implement domain exceptions
    - Create `InvalidInputException` (for validation errors, maps to 400)
    - Create `EntityNotFoundException` (for not-found cases, maps to 404)
    - Create `ServiceUnavailableException` (for circuit breaker open, maps to 503)
    - All extend `RuntimeException` with a message constructor
    - _Requirements: 11.1, 11.2_

- [ ] 3. Domain model — Port interfaces
  - [ ] 3.1 Define repository port interfaces
    - Create `FranchiseRepository` interface: `save(Franchise)`, `findById(String)`, `update(Franchise)` returning Mono types
    - Create `BranchRepository` interface: `save(Branch)`, `findById(String)`, `update(Branch)`, `findByFranchiseId(String)` returning Mono/Flux types
    - Create `ProductRepository` interface: `save(Product)`, `findById(String)`, `update(Product)`, `deleteById(String)`, `findByBranchId(String)` returning Mono/Flux types
    - All interfaces located in the model module with zero framework dependencies
    - _Requirements: 13.3, 14.2_

- [ ] 4. Use cases — Business logic implementation
  - [ ] 4.1 Implement CreateFranchiseUseCase
    - Accept franchise name, delegate to `Franchise.create()`, persist via `FranchiseRepository.save()`
    - Return `Mono<Franchise>` with generated id
    - _Requirements: 1.1, 1.2_

  - [ ] 4.2 Implement AddBranchUseCase
    - Verify franchise exists via `FranchiseRepository.findById()`, throw `EntityNotFoundException` if not found
    - Create branch with `Branch.create()`, persist via `BranchRepository.save()`
    - Return `Mono<Branch>`
    - _Requirements: 2.1, 2.2, 2.3_

  - [ ] 4.3 Implement AddProductUseCase
    - Verify branch exists via `BranchRepository.findById()`, throw `EntityNotFoundException` if not found
    - Create product with `Product.create()`, persist via `ProductRepository.save()`
    - Return `Mono<Product>`
    - _Requirements: 3.1, 3.2, 3.3, 3.4_

  - [ ] 4.4 Implement RemoveProductUseCase
    - Verify product exists via `ProductRepository.findById()`, throw `EntityNotFoundException` if not found
    - Delete via `ProductRepository.deleteById()`
    - Return `Mono<Void>`
    - _Requirements: 4.1, 4.2, 4.3_

  - [ ] 4.5 Implement UpdateProductStockUseCase
    - Verify product exists via `ProductRepository.findById()`, throw `EntityNotFoundException` if not found
    - Call `product.updateStock(newStock)` (validation in domain), persist via `ProductRepository.update()`
    - Return `Mono<Product>`
    - _Requirements: 5.1, 5.2, 5.3_

  - [ ] 4.6 Implement GetHighestStockProductUseCase
    - Verify franchise exists via `FranchiseRepository.findById()`, throw `EntityNotFoundException` if not found
    - Fetch all branches via `BranchRepository.findByFranchiseId()`
    - For each branch, fetch products via `ProductRepository.findByBranchId()`, find max stock product
    - Exclude branches with no products
    - Return `Flux` of highest-stock results including branch name info
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

  - [ ] 4.7 Implement UpdateFranchiseNameUseCase
    - Verify franchise exists, call `franchise.updateName(newName)`, persist update
    - Return `Mono<Franchise>`
    - _Requirements: 7.1, 7.2, 7.3_

  - [ ] 4.8 Implement UpdateBranchNameUseCase
    - Verify branch exists, call `branch.updateName(newName)`, persist update
    - Return `Mono<Branch>`
    - _Requirements: 8.1, 8.2, 8.3_

  - [ ] 4.9 Implement UpdateProductNameUseCase
    - Verify product exists, call `product.updateName(newName)`, persist update
    - Return `Mono<Product>`
    - _Requirements: 9.1, 9.2, 9.3_

  - [ ]* 4.10 Write unit tests for all use cases
    - Test each use case with mocked repository ports
    - Verify EntityNotFoundException thrown when entity not found (Req 2.2, 3.2, 4.2, 4.3, 5.3, 6.2, 7.2, 8.2, 9.2)
    - Verify successful path returns expected domain objects
    - Use StepVerifier for reactive assertions
    - _Requirements: 2.2, 3.2, 4.2, 4.3, 5.3, 6.2, 7.2, 8.2, 9.2_

  - [ ]* 4.11 Write property test: Valid stock update (Property 5)
    - **Property 5: Valid stock update**
    - Generate non-negative integers, create a product, call `updateStock()`, verify returned stock matches exactly
    - Use jqwik `@Property` with minimum 100 tries
    - **Validates: Requirements 5.1**

  - [ ]* 4.12 Write property test: Highest stock query correctness (Property 7)
    - **Property 7: Highest stock query correctness**
    - Generate random lists of products with varying stock values per branch
    - Verify: exactly one product per branch returned, each returned product's stock >= all other products in that branch, branches with no products excluded
    - Use jqwik `@Property` with minimum 100 tries
    - **Validates: Requirements 6.1, 6.3, 6.4**

- [ ] 5. Checkpoint - Ensure domain and use cases compile
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 6. Entry points — Router, Handler, DTOs
  - [ ] 6.1 Create request/response DTO records
    - `CreateFranchiseRequest(String name)`
    - `CreateBranchRequest(String name)`
    - `CreateProductRequest(String name, int stock)`
    - `UpdateNameRequest(String name)`
    - `UpdateStockRequest(int stock)`
    - `FranchiseResponse(String id, String name)`
    - `BranchResponse(String id, String franchiseId, String name)`
    - `ProductResponse(String id, String branchId, String name, int stock)`
    - `HighestStockProductResponse(String branchId, String branchName, String productId, String productName, int stock)`
    - `ErrorResponse(String code, String message)`
    - _Requirements: 11.2_

  - [ ] 6.2 Implement FranchiseHandler
    - Implement handler methods for each endpoint: `createFranchise`, `addBranch`, `addProduct`, `removeProduct`, `updateProductStock`, `updateProductName`, `updateBranchName`, `updateFranchiseName`, `getHighestStockProducts`
    - Each method: extract path variables, deserialize request body, delegate to use case, map result to ServerResponse with correct status (201/200/204)
    - All methods return `Mono<ServerResponse>`
    - _Requirements: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1, 10.2_

  - [ ] 6.3 Implement FranchiseRouter
    - Define `RouterFunction<ServerResponse>` bean with all 9 routes
    - POST `/api/franchises`, PATCH `/api/franchises/{franchiseId}/name`
    - POST `/api/franchises/{franchiseId}/branches`, PATCH `/api/branches/{branchId}/name`
    - POST `/api/branches/{branchId}/products`, DELETE `/api/branches/{branchId}/products/{productId}`
    - PATCH `/api/products/{productId}/stock`, PATCH `/api/products/{productId}/name`
    - GET `/api/franchises/{franchiseId}/highest-stock`
    - _Requirements: 10.1, 10.3_

  - [ ] 6.4 Implement GlobalErrorHandler
    - Implement `WebExceptionHandler` with `@Order(-2)`
    - Map `InvalidInputException` → 400 with code `VALIDATION_ERROR`
    - Map `EntityNotFoundException` → 404 with code `NOT_FOUND`
    - Map `ServiceUnavailableException` → 503 with code `SERVICE_UNAVAILABLE`
    - Map any unhandled `Exception` → 500 with code `INTERNAL_ERROR` and generic message
    - Write JSON `ErrorResponse` body, never expose stack traces
    - _Requirements: 11.1, 11.2, 11.3_

  - [ ]* 6.5 Write property test: Error response structure consistency (Property 8)
    - **Property 8: Error response structure consistency**
    - Generate random exception types (InvalidInputException, EntityNotFoundException, ServiceUnavailableException, generic RuntimeException) with random messages
    - Verify: response always contains `code` and `message` fields, never contains stack traces or class names
    - Use jqwik `@Property` with minimum 100 tries
    - **Validates: Requirements 11.1, 11.2**

  - [ ]* 6.6 Write unit tests for Handler and Router
    - Test each handler method returns correct HTTP status codes
    - Test request body deserialization and path variable extraction
    - Verify router maps all 9 routes correctly
    - Use `WebTestClient` with mocked use cases
    - _Requirements: 10.1, 10.2, 10.3_

- [ ] 7. MongoDB driven adapter implementation
  - [ ] 7.1 Create MongoDB document classes
    - `FranchiseDocument` with `@Document(collection = "franchises")`, fields: id, name
    - `BranchDocument` with `@Document(collection = "branches")`, fields: id, franchiseId, name
    - `ProductDocument` with `@Document(collection = "products")`, fields: id, branchId, name, stock
    - _Requirements: 14.1_

  - [ ] 7.2 Create Spring Data reactive repository interfaces
    - `FranchiseDataRepository extends ReactiveMongoRepository<FranchiseDocument, String>`
    - `BranchDataRepository extends ReactiveMongoRepository<BranchDocument, String>` with `findByFranchiseId(String)` method
    - `ProductDataRepository extends ReactiveMongoRepository<ProductDocument, String>` with `findByBranchId(String)` method
    - _Requirements: 14.2_

  - [ ] 7.3 Implement repository adapters with entity-document mapping
    - `MongoFranchiseRepository` implements `FranchiseRepository` port — maps between `Franchise` entity and `FranchiseDocument`
    - `MongoBranchRepository` implements `BranchRepository` port — maps between `Branch` entity and `BranchDocument`
    - `MongoProductRepository` implements `ProductRepository` port — maps between `Product` entity and `ProductDocument`
    - Each adapter uses the corresponding Spring Data repository internally
    - _Requirements: 13.3, 14.1, 14.2_

- [ ] 8. Circuit Breaker integration
  - [ ] 8.1 Configure Resilience4j circuit breaker
    - Add Resilience4j configuration in application.yml for `mongoCircuitBreaker` instance
    - failureRateThreshold: 50, waitDurationInOpenState: 30s, slidingWindowSize: 10, permittedNumberOfCallsInHalfOpenState: 3
    - Wire circuit breaker decorator into repository adapter calls using `Mono.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))`
    - On circuit open, throw `ServiceUnavailableException`
    - _Requirements: 15.1, 15.2, 15.3, 15.4_

  - [ ]* 8.2 Write unit tests for circuit breaker behavior
    - Test circuit transitions: closed → open when failure rate exceeded
    - Test that open circuit returns ServiceUnavailableException (503)
    - Test half-open state allows test calls through
    - _Requirements: 15.2, 15.3, 15.4_

- [ ] 9. Checkpoint - Ensure full application compiles and unit tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 10. OpenAPI/Swagger documentation
  - [ ] 10.1 Configure springdoc-openapi for WebFlux
    - Add springdoc-openapi-starter-webflux-ui dependency (already in 1.2)
    - Configure OpenAPI info (title, version, description) in application.yml or a config class
    - Create `@RouterOperations` annotations on the router bean or use OpenAPI config class to define all operations
    - Document all request/response schemas, path parameters, and status codes
    - Verify Swagger UI is accessible at `/swagger-ui.html` or configured path
    - _Requirements: 12.1, 12.2, 12.3_

- [ ] 11. Docker containerization
  - [ ] 11.1 Create multi-stage Dockerfile
    - Stage 1: Build with Gradle (use `eclipse-temurin:17-jdk` as builder image)
    - Stage 2: Runtime with `eclipse-temurin:17-jre-alpine` for minimal image size
    - Copy built JAR from builder stage
    - Expose port 8080, set JVM memory flags
    - _Requirements: 16.1, 16.3_

  - [ ] 11.2 Create docker-compose.yml for local development
    - Define `app` service building from Dockerfile with port mapping 8080:8080
    - Define `mongodb` service using `mongo:7` image with port 27017
    - Configure environment variables for MongoDB connection
    - Add health checks and depends_on configuration
    - _Requirements: 16.2_

- [ ] 12. Terraform infrastructure as code
  - [ ] 12.1 Create Terraform configuration for cloud deployment
    - Create `terraform/main.tf` with provider configuration and resource definitions
    - Provision MongoDB-compatible database instance (e.g., AWS DocumentDB or Atlas, or Azure CosmosDB)
    - Provision compute resource for application (e.g., ECS Fargate, App Runner, or Azure Container Apps)
    - Provision networking (VPC, subnets, security groups)
    - _Requirements: 17.1, 17.2_

  - [ ] 12.2 Create Terraform variables and outputs
    - Create `terraform/variables.tf` with: region, instance sizes, database credentials, environment name
    - Create `terraform/outputs.tf` with: application endpoint URL, database endpoint
    - _Requirements: 17.3, 17.4_

- [ ] 13. Integration tests
  - [ ]* 13.1 Write repository integration tests
    - Use embedded MongoDB (Flapdoodle) or Testcontainers
    - Test save/findById/update round-trips for all three repository adapters
    - Test `findByFranchiseId` and `findByBranchId` queries
    - Verify data integrity across operations
    - _Requirements: 14.1, 14.2_

  - [ ]* 13.2 Write property test: Product deletion round-trip (Property 6)
    - **Property 6: Product deletion round-trip**
    - Generate random products, save them, delete them, verify they are no longer retrievable by id and no longer appear in branch product list
    - Use jqwik `@Property` with minimum 100 tries and embedded MongoDB
    - **Validates: Requirements 4.1**

  - [ ]* 13.3 Write property test: Name update preserves new value (Property 3)
    - **Property 3: Name update preserves new value**
    - Generate arbitrary non-blank strings as new names, update each entity type, verify returned entity has trimmed new name while all other fields are preserved
    - Use jqwik `@Property` with minimum 100 tries
    - **Validates: Requirements 7.1, 8.1, 9.1**

  - [ ]* 13.4 Write end-to-end API integration tests
    - Use `WebTestClient` with full application context and embedded MongoDB
    - Test complete request/response cycles for all 9 endpoints
    - Verify correct HTTP status codes, response bodies, and error handling
    - Test not-found scenarios return 404
    - _Requirements: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1, 11.1, 11.2_

- [ ] 14. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties from the design document using jqwik
- Unit tests validate specific examples and edge cases using JUnit 5 + Reactor StepVerifier
- All code is written in Java with Spring WebFlux reactive types
- The Bancolombia scaffold plugin handles initial module generation; subsequent tasks build upon the generated structure
