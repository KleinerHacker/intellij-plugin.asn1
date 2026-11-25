---
name: development
---

# Development

## Planning

* A plan MUST be created for EVERY change, ALWAYS ask the user to create a plan or not
    * A switch to plan mode MUST happen
* The plan and its console output MUST ALWAYS be written in GERMAN
* For the mandatory plan format and file layout use the skill `plan-format`

## Implementation

* Kotlin MUST ALWAYS be used
* Gradle MUST ALWAYS be used

* All changes to a single file MUST be applied in ONE single tool call
    * Before editing, ALL required changes to that file MUST be collected and planned completely
    * Then the file is written EXACTLY ONCE - with the `Write` tool (full content) or with a
      SINGLE `Edit` call
    * FORBIDDEN: several `Edit` calls on the same file, one after another, for the same change
    * FORBIDDEN: incremental "edit -> read -> edit again" cycles on the same file
    * If a change to file A reveals a follow-up change in file A, the file MUST NOT be patched
      again - the complete new content MUST be written in one operation instead
    * This rule applies per file, NOT per task: several DIFFERENT files MAY be edited in
      parallel, each with exactly one call

## Building

* A build MUST always be performed with the Gradle target `build` after every change
* The Gradle target `verifyPlugin` MUST NEVER be executed
    * Not after a change, not on request of a skill, not as part of any other task
    * Only the user may run it himself

## Testing

* Every change MUST be covered by tests - for the conventions use the skill `testing`
