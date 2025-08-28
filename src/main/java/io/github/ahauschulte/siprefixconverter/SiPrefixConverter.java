package io.github.ahauschulte.siprefixconverter;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.*;

/**
 * Utility for converting numeric values between different SI prefixes.
 *
 * <p>Supports {@code double}, {@code long}, and {@link java.math.BigInteger} inputs.
 *
 * <p><b>Examples</b>
 * {@snippet :
 * import io.github.ahauschulte.siprefixconverter.SiPrefix;
 * import io.github.ahauschulte.siprefixconverter.SiPrefixConverter;
 *
 * // Simple one-shot conversions
 * double metres = SiPrefixConverter.convert(SiPrefix.KILO, SiPrefix.UNIT, 3.2); // 3200.0
 * long nanos    = SiPrefixConverter.convert(SiPrefix.MILLI, SiPrefix.NANO, 1L); // 1_000_000L
 *
 * // Builder API: fix source or target and reuse the converter
 * var fromKilo = SiPrefixConverter.builder().forDouble().fixedSourcePrefixConverter(SiPrefix.KILO);
 * double metres2 = fromKilo.convert(SiPrefix.UNIT, 2.5); // 2500.0
 *
 * var toMilli = SiPrefixConverter.builder().forLong().fixedTargetPrefixConverter(SiPrefix.MILLI);
 * long milliMetres = toMilli.convert(SiPrefix.UNIT, 1234L); // 1_234_000L
 *}
 *
 * <p>Under {@code @NullMarked} (package level), all parameters are non-null by default. Passing {@code null} is a
 * programmer error and leads to a {@link NullPointerException}.
 */
public final class SiPrefixConverter {

    private static final String SOURCE_SI_PREFIX_NULL_CHECK_MSG = "sourceSiPrefix must not be null";
    private static final String TARGET_SI_PREFIX_NULL_CHECK_MSG = "targetSiPrefix must not be null";

    /**
     * Converts a {@code double} value from a source SI prefix to a target SI prefix.
     *
     * @param sourceSiPrefix the prefix the {@code sourceValue} is currently expressed in
     * @param targetSiPrefix the prefix to convert to
     * @param sourceValue    the value to convert
     * @return the converted value
     * @throws NullPointerException if any prefix is {@code null}
     */
    public static double convert(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix, final double sourceValue) {
        Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);
        Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

        return doConvertDouble(sourceSiPrefix, targetSiPrefix, sourceValue);
    }

    /**
     * Converts a {@code long} value from a source SI prefix to a target SI prefix.
     *
     * <p>Uses integer arithmetic. When scaling down (division), the result is truncated towards zero.
     * Throws {@link ArithmeticException} if the required conversion factor exceeds 10<sup>18</sup>
     * (for both up and down scaling) or the conversion calculation overflows.
     *
     * @param sourceSiPrefix the prefix the {@code sourceValue} is currently expressed in
     * @param targetSiPrefix the prefix to convert to
     * @param sourceValue    the value to convert
     * @return the converted value (possibly truncated)
     * @throws NullPointerException if any prefix is {@code null}
     * @throws ArithmeticException  if the required conversion factor is beyond 10^18 (for both up and down scaling) or
     *                              the conversion calculation overflows
     */
    public static long convert(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix, final long sourceValue) {
        Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);
        Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

        return doConvertLong(sourceSiPrefix, targetSiPrefix, sourceValue);
    }

    /**
     * Converts an {@code int} value from a source SI prefix to a target SI prefix.
     *
     * <p>Uses integer arithmetic. When scaling down (division), the result is truncated towards zero.
     * Throws {@link ArithmeticException} if the required conversion factor exceeds 10<sup>9</sup>
     * (for both up and down scaling) or the conversion calculation overflows.
     *
     * @param sourceSiPrefix the prefix the {@code sourceValue} is currently expressed in
     * @param targetSiPrefix the prefix to convert to
     * @param sourceValue    the value to convert
     * @return the converted value (possibly truncated)
     * @throws NullPointerException if any prefix is {@code null}
     * @throws ArithmeticException  if the required conversion factor is beyond 10^9 (for both up and down scaling) or
     *                              the conversion calculation overflows
     */
    public static int convert(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix, final int sourceValue) {
        Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);
        Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

        return doConvertInt(sourceSiPrefix, targetSiPrefix, sourceValue);
    }

    /**
     * Converts a {@link java.math.BigInteger} value from a source SI prefix to a target SI prefix.
     *
     * @param sourceSiPrefix the prefix the {@code sourceValue} is currently expressed in
     * @param targetSiPrefix the prefix to convert to
     * @param sourceValue    the value to convert
     * @return the converted value
     * @throws NullPointerException if any of the parameters is {@code null}
     */
    public static BigInteger convert(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix, final BigInteger sourceValue) {
        Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);
        Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

        return doConvertBigInteger(sourceSiPrefix, targetSiPrefix, sourceValue);
    }

    /**
     * Starts a fluent builder for creating reusable converters for a specific numeric type.
     *
     * <p>Choose {@link SiPrefixConverterBuilder.BuilderChoice#forDouble()}, {@link SiPrefixConverterBuilder.BuilderChoice#forLong()}
     * or {@link SiPrefixConverterBuilder.BuilderChoice#forBigInteger()} to obtain type-safe converter factories.
     *
     * @return a type choice for the builder
     */
    public static SiPrefixConverterBuilder.BuilderChoice builder() {
        return BuilderChoiceImpl.INSTANCE;
    }

    private SiPrefixConverter() {
        throw new AssertionError("Instantiation not allowed");
    }

    private static int calculateConversionExponent(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix) {
        return sourceSiPrefix.exponent() - targetSiPrefix.exponent();
    }

    private static double doConvertDouble(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix, final double sourceValue) {
        final int conversionExponent = calculateConversionExponent(sourceSiPrefix, targetSiPrefix);
        final double conversionFactor = ConversionLookupDouble.getConversionFactor(conversionExponent);

        return conversionFactor * sourceValue;
    }

    private static long doConvertLong(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix, final long sourceValue) {
        final int conversionExponent = calculateConversionExponent(sourceSiPrefix, targetSiPrefix);
        final long conversionFactor = ConversionLookupLong.getConversionFactor(conversionExponent);
        final LongBinaryOperator factorApplicationStrategy = ConversionLookupLong.getFactorApplicationStrategy(conversionExponent);

        return factorApplicationStrategy.applyAsLong(sourceValue, conversionFactor);
    }

    private static int doConvertInt(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix, final int sourceValue) {
        final int conversionExponent = calculateConversionExponent(sourceSiPrefix, targetSiPrefix);
        final int conversionFactor = ConversionLookupInt.getConversionFactor(conversionExponent);
        final IntBinaryOperator factorApplicationStrategy = ConversionLookupInt.getFactorApplicationStrategy(conversionExponent);

        return factorApplicationStrategy.applyAsInt(sourceValue, conversionFactor);
    }

    private static BigInteger doConvertBigInteger(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix, final BigInteger sourceValue) {
        Objects.requireNonNull(sourceValue, "sourceValue must not be null");

        final int conversionExponent = calculateConversionExponent(sourceSiPrefix, targetSiPrefix);
        final BigInteger conversionFactor = ConversionLookupBigInteger.getConversionFactor(conversionExponent);
        final BinaryOperator<BigInteger> factorApplicationStrategy = ConversionLookupBigInteger.getFactorApplicationStrategy(conversionExponent);

        return factorApplicationStrategy.apply(sourceValue, conversionFactor);
    }

    private enum SiPrefixDoubleConverterBuilder implements
            SiPrefixConverterBuilder<SiPrefixConverterBuilder.FixedSourceDoubleConverter,
                    SiPrefixConverterBuilder.FixedTargetDoubleConverter,
                    SiPrefixConverterBuilder.FixedDoubleConverter> {
        INSTANCE;

        @Override
        public FixedSourceDoubleConverter fixedSourcePrefixConverter(final SiPrefix sourceSiPrefix) {
            Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);

            return (SiPrefix targetSiPrefix, double sourceValue) -> doConvertDouble(sourceSiPrefix, targetSiPrefix, sourceValue);
        }

        @Override
        public FixedTargetDoubleConverter fixedTargetPrefixConverter(final SiPrefix targetSiPrefix) {
            Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

            return (SiPrefix sourceSiPrefix, double sourceValue) -> doConvertDouble(sourceSiPrefix, targetSiPrefix, sourceValue);
        }

        @Override
        public FixedDoubleConverter fixedConverter(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix) {
            Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);
            Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

            final int conversionExponent = calculateConversionExponent(sourceSiPrefix, targetSiPrefix);
            final double conversionFactor = ConversionLookupDouble.getConversionFactor(conversionExponent);

            return (double sourceValue) -> conversionFactor * sourceValue;
        }
    }

    private enum SiPrefixLongConverterBuilder implements
            SiPrefixConverterBuilder<SiPrefixConverterBuilder.FixedSourceLongConverter,
                    SiPrefixConverterBuilder.FixedTargetLongConverter,
                    SiPrefixConverterBuilder.FixedLongConverter> {
        INSTANCE;

        @Override
        public FixedSourceLongConverter fixedSourcePrefixConverter(final SiPrefix sourceSiPrefix) {
            Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);

            return (SiPrefix targetSiPrefix, long sourceValue) -> doConvertLong(sourceSiPrefix, targetSiPrefix, sourceValue);
        }

        @Override
        public FixedTargetLongConverter fixedTargetPrefixConverter(final SiPrefix targetSiPrefix) {
            Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

            return (SiPrefix sourceSiPrefix, long sourceValue) -> doConvertLong(sourceSiPrefix, targetSiPrefix, sourceValue);
        }

        @Override
        public FixedLongConverter fixedConverter(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix) {
            Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);
            Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

            final int conversionExponent = calculateConversionExponent(sourceSiPrefix, targetSiPrefix);
            final long conversionFactor = ConversionLookupLong.getConversionFactor(conversionExponent);
            final LongBinaryOperator factorApplicationStrategy = ConversionLookupLong.getFactorApplicationStrategy(conversionExponent);

            return (long sourceValue) -> factorApplicationStrategy.applyAsLong(sourceValue, conversionFactor);
        }
    }

    private enum SiPrefixIntConverterBuilder implements
            SiPrefixConverterBuilder<SiPrefixConverterBuilder.FixedSourceIntConverter,
                    SiPrefixConverterBuilder.FixedTargetIntConverter,
                    SiPrefixConverterBuilder.FixedIntConverter> {
        INSTANCE;

        @Override
        public FixedSourceIntConverter fixedSourcePrefixConverter(final SiPrefix sourceSiPrefix) {
            Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);

            return (SiPrefix targetSiPrefix, int sourceValue) -> doConvertInt(sourceSiPrefix, targetSiPrefix, sourceValue);
        }

        @Override
        public FixedTargetIntConverter fixedTargetPrefixConverter(final SiPrefix targetSiPrefix) {
            Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

            return (SiPrefix sourceSiPrefix, int sourceValue) -> doConvertInt(sourceSiPrefix, targetSiPrefix, sourceValue);
        }

        @Override
        public FixedIntConverter fixedConverter(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix) {
            Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);
            Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

            final int conversionExponent = calculateConversionExponent(sourceSiPrefix, targetSiPrefix);
            final int conversionFactor = ConversionLookupInt.getConversionFactor(conversionExponent);
            final IntBinaryOperator factorApplicationStrategy = ConversionLookupInt.getFactorApplicationStrategy(conversionExponent);

            return (int sourceValue) -> factorApplicationStrategy.applyAsInt(sourceValue, conversionFactor);
        }
    }

    private enum SiPrefixBigIntegerConverterBuilder implements
            SiPrefixConverterBuilder<SiPrefixConverterBuilder.FixedSourceBigIntegerConverter,
                    SiPrefixConverterBuilder.FixedTargetBigIntegerConverter,
                    SiPrefixConverterBuilder.FixedBigIntegerConverter> {
        INSTANCE;

        @Override
        public FixedSourceBigIntegerConverter fixedSourcePrefixConverter(final SiPrefix sourceSiPrefix) {
            Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);

            return (SiPrefix targetSiPrefix, BigInteger sourceValue) -> doConvertBigInteger(sourceSiPrefix, targetSiPrefix, sourceValue);
        }

        @Override
        public FixedTargetBigIntegerConverter fixedTargetPrefixConverter(final SiPrefix targetSiPrefix) {
            Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

            return (SiPrefix sourceSiPrefix, BigInteger sourceValue) -> doConvertBigInteger(sourceSiPrefix, targetSiPrefix, sourceValue);
        }

        @Override
        public FixedBigIntegerConverter fixedConverter(final SiPrefix sourceSiPrefix, final SiPrefix targetSiPrefix) {
            Objects.requireNonNull(sourceSiPrefix, SOURCE_SI_PREFIX_NULL_CHECK_MSG);
            Objects.requireNonNull(targetSiPrefix, TARGET_SI_PREFIX_NULL_CHECK_MSG);

            final int conversionExponent = calculateConversionExponent(sourceSiPrefix, targetSiPrefix);
            final BigInteger conversionFactor = ConversionLookupBigInteger.getConversionFactor(conversionExponent);
            final BinaryOperator<BigInteger> factorApplicationStrategy = ConversionLookupBigInteger.getFactorApplicationStrategy(conversionExponent);

            return (BigInteger sourceValue) -> {
                Objects.requireNonNull(sourceValue, "sourceValue must not be null");
                return factorApplicationStrategy.apply(sourceValue, conversionFactor);
            };
        }
    }

    private enum BuilderChoiceImpl implements SiPrefixConverterBuilder.BuilderChoice {
        INSTANCE;

        @Override
        public SiPrefixConverterBuilder<SiPrefixConverterBuilder.FixedSourceDoubleConverter, SiPrefixConverterBuilder.FixedTargetDoubleConverter, SiPrefixConverterBuilder.FixedDoubleConverter> forDouble() {
            return SiPrefixDoubleConverterBuilder.INSTANCE;
        }

        @Override
        public SiPrefixConverterBuilder<SiPrefixConverterBuilder.FixedSourceLongConverter, SiPrefixConverterBuilder.FixedTargetLongConverter, SiPrefixConverterBuilder.FixedLongConverter> forLong() {
            return SiPrefixLongConverterBuilder.INSTANCE;
        }

        @Override
        public SiPrefixConverterBuilder<SiPrefixConverterBuilder.FixedSourceIntConverter, SiPrefixConverterBuilder.FixedTargetIntConverter, SiPrefixConverterBuilder.FixedIntConverter> forInt() {
            return SiPrefixIntConverterBuilder.INSTANCE;
        }

        @Override
        public SiPrefixConverterBuilder<SiPrefixConverterBuilder.FixedSourceBigIntegerConverter, SiPrefixConverterBuilder.FixedTargetBigIntegerConverter, SiPrefixConverterBuilder.FixedBigIntegerConverter> forBigInteger() {
            return SiPrefixBigIntegerConverterBuilder.INSTANCE;
        }
    }
}

final class ConversionLookupDouble {
    private static final double[] CONVERSION_FACTOR_LOOKUP_TABLE = {
            1.e-60,
            1.e-59, 1.e-58, 1.e-57, 1.e-56, 1.e-55, 1.e-54, 1.e-53, 1.e-52, 1.e-51, 1.e-50,
            1.e-49, 1.e-48, 1.e-47, 1.e-46, 1.e-45, 1.e-44, 1.e-43, 1.e-42, 1.e-41, 1.e-40,
            1.e-39, 1.e-38, 1.e-37, 1.e-36, 1.e-35, 1.e-34, 1.e-33, 1.e-32, 1.e-31, 1.e-30,
            1.e-29, 1.e-28, 1.e-27, 1.e-26, 1.e-25, 1.e-24, 1.e-23, 1.e-22, 1.e-21, 1.e-20,
            1.e-19, 1.e-18, 1.e-17, 1.e-16, 1.e-15, 1.e-14, 1.e-13, 1.e-12, 1.e-11, 1.e-10,
            1.e-9, 1.e-8, 1.e-7, 1.e-6, 1.e-5, 1.e-4, 1.e-3, 1.e-2, 1.e-1,
            1.e0,
            1.e1, 1.e2, 1.e3, 1.e4, 1.e5, 1.e6, 1.e7, 1.e8, 1.e9, 1.e10,
            1.e11, 1.e12, 1.e13, 1.e14, 1.e15, 1.e16, 1.e17, 1.e18, 1.e19, 1.e20,
            1.e21, 1.e22, 1.e23, 1.e24, 1.e25, 1.e26, 1.e27, 1.e28, 1.e29, 1.e30,
            1.e31, 1.e32, 1.e33, 1.e34, 1.e35, 1.e36, 1.e37, 1.e38, 1.e39, 1.e40,
            1.e41, 1.e42, 1.e43, 1.e44, 1.e45, 1.e46, 1.e47, 1.e48, 1.e49, 1.e50,
            1.e51, 1.e52, 1.e53, 1.e54, 1.e55, 1.e56, 1.e57, 1.e58, 1.e59, 1.e60
    };

    private static final int CONVERSION_FACTOR_LOOKUP_INDEX_OFFSET = CONVERSION_FACTOR_LOOKUP_TABLE.length / 2;

    static double getConversionFactor(final int index) {
        final int conversionFactorLookupIndex = index + CONVERSION_FACTOR_LOOKUP_INDEX_OFFSET;
        return CONVERSION_FACTOR_LOOKUP_TABLE[conversionFactorLookupIndex];
    }
}

final class ConversionLookupLong {
    private static final long[] CONVERSION_FACTOR_LOOKUP_TABLE = {
            10L,
            100L,
            1_000L,
            10_000L,
            100_000L,
            1_000_000L,
            10_000_000L,
            100_000_000L,
            1_000_000_000L,
            10_000_000_000L,
            100_000_000_000L,
            1_000_000_000_000L,
            10_000_000_000_000L,
            100_000_000_000_000L,
            1_000_000_000_000_000L,
            10_000_000_000_000_000L,
            100_000_000_000_000_000L,
            1_000_000_000_000_000_000L,
    };

    private static final LongBinaryOperator[] FACTOR_APPLICATION_STRATEGIES = {
            (a, b) -> a / b,
            (a, b) -> a,
            Math::multiplyExact
    };

    static long getConversionFactor(final int index) {
        final int conversionFactorLookupIndex = Math.abs(index - Integer.signum(index));

        if (conversionFactorLookupIndex >= CONVERSION_FACTOR_LOOKUP_TABLE.length) {
            throw new ArithmeticException("Required conversion factor exceeds supported range for long (max 10^18). Index was " + index);
        }

        return CONVERSION_FACTOR_LOOKUP_TABLE[conversionFactorLookupIndex];
    }

    static LongBinaryOperator getFactorApplicationStrategy(final int index) {
        final int strategyIndex = Integer.signum(index) + 1;
        return FACTOR_APPLICATION_STRATEGIES[strategyIndex];
    }
}

final class ConversionLookupInt {
    private static final int[] CONVERSION_FACTOR_LOOKUP_TABLE = {
            10,
            100,
            1_000,
            10_000,
            100_000,
            1_000_000,
            10_000_000,
            100_000_000,
            1_000_000_000
    };

    private static final IntBinaryOperator[] FACTOR_APPLICATION_STRATEGIES = {
            (a, b) -> a / b,
            (a, b) -> a,
            Math::multiplyExact
    };

    static int getConversionFactor(final int index) {
        final int conversionFactorLookupIndex = Math.abs(index - Integer.signum(index));

        if (conversionFactorLookupIndex >= CONVERSION_FACTOR_LOOKUP_TABLE.length) {
            throw new ArithmeticException("Required conversion factor exceeds supported range for long (max 10^9). Index was " + index);
        }

        return CONVERSION_FACTOR_LOOKUP_TABLE[conversionFactorLookupIndex];
    }

    static IntBinaryOperator getFactorApplicationStrategy(final int index) {
        final int strategyIndex = Integer.signum(index) + 1;
        return FACTOR_APPLICATION_STRATEGIES[strategyIndex];
    }
}

final class ConversionLookupBigInteger {
    private static final BigInteger[] CONVERSION_FACTOR_LOOKUP_TABLE = {
            BigInteger.TEN,
            BigInteger.TEN.pow(2),
            BigInteger.TEN.pow(3),
            BigInteger.TEN.pow(4),
            BigInteger.TEN.pow(5),
            BigInteger.TEN.pow(6),
            BigInteger.TEN.pow(7),
            BigInteger.TEN.pow(8),
            BigInteger.TEN.pow(9),
            BigInteger.TEN.pow(10),
            BigInteger.TEN.pow(11),
            BigInteger.TEN.pow(12),
            BigInteger.TEN.pow(13),
            BigInteger.TEN.pow(14),
            BigInteger.TEN.pow(15),
            BigInteger.TEN.pow(16),
            BigInteger.TEN.pow(17),
            BigInteger.TEN.pow(18),
            BigInteger.TEN.pow(19),
            BigInteger.TEN.pow(20),
            BigInteger.TEN.pow(21),
            BigInteger.TEN.pow(22),
            BigInteger.TEN.pow(23),
            BigInteger.TEN.pow(24),
            BigInteger.TEN.pow(25),
            BigInteger.TEN.pow(26),
            BigInteger.TEN.pow(27),
            BigInteger.TEN.pow(28),
            BigInteger.TEN.pow(29),
            BigInteger.TEN.pow(30),
            BigInteger.TEN.pow(31),
            BigInteger.TEN.pow(32),
            BigInteger.TEN.pow(33),
            BigInteger.TEN.pow(34),
            BigInteger.TEN.pow(35),
            BigInteger.TEN.pow(36),
            BigInteger.TEN.pow(37),
            BigInteger.TEN.pow(38),
            BigInteger.TEN.pow(39),
            BigInteger.TEN.pow(40),
            BigInteger.TEN.pow(41),
            BigInteger.TEN.pow(42),
            BigInteger.TEN.pow(43),
            BigInteger.TEN.pow(44),
            BigInteger.TEN.pow(45),
            BigInteger.TEN.pow(46),
            BigInteger.TEN.pow(47),
            BigInteger.TEN.pow(48),
            BigInteger.TEN.pow(49),
            BigInteger.TEN.pow(50),
            BigInteger.TEN.pow(51),
            BigInteger.TEN.pow(52),
            BigInteger.TEN.pow(53),
            BigInteger.TEN.pow(54),
            BigInteger.TEN.pow(55),
            BigInteger.TEN.pow(56),
            BigInteger.TEN.pow(57),
            BigInteger.TEN.pow(58),
            BigInteger.TEN.pow(59),
            BigInteger.TEN.pow(60)
    };

    private interface BigIntegerBinaryOperator extends BinaryOperator<BigInteger> {
    }

    private static final BigIntegerBinaryOperator[] FACTOR_APPLICATION_STRATEGIES = new BigIntegerBinaryOperator[]{
            BigInteger::divide,
            (a, b) -> a,
            BigInteger::multiply
    };

    static BigInteger getConversionFactor(final int index) {
        final int conversionFactorLookupIndex = Math.abs(index - Integer.signum(index));
        return CONVERSION_FACTOR_LOOKUP_TABLE[conversionFactorLookupIndex];
    }

    static BinaryOperator<BigInteger> getFactorApplicationStrategy(final int index) {
        final int strategyIndex = Integer.signum(index) + 1;
        return FACTOR_APPLICATION_STRATEGIES[strategyIndex];
    }
}
