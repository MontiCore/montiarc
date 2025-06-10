/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Selects one option when prompted to decide among arbitrary option
 * alternatives.<p>
 * Implementations may implement different strategies to determine their
 * decisions.<p>
 * If deterministic behavior is desired, a deterministic oracle implementation
 * should be chosen, such as {@link PreferFirstOracle}. Additionally, the
 * options passed to the methods should stay the same among system runs in the
 * view of the criteria that are used by the chosen oracle implementation. E.g.,
 * their hashCode methods should return the same value, no matter in which
 * system execution. If this is not possible, {@link #decideAmong(Map)} can be
 * used in which every option is qualified by an identifier which itself stays
 * the same among system runs.
 */
public interface Oracle {
  
  /**
   * Selects one of the items in the list and returns it.<p>
   * The order of the list may be considered in the decision.
   * @param options Must not be empty; Must not be null.
   * @param <T> The type of the options upon which this oracle decides.
   */
  <T> T decideAmong(List<T> options);

  /**
   * Selects on of the items in the collection and returns it.<p>
   * If this method is not overwritten, then this oracle selects an option by
   * calling the implementation of {@link #decideAmong(List)} with a list
   * created by {@link ArrayList#ArrayList(Collection)}. Hence, if the
   * collection is ordered, the order is preserved.
   * @param options Must not be empty; Must not be null.
   * @param <T> The type of the options upon which this oracle decides.
   */
  default <T> T decideAmong(Collection<T> options) {
    return decideAmong(new ArrayList<>(options));
  }

  /**
   * Decides among one of the options and returns it.<p>
   * To allow for deterministic oracle decisions if the options can not be
   * guaranteed to behave the same in every system execution (e.g., because the
   * {@link Object#hashCode()} method is object-identity-based and not
   * attribute-based), this method accepts options that are qualified by an
   * identifier that provides this property: It must behave the same among
   * different system runs if it refers to the same option (e.g., its hashCode
   * method shall always return the same value).<p>
   * If this method is not overwritten, then this oracle selects an option key
   * with the implementation {@link #decideAmong(List)} and returns the
   * corresponding option. I.e., {@link #decideAmong(List)} is called with the
   * {@link Map#keySet()} of {@code options}.
   * @param options Must not be empty; Must not be null.
   * @param <K> The identifier type for qualifying the options upon which this
   *           oracle decides. Identifiers must keep their identity between
   *           different system executions. I.e., the value returned by
   *           {@link Object#hashCode()} must remain the same if the identifier
   *           refers to the same option.
   * @param <V> The type of the options upon which this oracle decides.
   */
  default <K, V> V decideAmong(Map<K, V> options) {
    Preconditions.checkNotNull(options);
    Preconditions.checkArgument(!options.isEmpty());

    return options.get(decideAmong(options.keySet()));
  }
}
