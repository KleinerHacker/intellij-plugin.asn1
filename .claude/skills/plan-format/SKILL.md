---
name: plan-format
description: Format and file layout for plans in .claude/plans. Use when writing, updating or resuming a plan, and before leaving plan mode.
---

# Plan Format

* The PLAN MUST ALWAYS be written in GERMAN - both the plan file and the console output
    * This applies to headings, bullet points and every other text of the plan
* The PLAN MUST NOT contain a summary or explanation of the changes
    * FORBIDDEN sections: "Context", "Background", "Summary", "Overview", "Rationale", "Trade-offs"
    * FORBIDDEN: prose paragraphs of any kind - the plan consists of bullet points ONLY
* The implementation tasks MUST be explained in short bullet points with no more than 20 words per bullet and a maximum of 10 bullets per task
    * A bullet describes WHAT is done, NOT WHY
* Before leaving plan mode the plan MUST be checked against ALL rules above

## Files

* The plan MUST be written into the local `.claude/plans` directory
    * Naming scheme: `<Name>.md`
* An implementation plan MUST NOT get a status file
    * ONLY the Feature Plan gets a status file, see the command `feature-plan`
* Plans MUST ALWAYS be added to GIT (`git add`) together with the change they describe
    * Plans stay in `.claude/plans` after the plan is finished, they are NOT cleaned up
* When restarting an existing plan after an interruption, plan mode MUST be entered
    * The remaining items are laid out again according to the prescribed scheme
