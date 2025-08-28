# SI Prefix Converter

[![Java CI with Maven](https://github.com/ahauschulte/si-prefix-converter/actions/workflows/maven.yml/badge.svg)](https://github.com/ahauschulte/si-prefix-converter/actions/workflows/maven.yml)
[![CodeQL](https://github.com/ahauschulte/si-prefix-converter/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/ahauschulte/si-prefix-converter/actions/workflows/github-code-scanning/codeql)

## Summary

Concise utilities for converting numeric values between [SI prefixes](https://en.wikipedia.org/wiki/Metric_prefix) in
Java.  
The public API centres around three types:

- `SiPrefix` — an enum of SI prefixes from **quecto** (10⁻³⁰) to **quetta** (10³⁰); `UNIT` represents 10⁰.
- `SiPrefixConverter` — static, thread‑safe conversion methods for `double`, `long`, `int`, and `BigInteger`, plus a
  builder entry point.
- `SiPrefixConverterBuilder` — a small, type‑safe builder that produces reusable converters (fix source/target prefixes,
  or both).

This package is annotated with **`@NullMarked`** (JSpecify), i.e. types are **non‑null by default**. Passing `null` to
API methods is a programming error and results in `NullPointerException`.

## Motivation

If you only need to scale numbers by decimal SI prefixes, a full measurement framework—such as javax.measure (JSR-385)
and [Indriya](https://unitsofmeasurement.github.io/indriya/)—may be more than you need. This library provides a small
API for predictable SI prefix conversion across the full range from quecto to quetta, designed for clarity and steady
performance. For primitives, it avoids allocations, and its behaviour is explicit: overflow-checked; integer downscaling
truncates towards zero.

By contrast, javax.measure/Indriya are comprehensive unit-and-quantity frameworks with dimensional analysis, unit
algebra, formatting, and localisation. Prefer them when you need those capabilities. Choose this library when you simply
rescale existing numbers and value a minimal footprint; both approaches can comfortably coexist in the same codebase.

## Features

- Full SI range from **quecto** (10⁻³⁰) to **quetta** (10³⁰).
- `double`, `int`, `long`, and `BigInteger` support.
- **Zero allocations** in the hot path for conversions of primitive types (i.e. `long`, `int`, `double`).
- Zero mandatory runtime dependencies
- Reusable converters via a **builder** API (fix source/target/both).
- **Non‑null‑by‑default** (`@NullMarked` with [NullAway](https://github.com/uber/NullAway)/
  [Error Prone](https://github.com/google/error-prone) configured).
- Clear failure modes: integer conversions throw on excessive factors or overflow during conversion.

## Dependencies

This library is designed to be completely self-contained with **zero mandatory runtime dependencies**.  
To improve API clarity and developer experience, it makes use of [JSpecify](https://jspecify.dev/) annotations
to declare nullness contracts. The JSpecify dependency is declared as *optional* and is not required at runtime.
Consumers may include it in their own build if they want to take advantage of nullness information in IDEs
or static analysis tools, but the core functionality of the library does not depend on it.

## Requirements

- **Java 21+** (Maven compiler `release` set to 21).
- Build tool: Maven 3.9+.

## Installation

This project uses Maven coordinates from the POM:

```xml

<dependency>
    <groupId>io.github.ahauschulte.siprefixconverter</groupId>
    <artifactId>si-prefix-converter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

> The artifact is currently a snapshot. If you wish to depend on it immediately, build and install it locally:

```bash
mvn install
```

Then add the dependency to your project as shown above.

## Quick Start

### One‑Shot Conversions

```java
import java.math.BigInteger;

import io.github.ahauschulte.siprefixconverter.SiPrefix;
import io.github.ahauschulte.siprefixconverter.SiPrefixConverter;

// Doubles — full SI range
double metres = SiPrefixConverter.convert(SiPrefix.KILO, SiPrefix.UNIT, 3.2); // 3200.0

        // Longs — integer arithmetic (see rounding semantics below)
        long nanosLong = SiPrefixConverter.convert(SiPrefix.MILLI, SiPrefix.NANO, 1L);    // 1_000_000L

        // Ints — integer arithmetic (see rounding semantics below)
        int nanosInt = SiPrefixConverter.convert(SiPrefix.MILLI, SiPrefix.NANO, 1);    // 1_000_000

        // BigInteger — arbitrary precision
        BigInteger seconds = SiPrefixConverter.convert(SiPrefix.MICRO, SiPrefix.UNIT, BigInteger.valueOf(250_000)); // 250
```

### Reusable Converters via Builders

```java
import java.math.BigInteger;

import io.github.ahauschulte.siprefixconverter.SiPrefix;
import io.github.ahauschulte.siprefixconverter.SiPrefixConverter;

// Fix the source prefix for doubles (kilo → ?)
var fromKilo = SiPrefixConverter.builder()
        .forDouble()
        .fixedSourcePrefixConverter(SiPrefix.KILO);

        double metres2 = fromKilo.convert(SiPrefix.UNIT, 2.5); // 2500.0

        // Fix the target prefix for longs (? → milli)
        var toMilli = SiPrefixConverter.builder()
                .forLong()
                .fixedTargetPrefixConverter(SiPrefix.MILLI);

        long milliMetresLong = toMilli.convert(SiPrefix.UNIT, 1234L); // 1_234_000L

        // Fix the target prefix for ints (? → milli)
        var toMilli = SiPrefixConverter.builder()
                .forInt()
                .fixedTargetPrefixConverter(SiPrefix.MILLI);

        int milliMetresInt = toMilli.convert(SiPrefix.UNIT, 1234); // 1_234_000

        // Fix both for BigInteger (micro → unit)
        var microToUnit = SiPrefixConverter.builder()
                .forBigInteger()
                .fixedConverter(SiPrefix.MICRO, SiPrefix.UNIT);

        BigInteger exact = microToUnit.convert(java.math.BigInteger.valueOf(250_000)); // 250
```

## API Overview

- **`SiPrefix`** — enum constants representing SI prefixed (e.g. `KILO`, `MILLI`).  
  The enum covers *quecto* to *quetta*. `UNIT` indicates "no prefix".
- **`SiPrefixConverter`**
    - `convert(SiPrefix source, SiPrefix target, double value): double`
    - `convert(SiPrefix source, SiPrefix target, long value): long`
    - `convert(SiPrefix source, SiPrefix target, int value): int`
    - `convert(SiPrefix source, SiPrefix target, BigInteger value): BigInteger`
    - `builder(): SiPrefixConverterBuilder.BuilderChoice`
- **`SiPrefixConverterBuilder`**
    - `forDouble() / forLong() / forInt() /forBigInteger()` → type‑specialised builders
    - `fixedSourcePrefixConverter(SiPrefix source)`
    - `fixedTargetPrefixConverter(SiPrefix target)`
    - `fixedConverter(SiPrefix source, SiPrefix target)`

All methods are **thread‑safe** and expect **non‑null** arguments (package‑level `@NullMarked`).

## Rounding Semantics (long, int, and BigInteger Conversions)

- When *downscaling* (÷ 10ᵏ), the result is **truncated towards zero** (no rounding).

This behaviour is fast and predictable for counters, timestamps, and sizes.

## Overflow Semantics (long and int Conversions)

- When *upscaling* (× 10ᵏ), the value must not overflow `long` or `int`. If the factor exceeds **10¹⁸** for a long
  conversion, **10⁹** for an int conversion or the conversion calculation overflows, an `ArithmeticException` is thrown.

## Performance Notes

This library

- Uses precomputed conversion factors and simple multiplies/divides.
- Avoids heap allocations in normal operation when converting primitive values (i.e. `long`, `int`, `double`).
- Performs minimal branching; friendly to JIT in hot code paths.

All conversions run in the **nanosecond range**. For primitive types, performance is effectively free (~1 ns/op), while
`BigInteger` shows the expected overhead of arbitrary-precision arithmetic.

| Type           | Typical conversion (ns/op) |
|----------------|----------------------------|
| **int**        | 1.0-1.8                    |
| **long**       | 1.0-1.8                    |
| **double**     | 0.8–1.3                    |
| **BigInteger** | 7.1–17.5                   |

### Detailed Benchmarking Results

Benchmarks were conducted using [JMH](https://openjdk.org/projects/code-tools/jmh/) (JDK 21.0.4, OpenJDK 64-Bit Server
VM, 21.0.4+7-LTS, JMH 1.37, 2 warmup iterations, 4 measurement iterations, 1 fork.)

All conversions run in the nanosecond range, with predictable differences across API styles and data types.

| Type           | Conversion | Static (ns/op) | Builder: Both prefixes fixed | Builder: Source prefix fixed | Builder: Target prefix fixed |
|----------------|------------|----------------|------------------------------|------------------------------|------------------------------|
| **int**        | UNIT:UNIT  | 1.24 ± 0.01    | 0.90 ± 0.04                  | 1.62 ± 0.11                  | 1.53 ± 0.05                  |
|                | GIGA:KILO  | 1.37 ± 0.01    | 1.01 ± 0.04                  | 1.74 ± 0.07                  | 1.75 ± 0.01                  |
|                | KILO:GIGA  | 1.40 ± 0.04    | 1.03 ± 0.08                  | 1.72 ± 0.09                  | 1.76 ± 0.01                  |
| **long**       | UNIT:UNIT  | 1.17 ± 0.01    | 0.90 ± 0.04                  | 1.50 ± 0.05                  | 1.50 ± 0.03                  |
|                | GIGA:KILO  | 1.38 ± 0.03    | 1.04 ± 0.03                  | 1.76 ± 0.04                  | 1.77 ± 0.08                  |
|                | KILO:GIGA  | 1.30 ± 0.01    | 1.01 ± 0.01                  | 1.69 ± 0.03                  | 1.74 ± 0.00                  |
| **double**     | UNIT:UNIT  | 0.96 ± 0.01    | 0.78 ± 0.01                  | 1.23 ± 0.01                  | 1.23 ± 0.01                  |
|                | GIGA:KILO  | 0.97 ± 0.02    | 0.84 ± 0.19                  | 1.26 ± 0.08                  | 1.24 ± 0.06                  |
|                | KILO:GIGA  | 0.91 ± 0.05    | 0.85 ± 0.07                  | 1.23 ± 0.01                  | 1.23 ± 0.00                  |
| **BigInteger** | UNIT:UNIT  | 1.49 ± 0.07    | 1.02 ± 0.07                  | 1.69 ± 0.03                  | 1.70 ± 0.02                  |
|                | GIGA:KILO  | 8.10 ± 0.13    | 7.07 ± 0.21                  | 8.42 ± 0.13                  | 8.45 ± 0.08                  |
|                | KILO:GIGA  | 16.71 ± 0.15   | 15.46 ± 0.14                 | 17.52 ± 3.49                 | 17.11 ± 0.20                 |

(These numbers include only the conversion calls; builder creation cost is not measured).

### Key Performance Findings

- **Static vs. prefix fixing using builders**:
    - *Fixing both prefixes* (converter with source & target locked in) is consistently the fastest variant.
    - *Static methods* perform second-fastest.
    - *Fixing only the source or the target prefix* performs slightly slower.
- **Data types**:
    - `int`, `long`, and `double` conversions all run around **1 ns/op**.
    - `double` conversions are fastest.
    - `BigInteger` conversions are an order of magnitude slower than primitive conversions.
- **Scaling factors**:
    - No significant penalty for down-scaling when using `int`, `long`, and `double`. Actually, up- and down-scaling
      conversions show almost identical performance.
    - For BigInteger, down-scaling takes roughly twice as long as up-scaling.

## Build, Test, Docs, Benchmark

```bash
# Build
mvn package

# Run tests (JUnit 5)
mvn test

# Generate Javadoc (also attaches javadoc JAR via plugin)
mvn javadoc:jar

# Benchmark
mvn jmh:benchmark
```

The project is configured with **Error Prone** and **NullAway** to facilitate null‑safety at compile time.

## Versioning & Compatibility

- The current POM targets **Java 21** (`<maven.compiler.release>21</maven.compiler.release>`).

## Contributing

Issues and pull requests are welcome. Please ensure:

- Code is formatted consistently and passes the build.
- Error Prone/NullAway warnings are addressed.
- New behaviour is covered by unit tests and documented.

## License

**MIT License** — see the [`LICENSE`](./LICENSE) file for details.

## AI Tools Used

This project utilises the assistance of AI tools, specifically ChatGPT by OpenAI, to help generate and refine
documentation. All AI-generated content has been thoroughly reviewed and validated by human contributors to ensure
its accuracy and quality.
