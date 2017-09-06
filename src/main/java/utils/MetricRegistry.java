package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MetricRegistry {

  private final static MetricRegistry instance = new MetricRegistry();
  private Map<String, Meter> metersMap = new HashMap<>();
  private Map<String, Gauge> gaugesMap = new HashMap<>();
  private Map<String, Counter> countersMap = new HashMap<>();
  private Map<String, Timer> timersMap = new HashMap<>();
  private Map<String, Histogram> histogramsMap = new HashMap<>();

  private MetricRegistry(){

  }

  public static MetricRegistry getInstance() {
    return instance;
  }

  public Meter getMeter(String name){
    if(!metersMap.containsKey(name)){
      metersMap.put(name, new Meter());
    }
    return metersMap.get(name);
  }

  public Gauge getGauge(String name){
    if(!gaugesMap.containsKey(name)){
      gaugesMap.put(name, new Gauge());
    }
    return gaugesMap.get(name);
  }

  public Counter getCounter(String name){
    if(!countersMap.containsKey(name)){
      countersMap.put(name, new Counter());
    }
    return countersMap.get(name);
  }

  public Timer getTimer(String name){
    if(!timersMap.containsKey(name)){
      timersMap.put(name, new Timer());
    }
    return timersMap.get(name);
  }

  public Histogram getHistogram(String name){
    if(!histogramsMap.containsKey(name)){
      histogramsMap.put(name, new Histogram());
    }
    return histogramsMap.get(name);
  }

  public static class Reservoir {
    private static int MAX_SIZE = 1024;
    private Random randomGenerator = new Random();
    private List<Long> valueList = new ArrayList<>();

    public void update(long value){
      if(valueList.size() >= MAX_SIZE){
        // randomely select a position
        int i = randomGenerator.nextInt(MAX_SIZE);
        valueList.set(i, value);
      } else {
        valueList.add(value);
      }
    }

    public int getSize(){
      return valueList.size();
    }
  }

  public static class Histogram {

    private long minValue=Long.MAX_VALUE;
    private long maxValue=Long.MIN_VALUE;
    private long valuesSum=0;
    private long valuesCount = 0;
    private double avgValue=0;
    private Reservoir reservoir = new Reservoir();


    public void update(long value) {
      reservoir.update(value);

      if(value < minValue) minValue = value;
      if(value > maxValue) maxValue = value;
      valuesSum += value;
      valuesCount ++;
      avgValue = valuesSum / valuesCount;
    }

    public long getMinValue() {
      return minValue;
    }

    public long getMaxValue() {
      return maxValue;
    }

    public long getValuesSum() {
      return valuesSum;
    }

    public double getAvgValue() {
      return avgValue;
    }

    public long getValuesCount() {
      return valuesCount;
    }
  }

  public static class Timer {
    private long elapsedTime=0;
    private long startTime=0;
    private State state=State.STOPPED;
    private Histogram history = new Histogram();
    private Meter meter = new Meter();

    public void startMeasure(){
      if(state != State.STARTED) {
        startTime = System.nanoTime();
        if(state == State.STOPPED) {
          elapsedTime = 0;
          meter.update();
        }
        state = State.STARTED;
      } else {
        throw new RuntimeException("Timer already started");
      }
    }

    public void pauseMeasure(){
      if(state == State.STARTED) {
        elapsedTime = getElapsed();
        state = State.PAUSED;
      } else {
        throw new RuntimeException("Timer is not started");
      }

    }

    public void stopMeasure(){
      if(state != State.STOPPED) {
        elapsedTime = getElapsed();
        history.update(elapsedTime);
        state = State.STOPPED;
      } else {
        throw new RuntimeException("Timer already stopped");
      }
    }

    private long getElapsed(){
      return System.nanoTime() - startTime + elapsedTime;
    }

    public double getAvgTime(){
      return history.getAvgValue() / Math.pow(10, 9);
    }

    public double getMinTime(){
      return history.getMinValue() / Math.pow(10, 9);
    }

    public double getMaxTime(){
      return history.getMaxValue() / Math.pow(10, 9);
    }

    private enum State {
      STOPPED,
      PAUSED,
      STARTED
    }
  }


  public static class Counter {
    int count=0;


    public void reset(){
      count = 0;
    }

    public void update(){
      count++;
    }

    public void update(int items){
      count+=items;
    }

    public int getCount(){
      return count;
    }
  }

  public static class Meter {
    private Histogram histogram = new Histogram();
    private Counter count = new Counter();
    private long lastTime = 0;

    public void update(int items){
      accountForPastTime();
      count.update(items);
      lastTime = Math.round(System.currentTimeMillis() / 1000);
    }

    private void accountForPastTime() {
      long currentTime = Math.round(System.currentTimeMillis() / 1000);
      if(lastTime > 0 && lastTime != currentTime) {
        for (long t = lastTime; t < currentTime; t++) {
          histogram.update(t == lastTime ? count.getCount() : 0);
        }
        count.reset();
      }
    }

    public  void update(){
      update(1);
    }

    public long getMinValue() {
      accountForPastTime();
      return histogram.getMinValue();
    }

    public long getMaxValue() {
      accountForPastTime();
      return histogram.getMaxValue();
    }

    public long getValueSum() {
      accountForPastTime();
      return histogram.getValuesSum();
    }

    public double getAvgValue() {
      accountForPastTime();
      return histogram.getAvgValue();
    }
  }

  public static class Gauge {
    long value =0;

    public void update(long value){
      this.value = value;
    }

    public long getValue(){
      return value;
    }
  }
}
