package lib.mcts;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public interface SolverSupport {

  static int cmp(double a, double b) { return a < b ? -1 : a > b ? 1 : 0; }

  static <A,S> Set<A> exploredActions(Node<A,? super S> node) {
    return node.children().stream().map(c -> c.getInducingAction()).collect(toSet()); // .distinct()
  }

  static <A,S> List<A> unexploredActions(Node<A,? super S> node, Set<A> exploredActions) {
    var unexploredActions = new ArrayList<A>();
    for (var action:node.validActions()) {
      if (exploredActions.contains(action)) continue;
      unexploredActions.add(action);
    }
    return unexploredActions;
  }

  static <A,S> List<A> unexploredActions(Node<A,? super S> node) {
    return unexploredActions(node, exploredActions(node));
  }

  static final Random random = new Random();

  static <T> T random(Iterable<T> i) {
    if (i instanceof List<T> list) {
      return list.get(random.nextInt(list.size()));
    } else {
      var list = i.iterator();
      return list.hasNext() ? list.next() : null;
    }
  }

}
