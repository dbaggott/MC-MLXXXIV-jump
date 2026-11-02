# Changelogs

One Markdown file per released version, named `<mod_version>.md` (`0.1.0.md` for `mod_version=0.1.0`). The file is the body of that version's GitHub Release, so write it for players: what changed in the game, not how the code changed.

The file has to exist before the version bump merges. CI fails a PR whose `mod_version` has no changelog here, and the release build refuses to publish without one.
