package io.github.ahauschulte.siprefixconverter;

/**
 * Enumerates the International System of Units (SI) prefixes.
 *
 * <p>The enumeration covers the full range from quecto (10<sup>-30</sup>) to quetta (10<sup>30</sup>).
 * The constant {@link #UNIT} represents the absence of a prefix (10<sup>0</sup> = 1).
 *
 * <p><b>Usage</b>
 * {@snippet :
 * // Convert 2.5 kilometres (kilo) to metres (unit):
 * double meters = SiPrefixConverter.convert(SiPrefix.KILO, SiPrefix.UNIT, 2.5); // -> 2500.0
 *
 * // Convert 3_000_000 nanoseconds (nano) to milliseconds (milli) using longs
 * long ms = SiPrefixConverter.convert(SiPrefix.NANO, SiPrefix.MILLI, 3_000_000L); // -> 3L
 *}
 */
public enum SiPrefix {

    /** quecto prefix (10<sup>-30</sup>) */
    QUECTO(-30),

    /** ronto prefix (10<sup>-27</sup>) */
    RONTO(-27),

    /** yocto prefix (10<sup>-24</sup>) */
    YOCTO(-24),

    /** zepto prefix (10<sup>-21</sup>) */
    ZEPTO(-21),

    /** atto prefix (10<sup>-18</sup>) */
    ATTO(-18),

    /** femto prefix (10<sup>-15</sup>) */
    FEMTO(-15),

    /** pico prefix (10<sup>-12</sup>) */
    PICO(-12),

    /** nano prefix (10<sup>-9</sup>) */
    NANO(-9),

    /** micro prefix (10<sup>-6</sup>) */
    MICRO(-6),

    /** milli prefix (10<sup>-3</sup>) */
    MILLI(-3),

    /** centi prefix (10<sup>-2</sup>) */
    CENTI(-2),

    /** deci prefix (10<sup>-1</sup>) */
    DECI(-1),

    /** no prefix (10<sup>0</sup> = 1) */
    UNIT(0),

    /** deca prefix (10<sup>1</sup>) */
    DECA(1),

    /** hecto prefix (10<sup>2</sup>) */
    HECTO(2),

    /** kilo prefix (10<sup>3</sup>) */
    KILO(3),

    /** mega prefix (10<sup>6</sup>) */
    MEGA(6),

    /** giga prefix (10<sup>9</sup>) */
    GIGA(9),

    /** tera prefix (10<sup>12</sup>) */
    TERA(12),

    /** peta prefix (10<sup>15</sup>) */
    PETA(15),

    /** exa prefix (10<sup>18</sup>) */
    EXA(18),

    /** zetta prefix (10<sup>21</sup>) */
    ZETTA(21),

    /** yotta prefix (10<sup>24</sup>) */
    YOTTA(24),

    /** ronna prefix (10<sup>27</sup>) */
    RONNA(27),

    /** quetta prefix (10<sup>30</sup>) */
    QUETTA(30);


    private final int exponent;

    SiPrefix(final int exponent) {
        this.exponent = exponent;
    }

    int exponent() {
        return exponent;
    }
}
