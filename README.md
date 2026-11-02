# MC-MLXXXIV-jump
Go ahead and jump!  Minecraft Fabric mod adding configurable auto-jumping capabilities.

## Building

Requires Java 25 (`mise install` picks it up from `mise.toml`).

```sh
./gradlew build      # compiles, runs the unit tests, and writes the jar to build/libs/
./gradlew runClient  # launches a Minecraft 26.2 dev client with the mod loaded
```

### `jump_run_dir`

By default the dev client keeps its world, options and keybinds in `run/` under the directory Gradle was run from, so each worktree starts empty. To share one run directory across every checkout on a machine, set an absolute path in `~/.gradle/gradle.properties`:

```properties
jump_run_dir=/Users/you/mcmlxxxiv-jump-run
```

Leaving it unset or blank keeps `run/`.
