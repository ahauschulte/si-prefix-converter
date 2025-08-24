package io.github.ahauschulte.siprefixconverter;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SiPrefixConverterTest {

    @Test
    void testConversion() {
        assertEquals(100L, SiPrefixConverter.convert(SiPrefix.DECA, SiPrefix.DECI, 1L));
        assertEquals(1_000L, SiPrefixConverter.convert(SiPrefix.QUETTA, SiPrefix.RONNA, 1L));
        assertEquals(1_000_000_000_000_000L, SiPrefixConverter.convert(SiPrefix.QUETTA, SiPrefix.PETA, 1L));

        assertEquals(100., SiPrefixConverter.convert(SiPrefix.DECA, SiPrefix.DECI, 1.));
        assertEquals(1_000., SiPrefixConverter.convert(SiPrefix.QUETTA, SiPrefix.RONNA, 1.));
        assertEquals(1_000_000_000_000_000., SiPrefixConverter.convert(SiPrefix.QUETTA, SiPrefix.PETA, 1.));

        assertEquals(BigInteger.valueOf(100), SiPrefixConverter.convert(SiPrefix.DECA, SiPrefix.DECI, BigInteger.ONE));
        assertEquals(BigInteger.valueOf(1_000L), SiPrefixConverter.convert(SiPrefix.QUETTA, SiPrefix.RONNA, BigInteger.ONE));
        assertEquals(BigInteger.valueOf(1_000_000_000_000_000L), SiPrefixConverter.convert(SiPrefix.QUETTA, SiPrefix.PETA, BigInteger.ONE));
    }

    @Test
    void testConversionRange() {
        assertEquals(1_000_000_000_000_000_000L, SiPrefixConverter.convert(SiPrefix.TERA, SiPrefix.MICRO, 1L));

        assertEquals(0L, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.DECA, 20L));

        assertThrows(ArithmeticException.class,
                () -> SiPrefixConverter.convert(SiPrefix.TERA, SiPrefix.NANO, 1L));

        assertEquals(100_000_000_000_000_000L, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, 1_000_000_000_000_000_000L));

        assertThrows(ArithmeticException.class,
                () -> SiPrefixConverter.convert(SiPrefix.UNIT, SiPrefix.DECI, 1_000_000_000_000_000_000L));
    }

    @Test
    void testUnchangedConversion() {
        assertEquals(123456789L, SiPrefixConverter.convert(SiPrefix.KILO, SiPrefix.KILO, 123456789L));
        assertEquals(-42., SiPrefixConverter.convert(SiPrefix.UNIT, SiPrefix.UNIT, -42.));
        assertEquals(BigInteger.TEN, SiPrefixConverter.convert(SiPrefix.MILLI, SiPrefix.MILLI, BigInteger.TEN));
    }

    @Test
    void testTruncation() {
        assertEquals(1L, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, 15L));
        assertEquals(0L, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, 9L));
        assertEquals(-1L, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, -15L));
        assertEquals(0L, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, -9L));

        assertEquals(BigInteger.ONE, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, BigInteger.valueOf(15L)));
        assertEquals(BigInteger.ZERO, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, BigInteger.valueOf(9L)));
        assertEquals(BigInteger.ONE.negate(), SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, BigInteger.valueOf(-15L)));
        assertEquals(BigInteger.ZERO, SiPrefixConverter.convert(SiPrefix.DECI, SiPrefix.UNIT, BigInteger.valueOf(-9L)));
    }

    @Test
    void testFixedSourceConverter() {
        assertEquals(100.,
                SiPrefixConverter.builder()
                        .forDouble()
                        .fixedSourcePrefixConverter(SiPrefix.DECA)
                        .convert(SiPrefix.DECI, 1.));

        assertEquals(100,
                SiPrefixConverter.builder()
                        .forLong()
                        .fixedSourcePrefixConverter(SiPrefix.DECA)
                        .convert(SiPrefix.DECI, 1));

        assertEquals(BigInteger.valueOf(100),
                SiPrefixConverter.builder()
                        .forBigInteger()
                        .fixedSourcePrefixConverter(SiPrefix.DECA)
                        .convert(SiPrefix.DECI, BigInteger.ONE));
    }

    @Test
    void testFixedTargetConverter() {
        assertEquals(100.,
                SiPrefixConverter.builder()
                        .forDouble()
                        .fixedTargetPrefixConverter(SiPrefix.DECI)
                        .convert(SiPrefix.DECA, 1.));

        assertEquals(100,
                SiPrefixConverter.builder()
                        .forLong()
                        .fixedTargetPrefixConverter(SiPrefix.DECI)
                        .convert(SiPrefix.DECA, 1));

        assertEquals(10,
                SiPrefixConverter.builder()
                        .forLong()
                        .fixedTargetPrefixConverter(SiPrefix.DECA)
                        .convert(SiPrefix.DECI, 1_000));

        assertEquals(BigInteger.valueOf(100),
                SiPrefixConverter.builder()
                        .forBigInteger()
                        .fixedTargetPrefixConverter(SiPrefix.DECI)
                        .convert(SiPrefix.DECA, BigInteger.ONE));

        assertEquals(BigInteger.valueOf(10),
                SiPrefixConverter.builder()
                        .forBigInteger()
                        .fixedTargetPrefixConverter(SiPrefix.DECA)
                        .convert(SiPrefix.DECI, BigInteger.valueOf(1_000)));
    }

    @Test
    void testFixedConverter() {
        assertEquals(100.,
                SiPrefixConverter.builder()
                        .forDouble()
                        .fixedConverter(SiPrefix.DECA, SiPrefix.DECI)
                        .convert(1.));

        assertEquals(100,
                SiPrefixConverter.builder()
                        .forLong()
                        .fixedConverter(SiPrefix.DECA, SiPrefix.DECI)
                        .convert(1));

        assertEquals(10,
                SiPrefixConverter.builder()
                        .forLong()
                        .fixedConverter(SiPrefix.DECI, SiPrefix.DECA)
                        .convert(1_000));

        assertEquals(BigInteger.valueOf(100),
                SiPrefixConverter.builder()
                        .forBigInteger()
                        .fixedConverter(SiPrefix.DECA, SiPrefix.DECI)
                        .convert(BigInteger.ONE));

        assertEquals(BigInteger.valueOf(10),
                SiPrefixConverter.builder()
                        .forBigInteger()
                        .fixedConverter(SiPrefix.DECI, SiPrefix.DECA)
                        .convert(BigInteger.valueOf(1_000)));
    }

    @Test
    void testNullArguments() {
        assertThrows(NullPointerException.class,
                () -> SiPrefixConverter.convert(null, SiPrefix.UNIT, 1L));
        assertThrows(NullPointerException.class,
                () -> SiPrefixConverter.convert(SiPrefix.UNIT, null, 1L));

        assertThrows(NullPointerException.class,
                () -> SiPrefixConverter.convert(null, SiPrefix.UNIT, 1.));
        assertThrows(NullPointerException.class,
                () -> SiPrefixConverter.convert(SiPrefix.UNIT, null, 1.));

        assertThrows(NullPointerException.class,
                () -> SiPrefixConverter.convert(null, SiPrefix.UNIT, BigInteger.ONE));
        assertThrows(NullPointerException.class,
                () -> SiPrefixConverter.convert(SiPrefix.UNIT, null, BigInteger.ONE));
        assertThrows(NullPointerException.class,
                () -> SiPrefixConverter.convert(SiPrefix.UNIT, SiPrefix.KILO, (BigInteger) null));

        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forLong().fixedSourcePrefixConverter(null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forLong().fixedTargetPrefixConverter(null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forLong().fixedConverter(SiPrefix.KILO, null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forLong().fixedConverter(null, SiPrefix.KILO));

        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forDouble().fixedSourcePrefixConverter(null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forDouble().fixedTargetPrefixConverter(null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forDouble().fixedConverter(SiPrefix.KILO, null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forDouble().fixedConverter(null, SiPrefix.KILO));

        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forBigInteger().fixedSourcePrefixConverter(null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forBigInteger().fixedTargetPrefixConverter(null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forBigInteger().fixedConverter(SiPrefix.KILO, null));
        assertThrows((NullPointerException.class),
                () -> SiPrefixConverter.builder().forBigInteger().fixedConverter(null, SiPrefix.KILO));
    }
}
