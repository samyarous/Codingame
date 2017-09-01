package utils;

import org.junit.BeforeClass;
import org.junit.Test;
import utils.MetricRegistry.Counter;

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
}
