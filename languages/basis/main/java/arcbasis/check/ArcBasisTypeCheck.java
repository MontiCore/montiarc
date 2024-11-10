/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * TypeCheck3 implementation for ArcBasis. After calling {@link #init()}, this
 * implementation will be available through the TypeCheck3 interface.
 */
public class ArcBasisTypeCheck extends MapBasedTypeCheck3 {

  private static final String LOG_NAME = ArcBasisTypeCheck.class.getSimpleName();

  protected static ExpressionBasisTypeVisitor expressionBasis;
  protected static MCBasicTypesTypeVisitor mcBasicTypes;
  protected static MCCommonLiteralsTypeVisitor mcCommonLiterals;

  /**
   * @see MapBasedTypeCheck3(ITraverser, Type4Ast, InferenceContext4Ast)
   */
  protected ArcBasisTypeCheck(ArcBasisTraverser typeTraverser,
                              Type4Ast type4Ast,
                              InferenceContext4Ast ctx4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
  }

  public static void init() {
    Log.trace("Start initializing the type-check", LOG_NAME);
    initTC3Delegate();
    Log.trace("Finished initializing the type-check", LOG_NAME);
  }

  protected static void initTC3Delegate() {
    initTC3Delegate(
      ArcBasisMill.inheritanceTraverser(),
      new Type4Ast(),
      new InferenceContext4Ast(),
      new ArcBasisWithinScopeBasicSymbolsResolver(),
      new WithinTypeBasicSymbolsResolver()
    );
  }

  protected static void initTC3Delegate(@NotNull ArcBasisTraverser traverser,
                                        @NotNull Type4Ast type4Ast,
                                        @NotNull InferenceContext4Ast ctx4Ast,
                                        @NotNull WithinScopeBasicSymbolsResolver inScopeResolver,
                                        @NotNull WithinTypeBasicSymbolsResolver inTypeResolver) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Preconditions.checkNotNull(inScopeResolver);
    Preconditions.checkNotNull(inTypeResolver);
    Log.trace("Start initializing the type-check delegate", LOG_NAME);
    initTypeVisitors(traverser, type4Ast, ctx4Ast, inScopeResolver, inTypeResolver);
    Log.trace("Set the type-check delegate as global TC3 delegate", LOG_NAME);
    setDelegate(new ArcBasisTypeCheck(traverser, type4Ast, ctx4Ast));
    Log.trace("Finish initializing the type-check delegate", LOG_NAME);
  }

  protected static void initTypeVisitors(@NotNull ArcBasisTraverser traverser,
                                        @NotNull Type4Ast type4Ast,
                                        @NotNull InferenceContext4Ast ctx4Ast,
                                        @NotNull WithinScopeBasicSymbolsResolver inScopeResolver,
                                        @NotNull WithinTypeBasicSymbolsResolver inTypeResolver) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Preconditions.checkNotNull(inScopeResolver);
    Preconditions.checkNotNull(inTypeResolver);
    Log.trace("Start initializing the visitors of the type-check delegate", LOG_NAME);
    initExpressionBasisTypeVisitor(traverser, type4Ast, ctx4Ast, inScopeResolver);
    initMCBasicTypesTypeVisitor(traverser, type4Ast, ctx4Ast, inScopeResolver, inTypeResolver);
    initMCCommonLiteralsTypeVisitor(traverser, type4Ast, ctx4Ast);
    Log.trace("Finish initializing the visitors of the type-check delegate", LOG_NAME);
  }

  protected static void initExpressionBasisTypeVisitor(@NotNull ArcBasisTraverser traverser,
                                                       @NotNull Type4Ast type4Ast,
                                                       @NotNull InferenceContext4Ast ctx4Ast,
                                                       @NotNull WithinScopeBasicSymbolsResolver inScopeResolver) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Preconditions.checkNotNull(inScopeResolver);
    Log.trace("Start initializing the ExpressionBasis visitor of the type-check delegate", LOG_NAME);
    expressionBasis = new ExpressionBasisTypeVisitor();
    expressionBasis.setType4Ast(type4Ast);
    expressionBasis.setContext4Ast(ctx4Ast);
    expressionBasis.setWithinScopeResolver(inScopeResolver);
    traverser.add4ExpressionsBasis(expressionBasis);
    Log.trace("Finish initializing the ExpressionBasis visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initMCBasicTypesTypeVisitor(@NotNull ArcBasisTraverser traverser,
                                                    @NotNull Type4Ast type4Ast,
                                                    @NotNull InferenceContext4Ast ctx4Ast,
                                                    @NotNull WithinScopeBasicSymbolsResolver inScopeResolver,
                                                    @NotNull WithinTypeBasicSymbolsResolver inTypeResolver) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Preconditions.checkNotNull(inScopeResolver);
    Preconditions.checkNotNull(inTypeResolver);
    Log.trace("Start initializing the MCBasicTypes visitor of the type-check delegate", LOG_NAME);
    mcBasicTypes = new MCBasicTypesTypeVisitor();
    mcBasicTypes.setType4Ast(type4Ast);
    mcBasicTypes.setContext4Ast(ctx4Ast);
    mcBasicTypes.setWithinScopeResolver(inScopeResolver);
    mcBasicTypes.setWithinTypeResolver(inTypeResolver);
    traverser.add4MCBasicTypes(mcBasicTypes);
    Log.trace("Finish initializing the MCBasicTypes visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initMCCommonLiteralsTypeVisitor(@NotNull ArcBasisTraverser traverser,
                                                        @NotNull Type4Ast type4Ast,
                                                        @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace("Start initializing the MCCommonLiterals visitor of the type-check delegate", LOG_NAME);
    mcCommonLiterals = new MCCommonLiteralsTypeVisitor();
    mcCommonLiterals.setType4Ast(type4Ast);
    mcCommonLiterals.setContext4Ast(ctx4Ast);
    traverser.add4MCCommonLiterals(mcCommonLiterals);
    Log.trace("Finish initializing the MCCommonLiterals visitor of the type-check delegate", LOG_NAME);
  }
}
