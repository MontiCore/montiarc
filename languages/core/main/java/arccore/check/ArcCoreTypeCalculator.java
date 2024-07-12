/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCalculator3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcCore.
 */
public class ArcCoreTypeCalculator extends AbstractArcTypeCalculator {

  public ArcCoreTypeCalculator() {
    this(init(new TypeCalculator3(ArcCoreMill.traverser(), new Type4Ast(), new InferenceContext4Ast())));
  }

  protected ArcCoreTypeCalculator(@NotNull TypeCalculator3 t) {
    super(t);
  }

  public static TypeCalculator3 init(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    return t;
  }
}