package algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.MetricRegistry;
import utils.MetricRegistry.Counter;
import utils.MetricRegistry.Timer;

/**
 * Generic implementation of the minimax algorithm
 *
 * Supports partial search, caching and backtracking
 */
public class MiniMaxAlgorithm <N extends MiniMaxAlgorithm.INode<A>, A extends MiniMaxAlgorithm.IAction<N>>{


  private boolean useCaching = false;
  private int     startDepth = Integer.MAX_VALUE;
  private Map<INode, Double> minCache = new HashMap<>();
  private Map<INode, Double> maxCache = new HashMap<>();

  private String prefix = this.getClass().getName();

  private static MetricRegistry metricRegistry = MetricRegistry.getInstance();

  /**
   *
   * @param useCaching if set to true, we will cache previously visited nodes
   * @param startDepth Specify the maximal depth to explore
   */
  public MiniMaxAlgorithm(boolean useCaching, int startDepth) {
    this.useCaching = useCaching;
    this.startDepth = startDepth;
  }

  /**
   *
   */
  public MiniMaxAlgorithm() {
  }

  public boolean isUseCaching() {
    return useCaching;
  }

  public void setUseCaching(boolean useCaching) {
    this.useCaching = useCaching;
  }

  public int getStartDepth() {
    return startDepth;
  }

  public void setStartDepth(int startDepth) {
    this.startDepth = startDepth;
  }

  /**
   *  Given a start node, return the best possible action based on the current state
   *
   * @param startNode
   */
  public A computeBestAction(N startNode){

    Timer globalTimer = metricRegistry.getTimer(prefix + "computeBestAction");

    globalTimer.startMeasure();
    double bestOutcome = Double.NEGATIVE_INFINITY;
    A bestAction = null;

    for (A action: startNode.getPossibleActions()){
      Timer perNodeTimer = metricRegistry.getTimer(prefix + "computeBestActionPerNode");
      perNodeTimer.startMeasure();
      N node  = action.apply(startNode);
      double outcome = minValue(node, this.startDepth);
      if (outcome > bestOutcome  ){
        bestAction = action;
        bestOutcome = outcome;
      }
      action.undo(node);
      perNodeTimer.stopMeasure();
    }
    globalTimer.stopMeasure();
    return bestAction;
  }

  /**
   *  Given a startNode, return the max value of all the possible actions that follow
   *  Depth is used to limit how far down the tree we should go. A value greater than zero means we can explore more
   * @param startNode
   * @param depth
   */
  public double maxValue(N startNode, int depth){
    metricRegistry.getCounter(prefix + "maxValue").update();

    double value = Double.NEGATIVE_INFINITY;
    if(depth == 0 || startNode.isTerminal()){
      return startNode.getUtility();
    }
    if(useCaching && maxCache.containsKey(startNode)){
      metricRegistry.getCounter(prefix + "CacheHit").update();
      return maxCache.get(startNode);
    } else {
      metricRegistry.getCounter(prefix + "CacheMiss").update();
    }

    for (A action: startNode.getPossibleActions()){
      N node  = action.apply(startNode);
      value = Math.max(value, minValue(node, depth - 1));
      action.undo(node);

    }
    if(useCaching){
      maxCache.put(startNode, value);
      metricRegistry.getHistogram(prefix + "Cache").update(minCache.size());
    }
    return value;
  }

  /**
   *  Given a startNode, return the min value of all the possible actions that follow
   *  Depth is used to limit how far down the tree we should go. A value greater than zero means we can explore more
   *
   * @param startNode
   * @param depth
   */
  public double minValue(N startNode, int depth){
    metricRegistry.getCounter(prefix + "minValue").update();
    if(depth == 0 || startNode.isTerminal()){
      return startNode.getUtility();
    }
    if(useCaching && minCache.containsKey(startNode)){
      metricRegistry.getCounter(prefix + "CacheHit").update();
      return minCache.get(startNode);
    } else {
      metricRegistry.getCounter(prefix + "CacheMiss").update();
    }

    double value = Double.POSITIVE_INFINITY;
    for (A action: startNode.getPossibleActions()){
      N node  = action.apply(startNode);
      value = Math.min(value, maxValue(node, depth - 1));
      action.undo(node);
    }

    if(useCaching){
      minCache.put(startNode, value);
      metricRegistry.getHistogram(prefix + "Cache").update(minCache.size());
    }
    return value;
  }

  public String report() {
    return String.format(
      this.getClass().getName() +
        ": \n"
        + " GlobalTimer: MIN: %.2f, AVG: %.2f, MAX: %.2f\n"
        + " LocalTimer: MIN: %.2f, AVG: %.2f, MAX: %.2f\n"
        + " Counter: Min: %d, Max: %d\n"
        + " CacheSize: Min: %s, AVG: %.2f, Max: %s\n"
        + " Cache: Miss: %s, Hit: %s",
      metricRegistry.getTimer(prefix + "computeBestAction").getMinTime(),
      metricRegistry.getTimer(prefix + "computeBestAction").getAvgTime(),
      metricRegistry.getTimer(prefix + "computeBestAction").getMaxTime(),
      metricRegistry.getTimer(prefix + "computeBestActionPerNode").getMinTime(),
      metricRegistry.getTimer(prefix + "computeBestActionPerNode").getAvgTime(),
      metricRegistry.getTimer(prefix + "computeBestActionPerNode").getMaxTime(),
      metricRegistry.getCounter(prefix + "minValue").getCount(),
      metricRegistry.getCounter(prefix + "maxValue").getCount(),
      metricRegistry.getHistogram(prefix + "Cache").getMinValue(),
      metricRegistry.getHistogram(prefix + "Cache").getAvgValue(),
      metricRegistry.getHistogram(prefix + "Cache").getMaxValue(),
      metricRegistry.getCounter(prefix + "CacheMiss").getCount(),
      metricRegistry.getCounter(prefix + "CacheHit").getCount()
    );
  }

  public static interface IAction<N extends INode> {
    /**
     * Apply the action to the current node. Returns the result node.
     * In order to save memory, it is recommended to return the passed node
     *
     * @param node
     */
    public N apply(N node);

    /**
     * Undo a previously executed action. The goal from this approach is to use backtracking to save memory instead
     * of generating new objects.
     *
     * @param node
     */
    public void undo(N node);
  }

  public static interface INode<A extends IAction> {
    /**
     *  Given the current node, return all actions which can move to the next node
     */
    public List<A> getPossibleActions();
    /**
     *  Is the current node a terminal
     */
    public boolean isTerminal();
    /**
     * Return the utility of the node
     */
    public double getUtility();
  }


}



