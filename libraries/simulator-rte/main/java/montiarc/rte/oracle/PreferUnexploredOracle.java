/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import com.google.common.base.Preconditions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An oracle that selects options that have not been selected before with
 * priority over already selected options.<br>
 * If there are multiple of such unexplored options, the choice is delegated
 * to another configurable oracle. This oracle is also queried if all options
 * have already been explored.
 */
public class PreferUnexploredOracle implements Oracle {

  private final Set<Object> exploredOptions = new HashSet<>();

  private final Oracle discriminator;

  /**
   * @param discriminator The oracle which takes a decision among multiple
   *                      unexplored options or when all options have already
   *                      been explored.
   */
  public PreferUnexploredOracle(Oracle discriminator) {
    this.discriminator = Preconditions.checkNotNull(discriminator);
  }

  @Override
  public <T> T decideAmong(List<T> options) {
    Preconditions.checkNotNull(options);
    Preconditions.checkArgument(!options.isEmpty());

    List<T> unexploredOptions = findUnexploredOptions(options);

    if (unexploredOptions.isEmpty()) {
      return discriminator.decideAmong(options);
    } else {
      T decision = discriminator.decideAmong(unexploredOptions);
      exploredOptions.add(decision);
      return decision;
    }
  }

  protected <T> List<T> findUnexploredOptions(List<T> options) {
    Preconditions.checkNotNull(options);
    return options.stream()
      .filter(Predicate.not(exploredOptions::contains))
      .collect(Collectors.toList());
  }
}
