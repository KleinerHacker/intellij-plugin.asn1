---
name: icons
description: Create new icons for ASN.1 plugin
---

# Source Logo

* The logo `docs/docs/assets/images/logo.png` is the starting point of ALL branding (plugin icon, readme, MkDocs)
* Brand colors are taken from the logo: violet-to-blue and magenta gradients, dark navy tile (`#0F0A3C`), white text
* The plugin icon `plugin/src/main/resources/META-INF/pluginIcon.svg` is an SVG with an embedded raster image
  (`<image xlink:href="data:image/png;base64,...">`, 40x40 viewBox, 80x80 pixel PNG) generated from the logo
    * It MUST be regenerated from the logo whenever the logo changes
    * The file MUST stay small (a few KB)

# Style

If you need a new icon, look at the existing `language/src/main/resources/icons/*.svg` icons and reuse their
color, stroke, fill, shape and size for the new icon.

# Usage

1. Create a new SVG file in `src/main/resources/icons/` with a name that reflects the purpose of the icon.
2. Use the existing icons as a reference for color, stroke, fill, shape, size, and other design elements.
3. Ensure that the new icon is consistent with the overall style of the existing icons and the source logo.
4. Add it to the local `*Icons` class in the root package to use it in the plugin.
