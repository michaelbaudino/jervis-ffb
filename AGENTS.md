# AGENTS.md

Instructions for coding agents working in the Jervis Fantasy Football
repository. Read this before making changes.

## Project overview

Jervis is a Kotlin Multiplatform implementation of the Blood Bowl board game,
consisting of a UI-agnostic rules engine, a Compose Multiplatform client
(Desktop/Web/iPad), a lightweight game server, and adapters for the FUMBBL
platform. Requires **Java 21** and the Gradle wrapper (`./gradlew`).

More background lives in `README.md` and `docs/`:

- `docs/architecture-overview-faq.md` — high-level design goals.
- `docs/architecture-rules-engine.md` — engine architecture (start here for
  engine work).
- `docs/architecture-network.md`, `docs/architecture-ui.md` — network and UI
  layers.

## Module layout (`modules/`)

- `jervis-engine` — the Blood Bowl rules engine and game model. Pure Kotlin,
  no UI. Where most engine work happens.
- `jervis-ui` — Compose Multiplatform client (`shared`, `desktopApp`,
  `webApp`, `iosApp`). All code should go to `shared` unless it is 
   platform-specific.
- `jervis-net` — network code for the lightweight game server.
- `jervis-test-utils` — shared test helpers (works around
  [KT-35073](https://youtrack.jetbrains.com/issue/KT-35073)).
- `jervis-resources` — shared resource module containing team and roster setups.
- `platform-utils` — platform-specific helpers (filesystem, networking, 
   reflection).
- `fumbbl-net` — helper classes for communicating with the Fumbbl API.
- `tourplay-net` — helper classes for communicating with the TourPlay API.
- `fumbbl-cli` - small CLI tool for interacting with the Fumbbl API from the 
   commandline
- `replay-analyzer` — adapter for analyzing Fumbbl replays. Highly experimental.

## Engine architecture (essentials)

The engine is a deterministic finite state machine driven by `GameAction`s.
Three layers:

1. **`GameEngineController`** — driver. Exposes `getAvailableActions()` and
   `handleAction(action)`. Does not care where actions come from.
2. **Rules layer** — `Rules` subclasses (`StandardBB2020Rules`,
   `StandardBB2025Rules`, `BB72020Rules`, ...) plus `Procedure`/`Node` classes
   under `engine/rules/**/procedures/`. **Stateless** — all mutations happen
   via `Command` objects so state can be replayed/undone.
3. **Game state** — the `Game` class in `engine/model/` holds the full
   snapshot, including the procedure stack and per-procedure `ProcedureContext`
   entries.

Rulesets are versioned side-by-side: `engine/rules/bb2020/` and
`engine/rules/bb2025/` are parallel implementations. When editing rules,
check whether a change needs mirroring in the other ruleset — the git status
of an in-progress branch is often a good hint (both packages tend to move
together).

Rules shared between BB2025 and BB2020 are placed in `engine/rules/common`.

Some guidelines when creating new procedures:

- Dice rolls should be implemented using `D3WithRerollProcedure`/
  `D6WithRerollProcedure` when applicable
- Avoid using `ComputationNode` if possible. `ParentNode` has `skipNodeFor`
  for the most common scenario where ComputationNodes are used.
- ActionNodes do not need to have extensive input checking, outside casting to 
  the appropriate type. Input-validation is done through 
  `GameController.validateAction(action)`.
- Naming of Procedures should follow terminology and keywords from the rulebook.

## Running tests

CI runs `./gradlew jvmTest` (see `.github/workflows/test.yml`). Match that
locally before pushing:

```shell
./gradlew jvmTest
```

Useful variants:

- Single module: `./gradlew :modules:jervis-engine:jvmTest`
- Single test class:
  `./gradlew :modules:jervis-engine:jvmTest --tests "com.jervisffb.test.bb2025.GameProgressTests"`
- Single test method: append `.testMethodName` to the class filter.

Engine test helpers of note:

- `JervisGameBB2020Test` / `JervisGameBB2025Test` — abstract bases that spin
  up a controller and expose `state`, `controller`, `homeTeam`, `awayTeam`.
- `controller.rollForward(...)` — replay a sequence of `GameAction`s
  (`defaultPregame()`, `defaultSetup()`, `defaultKickOffHomeTeam()`, etc. are
  provided).
- `com.jervisffb.engine.ext.d6/d8/d3` — build dice roll actions
  (`4.d6`, `DiceRollResults(4.d6, 5.d6)`).

### Fuzz testing

`modules/jervis-engine/src/commonTest/kotlin/com/jervisffb/test/FuzzTester.kt`
runs random games against the engine to catch crashes. It is `@Ignore`d by
default — comment out the annotation to run. When using it:

- Set `com.jervisffb.utils.DEFAULT_LOG_LEVEL` to `Severity.Assert` first;
  logging dominates runtime otherwise. `runRandomBB2025Games` will error out
  if you forget.
- Failures print the game index and RNG seed
  (`fail("Game $gameNo (seed: $seed) ...")`) — reuse the seed to reproduce.
- Average game runs ~4–5 ms on an M3; tests are parallel across 8 threads but
  memory-hungry, so pay attention when raising `games` / `batchSize`.
- Run it after non-trivial rules-engine changes; it exercises paths unit
  tests will not.

## Formatting and linting

The project uses [ktlint](https://github.com/pinterest/ktlint) via the Gradle
plugin:

```shell
./gradlew ktlintCheck     # verify style
./gradlew ktlintFormat    # auto-fix
```

`ktlintCheck` runs as part of `./gradlew check`. Run `ktlintFormat` (or the
per-module variant, e.g. `:modules:jervis-engine:ktlintFormat`) before
opening a PR.

## Before submitting a PR

1. `./gradlew ktlintFormat`
2. `./gradlew jvmTest` (matches CI)
3. If touching the rules engine substantially, un-ignore `FuzzTester` and run
   `FuzzTester.runRandomBB2025Games()` to catch crashes on random paths.
4. If touching both `bb2020/` and `bb2025/`, verify the change is applied
   consistently across both packages.
5. Update or add tests under `modules/jervis-engine/src/commonTest/` — the
   existing tests use `rollForward(...)` + assertions on `state`, mirror that
   style.
6. Do not commit generated files under `build/` or resource copies pulled in
   by the `updateFFBResources` task unless that is the intent of the change.

## Running the app locally

Only needed for UI changes; not required for engine-only PRs.

- Desktop: `./gradlew :modules:jervis-ui:desktopApp:run`
- Web (Wasm): `./gradlew :modules:jervis-ui:webApp:wasmJsBrowserDevelopmentRun`
- iOS: open `modules/iosApp/iosApp.xcodeproj` in Xcode 16.2 and set a
  signing identity.

Some settings code is generated at build time; unresolved references on a
fresh clone usually resolve after the first successful build.

## Conventions

- Avoid platform-specific code unless absolutely necessary. If needed, all 
  platform-specific code must be in `platform-utils` behind an expect/actual 
  interface. 
- Follow existing patterns in neighboring files rather than introducing new
  ones. The rules engine leans heavily on the `Procedure` / `Node` / `Command`
  pattern — new rules should extend it, not sidestep it.
- Keep the engine UI-free. All UI code should be in `jervis-ui`.
- Prefer editing existing files over creating new ones; do not add documentation 
  files unless asked.
- Reference the Blood Bowl rulebook page when adding non-obvious rule logic,
  but keep quoted text short (avoid copyright). Do not try to guess a page 
  number, just use XXX as a placeholder.
- An online rulebook can be found here: https://bloodbowlbase.ru/bb2025/
