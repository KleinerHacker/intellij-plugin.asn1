---
name: github-pipeline
description: Job structure of the GitHub Actions pipelines ci.yml and release.yml. Use when editing workflows in .github or when a structural change affects them.
---

# GitHub Pipeline

* GitHub MUST be used
* All files around GitHub reside in `.github`
* For deeper structural changes the pipeline MUST be checked and adjusted if necessary

## ci.yml

* There MUST be a pipeline for the regular build in `ci.yml`
    * It contains: [Build] Build -> Test (developer / integration in parallel), [Verify plugin] directly after Build, [Verify] Licences / Signature / Build and verify MkDocs
    * `Verify plugin` depends on `Build` only and MUST run in parallel to `Test`, not after it
    * `Build` and `Verify` MUST be processed in parallel; everything within `Verify` MUST also run in parallel

## release.yml

* There MUST be a pipeline for a tag based release named `release.yml`
    * It contains: [Changelog] Verify against version -> ([Build] Build -> Test -> Verify plugin, [Verify] Verify licences / Create signature, [MkDocs] Build -> Deploy, [Release] Push artifacts / Deploy to marketplace -> Write release)
    * `Changelog` runs first
    * `Build`, `Verify`, `MkDocs` in parallel afterwards
        * `Verify` internally in parallel as well
    * `Release` at the end
        * Deployment errors MUST be ignored, but shown as a warning
