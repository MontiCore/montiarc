/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in GenericArc.
 */
public class GenericArcTypeCalculator extends AbstractArcTypeCalculator {

  public GenericArcTypeCalculator() {
    this(init(GenericArcMill.traverser()));
  }

  protected GenericArcTypeCalculator(@NotNull GenericArcTraverser t) {
    super(t);
  }

  public static GenericArcTraverser init(@NotNull GenericArcTraverser t) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    return t;
  }
}
