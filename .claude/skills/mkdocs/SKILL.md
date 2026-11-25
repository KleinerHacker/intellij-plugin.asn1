---
name: mkdocs
description: Layout of the MkDocs documentation under docs/. Use when editing docs/, mkdocs.yml, assets or stylesheets.
---

# MkDocs

* MkDocs MUST be integrated under `docs`
* Structure:
    * `docs/mkdocs.yml` - Root file
    * `docs/docs` - *.MD files
    * `docs/docs/assets` - Further asset files (MUST reside inside `docs_dir` so that MkDocs ships them)
    * `docs/docs/stylesheets` - Additional CSS files
* The documentation MUST be checked after every change and adjusted if necessary
* The ASN.1 specification (ITU-T X.680) is available here: `https://www.itu.int/rec/T-REC-X.680`

## Logo and Colors

* `docs/docs/assets/images/logo.png` is the source logo (see skill `icons`)
* `docs/docs/assets/images/logo-header.png` is the cropped, square 256x256 variant used as header logo and favicon
* The color scheme in `stylesheets/extra.css` MUST follow the logo (violet and magenta) in light and dark mode
