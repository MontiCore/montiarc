/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCalculator3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in VariableArc.
 */
public class VariableArcTypeCalculator extends AbstractArcTypeCalculator {

  public VariableArcTypeCalculator() {
    this(init(new TypeCalculator3(VariableArcMill.traverser(), new Type4Ast(), new InferenceContext4Ast())));
  }

  protected VariableArcTypeCalculator(@NotNull TypeCalculator3 t) {
    super(t);
  }

  public static TypeCalculator3 init(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    return t;
  }
}
