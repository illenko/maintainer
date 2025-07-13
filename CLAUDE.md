# Component Availability Management Service

## Overview
Backend service to manage system component accessibility status. Provides availability information for components (payment systems, external APIs, etc.) with support for scheduled maintenance and incident tracking.

## Technology Stack
- **Language**: Kotlin
- **Framework**: Spring Boot 4.0.0-SNAPSHOT
- **Database**: PostgreSQL with Liquibase migrations
- **Build Tool**: Gradle with Kotlin DSL
- **Testing**: Testcontainers, JUnit 5

## Domain Model

### Component
- `id`: Long (Primary key)
- `name`: String (Unique, URL-safe identifier)
- `createdAt`: LocalDateTime
- `updatedAt`: LocalDateTime

### Outage
- `id`: Long (Primary key)
- `componentId`: Long (Foreign key to Component)
- `type`: OutageType enum (`SCHEDULED`, `INCIDENT`)
- `from`: LocalDateTime (Start time)
- `to`: LocalDateTime? (End time - nullable for ongoing incidents)
- `reason`: String (Optional description)
- `autoEndHours`: Int (Auto-end ongoing incidents after X hours, default: 24)
- `createdAt`: LocalDateTime
- `updatedAt`: LocalDateTime

### ComponentStatus Enum
- `AVAILABLE`
- `UNAVAILABLE`

## API Design

### Component Management
- `GET /api/v1/components` - List components (pagination, filtering)
- `GET /api/v1/components/{id}` - Get component by ID
- `POST /api/v1/components` - Create component
- `PUT /api/v1/components/{id}` - Update component
- `DELETE /api/v1/components/{id}` - Delete component

### Status API (Performance Critical)
- `GET /api/v1/status/{componentName}` - Get current status by component name

### Outage Management
- `GET /api/v1/outages` - List outages (pagination, filtering by type/component/date)
- `GET /api/v1/outages/{id}` - Get outage by ID
- `POST /api/v1/outages` - Create outage (scheduled or incident)
- `PUT /api/v1/outages/{id}` - Update outage
- `PUT /api/v1/outages/{id}/resolve` - Resolve ongoing outage (sets to = now)
- `DELETE /api/v1/outages/{id}` - Delete outage

## Architecture

```
┌─────────────────┐
│   Controllers   │ ← REST API endpoints
├─────────────────┤
│    Services     │ ← Business logic + status calculation
├─────────────────┤
│  Repositories   │ ← Data access (Spring Data JDBC)
├─────────────────┤
│   PostgreSQL    │ ← Persistent storage
└─────────────────┘
```

## Status Calculation Logic
- Component is `UNAVAILABLE` if ANY active outage exists: `from <= now && (to == null || to > now)`
- Component is `AVAILABLE` otherwise
- Status calculated in real-time but cached for performance

## Business Rules
- **Validation**: Cannot create outages in the past (`from` must be >= now)
- **Precision**: Seconds-level accuracy (LocalDateTime)
- **Automation**: Scheduled outages auto-start/end based on time ranges
- **Ongoing Incidents**: `to = null` for unknown duration incidents
- **Manual Resolution**: Set `to = now()` to resolve ongoing incidents
- **Auto-Resolution**: Background job auto-ends ongoing incidents after `autoEndHours`
- **Cascade Delete**: Deleting component removes all associated outages
- **Duration**: No maximum limit on outage duration

## Performance & Scaling Strategy
- **Caching**: Status API results cached by component name
- **Database Indexing**: Component name, outage time ranges
- **Horizontal Scaling**: Stateless service design
- **Fast Lookups**: Status endpoint optimized for sub-100ms response times

## Database Schema
- Components table with unique constraint on name
- Outages table with foreign key to components
- Indexes on component name and outage time ranges

## Implementation Plan
1. ✅ Design overall service architecture and domain model
2. ⏳ Design database schema for components and outages
3. ⏳ Design caching strategy for fast status API
4. ✅ Design API endpoints and request/response models
5. ⏳ Plan horizontal scaling considerations
6. ⏳ Create domain entities (Component, Outage)
7. ⏳ Implement database repositories
8. ⏳ Implement service layer with business logic
9. ⏳ Implement REST controllers
10. ⏳ Add caching for status API

## End-to-End Test Cases

### Happy Path Scenarios
**TC1: Basic Component Lifecycle**
```
1. Create component "payment-gateway"
2. GET /api/v1/status/payment-gateway → AVAILABLE
3. Create scheduled outage (tomorrow 2-4 AM)
4. GET /api/v1/status/payment-gateway → AVAILABLE (outage in future)
5. Time travel to 2:30 AM tomorrow → UNAVAILABLE
6. Time travel to 4:01 AM → AVAILABLE
```

**TC2: Incident Management**
```
1. Component "user-service" is AVAILABLE
2. Create immediate incident outage (now → null)
3. GET /api/v1/status/user-service → UNAVAILABLE
4. PUT /api/v1/outages/{id}/resolve
5. GET /api/v1/status/user-service → AVAILABLE
```

### Critical Edge Cases
**TC3: Overlapping Outages**
```
1. Create component "api-gateway"
2. Create scheduled outage: Jan 1 10:00-12:00
3. Create incident outage: Jan 1 11:00-13:00 (overlaps)
4. Time travel to Jan 1 11:30 → UNAVAILABLE (any active outage)
5. Time travel to Jan 1 13:01 → AVAILABLE (all ended)
```

**TC4: Ongoing Incident Auto-Resolution**
```
1. Create incident outage (now → null, autoEndHours=24)
2. Status → UNAVAILABLE
3. Wait 25 hours
4. Background job runs → sets to = from + 24 hours
5. Status → AVAILABLE (auto-resolved)
```

**TC5: Component Deletion with Active Outages**
```
1. Create component "legacy-service"
2. Create active outage (now → +1 hour)
3. DELETE component
4. Verify: All outages cascade deleted
5. Verify: GET /api/v1/status/legacy-service → 404
```

**TC6: Validation Edge Cases**
```
1. Try creating outage in past → 400 Bad Request
2. Try creating outage with end before start → 400 Bad Request
3. Try creating outage for non-existent component → 404 Not Found
4. Component name with special chars validation
```

**TC7: Cache Consistency**
```
1. GET /api/v1/status/payment-gateway (cached as AVAILABLE)
2. Create immediate incident outage
3. Cache invalidation triggered
4. GET /api/v1/status/payment-gateway → UNAVAILABLE (updated)
```

## Commands
- Build: `./gradlew build`
- Test: `./gradlew test`
- Run: `./gradlew bootRun`