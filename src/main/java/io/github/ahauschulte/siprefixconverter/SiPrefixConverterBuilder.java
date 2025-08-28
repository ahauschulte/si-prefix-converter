package io.github.ahauschulte.siprefixconverter;

import java.math.BigInteger;

/**
 * Type-safe factory for building reusable SI prefix converters for a chosen numeric type.
 *
 * <p>The builder lets you fix either the source prefix, the target prefix, or both, and obtain a
 * small functional converter that you can apply to many values without re-specifying the prefixes.
 *
 * <p><b>Example</b>
 * {@snippet :
 * import io.github.ahauschulte.siprefixconverter.SiPrefix;
 * import io.github.ahauschulte.siprefixconverter.SiPrefixConverter;
 *
 * // Prepare a converter for double values from kilo- to unit- (×1000)
 * var builder = SiPrefixConverter.builder().forDouble();
 * var kmToM   = builder.fixedConverter(SiPrefix.KILO, SiPrefix.UNIT);
 * double metres = kmToM.convert(1.2); // 1200.0
 *}
 *
 * @param <S> type of the Converter with the source SI prefix fixed
 * @param <T> type of the Converter with the target SI prefix fixed
 * @param <C> type of the Converter with both source and target SI prefix fixed
 */
public interface SiPrefixConverterBuilder<S, T, C> {

    /**
     * Fixes the source prefix and returns a converter that accepts a target prefix and a value.
     *
     * @param sourceSiPrefix the prefix the incoming values are expressed in
     * @return a functional converter expecting the target prefix and the value to convert
     * @throws NullPointerException if {@code sourceSiPrefix} is {@code null}
     */
    S fixedSourcePrefixConverter(SiPrefix sourceSiPrefix);

    /**
     * Fixes the target prefix and returns a converter that accepts a source prefix and a value.
     *
     * @param targetSiPrefix the prefix to convert values to
     * @return a functional converter expecting the source prefix and the value to convert
     * @throws NullPointerException if {@code targetSiPrefix} is {@code null}
     */
    T fixedTargetPrefixConverter(SiPrefix targetSiPrefix);

    /**
     * Fixes both source and target prefixes and returns a converter that accepts only the value.
     *
     * @param sourceSiPrefix the prefix the incoming values are expressed in
     * @param targetSiPrefix the prefix to convert values to
     * @return a functional converter of values
     * @throws NullPointerException if any prefix is {@code null}
     */
    C fixedConverter(SiPrefix sourceSiPrefix, SiPrefix targetSiPrefix);

    /**
     * Selects the numeric type for which to build converters.
     *
     * <p>Each method returns a type-specialised builder with strongly typed functional interfaces.
     */
    interface BuilderChoice {

        /**
         * Returns a {@link SiPrefixConverterBuilder} for {@code double} conversions.
         *
         * @return a {@link SiPrefixConverterBuilder} for {@code double} conversions.
         */
        SiPrefixConverterBuilder<FixedSourceDoubleConverter, FixedTargetDoubleConverter, FixedDoubleConverter> forDouble();

        /**
         * Returns a {@link SiPrefixConverterBuilder} for {@code long} conversions.
         *
         * @return a {@link SiPrefixConverterBuilder} for {@code long} conversions.
         */
        SiPrefixConverterBuilder<FixedSourceLongConverter, FixedTargetLongConverter, FixedLongConverter> forLong();

        /**
         * Returns a {@link SiPrefixConverterBuilder} for {@code int} conversions.
         *
         * @return a {@link SiPrefixConverterBuilder} for {@code int} conversions.
         */
        SiPrefixConverterBuilder<FixedSourceIntConverter, FixedTargetIntConverter, FixedIntConverter> forInt();

        /**
         * Returns a {@link SiPrefixConverterBuilder} for {@link BigInteger} conversions.
         *
         * @return a {@link SiPrefixConverterBuilder} for {@link BigInteger} conversions.
         */
        SiPrefixConverterBuilder<FixedSourceBigIntegerConverter, FixedTargetBigIntegerConverter, FixedBigIntegerConverter> forBigInteger();
    }

    /**
     * A converter for converting {@code double}s with a fixed source SI prefix.
     */
    @FunctionalInterface
    interface FixedSourceDoubleConverter {

        /**
         * Converts a {@code double} value when the source prefix is fixed.
         *
         * @param targetSiPrefix the target prefix
         * @param sourceValue    the value to convert
         * @return the converted value
         * @throws NullPointerException if {@code targetSiPrefix} is {@code null}
         */
        double convert(SiPrefix targetSiPrefix, double sourceValue);
    }

    /**
     * A converter for converting {@code double}s with a fixed target SI prefix.
     */
    @FunctionalInterface
    interface FixedTargetDoubleConverter {

        /**
         * Converts a {@code double} value when the target prefix is fixed.
         *
         * @param sourceSiPrefix the source prefix
         * @param sourceValue    the value to convert
         * @return the converted value
         * @throws NullPointerException if {@code sourceSiPrefix} is {@code null}
         */
        double convert(SiPrefix sourceSiPrefix, double sourceValue);
    }

    /**
     * A converter for converting {@code double}s with both source and target SI prefix fixed.
     */
    @FunctionalInterface
    interface FixedDoubleConverter {

        /**
         * Converts a {@code double} value when both prefixes are fixed.
         *
         * @param sourceValue the value to convert
         * @return the converted value
         */
        double convert(double sourceValue);
    }

    /**
     * A converter for converting {@code long}s with a fixed source SI prefix.
     */
    @FunctionalInterface
    interface FixedSourceLongConverter {

        /**
         * Converts a {@code long} value when the source prefix is fixed.
         * <p>Integer arithmetic; scaling down truncates towards zero and an {@link ArithmeticException}
         * is thrown if the conversion factor exceeds 10<sup>18</sup> for up-scaling or the conversion
         * calculation overflows.
         *
         * @param targetSiPrefix the target prefix
         * @param sourceValue    the value to convert
         * @return the converted value (possibly truncated)
         * @throws NullPointerException if {@code targetSiPrefix} is {@code null}
         * @throws ArithmeticException  if the conversion factor is beyond 10^18 for up-scaling or
         * the conversion calculation overflows
         */
        long convert(SiPrefix targetSiPrefix, long sourceValue);
    }

    /**
     * A converter for converting {@code long}s with a fixed target SI prefix.
     */
    @FunctionalInterface
    interface FixedTargetLongConverter {

        /**
         * Converts a {@code long} value when the target prefix is fixed.
         * <p>Integer arithmetic; scaling down truncates towards zero and an {@link ArithmeticException}
         * is thrown if the conversion factor exceeds 10<sup>18</sup> for up-scaling or the conversion
         * calculation overflows.
         *
         * @param sourceSiPrefix the source prefix
         * @param sourceValue    the value to convert
         * @return the converted value (possibly truncated)
         * @throws NullPointerException if {@code sourceSiPrefix} is {@code null}
         * @throws ArithmeticException  if the conversion factor is beyond 10^18 for up-scaling
         * or the conversion calculation overflows
         */
        long convert(SiPrefix sourceSiPrefix, long sourceValue);
    }

    /**
     * A converter for converting {@code long}s with both source and target SI prefix fixed.
     */
    @FunctionalInterface
    interface FixedLongConverter {

        /**
         * Converts a {@code long} value when both prefixes are fixed.
         * <p>Integer arithmetic; scaling down truncates towards zero and an {@link ArithmeticException}
         * is thrown if the conversion factor exceeds 10<sup>18</sup> for up-scaling or the conversion
         * calculation overflows.
         *
         * @param sourceValue the value to convert
         * @return the converted value (possibly truncated)
         * @throws ArithmeticException if the conversion factor is beyond 10^18 for up-scaling or
         * the conversion calculation overflows
         */
        long convert(long sourceValue);
    }

    /**
     * A converter for converting {@code int}s with a fixed source SI prefix.
     */
    @FunctionalInterface
    interface FixedSourceIntConverter {

        /**
         * Converts an {@code int} value when the source prefix is fixed.
         * <p>Integer arithmetic; scaling down truncates towards zero and an {@link ArithmeticException}
         * is thrown if the conversion factor exceeds 10<sup>9</sup> for up-scaling or the conversion
         * calculation overflows.
         *
         * @param targetSiPrefix the target prefix
         * @param sourceValue    the value to convert
         * @return the converted value (possibly truncated)
         * @throws NullPointerException if {@code targetSiPrefix} is {@code null}
         * @throws ArithmeticException  if the conversion factor is beyond 10^9 for up-scaling or
         * the conversion calculation overflows
         */
        int convert(SiPrefix targetSiPrefix, int sourceValue);
    }

    /**
     * A converter for converting {@code int}s with a fixed target SI prefix.
     */
    @FunctionalInterface
    interface FixedTargetIntConverter {

        /**
         * Converts a {@code int} value when the target prefix is fixed.
         * <p>Integer arithmetic; scaling down truncates towards zero and an {@link ArithmeticException}
         * is thrown if the conversion factor exceeds 10<sup>9</sup> for up-scaling or the conversion
         * calculation overflows.
         *
         * @param sourceSiPrefix the source prefix
         * @param sourceValue    the value to convert
         * @return the converted value (possibly truncated)
         * @throws NullPointerException if {@code sourceSiPrefix} is {@code null}
         * @throws ArithmeticException  if the conversion factor is beyond 10^9 for up-scaling
         * or the conversion calculation overflows
         */
        int convert(SiPrefix sourceSiPrefix, int sourceValue);
    }

    /**
     * A converter for converting {@code ints}s with both source and target SI prefix fixed.
     */
    @FunctionalInterface
    interface FixedIntConverter {

        /**
         * Converts a {@code int} value when both prefixes are fixed.
         * <p>Integer arithmetic; scaling down truncates towards zero and an {@link ArithmeticException}
         * is thrown if the conversion factor exceeds 10<sup>9</sup> for up-scaling or the conversion
         * calculation overflows.
         *
         * @param sourceValue the value to convert
         * @return the converted value (possibly truncated)
         * @throws ArithmeticException if the conversion factor is beyond 10^9 for up-scaling or
         * the conversion calculation overflows
         */
        int convert(int sourceValue);
    }

    /**
     * A converter for converting {@link BigInteger}s with a fixed source SI prefix.
     */
    @FunctionalInterface
    interface FixedSourceBigIntegerConverter {

        /**
         * Converts a {@link BigInteger} value when the source prefix is fixed.
         *
         * @param targetSiPrefix the target prefix
         * @param sourceValue    the value to convert
         * @return the converted value
         * @throws NullPointerException if any of the arguments are {@code null}
         */
        BigInteger convert(SiPrefix targetSiPrefix, BigInteger sourceValue);
    }

    /**
     * A converter for converting {@link BigInteger}s with a fixed target SI prefix.
     */
    @FunctionalInterface
    interface FixedTargetBigIntegerConverter {

        /**
         * Converts a {@link BigInteger} value when the target prefix is fixed.
         *
         * @param sourceSiPrefix the source prefix
         * @param sourceValue    the value to convert
         * @return the converted value
         * @throws NullPointerException if any of the arguments are {@code null}
         */
        BigInteger convert(SiPrefix sourceSiPrefix, BigInteger sourceValue);
    }

    /**
     * A converter for converting {@link BigInteger}s with both source and target SI prefix fixed.
     */
    @FunctionalInterface
    interface FixedBigIntegerConverter {

        /**
         * Converts a {@link java.math.BigInteger} value when both prefixes are fixed.
         *
         * @param sourceValue the value to convert
         * @return the converted value
         * @throws NullPointerException if {@code sourceValue} is {@code null}
         */
        BigInteger convert(BigInteger sourceValue);
    }
}
