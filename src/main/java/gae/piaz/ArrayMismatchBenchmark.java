package gae.piaz;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class ArrayMismatchBenchmark {

    @Param({"10", "100", "1000", "10000"})
    int size;

    @Param({"0.5", "1.0"})
    double prefix;

    int[] intData1;
    int[] intData2;

    static final VectorSpecies<Integer> INT_SPECIES_PREFERRED = IntVector.SPECIES_PREFERRED;

    @Setup
    public void setup(BenchmarkParams params) {
        RandomGenerator random = RandomGenerator.getDefault();
        int common = (int) (prefix * size);
        intData1 = random.ints(size).toArray();
        intData2 = random.ints(size).toArray();
        int[] commonInts = random.ints(common).toArray();
        System.arraycopy(commonInts, 0, intData1, 0, common);
        System.arraycopy(commonInts, 0, intData2, 0, common);
    }

    @Benchmark
    public int mismatchIntrinsicInt() {
        return Arrays.mismatch(intData1, intData2);
    }

    @Benchmark
    public int mismatchVectorInt() {
        int length = Math.min(intData1.length, intData2.length);
        int index = 0;
        for (; index < INT_SPECIES_PREFERRED.loopBound(length); index += INT_SPECIES_PREFERRED.length()) {
            IntVector vector1 = IntVector.fromArray(INT_SPECIES_PREFERRED, intData1, index);
            IntVector vector2 = IntVector.fromArray(INT_SPECIES_PREFERRED, intData2, index);
            VectorMask<Integer> mask = vector1.compare(VectorOperators.NE, vector2);
            if (mask.anyTrue()) {
                return index + mask.firstTrue();
            }
        }
        // process the tail
        int mismatch = -1;
        for (int i = index; i < length; ++i) {
            if (intData1[i] != intData2[i]) {
                mismatch = i;
                break;
            }
        }
        return mismatch;
    }

}