# MC-MLXXXIV-jump
Go ahead and jump!  Minecraft Fabric mod adding configurable auto-jumping capabilities.

## Edge jump

Edge jump jumps for you as you walk toward a ledge, once you are within a set distance of it, and always before you would fall off. It has nothing to do with vanilla's **Auto-Jump** option, which hops you up one-block steps, and it leaves that option alone.

A ledge is wherever sneaking would stop you: the ground ahead drops by more than a step (so walking down a slab or a stair is not a ledge). Only a ledge you can actually walk to counts. A railing, glass pane, iron bars or anything else taller than a step in your way means there is no ledge ahead, so edge jump never throws you over a barrier.

The mod needs [Fabric API](https://modrinth.com/mod/fabric-api) and [Cloth Config](https://modrinth.com/mod/cloth-config). Configure it from the **Mods** screen (with [Mod Menu](https://modrinth.com/mod/modmenu) installed), or edit `config/mcmlxxxiv_jump.json` while the game is closed: the file is read at startup, and the screen and the toggle key save over it. The **Toggle edge jump** key (unbound by default, under Controls → Movement) turns it on and off and shows the new state on the action bar.

| Option | Range | Default | What it does |
|---|---|---|---|
| Enabled | on / off | off | Turns edge jump on. Installing the mod changes nothing until you do. |
| Edge distance | 0.00–1.00 blocks | 0.10 | Distance from the edge at which the jump is triggered. |
| Jitter | on / off | off | Randomly varies the distance from the edge at which the jump is triggered. |
| Jitter amount | 0–50 % | 10 | When jitter is on, how far the distance can vary either way, as a percentage of the edge distance. |
| Auto-jump while sneaking | on / off | off | When off, holding sneak turns edge jump off. When on, sneaking toward a ledge jumps too. |

**How distance is measured.** The edge distance is the travel you have left, in the direction you are moving, before you would start to fall. At 0 the jump comes on your last moment on the ground. Walking along a ledge rather than toward it never triggers a jump. When you move faster than the distance allows for, as sprinting at a small distance does, the jump comes on the last tick you are still on the ground instead, so you never walk off.

**How jitter is applied.** With jitter at *p* %, each jump uses a distance picked uniformly between (1 − *p*/100) and (1 + *p*/100) times the setting: at 10 %, a 0.20 setting jumps somewhere between 0.18 and 0.22. A new distance is picked each time you land, and when you change the settings, so it holds steady while you walk up to a ledge.

## Building

Requires Java 25 (`mise install` picks it up from `mise.toml`).

```sh
./gradlew build      # compiles, runs the unit tests, and writes the jar to build/libs/
./gradlew runClient  # launches a Minecraft 26.2 dev client with the mod loaded
```

### `jump_run_dir`

By default the dev client keeps its world, options and keybinds in `run/` inside the checkout, so each worktree starts empty. To share one run directory across every checkout on a machine, set an absolute path in `~/.gradle/gradle.properties`:

```properties
jump_run_dir=/Users/you/mcmlxxxiv-jump-run
```

Leaving it unset or blank keeps `run/`.
