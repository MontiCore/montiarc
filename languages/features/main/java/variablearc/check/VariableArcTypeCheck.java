/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._visitor.ArcBasisTraverser;
import arcbasis.check.ArcBasisTypeCheck;
import arcbasis.check.ArcBasisTypeContextCalculator;
import arcbasis.check.util.ArcBasisTypeVisitorOperatorCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;


/**
 * TypeCheck3 implementation for Montiarc. After calling {@link #init()}, this
 * implementation will be available through the TypeCheck3 interface.
 */
public class VariableArcTypeCheck extends ArcBasisTypeCheck {
  
  private static final String LOG_NAME = VariableArcTypeCheck.class.getSimpleName();

  protected final SymbolSelection selection;

  /**
   * @see MapBasedTypeCheck3(ITraverser, Type4Ast, InferenceContext4Ast)
   */
  protected VariableArcTypeCheck(ArcBasisTraverser typeTraverser,
                                 Type4Ast type4Ast,
                                 InferenceContext4Ast ctx4Ast,
                                 SymbolSelection selection) {
    super(typeTraverser, type4Ast, ctx4Ast);
    this.selection = Preconditions.checkNotNull(selection);
  }

  /**
   * @return the selection of symbols the initialized type check resolves names
   * to
   */
  public static SymbolSelection getSymbolSelection() {
    Preconditions.checkState(getDelegate() instanceof VariableArcTypeCheck,
      "The type check has not been initialized with a symbol selection");
    return ((VariableArcTypeCheck) getDelegate()).selection;
  }

  public static void init() {
    Log.trace(() -> "Start initializing the type-check", LOG_NAME);
    initTC3Delegate();
    Log.trace(() -> "Finished initializing the type-check", LOG_NAME);
  }

  protected static void initTC3Delegate() {
    initTC3Delegate(
      VariableArcMill.inheritanceTraverser(),
      new Type4Ast(),
      new InferenceContext4Ast()
    );
  }

  protected static void initTC3Delegate(@NotNull VariableArcTraverser traverser,
                                        @NotNull Type4Ast type4Ast,
                                        @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the type-check delegate", LOG_NAME);
    SymbolSelection selection = new SymbolSelection(type4Ast, ctx4Ast);
    VariableArcWithinScopeBasicSymbolsResolver.init(selection);
    VariableArcWithinTypeBasicSymbolsResolver.init(selection);
    VariableArcOOWithinTypeBasicSymbolsResolver.init(selection);
    ArcBasisTypeContextCalculator.init();
    ArcBasisTypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    initTypeVisitors(traverser, type4Ast, ctx4Ast);
    Log.trace(() -> "Set the type-check delegate as global type-check delegate", LOG_NAME);
    setDelegate(new VariableArcTypeCheck(traverser, type4Ast, ctx4Ast, selection));
    Log.trace(() -> "Finish initializing the type-check delegate", LOG_NAME);
  }
}
