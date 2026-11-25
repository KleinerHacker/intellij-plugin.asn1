---
name: git and GitHub
---

# GIT

* All changes are made through GIT:
    * Rename / move: `git mv`
    * Delete: `git rm`
    * Create: add with `git add` after creation
* Commits, pushes, pulls or any other actions communicating with the Git server MUST NEVER be invoked.
    * Should it be required, the user MUST be asked
* Exceptions:
    * NEVER add plans or plan status

## Target Environment

* GitHub MUST be used
* All files around GitHub reside in `.github`
* For deeper structural changes the pipelines in `.github` MUST be checked and adjusted if necessary
    * For the required job structure of `ci.yml` and `release.yml` use the skill `github-pipeline`
