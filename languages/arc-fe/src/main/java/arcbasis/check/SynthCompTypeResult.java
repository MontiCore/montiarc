/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Wraps a {@link CompSymTypeExpression} (if present). This class is used as a common state for composed visitors that
 * implement {@link ISynthesizeCompSymTypeExpression}.
 */
public class SynthCompTypeResult {

  protected CompSymTypeExpression currentResult = null;

  public void reset() {
    this.setCurrentResultAbsent();
  }

  public void setCurrentResultAbsent() {
    this.currentResult = null;
  }

  public void setCurrentResult(@NotNull CompSymTypeExpression result) {
    this.currentResult = Preconditions.checkNotNull(result);
  }

  public Optional<CompSymTypeExpression> getCurrentResult() {
    return Optional.ofNullable(currentResult);
  }
}
