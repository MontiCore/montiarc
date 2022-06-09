/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Wraps a {@link CompTypeExpression} (if present). This class is used as a common state for composed visitors that
 * implement {@link ISynthesizeComponent}.
 */
public class SynthCompTypeResult {

  protected CompTypeExpression result = null;

  public void reset() {
    this.setResultAbsent();
  }

  public void setResultAbsent() {
    this.result = null;
  }

  public void setResult(@NotNull CompTypeExpression result) {
    this.result = Preconditions.checkNotNull(result);
  }

  public Optional<CompTypeExpression> getResult() {
    return Optional.ofNullable(result);
  }
}
