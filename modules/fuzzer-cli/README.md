# Fuzzer CLI

The Fuzzer CLI drives the Jervis rules engine through large batches of games
built from random actions to force crashes or inconsistent state. Any errors
will be logged to the console with the offending game seed. 

## Usage

First build the `fuzzercli.jar` using Gradle: `./gradlew buildTools`. This will
build the CLI and place the `fuzzercli.jar` file in the `<root>/tools` folder.

This will embed the current version of the rules in the project. If these are
updated later, it is necessary to run `./gradlew buildTools` again to update
the fuzzer CLI.

Call the JAR file either directly with `java -jar tools/fuzzercli.jar` or via
the helper script placed at the root of this project: `./fuzzer-cli <options>`.

```
Usage: fuzzer-cli [<options>] <fuzzer-configuration>

  Fuzz-test the Jervis rules engine by running batches of randomised games.

Options:
  --games=<int>       Number of games to run. Default: 100.000
  --batch-size=<int>  Games per parallel batch. Default: 5.000
  --seed=<int>        Seed for the RNG. Combine with --games 1 to reproduce a
                      specific game.
  --threads=<int>     Size of the parallel worker pool. Default: 8
  -h, --help          Show this message and exit

Arguments:
  <fuzzer-configuration>  Which fuzzer configuration to run. Should be one of:
                          [bb2020, bb2025, bb2020-bb7]
```

## Reproducing a crash

When a game crashes, the CLI prints the `gameNo` and the `seed` that produced
it, then continues fuzzing the rest of the batch. To reproduce the exact game
locally, rerun with the reported seed against a single game:

```
./fuzzer-cli --games 1 --seed "<reported-seed>" bb2025
```

## Run from Gradle

It is possible to run the fuzzer through Gradle. This approach will always
run against the current changes in the project but will be slightly slower to
start:

```shell
./gradlew :modules:fuzzer-cli:run --args='--games 100000 --batch-size 5000 bb2025'
```

## IntelliJ Run Configurations

To make it easier to use the fuzzer during development, IntelliJ has a 
"Fuzz Tester" run configuration group that makes it easy to run the common
fuzz tests.

There is also a "Debug Fuzz Test" run configuration that be used to debug
a failed fuzz test. Edit the configuration to configure the ruleset and seed 
and run it in Debug mode.

## Notes

- The CLI automatically forces the Logger log level to `Severity.Assert`. 
  Logging otherwise dominates runtime and can slow the fuzzer down by an 
  order of magnitude.
- An average game runs in ~4–5 ms on an Apple M3. Increasing `--games`,
  `--batch-size`, or `--threads` raises memory pressure. You might need to 
  adjust memory settings if OOM errors occur.
