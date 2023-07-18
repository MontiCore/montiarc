/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcTraverser;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ComfortableArc.
 */
public class ComfortableArcTypeCalculator extends AbstractArcTypeCalculator {

  public ComfortableArcTypeCalculator() {
    this(init(ComfortableArcMill.traverser()));
  }

  protected ComfortableArcTypeCalculator(@NotNull ComfortableArcTraverser t) {
    super(t);
  }

  public static ComfortableArcTraverser init(@NotNull ComfortableArcTraverser t) {
    Preconditions.checkNotNull(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    return t;
  }
}
