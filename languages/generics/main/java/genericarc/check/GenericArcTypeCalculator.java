/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCalculator3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in GenericArc.
 */
public class GenericArcTypeCalculator extends AbstractArcTypeCalculator {

  public GenericArcTypeCalculator() {
    this(init(new TypeCalculator3(GenericArcMill.traverser(), new Type4Ast(), new InferenceContext4Ast())));
  }

  protected GenericArcTypeCalculator(@NotNull TypeCalculator3 t) {
    super(t);
  }

  public static TypeCalculator3 init(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    Preconditions.checkNotNull(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    // initialize the global delegate
    new MapBasedTypeCheck3(t.getTypeTraverser(), t.getType4Ast(), t.getCtx4Ast())
        .setThisAsDelegate();
    return t;
  }
}
