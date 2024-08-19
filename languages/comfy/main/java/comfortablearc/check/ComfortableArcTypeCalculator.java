/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCalculator3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ComfortableArc.
 */
public class ComfortableArcTypeCalculator extends AbstractArcTypeCalculator {

  public ComfortableArcTypeCalculator() {
    this(init(new TypeCalculator3(ComfortableArcMill.traverser(), new Type4Ast(), new InferenceContext4Ast())));
  }

  protected ComfortableArcTypeCalculator(@NotNull TypeCalculator3 t) {
    super(t);
  }

  public static TypeCalculator3 init(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    // initialize the global delegate
    new MapBasedTypeCheck3(t.getTypeTraverser(), t.getType4Ast(), t.getCtx4Ast())
        .setThisAsDelegate();
    return t;
  }
}
