# SI Prefix Converter

[![Java CI with Maven](https://github.com/ahauschulte/si-prefix-converter/actions/workflows/maven.yml/badge.svg)](https://github.com/ahauschulte/si-prefix-converter/actions/workflows/maven.yml)
[![CodeQL](https://github.com/ahauschulte/si-prefix-converter/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/ahauschulte/si-prefix-converter/actions/workflows/github-code-scanning/codeql)


## Summary

Concise utilities for converting numeric values between [SI prefixes](https://en.wikipedia.org/wiki/Metric_prefix) in Java.  
The public API centres around three types:

- `SiPrefix` — an enum of SI prefixes from **quecto** (10⁻³⁰) to **quetta** (10³⁰); `UNIT` represents 10⁰.
- `SiPrefixConverter` — static, thread‑safe conversion methods for `double`, `long`, and `BigInteger`, plus a builder
  entry point.
- `SiPrefixConverterBuilder` — a small, type‑safe builder that produces reusable converters (fix source/target prefixes,
  or both).

This package is annotated with **`@NullMarked`** (JSpecify), i.e. types are **non‑null by default**. Passing `null` to
API methods is a programming error and results in `NullPointerException`.

## Motivation

If you only need to scale numbers by decimal SI prefixes, a full measurement framework—such as javax.measure (JSR-385)
and Indriya—may be more than you need. This library provides a small API for predictable SI prefix conversion across
the full range from quecto to quetta, designed for clarity and steady performance. For primitives, it
avoids allocations, and its behaviour is explicit: overflow-checked; integer downscaling truncates towards zero.

By contrast, javax.measure/Indriya are comprehensive unit-and-quantity frameworks with dimensional analysis, unit
algebra, formatting, and localisation. Prefer them when you need those capabilities. Choose this library when you simply
rescale existing numbers and value a minimal footprint; both approaches can comfortably coexist in the same codebase.

## Features

- Full SI range from **quecto** (10⁻³⁰) to **quetta** (10³⁰).
- **Zero allocations** in the hot path for conversions of primitive types (i.e. `long`, `double`).
- `double`, `long`, and `BigInteger` support.
- Reusable converters via a **builder** API (fix source/target/both).
- **Non‑null‑by‑default** (`@NullMarked` with NullAway/Error Prone configured).
- Clear failure modes: integer conversions throw on excessive factors or overflow during conversion.

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

## Quick start

### One‑shot conversions

```java
import java.math.BigInteger;

import io.github.ahauschulte.siprefixconverter.SiPrefix;
import io.github.ahauschulte.siprefixconverter.SiPrefixConverter;

// Doubles — full SI range
double metres = SiPrefixConverter.convert(SiPrefix.KILO, SiPrefix.UNIT, 3.2); // 3200.0

// Longs — integer arithmetic (see rounding semantics below)
long nanos = SiPrefixConverter.convert(SiPrefix.MILLI, SiPrefix.NANO, 1L);    // 1_000_000L

// BigInteger — arbitrary precision
BigInteger seconds = SiPrefixConverter.convert(SiPrefix.MICRO, SiPrefix.UNIT, java.math.BigInteger.valueOf(250_000)); // 250
```

### Reusable converters (builder)

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

long milliMetres = toMilli.convert(SiPrefix.UNIT, 1234L); // 1_234_000L
        
// Fix both for BigInteger (micro → unit)
var microToUnit = SiPrefixConverter.builder()
        .forBigInteger()
        .fixedConverter(SiPrefix.MICRO, SiPrefix.UNIT);

BigInteger exact = microToUnit.convert(java.math.BigInteger.valueOf(250_000)); // 250
```

## API overview

- **`SiPrefix`** — enum constants representing SI prefixed (e.g. `KILO`, `MILLI`).  
  The enum covers *quecto* to *quetta*. `UNIT` indicates "no prefix".
- **`SiPrefixConverter`**
    - `convert(SiPrefix source, SiPrefix target, double value): double`
    - `convert(SiPrefix source, SiPrefix target, long value): long`
    - `convert(SiPrefix source, SiPrefix target, BigInteger value): BigInteger`
    - `builder(): SiPrefixConverterBuilder.BuilderChoice`
- **`SiPrefixConverterBuilder`**
    - `forDouble() / forLong() / forBigInteger()` → type‑specialised builders
    - `fixedSourcePrefixConverter(SiPrefix source)`
    - `fixedTargetPrefixConverter(SiPrefix target)`
    - `fixedConverter(SiPrefix source, SiPrefix target)`

All methods are **thread‑safe** and expect **non‑null** arguments (package‑level `@NullMarked`).

## Rounding semantics (long and BigInteger conversions)

- When *downscaling* (÷ 10ᵏ), the result is **truncated towards zero** (no rounding).

This behaviour is fast and predictable for counters, timestamps, and sizes. If you require specific rounding rules or
exact decimal arithmetic, prefer `double` for now.

## Overflow semantics (long conversions)

- When *upscaling* (× 10ᵏ), the value must not overflow `long`. If the factor exceeds **10¹⁸** or
  the conversion calculation overflows, an `ArithmeticException` is thrown.

## Performance notes

- Uses precomputed conversion factors and simple multiplies/divides.
- Avoids heap allocations in normal operation when converting primitive values (i.e. `long`, `double`).
- Minimal branching; friendly to JIT in hot code paths.

## Build, test, docs

```bash
# Build
mvn package

# Run tests (JUnit 5)
mvn test

# Generate Javadoc (also attaches javadoc JAR via plugin)
mvn -q javadoc:javadoc
```

The project is configured with **Error Prone** and **NullAway** to facilitate null‑safety at compile time.

## Versioning & compatibility

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
