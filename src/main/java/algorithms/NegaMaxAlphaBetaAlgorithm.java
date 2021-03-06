package algorithms;

import algorithms.NegaMaxAlphaBetaAlgorithm.CachedValue.CacheFlag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.MetricRegistry;
import utils.MetricRegistry.Timer;

/**
 * Generic implementation of the minimax algorithm
 *
 * Supports partial search, caching and backtracking
 */
public class NegaMaxAlphaBetaAlgorithm<N extends NegaMaxAlphaBetaAlgorithm.INode<A>, A extends NegaMaxAlphaBetaAlgorithm.IAction<N>>{


  private static MetricRegistry metricRegistry = MetricRegistry.getInstance();
  private boolean useCaching = false;
  private int     startDepth = Integer.MAX_VALUE;
  private Map<INode, CachedValue> cache = new HashMap<>();
  private String prefix = this.getClass().getName();

  /**
   *
   * @param useCaching if set to true, we will cache previously visited nodes
   * @param startDepth Specify the maximal depth to explore
   */
  public NegaMaxAlphaBetaAlgorithm(boolean useCaching, int startDepth) {
    this.useCaching = useCaching;
    this.startDepth = startDepth;
  }

  /**
   *
   */
  public NegaMaxAlphaBetaAlgorithm() {
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
      double outcome = -negaMax(node, this.startDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
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
  public double negaMax(N startNode, int depth, double alpha, double beta){
    /*metricRegistry.getCounter(prefix + "negaMaxWithMemorisation").update();

    double value = Double.NEGATIVE_INFINITY;
    if(depth == 0 || startNode.isTerminal()){
      return startNode.getUtility();
    }
    if(useCaching && cache.containsKey(startNode)){
      metricRegistry.getCounter(prefix + "CacheHit").update();
      double cachedValue = cache.get(startNode);
      if(cachedValue >= beta) return cachedValue;
      if(cachedValue <= alpha) return cachedValue;
      alpha = Math.max(alpha, cachedValue);
      beta = Math.min(alpha, cachedValue);
    } else {
      metricRegistry.getCounter(prefix + "CacheMiss").update();
    }

    for (A action: startNode.getPossibleActions()){
      N node  = action.apply(startNode);
      value = Math.max(value, -negaMaxWithMemorisation(node, depth - 1, -beta, -alpha));
      alpha = Math.max(alpha, value);
      action.undo(node);
      if(alpha >= beta){
        break;
      }
    }

    if(useCaching){
      cache.put(startNode, value);
    }
    return value;*/
    double orgAlpha = alpha;
    if(useCaching && cache.containsKey(startNode)){
      metricRegistry.getCounter(prefix + "CacheHit").update();
      CachedValue cachedValue = cache.get(startNode);
      if(cachedValue.depth >= depth){
        switch (cachedValue.flag ){
          case EXACT:
            return cachedValue.value;
          case LOWERBOUND:
            alpha = Math.max(alpha, cachedValue.value);
            break;
          case UPPERBOUND:
            beta = Math.min(beta, cachedValue.value);
            break;
        }
        if (alpha >= beta) return cachedValue.value;
      }
    } else {
      metricRegistry.getCounter(prefix + "CacheMiss").update();
    }

    if(depth == 0 || startNode.isTerminal()){
      return startNode.getUtility();
    }

    double bestValue = Double.NEGATIVE_INFINITY;

    for (A action: startNode.getPossibleActions()){
      N node  = action.apply(startNode);
      bestValue = Math.max(bestValue, -negaMax(node, depth - 1, -beta, -alpha));
      alpha = Math.max(alpha, bestValue);
      action.undo(node);
      if(alpha >= beta){
        break;
      }
    }

    if(useCaching){
      CachedValue cachedValue = new CachedValue();
      cachedValue.setDepth(depth);
      cachedValue.setValue(bestValue);
      if (bestValue <= orgAlpha){
        cachedValue.setFlag(CacheFlag.UPPERBOUND);
      } else if (bestValue >= beta){
        cachedValue.setFlag(CacheFlag.LOWERBOUND);
      } else {
        cachedValue.setFlag(CacheFlag.EXACT);
      }
      cache.put(startNode, cachedValue);
    }

    return bestValue;
  }


  public String report() {
    return String.format(
        this.getClass().getName() +
        ": \n"
        + " GlobalTimer: MIN: %.2f, AVG: %.2f, MAX: %.2f\n"
        + " LocalTimer: MIN: %.2f, AVG: %.2f, MAX: %.2f\n"
        + " Counter: %d\n"
        + " CacheSize: Min: %s, AVG: %.2f, Max: %s\n"
        + " Cache: Miss: %s, Hit: %s",
      metricRegistry.getTimer(prefix + "computeBestAction").getMinTime(),
      metricRegistry.getTimer(prefix + "computeBestAction").getAvgTime(),
      metricRegistry.getTimer(prefix + "computeBestAction").getMaxTime(),
      metricRegistry.getTimer(prefix + "computeBestActionPerNode").getMinTime(),
      metricRegistry.getTimer(prefix + "computeBestActionPerNode").getAvgTime(),
      metricRegistry.getTimer(prefix + "computeBestActionPerNode").getMaxTime(),
      metricRegistry.getCounter(prefix + "negaMaxWithMemorisation").getCount(),
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

  public static class CachedValue{

    private CacheFlag flag;
    private int depth;
    private double value;

    public CacheFlag getFlag() {
      return flag;
    }

    public void setFlag(CacheFlag flag) {
      this.flag = flag;
    }

    public double getValue() {
      return value;
    }

    public void setValue(double value) {
      this.value = value;
    }

    public int getDepth() {
      return depth;
    }

    public void setDepth(int depth) {
      this.depth = depth;

    }

    public static enum CacheFlag {
      EXACT,
      LOWERBOUND,
      UPPERBOUND
    }
  }


}



