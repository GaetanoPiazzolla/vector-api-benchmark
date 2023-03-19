import gae.piaz.ArraySumBenchmark;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.random.RandomGenerator;

public class ArraySumUnitTest {

    static int[] arr1 = new int[525];
    static int[] arr2 = new int[525];

    static ArraySumBenchmark benc;

    static RandomGenerator random;
    @BeforeAll
    public static void beforeAll(){
        benc = new ArraySumBenchmark();
        arr1 = new int[525];
        arr2 = new int[525];
        Arrays.fill(arr1,1);
        Arrays.fill(arr2,2);
        random = RandomGenerator.getDefault();
    }

    @Test
    public void testArraySum(){
        var arrsum = benc.sum(arr1,arr2);
        Assertions.assertEquals(525,arrsum.length);
        Assertions.assertEquals(3, arrsum[random.nextInt(525)]);
    }

    @Test
    public void testArraySumVectorial() {
        var arrsum = benc.vectorSum(arr1,arr2);
        Assertions.assertEquals(525,arrsum.length);
        Assertions.assertEquals(3, arrsum[random.nextInt(525)]);
    }

    @Test
    public void testArraySumVectorialMask() {
        var arrsum = benc.vectorSumMask(arr1,arr2);
        Assertions.assertEquals(525,arrsum.length);
        Assertions.assertEquals(3, arrsum[random.nextInt(525)]);
    }
}
