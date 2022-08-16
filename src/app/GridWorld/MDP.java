package app.GridWorld;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

class MDP implements lib.mcts.MDP<State, Action> {

  private final int xSize;
  private final int ySize;
  private final List<Reward> rewards;
  private final double transitionProbability;
  private final State startingLocation;
  private final Map<State, State> rewardStates;

  MDP(int xSize, int ySize, List<Reward> rewards, double transitionProbability, State startingLocation) {
    this.xSize = xSize;
    this.ySize = ySize;
    assert rewards != null : "rewards";
    this.rewards = rewards;
    this.transitionProbability = transitionProbability;
    assert startingLocation != null : "startingLocation";
    this.startingLocation = startingLocation;
    this.rewardStates = new LinkedHashMap<>();
  }

  static Reward any(List<Reward> list, Position item) {
    for (var r:list) {
      if (r.equals(item)) return r;
    }
    return null;
  }

  @Override
  public State initialState() {
    return startingLocation;
  }

  @Override
  public boolean isTerminal(State state) {
    return any(rewards,state) != null;
  }

  @Override
  public double reward(State previousState, Action action, State state) {
    var r = any(rewards,state);
    return r != null ? r.value() : 0.0;
  }

  @Override
  public State transition(State state, Action action) {
    if (state.isTerminal()) {
      return state;
    } else if (any(rewards,state) != null) {
      return rewardStates.getOrDefault(state, new State(state.x, state.y, true));
    }

    // if target is out of bounds, return current state
    var targetNeighbour = state.resolveNeighbour(action, xSize, ySize);
    if (targetNeighbour == null) {
      return state;
    }

    if (Math.random() < transitionProbability){
      return targetNeighbour;
    } else {
      var actions = Action.values();
      var nonTargetNeighbours = new State[actions.length];
      var i = 0;
      for (var a:actions) {
        if (a.equals(action)) continue;
        var possibleNeighbour = state.resolveNeighbour(a, xSize, ySize);
        if (possibleNeighbour != null) {
          nonTargetNeighbours[i++] = possibleNeighbour;
        }
      }
      if (i > 0) {
        return nonTargetNeighbours[(int)(Math.random() * i)];
      }
      throw new IllegalStateException("No valid neighbours exist");
    }
  }

  @Override
  public Set<Action> actions(State state) {
    var set = new HashSet<Action>();
    for (var a:Action.values()) {
      if (state.isNeighbourValid(a, xSize, ySize)) {
        set.add(a);
      }
    }
    return set;
  }

}
/*
fun visualizeState() {
    val stateArray = Array(xSize) { Array(ySize){"-"}}
    stateArray[this.startingLocation.y][this.startingLocation.x] = "A"
    for (r in rewards) {
        if (r.value > 0) stateArray[r.y][r.x] = "*"
        if (r.value < 0) stateArray[r.y][r.x] = "X"
    }
    for (i in stateArray.size -1 downTo 0) {
        println(stateArray[i].contentToString())
    }
}
*/
