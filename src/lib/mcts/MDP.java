package lib.mcts;

import java.util.Collection;

/**
 * A representation of Markov Decision Processes.
 *
 * This abstract type contains no logic but includes the definitions of functions that must be implemented for a valid
 * MDP. The derived types of this class are used by the solvers when performing Monte Carlo Tree Search (MCTS)
 *
 * @param StateType the type that represents the states of the MDP.
 * @param ActionType the type that represents the actions that can be taken in the MDP.
 */
public interface MDP<StateType, ActionType> {

  /**
   * Represents a transition of MDP state. The arguments are the current state and the action to be taken. The return
   * value is the new state.
   */
  StateType transition(StateType state, ActionType action);

  /**
   * Represents the reward function of the MDP. The arguments are the previous state, the action taken and the
   * terminal state. The first two arguments may be null. The return value is a double that represents the score of
   * the terminal state.
   */
  double reward(StateType previousState, ActionType action, StateType state);

  /**
   * Returns the initial state of the MDP.
   */
  StateType initialState();

  /**
   * Determines whether the given state is a terminal state.
   */
  boolean isTerminal(StateType state);

  /**
   * Returns a collection of actions that are available for the given state.
   */
  Collection<ActionType> actions(StateType state);

}
