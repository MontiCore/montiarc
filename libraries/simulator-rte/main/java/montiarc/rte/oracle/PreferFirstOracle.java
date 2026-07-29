/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.List;

/**
 * Selects the first element passed to {@link #decideAmong(List)}.
 * <p>
 * {@link #decideAmong(java.util.Collection)} and
 * {@link #decideAmong(java.util.Map)} return the first element
 * of the iterator obtained by {@link java.util.Collection#iterator()} / the
 * iterator of {@link HashMap#keySet()}.
 * Thus, if an internally ordered data structure that upholds its order in its
 * iterator is passed as an argument, then the corresponding methods will return
 * the first element in that order. Examples for such ordered data structures
 * are {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap}.
 * If unordered data structures are passed, any element may be returned.
 */
public class PreferFirstOracle implements Oracle {

  /**
   * Returns the first element of {@code options}
   * @param options Must not be empty; Must not be null.
   * @param <T> The type of the options upon which this oracle decides.
   */
  @Override
  public <T> T decideAmong(List<T> options) {
    Preconditions.checkNotNull(options);
    Preconditions.checkArgument(!options.isEmpty());

    return options.getFirst();
  }
}
