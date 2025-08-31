# Gemini CLI Agent Guidelines

As the Gemini CLI agent, I must adhere to the following principles, derived from our previous discussions, when assisting the user:

## 1. Domain Model Principles:
- **Definition:** Prioritize business-meaningful objects with both data and behavior.
- **Distinction:** Clearly differentiate between `Entity` (ID, mutable state over time) and `Value Object` (no ID, immutable, equality by value).
- **Design:** Ensure responsibility-driven design, independent of presentation (DTOs) and persistence (DB/ORM).
- **Anti-pattern Avoidance:** Actively avoid `Anemic Domain Model` (data-only models); promote `Tell, Don't Ask` by embedding business logic within domain objects.

## 2. Clean Architecture & Layer Separation:
- **Strict Separation:** Maintain clear boundaries between DTOs (Network), Entities (Database), Domain Models, and UI Models (Feature). Use Mappers in the `Data` layer for conversions.
- **Dependency Isolation:** Ensure the `Domain` layer is pure Kotlin (POJO/POKO), free from Android framework or specific library dependencies (Room, Retrofit, etc.).
- **UseCase Focus:** Encapsulate business scenarios and complex logic within `UseCase` or `Domain Service` objects. Avoid direct `Repository` calls from `ViewModel`s.
- **Immutability & Type Safety:** Prefer `Value Object`s over primitive types (e.g., String for IDs, dates) for type safety and meaningful encapsulation. Promote immutability.

## 3. Multi-Module Architecture Strategy:
- **Avoid "God Domain":** Do not consolidate all `UseCase`s into a single, monolithic `domain` module.
- **`domain:core` vs. `domain:feature-xxx`:**
    - `domain:core`: For highly reusable, stable, common entities/VOs, shared rules, and common Repository interfaces.
    - `domain:feature-xxx`: For feature-specific `UseCase`s and policies.
- **Promotion Rule:** Implement a strategy to promote `UseCase`s or policies from `domain:feature-xxx` to `domain:core` when they become widely shared and stable across multiple features.
- **Clear Dependencies:** Ensure `feature` modules depend only on `domain:core` and their specific `domain:feature-xxx` modules. Prevent cross-module dependencies between `domain:feature-xxx` modules.

## 4. Testing Strategy:
- Prioritize robust unit testing for `Domain` and `UseCase` layers. Conduct lighter smoke tests for the UI layer.

---
By adhering to these guidelines, I will provide consistent, robust, and maintainable solutions.