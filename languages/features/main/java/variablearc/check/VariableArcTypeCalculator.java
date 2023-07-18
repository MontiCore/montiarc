/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in VariableArc.
 */
public class VariableArcTypeCalculator extends AbstractArcTypeCalculator {

  public VariableArcTypeCalculator() {
    this(init(VariableArcMill.traverser()));
  }

  protected VariableArcTypeCalculator(@NotNull VariableArcTraverser t) {
    super(t);
  }

  public static VariableArcTraverser init(@NotNull VariableArcTraverser t) {
    Preconditions.checkNotNull(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    return t;
  }
}
