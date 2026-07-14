# AGENTS.md

Instructions for coding agents working in the Jervis Fantasy Football
repository. Read this before making changes.

## Project overview
Jervis is a Kotlin Multiplatform implementation of the Blood Bowl board game,
consisting of a UI-agnostic rules engine, a Compose Multiplatform client
(Desktop/Web/iPad), a lightweight game server, and adapters for the FUMBBL
platform. Requires **Java 21** and the Gradle wrapper (`./gradlew`).

Read [`CONTRIBUTING.md`](CONTRIBUTING.md) for instructions on how to build
and test the project.

More background lives in [`README.md`](README.md) and `docs/`:

- `docs/architecture-overview-faq.md` — high-level design goals.
- `docs/architecture-rules-engine.md` — engine architecture (start here for
  engine work).
- `docs/architecture-network.md` — describe network architecture and the 
  network protocol.
- `docs/architecture-ui.md` — describes the high-level concepts of the UI layer.

## Project Structure
See [`README.md`](README.md#repository-structure) for an overview of how the
project is structured.

See [`README.md`](README.md#modules-structure) for an overview of how the 
code modules are structured under `modules/`.

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
Run the Fuzz Tester when adding new non-trivial rules. 
See [`CONTRIBUTING.md`](CONTRIBUTING.md#fuzz-tester) for instructions on how to
run it.

## Formatting and linting
Run the formatter and linter before declaring any work done.
See [CONTRIBUTING.md](CONTRIBUTING.md#formatting-code) for instructions on how
to run them.

## Before submitting a PR
Before submitting a PR to GitHub, make sure to run through the checklist found
in [`CONTRIBUTING.md`](CONTRIBUTING.md#pr-checklist).

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
  but keep the quoted text short (avoid copyright). Do not try to guess a page 
  number, just use XXX as a placeholder.
- An online rulebook can be found here: https://bloodbowlbase.ru/bb2025/
