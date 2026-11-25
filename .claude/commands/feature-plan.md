# Feature Plan

Create a **Feature Plan** for the requested feature.

A Feature Plan is a high-level planning artifact that describes **what the feature should achieve and how it is decomposed into several implementation plans**.

The Feature Plan is only the **skeleton** of the feature. It carries the objective, the overall
architecture, the list of implementation plans and the dependencies between them. It does **not**
carry the detailed design of any single implementation plan.

The substance of the work - detailed requirements, concrete design decisions, affected files,
migration steps, test design - belongs in the derived implementation plans, not in the Feature
Plan. Keep every section of the Feature Plan short so the file stays small even for large features.

## Decomposition Rule

* A feature is **always** split into **at least two** implementation plans. A Feature Plan with a
  single implementation plan is not allowed.
* If a feature seems too small to split, it does not need a Feature Plan - create a normal
  implementation plan instead.
* Every implementation plan must be a meaningful, independently expandable unit of work. Do not
  create plans that are merely collections of trivial file changes.
* Push each coherent topic of the feature into its own implementation plan. The Feature Plan keeps
  only the scaffold that ties the plans together.
* The dependencies between the implementation plans must **always** be identified and shown
  explicitly - in the overview table, in the per-plan entry and in the dependency graph. A plan
  with no prerequisites is marked explicitly as independent.

## Output Location

All Feature Plans **must be stored in**:

```text
.claude/plans/features/
```

Before creating a new Feature Plan, inspect the directory and determine the next available sequential number.

Use the following naming convention:

```text
FP-<NUMBER>-<FeatureName>.md
```

For example:

```text
.claude/plans/features/
├── FP-001-UserAuthentication.md
├── FP-002-AuditLogging.md
└── FP-003-UserImport.md
```

The number must:

* be sequential
* use three digits
* be determined from the existing Feature Plans
* use the next available number
* never overwrite an existing Feature Plan

The Feature Plan filename should use a concise, descriptive PascalCase feature name.

## Process

### 1. Inspect Existing Feature Plans

Before creating the Feature Plan:

1. Inspect `.claude/plans/features/`.
2. Determine the next available feature number.
3. Inspect existing Feature Plans if they are relevant to the requested feature.
4. Avoid creating a duplicate feature.
5. Reuse existing architectural decisions where appropriate.

### 2. Analyze the Existing System

Inspect the relevant parts of the repository before creating the plan.

Understand:

* existing architecture
* relevant modules and components
* existing abstractions
* data flow
* APIs and interfaces
* persistence
* configuration
* tests
* build and deployment structure
* existing conventions and patterns

Do not assume that the requested feature should be implemented according to an idealized architecture.

Prefer the architecture and conventions already established by the project unless there is a clear reason to change them.

### 3. Define the Feature

Describe the feature as a whole, briefly.

Establish:

* the problem being solved
* intended behavior
* functional requirements
* technical requirements
* architectural consequences
* affected components
* constraints
* assumptions
* open questions

Focus on the **end state**, not individual implementation steps. Keep this to the level a reader
needs to understand why the implementation plans exist; the detail goes into those plans.

### 4. Identify Implementation Boundaries

Divide the feature into logically coherent implementation plans. There must be **at least two**.

Good boundaries may include:

* a new domain capability
* a new API
* persistence changes
* infrastructure changes
* substantial refactoring
* UI/frontend work
* external integrations
* migration work
* testing work where this constitutes a meaningful independent unit

Avoid creating plans that are merely collections of trivial file changes.

Each implementation plan must represent a meaningful unit of work that can later be expanded into a normal detailed implementation plan.

### 5. Define Dependencies

Determine the dependency graph between implementation plans. This step is mandatory and its result
must appear in the Feature Plan.

For every implementation plan identify:

* prerequisites (explicitly `-` / independent when there are none)
* dependent plans
* whether it can be implemented independently
* interfaces or architectural changes required by other plans

Prefer parallelizable implementation plans where possible.

### 6. Create the Feature Plan

Create the Feature Plan in:

```text
.claude/plans/features/FP-<NUMBER>-<FeatureName>.md
```

Do not modify unrelated files.

## Required Feature Plan Structure

Keep every section short. The Feature Plan is a scaffold, not a detailed design document.

# Feature Plan: <Feature Name>

## 1. Objective

Describe the overall goal of the feature in a few sentences.

## 2. Current State

Briefly describe the relevant existing implementation and architecture.

## 3. Target State

Briefly describe the desired state after the entire feature has been implemented.

## 4. Requirements

### Functional Requirements

List the functional requirements as short bullets. Detailed requirements belong in the individual
implementation plans.

### Technical Requirements

List the technical requirements and constraints as short bullets.

## 5. Architecture

Describe the architectural changes required by the feature at overview level only.

Include relevant:

* components
* modules
* interfaces
* data flows
* persistence
* external integrations

Detailed design decisions belong in the individual implementation plans.

## 6. Implementation Plan Overview

Create a table. There must be at least two rows, and the `Dependencies` column must be filled for
every row (`-` when the plan is independent):

| ID    | Implementation Plan | Objective | Dependencies |
| ----- | ------------------- | --------- | ------------ |
| IP-01 | ...                 | ...       | -            |
| IP-02 | ...                 | ...       | IP-01        |
| IP-03 | ...                 | ...       | IP-01        |
| IP-04 | ...                 | ...       | IP-02, IP-03 |

## 7. Implementation Plans

For each implementation plan give only a short scaffold entry. The detailed design is produced
later in the derived implementation plan, not here.

### IP-01: <title>

**Objective**

One or two sentences on what this plan accomplishes.

**Scope**

Short bullets: what belongs in this plan and what explicitly does not.

**Dependencies**

List prerequisite implementation plans, or state `-` / independent.

**Interfaces to Other Plans**

Name the interfaces or architectural changes this plan provides to or requires from other plans.

Repeat for every implementation plan. Do not add detailed affected-file lists, migration steps or
design decisions here - those are created in the derived implementation plan.

## 8. Dependency Graph

Represent the dependency structure explicitly. This section is mandatory.

Example:

```text
IP-01
├── IP-02
│   └── IP-04
└── IP-03
    └── IP-04
```

## 9. Risks and Open Questions

List architectural risks, unresolved decisions, assumptions, and questions that must be answered before implementation.

## 10. Feature Completion Criteria

Define criteria that must be fulfilled for the entire feature to be considered complete.

These criteria should describe observable outcomes rather than individual implementation tasks.

## Status File Requirements

Create the status file alongside the Feature Plan.

The initial status file must contain all implementation plans identified in the Feature Plan.

Use:

```markdown
# Feature Status: <Feature Name>

Status: NOT_STARTED

## Implementation Plans

| ID | Implementation Plan | Status |
|----|---------------------|--------|
| IP-01 | <name> | NOT_STARTED |
| IP-02 | <name> | NOT_STARTED |
| IP-03 | <name> | NOT_STARTED |

## Overall Progress

0%

## Notes

Feature Plan created. No implementation plan has been started yet.
```

The status file is the authoritative source for the current implementation status of the feature.

When an implementation plan is completed, update its status and recalculate the overall progress.

## Derived Implementation Plans

Every implementation plan derived from a Feature Plan lives in `.claude/plans/implementation/` and
must carry the number of the Feature Plan it came from, followed by the ID of the plan itself:

```text
FP-<FEATURE_NUMBER>-IP-<PLAN_NUMBER>-<PlanName>.md
```

For example, for the Feature Plan `FP-001-PaperWritingSurface.md`:

```text
.claude/plans/implementation/
├── FP-001-Overview.md
├── FP-001-IP-01-FontDiscoveryAndTextMeasuring.md
├── FP-001-IP-02-DesignPageFormat.md
```

The naming rules:

* the feature number comes first, with the same three digits the Feature Plan carries
* the plan ID follows, as `IP-<PLAN_NUMBER>` with two digits, exactly the ID used in the Feature Plan
* the plan name follows last, in PascalCase
* a plan that belongs to no Feature Plan carries no `FP-` prefix

The prefix makes the plans of a feature stay together in the directory and tells two plans carrying
the same ID in different features apart.

The derived implementation plan is where the detail lives: affected files, concrete design
decisions, migration steps, test design and the task breakdown. The Feature Plan only points at
it.

### Overview File

Every Feature Plan must have exactly one overview file in `.claude/plans/implementation/`, carrying
the number of the Feature Plan:

```text
FP-<FEATURE_NUMBER>-Overview.md
```

The overview file must contain:

* the name of the Feature Plan and the path to it
* one line per implementation plan: ID, name and the file name of the plan
* the order in which the plans are implemented, with the prerequisites of each

The overview file is created together with the implementation plans and is updated whenever a plan
is added, renamed or removed. It is not a status file and carries no progress information; the
status of the feature stays in the status file of the Feature Plan.

The overview file stays in place until the last implementation plan of the feature is finished, and
is removed together with it.

## Important Rules

* Do not implement anything.
* Do not modify production or source code.
* Do not create commits.
* Do not create normal implementation plans unless explicitly requested.
* Create exactly one Feature Plan for the requested feature.
* Create exactly one corresponding status file.
* Always store both files in `.claude/plans/features/`.
* Never overwrite an existing Feature Plan.
* Always determine the next sequential three-digit number from the existing files.
* The Feature Plan is the **skeleton** of the feature - objective, architecture overview, plan list
  and dependencies only.
* Every feature is split into **at least two** implementation plans; a single-plan Feature Plan is
  forbidden.
* Push each coherent topic into its own implementation plan; keep the Feature Plan small.
* Implementation Plans describe the **major units required to implement the feature**.
* Do not split the feature into many trivial plans; each plan must be a meaningful unit of work.
* Dependencies between implementation plans must **always** be identified and shown in the overview
  table, the per-plan entry and the dependency graph.
* Prefer parallelizable plans where possible.
* Keep the detailed design out of the Feature Plan; it belongs in the derived implementation plans.
* Reuse existing project architecture and conventions.
* If the feature is ambiguous, investigate the codebase first and document unresolved questions.
* Do not silently make major architectural decisions that are not supported by the existing codebase.
* Keep the Feature Plan stable; implementation progress belongs in the status file.
* Every derived implementation plan carries the feature number, then its own ID, then its name.
* Every Feature Plan has exactly one overview file carrying the same feature number.
