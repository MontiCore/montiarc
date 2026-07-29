/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An oracle that selects options that have been selected the least.<br>
 * If there are multiple of such options with the same least amount of selections,
 * the choice is delegated to another configurable oracle.
 * This oracle is also queried if all options have already been explored.
 */
public class PreferLeastUsedOracle implements Oracle {

  private final Map<Object, Integer> exploredOptions = new HashMap<>();

  private final Oracle discriminator;

  /**
   * @param discriminator The oracle which takes a decision among multiple
   *                      unexplored options or when all options have already
   *                      been explored.
   */
  public PreferLeastUsedOracle(Oracle discriminator) {
    this.discriminator = Preconditions.checkNotNull(discriminator);
  }

  @Override
  public <T> T decideAmong(List<T> options) {
    Preconditions.checkNotNull(options);
    Preconditions.checkArgument(!options.isEmpty());

    List<T> leastUsedOptions = findUnexploredOptions(options);

    if (leastUsedOptions.isEmpty()) {
      return discriminator.decideAmong(options);
    } else {
      T decision = discriminator.decideAmong(leastUsedOptions);
      exploredOptions.put(decision, exploredOptions.getOrDefault(decision, 0) + 1);
      return decision;
    }
  }

  protected <T> List<T> findUnexploredOptions(List<T> options) {
    Preconditions.checkNotNull(options);

    int min = options.stream()
      .mapToInt(p -> exploredOptions.getOrDefault(p, 0))
      .min()
      .orElseThrow();

    return options.stream()
      .filter(p -> exploredOptions.getOrDefault(p, 0) == min)
      .collect(Collectors.toList());
  }
}
