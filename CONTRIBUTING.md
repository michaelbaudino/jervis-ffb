# Contributing to Jervis FFB
This document covers how to build, test and run the various parts of the Jervis 
project.

For a more in-depth technical and architectural background, see the documents
under [`docs/](docs/)
see [`README.md`](README.md) and the 
deeper design
notes under [`docs/`](docs/). Coding agents should also read
[`AGENTS.md`](AGENTS.md) for engine and rule-authoring conventions.


## Requirements
Development requirements are:
- Java 21
- Git on the commandline
- Xcode 16.2 (if building for iPad)
- The project has been developed in IntelliJ IDEA, while optional, it will
  provide a better experience.

Note, this has only been tested on Mac and Windows. Linux has not been 
tested thoroughly, but _should_ work.

Some code for handling game settings is generated as part of the build process,
so unresolved references might be present when opening this project for the
first time. This should be fixed after the first build.

## How To Build

### Building Game Clients
A local desktop game client can be started using:

```shell
./gradlew :modules:jervis-ui:desktopApp:run
```

A local WASM client can be started using:

```shell
./gradlew :modules:jervis-ui:webApp:wasmJsBrowserDevelopmentRun
```

The iPad version can be built and installed using Xcode. Use the project file
found here [`modules/iosApp/iosApp.xcodeproj`](modules/iosApp/iosApp.xcodeproj).
You will need to supply your own signature under "Signing & Capabilities".

With the `Koltlin Multiplatform` plugin installed in IntelliJ, it can also be
started from there using the `iosApp` run configuration.

### Building Documentation Website
The documentation website is built by installing Zensical:

```shell
pip install zensical
```

And then running the following command from the root of the repository:
```shell
# Website is visible on localhost:8080
zensical serve
```

## Running tests

While multiplatform supports running tests, for practical reasons we mostly only
run unit tests on the JVM target as this is considered the primary target. This
might change in the future.

Run all tests across all modules using 

```shell
./gradlew jvmTest
```

Useful variants when iterating:

- Single module: 
  ```
  ./gradlew :modules:jervis-engine:jvmTest
  ```
- Single test class:
  ```
  ./gradlew :modules:jervis-engine:jvmTest --tests "com.jervisffb.test.bb2025.GameProgressTests"
  ```
  
### Fuzz Tester
For non-trivial changes to the rule engine, also consider running the fuzz
tester. It is disabled by default and needs to be enabled first.

`modules/jervis-engine/src/commonTest/kotlin/com/jervisffb/test/FuzzTester.kt`
runs random games against the engine to force crashes or inconsistent state
It is run using the junit test frame and is`@Ignore`d by default - comment 
out the annotation to run. When using it:

- Set `com.jervisffb.utils.DEFAULT_LOG_LEVEL` to `Severity.Assert` first;
  logging dominates runtime otherwise. `runRandomBB2025Games()` will throw if
  you forget.
- The Fuzz Tester will generate random seeds for all runs that will be used
  to generate all random actions. If a crash occurs, the seed is printed, making
  it possible to reproduce the crash.
- An average game runs in ~4–5 ms on an Apple M3, tests are parallel across 8 
  threads but memory-hungry, so beware of OOM errors when raising `games` / 
  `batchSize`.

## Formatting code
The project uses [ktlint](https://github.com/pinterest/ktlint) via the Gradle
plugin. `ktlintCheck` runs as part of `./gradlew check` and CI will fail on
style violations.

Run the formatter before opening a PR:

```shell
./gradlew ktlintFormat
```

To only verify without modifying files:

```shell
./gradlew ktlintCheck
```

## Updating UI resources
The Jervis client borrows a large portion of its icons and sounds from the
[FUMBBL client](https://github.com/christerk/ffb) (used with permission — see
[FUMBBL attribution](https://fumbbl.com/p/attribution)). These assets live
under [`modules/jervis-ui/shared/src/commonMain/composeResources`](modules/jervis-ui/shared/src/commonMain/composeResources)
and can be re-synced with upstream FUMBBL using:

```shell
./gradlew :modules:jervis-ui:shared:updateFFBResources
```

The task clones the FFB repository into `build/ffb-repo`, flattens its resource
directory into a layout that Compose Multiplatform can consume, and copies the
result into the project. It also refreshes `icons.ini`, which maps local paths
to their remote FUMBBL URLs. Player icons and portraits are intentionally *not*
copied — their licensing status is unclear, so they are loaded and cached at
runtime instead. See [CMP-4196](https://youtrack.jetbrains.com/issue/CMP-4196)
for a background on why the flattening step is necessary.

The `icons.ini` file can be updated manually by using this command from 
the root of the project:

```shell
curl -fL https://raw.githubusercontent.com/christerk/ffb/refs/heads/master/ffb-client/src/main/resources-live/icons.ini -o modules/jervis-ui/shared/src/commonMain/composeResources/files/fumbbl/icons.ini
```

copying the contents of
[`icons.ini`]([modules/jervis-ui/shared/src/commonMain/composeResources/files/fumbbl/icons.ini](https://github.com/christerk/ffb/blob/4d834d6a31e87d9b78997af8017aad46c9ec536c/ffb-client/src/main/resources-live/icons.ini)
into [`icons-extra.ini`](modules/jervis-ui/shared/src/commonMain/composeResources/files/fumbbl/icons-extra.ini)
using the same `url=local/path` format.

Extra icons that are not defined in FUMBBL's `icons.ini` but should still be
loaded from the network can be added to[`icons-extra.ini`](modules/jervis-ui/shared/src/commonMain/composeResources/files/fumbbl/icons-extra.ini)
using the same `url=local/path` format.

## AI Agents
AI Agents have been used during the development of this repository, and 
contributions created using these are fine as well, with one warning: It is 
expected that any PR has actually been reviewed by a human. "AI-slop" will 
be closed without warning.

A [AGENTS.md](./AGENTS.md) file is included in this repository, which provides
additional guidance on how to use AI Agents effectively, but beware that this
file is very much a work-in-progress and currently provides little guidance.

Some notes when using these:
- AI Agents do not fully grasp the often conflicting rules and will make 
  errors when implementing these. Do not expect them to one-shoot any Procedures
  and associated tests. You will end up overspecifying things to a degree where
  it is faster to implement them manually.
- AI Agents are fairly capable when working with Compose, and with some 
  guidance they can speed up building UI elements. But beware of the difference
  between Jetpack Compose and Compose Multiplatform. There are subtle 
  differences, and AI will make mistakes here, especially on more niche 
  framework features.

## PR checklist
Before creating a PR against Jervis, run through the following steps:

1. `./gradlew ktlintFormat`
2. `./gradlew jvmTest`
3. If you touched both `engine/rules/bb2020/` and `engine/rules/bb2025/`, verify
   the change is mirrored consistently across both rulesets.
4. All changes that add to or change the the rules implementation should have 
   associated unit tests in `modules/jervis-engine/src/commonTest/` 
5. Do not commit generated files under `build/` or resource copies produced by
   `updateFFBResources` unless updating those resources is the point of the PR.
