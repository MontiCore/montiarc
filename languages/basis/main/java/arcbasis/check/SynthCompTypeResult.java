/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.types.check.CompKindExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Wraps a {@link CompKindExpression} (if present). This class is used as a common state for composed visitors that
 * implement {@link ISynthesizeComponent}.
 */
public class SynthCompTypeResult {

  protected CompKindExpression result = null;

  public void reset() {
    this.setResultAbsent();
  }

  public void setResultAbsent() {
    this.result = null;
  }

  public void setResult(@NotNull CompKindExpression result) {
    this.result = Preconditions.checkNotNull(result);
  }

  public Optional<CompKindExpression> getResult() {
    return Optional.ofNullable(result);
  }
}
