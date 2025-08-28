package io.github.ahauschulte.siprefixconverter;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1_000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 4, time = 1_000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1)
@State(Scope.Thread)
public class SiPrefixConverterBenchmark {

    @Param({
            "UNIT:UNIT",
            "GIGA:KILO",
            "KILO:GIGA",
    })
    public String pair;

    private SiPrefix src;
    private SiPrefix dst;

    @Param({"3.14159"})
    public double doubleValue;

    @Param({"345"})
    public long longValue;

    @Param({"123"})
    public int intValue;

    private final BigInteger bigValue = new BigInteger("1234567890123456789");

    // Pre-built Converter (Builder API)
    private SiPrefixConverterBuilder.FixedSourceDoubleConverter fixedSrcDouble;
    private SiPrefixConverterBuilder.FixedTargetDoubleConverter fixedDstDouble;
    private SiPrefixConverterBuilder.FixedDoubleConverter fixedBothDouble;

    private SiPrefixConverterBuilder.FixedSourceLongConverter fixedSrcLong;
    private SiPrefixConverterBuilder.FixedTargetLongConverter fixedDstLong;
    private SiPrefixConverterBuilder.FixedLongConverter fixedBothLong;

    private SiPrefixConverterBuilder.FixedSourceIntConverter fixedSrcInt;
    private SiPrefixConverterBuilder.FixedTargetIntConverter fixedDstInt;
    private SiPrefixConverterBuilder.FixedIntConverter fixedBothInt;

    private SiPrefixConverterBuilder.FixedSourceBigIntegerConverter fixedSrcBig;
    private SiPrefixConverterBuilder.FixedTargetBigIntegerConverter fixedDstBig;
    private SiPrefixConverterBuilder.FixedBigIntegerConverter fixedBothBig;

    @Setup(Level.Trial)
    public void setup() {
        String[] parts = pair.split(":");
        src = SiPrefix.valueOf(parts[0]);
        dst = SiPrefix.valueOf(parts[1]);

        // Double
        var dblBuilder = SiPrefixConverter.builder().forDouble();
        fixedSrcDouble = dblBuilder.fixedSourcePrefixConverter(src);
        fixedDstDouble = dblBuilder.fixedTargetPrefixConverter(dst);
        fixedBothDouble = dblBuilder.fixedConverter(src, dst);

        // Long
        var lngBuilder = SiPrefixConverter.builder().forLong();
        fixedSrcLong = lngBuilder.fixedSourcePrefixConverter(src);
        fixedDstLong = lngBuilder.fixedTargetPrefixConverter(dst);
        fixedBothLong = lngBuilder.fixedConverter(src, dst);

        // Int
        var intBuilder = SiPrefixConverter.builder().forInt();
        fixedSrcInt = intBuilder.fixedSourcePrefixConverter(src);
        fixedDstInt = intBuilder.fixedTargetPrefixConverter(dst);
        fixedBothInt = intBuilder.fixedConverter(src, dst);

        // BigInteger
        var bigBuilder = SiPrefixConverter.builder().forBigInteger();
        fixedSrcBig = bigBuilder.fixedSourcePrefixConverter(src);
        fixedDstBig = bigBuilder.fixedTargetPrefixConverter(dst);
        fixedBothBig = bigBuilder.fixedConverter(src, dst);
    }

    // -------- double --------

    @Benchmark
    public void double_static(Blackhole bh) {
        bh.consume(SiPrefixConverter.convert(src, dst, doubleValue));
    }

    @Benchmark
    public void double_fixedSource(Blackhole bh) {
        bh.consume(fixedSrcDouble.convert(dst, doubleValue));
    }

    @Benchmark
    public void double_fixedTarget(Blackhole bh) {
        bh.consume(fixedDstDouble.convert(src, doubleValue));
    }

    @Benchmark
    public void double_fixedBoth(Blackhole bh) {
        bh.consume(fixedBothDouble.convert(doubleValue));
    }

    // -------- long --------

    @Benchmark
    public void long_static(Blackhole bh) {
        bh.consume(SiPrefixConverter.convert(src, dst, longValue));
    }

    @Benchmark
    public void long_fixedSource(Blackhole bh) {
        bh.consume(fixedSrcLong.convert(dst, longValue));
    }

    @Benchmark
    public void long_fixedTarget(Blackhole bh) {
        bh.consume(fixedDstLong.convert(src, longValue));
    }

    @Benchmark
    public void long_fixedBoth(Blackhole bh) {
        bh.consume(fixedBothLong.convert(longValue));
    }

    // -------- int --------

    @Benchmark
    public void int_static(Blackhole bh) {
        bh.consume(SiPrefixConverter.convert(src, dst, intValue));
    }

    @Benchmark
    public void int_fixedSource(Blackhole bh) {
        bh.consume(fixedSrcInt.convert(dst, intValue));
    }

    @Benchmark
    public void int_fixedTarget(Blackhole bh) {
        bh.consume(fixedDstInt.convert(src, intValue));
    }

    @Benchmark
    public void int_fixedBoth(Blackhole bh) {
        bh.consume(fixedBothInt.convert(intValue));
    }

    // -------- BigInteger --------

    @Benchmark
    public void bigInteger_static(Blackhole bh) {
        bh.consume(SiPrefixConverter.convert(src, dst, bigValue));
    }

    @Benchmark
    public void bigInteger_fixedSource(Blackhole bh) {
        bh.consume(fixedSrcBig.convert(dst, bigValue));
    }

    @Benchmark
    public void bigInteger_fixedTarget(Blackhole bh) {
        bh.consume(fixedDstBig.convert(src, bigValue));
    }

    @Benchmark
    public void bigInteger_fixedBoth(Blackhole bh) {
        bh.consume(fixedBothBig.convert(bigValue));
    }
}