package app.PushYourLuck;

import java.util.Set;

class MDP implements lib.mcts.MDP<State,Action> {

  private final int nDice;
  private final int nSides;
  private Dice diceObject;

  MDP(int nDice, int nSides) {
    this.nDice = nDice;
    this.nSides = nSides;
    this.diceObject = new Dice(this.nDice, this.nSides, 0.0);
  }

  @Override
  public State initialState() {
    return new State(diceObject.markedSides());
  }

  @Override
  public State transition(State state, Action action) {
    assert state != null && action != null;
    return switch(action) {
      case ROLL -> {
        yield new State(diceObject.roll());
      }
      case CASHOUT -> {
        diceObject.cashOut();
        yield new State(diceObject.markedSides());
      }
    };
  }

  @Override
  public double reward(State previousState, Action action, State state) {
    assert state != null;
    return diceObject.instantReward();
  }

  @Override
  public boolean isTerminal(State state) {
    assert state != null;
    return false;
  }

  @Override
  public Set<Action> actions(State state) {
    assert state != null;
    return Set.of(Action.values());
  }

}
