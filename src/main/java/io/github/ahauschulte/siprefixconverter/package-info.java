/**
 * Conversion utilities for numeric values annotated with International System of Units (SI) prefixes.
 *
 * <p>The package is annotated with {@code @NullMarked} from <em>JSpecify</em>, which means all type usages are
 * non-null by default. Passing {@code null} where a value is required is a programmer error and will lead
 * to a {@link java.lang.NullPointerException NullPointerException} at runtime if checks are triggered.
 *
 * <p><b>Quick start</b>
 * {@snippet :
 * import io.github.ahauschulte.siprefixconverter.SiPrefix;
 * import io.github.ahauschulte.siprefixconverter.SiPrefixConverter;
 *
 * // Convert 12 kilometres to millimetres (result: 12_000_000.0)
 * double millimeters = SiPrefixConverter.convert(SiPrefix.KILO, SiPrefix.MILLI, 12.0);
 *
 * // Convert 1_000_000 nanoseconds to milliseconds using longs (truncation towards zero)
 * long ms = SiPrefixConverter.convert(SiPrefix.NANO, SiPrefix.MILLI, 1_000_000L); // -> 1L
 *
 * // Convert 1_000_000 nanoseconds to milliseconds using ints (truncation towards zero)
 * int ms = SiPrefixConverter.convert(SiPrefix.NANO, SiPrefix.MILLI, 1_000_000); // -> 1
 *
 * // Reusable converter: from milli to unit for BigInteger
 * var milliToUnit = SiPrefixConverter.builder()
 *         .forBigInteger()
 *         .fixedConverter(SiPrefix.MILLI, SiPrefix.UNIT);
 * java.math.BigInteger exact = milliToUnit.convert(java.math.BigInteger.valueOf(250_000)); // -> 250
 *}
 *
 * <p><b>Behaviour and limits</b>
 * <ul>
 *   <li><b>Doubles</b>: supports the full SI range from quecto (10^-30) to quetta (10^30).</li>
 *   <li><b>Longs</b>: uses integer arithmetic; scaling down truncates towards zero; conversion factors &gt; 10^18
 *   for up-scaling throw {@link java.lang.ArithmeticException ArithmeticException}.</li>
 *   <li><b>Ints</b>: uses integer arithmetic; scaling down truncates towards zero; conversion factors &gt; 10^9
 *   for up-scaling throw {@link java.lang.ArithmeticException ArithmeticException}.</li>
 *   <li><b>BigInteger</b>: supports the full SI range from quecto (10^-30) to quetta (10^30).</li>
 * </ul>
 */
@NullMarked
package io.github.ahauschulte.siprefixconverter;

import org.jspecify.annotations.NullMarked;