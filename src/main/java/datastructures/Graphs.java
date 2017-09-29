package datastructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graphs {

  public static class WeightedDirectedGraph<N, E> {

    private final Map<N, Map<E, N>> adjacencyMap = new HashMap<>();

    public void addNode(N value) {
      if (!containsNode(value)) {
        adjacencyMap.put(value, new HashMap<E, N>());
      }
    }

    public void addEdge(N src, E edge, N dst) {
      addNode(src);
      addNode(dst);
      getEdges(src).put(edge, dst);
    }

    public boolean containsNode(N node) {
      return adjacencyMap.containsKey(node);
    }

    public N removeEdge(N src, E edge) {
      return getEdges(src).remove(edge);
    }

    public Set<N> getNodes() {
      return adjacencyMap.keySet();
    }

    public Map<E, N> getEdges(N currentValue) {
      return adjacencyMap.get(currentValue);
    }

    public Map<E, N> removeNode(N src) {
      return adjacencyMap.remove(src);
    }
  }

  public static class DirectedGraph<N> {
    private final WeightedDirectedGraph<N, Integer> subGraph = new WeightedDirectedGraph<>();

    public Set<N> getNodes(){
      return subGraph.getNodes();
    }

    public Collection<N> removeNode(N src){
      return subGraph.removeNode(src).values();
    }

    public boolean containsNode(N src){
      return subGraph.containsNode(src);
    }

    public void addNode(N node){
      subGraph.addNode(node);
    }

    public Collection<N> getNext(N node){
      return subGraph.getEdges(node).values();
    }

    public void addEdge(N src, N dst){
      subGraph.addEdge(src, 1, dst);
    }
  }

  public static class UndirectedGraph<N> extends DirectedGraph<N>{

    @Override
    public void addEdge(N src, N dst) {
      super.addEdge(src, dst);
      super.addEdge(dst, src);
    }
  }

  public static class UndirectedWeightedGraph<N, E> extends WeightedDirectedGraph<N, E>{

    @Override
    public void addEdge(N src, E edge, N dst) {
      super.addEdge(src, edge, dst);
      super.addEdge(dst, edge, src);
    }
  }
}
