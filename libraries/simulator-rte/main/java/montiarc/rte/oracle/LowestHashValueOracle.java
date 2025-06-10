/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import com.google.common.base.Preconditions;

import java.util.Comparator;
import java.util.List;

/**
 * An oracle that selects the option with the lowest hash value.<p>
 * Hence, the order becomes irrelevant and the decision is deterministic, as
 * long as the {@link Object#hashCode()} method is implemented so that it
 * returns the same value among different system executions.
 */
public class LowestHashValueOracle implements Oracle {

  /**
   * Selects the option with the lowest hash value
   * @param options Must not be empty; must not be null.
   */
  @Override
  public <T> T decideAmong(List<T> options) {
    Preconditions.checkNotNull(options);
    Preconditions.checkArgument(!options.isEmpty());

    return
      options.stream()
        .min(Comparator.comparingInt(Object::hashCode))
        .orElseThrow();
  }
}
