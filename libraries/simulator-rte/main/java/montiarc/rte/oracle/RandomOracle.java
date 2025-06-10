/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Random;

/**
 * An oracle that randomly selects an option among alternatives.<p>
 * The randomness can be controlled by supplying a seed to
 * {@link #RandomOracle(long)} to support  determinism.
 */
public class RandomOracle implements Oracle {

  private final Random generator;

  public RandomOracle() {
    this.generator = new Random();
  }

  public RandomOracle(long seed) {
    this.generator = new Random(seed);
  }

  public RandomOracle(Random generator) {
    Preconditions.checkNotNull(generator);
    this.generator = generator;
  }

  /**
   * Selects a random element from within {@code options}
   * @param options Must not be empty; must not be null.
   */
  @Override
  public <T> T decideAmong(List<T> options) {
    Preconditions.checkNotNull(options);
    Preconditions.checkArgument(!options.isEmpty());

    return options.get(generator.nextInt(options.size()));
  }
}
