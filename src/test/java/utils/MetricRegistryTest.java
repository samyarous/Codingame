package utils;

import org.junit.BeforeClass;
import org.junit.Test;
import utils.MetricRegistry.Counter;
import utils.MetricRegistry.Histogram;

public class MetricRegistryTest {

  private static MetricRegistry registry;
  private static MetricRegistry.Counter testCounter;

  @BeforeClass
  public static void initRegistry(){
    registry = MetricRegistry.getInstance();
    testCounter = registry.getCounter("GLOBAL");
    testCounter.update(666);

  }

  @Test
  public void testGlobalAccess(){
    assert(registry.getCounter("GLOBAL").getCount() == 666);
  }

  @Test
  public void testCount(){
    Counter count = registry.getCounter("TEST_COUNT");
    assert(count.getCount() == 0);
    count.update();
    assert(count.getCount() == 1);
    count.update(10);
    assert(count.getCount() == 11);
  }

  @Test
  public void testHistogram(){
    Histogram histogram = registry.getHistogram("TEST_HIST");
    assert(histogram.getAvgValue() == 0);
    assert(histogram.getMinValue() == Long.MAX_VALUE);
    assert(histogram.getMaxValue() == Long.MIN_VALUE);
    assert(histogram.getValuesSum() == 0);
    assert(histogram.getValuesCount() == 0);
    histogram.update(5);
    assert(histogram.getAvgValue() == 5);
    assert(histogram.getMinValue() == 5);
    assert(histogram.getMaxValue() == 5);
    assert(histogram.getValuesSum() == 5);
    assert(histogram.getValuesCount() == 1);
    histogram.update(5);
    assert(histogram.getAvgValue() == 5);
    assert(histogram.getMinValue() == 5);
    assert(histogram.getMaxValue() == 5);
    assert(histogram.getValuesSum() == 10);
    assert(histogram.getValuesCount() == 2);
    histogram.update(20);
    assert(histogram.getAvgValue() == 10);
    assert(histogram.getMinValue() == 5);
    assert(histogram.getMaxValue() == 20);
    assert(histogram.getValuesSum() == 30);
    assert(histogram.getValuesCount() == 3);
  }

}
