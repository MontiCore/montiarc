/* (c) https://github.com/MontiCore/monticore */
package arcbasis.trafo;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;

public class SourcePositionUtil {
  private SourcePositionUtil() { }

  /**
   * @return A new {@link SourcePosition} with the same line number and file name (if given) like {@code otherPos}, but
   * with {@code amount} added to the column number.
   * @throws IllegalArgumentException if amount < 0
   */
  public static SourcePosition elongate(SourcePosition otherPos, int amount) {
    Preconditions.checkArgument(amount >= 0);

    SourcePosition newPos = new SourcePosition(otherPos.getLine(),otherPos.getColumn() + amount);
    otherPos.getFileName().ifPresent(newPos::setFileName);
    return newPos;
  }
}
