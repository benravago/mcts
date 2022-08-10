package lib.mcts;

import java.util.Set;
import java.util.Collection;

public abstract class AbstractNode<ActionType, SelfType extends Node<ActionType, SelfType>> implements Node<ActionType, SelfType> {

  public AbstractNode(SelfType parent, ActionType inducingAction) {
    this.parent = parent;
    this.inducingAction = inducingAction;
    this.depth = this.parent == null ? 0 : this.parent.depth() + 1;
  }

  private final SelfType parent;
  private final ActionType inducingAction;

  private final int depth;
  private int n;
  private double reward;
  private double maxReward;

  @Override
  public final SelfType parent() { return parent; }

  @Override
  public final ActionType inducingAction() { return inducingAction; }

  @Override
  public final int depth() { return depth; }

  @Override
  public final int n() { return n; }
  public final void n(int n) { this.n = n; }

  @Override
  public final double reward() { return reward; }
  public final void reward(double reward) { this.reward = reward; }

  @Override
  public final double maxReward() { return maxReward; }
  public final void maxReward(double maxReward) { this.maxReward = maxReward; }

  public abstract Collection<SelfType> children(ActionType action);
  public abstract Set<ActionType> validActions();

}
