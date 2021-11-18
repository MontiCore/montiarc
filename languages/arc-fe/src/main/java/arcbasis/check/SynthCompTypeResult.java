/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Wraps a {@link CompTypeExpression} (if present). This class is used as a common state for composed visitors that
 * implement {@link ISynthesizeCompTypeExpression}.
 */
public class SynthCompTypeResult {

  protected CompTypeExpression currentResult = null;

  public void reset() {
    this.setCurrentResultAbsent();
  }

  public void setCurrentResultAbsent() {
    this.currentResult = null;
  }

  public void setCurrentResult(@NotNull CompTypeExpression result) {
    this.currentResult = Preconditions.checkNotNull(result);
  }

  public Optional<CompTypeExpression> getCurrentResult() {
    return Optional.ofNullable(currentResult);
  }
}
