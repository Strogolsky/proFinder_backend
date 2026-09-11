---
name: code-documentation-check
description: Verify that code changes align with project documentation and standards
---

# Code-Documentation Check Skill

This skill ensures code changes follow documented patterns, standards, and architecture.

## When to Use

Use this skill when:
- Starting a new feature/task
- Before creating a PR
- When reviewing code changes
- When modifying core patterns
- When implementing from documentation requirements

## What to Check

### 1. Read the Task/Issue

Start by understanding:
- [ ] What feature/bug is being addressed?
- [ ] Which documentation files are relevant?
- [ ] What are the acceptance criteria?
- [ ] Which components need to change?

### 2. Check Against CLAUDE.md

**Project Patterns:**
- [ ] Follow naming conventions (snake_case resources, PascalCase classes)
- [ ] Use DTOs for API boundaries
- [ ] Apply `@Valid` annotations for validation
- [ ] Use `@JsonbTransient` for excluded fields
- [ ] Follow entity inheritance (User → Client/Specialist)

**Tech Stack:**
- [ ] Quarkus patterns correct?
- [ ] Panache repositories used?
- [ ] MapStruct mappers for DTOs?
- [ ] JUnit 5 + REST Assured for tests?

**Code Quality:**
- [ ] Checkstyle passes locally?
- [ ] No hardcoded secrets?
- [ ] Line length ≤ 120 chars?
- [ ] Proper indentation (4 spaces)?

**Logging & Security:**
- [ ] `@Slf4j` used for logging?
- [ ] BCrypt for passwords?
- [ ] JWT tokens validated?

### 3. Check Against Domain Documentation

Review relevant domain docs (pick based on task):

**If working on Auth** → Read [4 - Business logic.md](../../docs/4%20-%20Business%20logic.md) § Authentication
- [ ] Password validation rules from [13 - Validation Rules.md](../../docs/13%20-%20Validation%20Rules.md)
- [ ] Email format requirements
- [ ] Token expiry times correct
- [ ] User roles properly enforced

**If working on Orders** → Read [4 - Business logic.md](../../docs/4%20-%20Business%20logic.md) § Order Lifecycle
- [ ] Order status transitions match state machine from [5 - State Machines.md](../../docs/5%20-%20State%20Machines.md)
- [ ] Proposal workflow implemented correctly
- [ ] Database schema matches [6 - Database Schema.md](../../docs/6%20-%20Database%20Schema.md)

**If working on Search** → Read [12 - Elasticsearch Mapping.md](../../docs/12%20-%20Elasticsearch%20Mapping.md)
- [ ] Index mappings match documentation
- [ ] Query structure follows spec
- [ ] Filters implemented correctly

**If working on Validation** → Read [13 - Validation Rules.md](../../docs/13%20-%20Validation%20Rules.md)
- [ ] All field constraints applied
- [ ] Email format validated
- [ ] Password complexity checked
- [ ] Phone number format correct

### 4. Check API Compliance

Against [8 - API Specification.md](../../docs/8%20-%20API%20Specification.md):

- [ ] Endpoint path follows `/api/v1/{resource}` pattern
- [ ] HTTP methods correct (GET, POST, PUT, DELETE)
- [ ] Request/response DTOs match spec
- [ ] Status codes correct (201 for create, 400 for validation error, etc.)
- [ ] Authentication required where needed
- [ ] Role-based access control (`@RolesAllowed`)

### 5. Check Database Changes

If modifying entities or schema:

- [ ] Entity matches [6 - Database Schema.md](../../docs/6%20-%20Database%20Schema.md)
- [ ] JPA annotations correct
- [ ] Panache repository methods defined
- [ ] Relationships properly configured
- [ ] Flyway migration created (if structure changes)
- [ ] Import.sql updated (for test data)

### 6. Check Event/Messaging

If adding events or RabbitMQ usage:

- [ ] Event documented in [9 - Event Catalog.md](../../docs/9%20-%20Event%20Catalog.md)
- [ ] Transactional outbox pattern used
- [ ] Message structure matches spec
- [ ] Topics/queues properly named

### 7. Check Testing

Match testing strategy from [15 - SDLC & Workflow.md](../../docs/15%20-%20SDLC%20%26%20Workflow.md) § 6:

- [ ] Unit tests for business logic
- [ ] Integration tests with real database
- [ ] E2E tests for full workflows (if applicable)
- [ ] Coverage ≥ 80%
- [ ] No mocking of databases (use Testcontainers)
- [ ] Test naming: `*Test.java` for unit, `*IT.java` for integration, `*E2E.java` for E2E

### 8. Check Against Code Patterns

Review [CLAUDE.md](../../CLAUDE.md) examples for:

**Repository Pattern:**
```java
// ✓ Correct
var users = UserRepository.find("email", email).firstResult();

// ✗ Wrong
var users = entityManager.createQuery("SELECT u FROM User u WHERE u.email = ?1").setParameter(1, email).getResultList();
```

**Service Pattern:**
```java
// ✓ Correct
public class AuthService {
    public User signup(String email, String password) {
        validatePassword(password);  // Validate first
        User user = new User(email, hashPassword(password));
        return userRepository.persistAndFlush(user);
    }
}

// ✗ Wrong
// Raw SQL in service
// Direct entity manipulation without validation
// No transactional handling
```

**Mapper Pattern:**
```java
// ✓ Correct
@Mapper
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}

// ✗ Wrong
// Manual property copying in service
// No type safety
```

## Checklist by Task Type

### Feature Implementation
- [ ] Read [2 - Requirements.md](../../docs/2%20-%20Requirements.md) for requirements
- [ ] Check [4 - Business logic.md](../../docs/4%20-%20Business%20logic.md) for rules
- [ ] Verify [6 - Database Schema.md](../../docs/6%20-%20Database%20Schema.md) for data model
- [ ] Review [8 - API Specification.md](../../docs/8%20-%20API%20Specification.md) for endpoints
- [ ] Follow [13 - Validation Rules.md](../../docs/13%20-%20Validation%20Rules.md)
- [ ] Update [9 - Event Catalog.md](../../docs/9%20-%20Event%20Catalog.md) if events involved
- [ ] Write unit + integration + E2E tests
- [ ] Run: `mvn clean verify`, `mvn checkstyle:check`

### Bug Fix
- [ ] Read issue description
- [ ] Check if bug documented in code or docs
- [ ] Verify fix matches documented behavior
- [ ] Add test that reproduces bug, then fix it
- [ ] Ensure no regression in related features

### Refactoring
- [ ] Maintain all existing patterns
- [ ] Verify all tests still pass
- [ ] No behavioral changes (only code organization)
- [ ] Update code comments if patterns change
- [ ] Run: `mvn clean verify`

### Documentation-Only
- [ ] Follow markdown format from existing docs
- [ ] Link between related docs
- [ ] Add changelog entry
- [ ] Verify code examples compile
- [ ] Check links are valid

## Red Flags

Stop and ask for clarification if:
- [ ] Code pattern not found in [CLAUDE.md](../../CLAUDE.md) or docs
- [ ] Business rule contradicts [4 - Business logic.md](../../docs/4%20-%20Business%20logic.md)
- [ ] Database schema change not in [6 - Database Schema.md](../../docs/6%20-%20Database%20Schema.md)
- [ ] API endpoint pattern doesn't match [8 - API Specification.md](../../docs/8%20-%20API%20Specification.md)
- [ ] Validation rules missing from [13 - Validation Rules.md](../../docs/13%20-%20Validation%20Rules.md)
- [ ] Test coverage goal (≥80%) cannot be met

## Before Commit

Final checklist:
- [ ] All tests pass locally: `mvn clean verify`
- [ ] Checkstyle passes: `mvn checkstyle:check`
- [ ] No compiler warnings
- [ ] Code matches documented patterns
- [ ] API responses match spec
- [ ] Database schema updates reflected in docs
- [ ] Commit message references relevant docs
- [ ] PR description explains what & why

## Commit Message Template

```
[Component] Action: Brief description

- Specific change 1
- Specific change 2
- Specific change 3

Follows:
- [4 - Business logic.md § Relevant section]
- [6 - Database Schema.md § Relevant table]
- [8 - API Specification.md § Relevant endpoint]
- [13 - Validation Rules.md § Relevant rule]

Fixes: #123 (if applicable)
```

## Related Documentation

- [CLAUDE.md](../../CLAUDE.md) — Project guide & patterns
- [2 - Requirements.md](../../docs/2%20-%20Requirements.md) — Feature requirements
- [4 - Business logic.md](../../docs/4%20-%20Business%20logic.md) — Business rules
- [6 - Database Schema.md](../../docs/6%20-%20Database%20Schema.md) — Data model
- [8 - API Specification.md](../../docs/8%20-%20API%20Specification.md) — API contract
- [13 - Validation Rules.md](../../docs/13%20-%20Validation%20Rules.md) — Constraints
- [15 - SDLC & Workflow.md](../../docs/15%20-%20SDLC%20%26%20Workflow.md) — CI/CD & testing
