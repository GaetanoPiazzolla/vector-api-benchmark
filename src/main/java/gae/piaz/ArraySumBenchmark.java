package gae.piaz;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class ArraySumBenchmark {

    @Param({"9", "257", "100000"})
    int size;

    int[] v1;
    int[] v2;

    static final VectorSpecies<Integer> INT_SPECIES_PREFERRED = IntVector.SPECIES_PREFERRED;

    @Setup
    public void setup() {
        RandomGenerator random = RandomGenerator.getDefault();
        v1 = random.ints(size).toArray();
        v2 = random.ints(size).toArray();
    }

    @Benchmark
    public int[] benchMarkSum() {
        return this.sum(v1, v2);
    }

    public int[] sum(int[] intData1, int[] intData2) {
        int[] result = new int[intData1.length];
        for (int i = 0; i < intData1.length; i++) {
            result[i] = intData1[i] + intData2[i];
        }
        return result;
    }

    @Benchmark
    public int[] vectorSum() {
        return this.vectorSum(v1, v2);
    }

    public int[] vectorSum(int[] intData1, int[] intData2) {
        int[] result = new int[intData1.length];
        int index = 0;
        for (; index < INT_SPECIES_PREFERRED.loopBound(intData1.length); index += INT_SPECIES_PREFERRED.length()) {
            IntVector vector1 = IntVector.fromArray(INT_SPECIES_PREFERRED, intData1, index);
            IntVector vector2 = IntVector.fromArray(INT_SPECIES_PREFERRED, intData2, index);
            IntVector vectorSum = vector1.add(vector2);
            vectorSum.intoArray(result, index);
        }
        for (int index2 = index; index2 < intData1.length; index2++) {
            result[index2] = intData1[index2] + intData2[index2];
        }
        return result;
    }

    @Benchmark
    public int[] vectorSumMask() {
        return this.vectorSumMask(v1, v2);
    }

    public int[] vectorSumMask(int[] intData1, int[] intData2) {
        int[] result = new int[intData1.length];
        for (int index = 0; index < intData1.length; index += INT_SPECIES_PREFERRED.length()) {
            var mask = INT_SPECIES_PREFERRED.indexInRange(index, intData1.length);
            IntVector vector1 = IntVector.fromArray(INT_SPECIES_PREFERRED, intData1, index, mask);
            IntVector vector2 = IntVector.fromArray(INT_SPECIES_PREFERRED, intData2, index, mask);
            IntVector vectorSum = vector1.add(vector2, mask);
            vectorSum.intoArray(result, index, mask);
        }
        return result;
    }

}