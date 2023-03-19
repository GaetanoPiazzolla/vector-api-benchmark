# Benchmarking Java Vector-Api with JMH

### Credits:
- https://www.youtube.com/watch?v=42My8Yfzwbg
- https://github.com/openjdk/jdk/blob/master/test/micro/org/openjdk/bench/jdk/incubator/vector
- https://github.com/guozheng/jmh-tutorial

### Run with: 

```shell
mvn clean install
java -jar target/vector-api-benchmark.jar -wi 1 -i 1 -f 1 #default behavior -wi 20 -i 20 -f 10
```

Or to run a single class test:

```shell
mvn clean install
java -cp target/vector-api-benchmark.jar gae.piaz.ArraySumBenchmark
```


- wi = warm up iterations
- i = iterations
- f = number of fork for a single benchmark

