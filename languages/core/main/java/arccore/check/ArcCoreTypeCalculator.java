/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcCore.
 */
public class ArcCoreTypeCalculator extends AbstractArcTypeCalculator {

  public ArcCoreTypeCalculator() {
    this(init(ArcCoreMill.traverser()));
  }

  protected ArcCoreTypeCalculator(@NotNull ArcCoreTraverser t) {
    super(t);
  }

  public static ArcCoreTraverser init(@NotNull ArcCoreTraverser t) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    return t;
  }
}