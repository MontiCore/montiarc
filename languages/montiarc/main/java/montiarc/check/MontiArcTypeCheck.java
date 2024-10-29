/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.ArcBasisTypeCheck;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsCTTIVisitor;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.ocl.setexpressions.types3.SetExpressionsCTTIVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.VariableArcTypeCheck;
import variablearc.check.VariableArcVariantWithinScopeBasicSymbolsResolver;

/**
 * TypeCheck3 implementation for Montiarc. After calling {@link #init()}, this
 * implementation will be available through the TypeCheck3 interface.
 */
public class MontiArcTypeCheck extends VariableArcTypeCheck {

  private static final String LOG_NAME = MontiArcTypeCheck.class.getSimpleName();

  /**
   * @see MapBasedTypeCheck3(ITraverser, Type4Ast, InferenceContext4Ast)
   */
  protected MontiArcTypeCheck(MontiArcTraverser typeTraverser,
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
      MontiArcMill.inheritanceTraverser(),
      new Type4Ast(),
      new InferenceContext4Ast(),
      new VariableArcVariantWithinScopeBasicSymbolsResolver(),
      new MAOOWithinTypeBasicSymbolsResolver()
    );
  }

  protected static void initTC3Delegate(@NotNull MontiArcTraverser traverser,
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
    setDelegate(new MontiArcTypeCheck(traverser, type4Ast, ctx4Ast));
    Log.trace("Finish initializing the type-check delegate", LOG_NAME);
  }

  protected static void initTypeVisitors(@NotNull MontiArcTraverser traverser,
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
    ArcBasisTypeCheck.initTypeVisitors(traverser, type4Ast, ctx4Ast, inScopeResolver, inTypeResolver);
    initCommonExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast, inScopeResolver, inTypeResolver);
    initAssignmentExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initBitExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initMCCollectionTypesTypeVisitor(traverser, type4Ast, ctx4Ast);
    initMCSimpleGenericTypesTypeVisitor(traverser, type4Ast, ctx4Ast);
    initSetExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    Log.trace("Finish initializing the visitors of the type-check delegate", LOG_NAME);
  }

  protected static void initCommonExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                         @NotNull Type4Ast type4Ast,
                                                         @NotNull InferenceContext4Ast ctx4Ast,
                                                         @NotNull WithinScopeBasicSymbolsResolver inScopeResolver,
                                                         @NotNull WithinTypeBasicSymbolsResolver inTypeResolver) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Preconditions.checkNotNull(inScopeResolver);
    Preconditions.checkNotNull(inTypeResolver);
    Log.trace("Start initializing the CommonExpressions visitor of the type-check delegate", LOG_NAME);
    CommonExpressionsCTTIVisitor visitor = new CommonExpressionsCTTIVisitor();
    visitor.setType4Ast(type4Ast);
    visitor.setContext4Ast(ctx4Ast);
    visitor.setWithinTypeBasicSymbolsResolver(inTypeResolver);
    visitor.setWithinScopeResolver(inScopeResolver);
    traverser.add4CommonExpressions(visitor);
    traverser.setCommonExpressionsHandler(visitor);
    Log.trace("Finish initializing the CommonExpressions visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initAssignmentExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                             @NotNull Type4Ast type4Ast,
                                                             @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace("Start initializing the AssignmentExpressions visitor of the type-check delegate", LOG_NAME);
    AssignmentExpressionsCTTIVisitor visitor = new AssignmentExpressionsCTTIVisitor();
    visitor.setType4Ast(type4Ast);
    visitor.setContext4Ast(ctx4Ast);
    traverser.add4AssignmentExpressions(visitor);
    traverser.setAssignmentExpressionsHandler(visitor);
    Log.trace("Finish initializing the AssignmentExpressions visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initBitExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                      @NotNull Type4Ast type4Ast,
                                                      @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace("Start initializing the BitExpressions visitor of the type-check delegate", LOG_NAME);
    BitExpressionsTypeVisitor visitor = new BitExpressionsTypeVisitor();
    visitor.setType4Ast(type4Ast);
    visitor.setContext4Ast(ctx4Ast);
    traverser.add4BitExpressions(visitor);
    Log.trace("Finish initializing the BitExpressions visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initMCCollectionTypesTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                         @NotNull Type4Ast type4Ast,
                                                         @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace("Start initializing the MCCollectionTypes visitor of the type-check delegate", LOG_NAME);
    MCCollectionTypesTypeVisitor visitor = new MCCollectionTypesTypeVisitor();
    visitor.setType4Ast(type4Ast);
    visitor.setContext4Ast(ctx4Ast);
    traverser.add4MCCollectionTypes(visitor);
    Log.trace("Finish initializing the MCCollectionTypes visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initMCSimpleGenericTypesTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                            @NotNull Type4Ast type4Ast,
                                                            @NotNull InferenceContext4Ast ctx4Ast) {
    Log.trace("Start initializing the SimpleGenericTypes visitor of the type-check delegate", LOG_NAME);
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    MCSimpleGenericTypesTypeVisitor visitor = new MCSimpleGenericTypesTypeVisitor();
    visitor.setType4Ast(type4Ast);
    visitor.setContext4Ast(ctx4Ast);
    traverser.add4MCSimpleGenericTypes(visitor);
    Log.trace("Finish initializing the SimpleGenericTypes visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initSetExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                      @NotNull Type4Ast type4Ast,
                                                      @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace("Start initializing the SetExpressionsType visitor of the type-check delegate", LOG_NAME);
    SetExpressionsCTTIVisitor visitor = new SetExpressionsCTTIVisitor();
    visitor.setType4Ast(type4Ast);
    visitor.setContext4Ast(ctx4Ast);
    traverser.add4SetExpressions(visitor);
    traverser.setSetExpressionsHandler(visitor);
    Log.trace("Finish initializing the SetExpressionsType visitor of the type-check delegate", LOG_NAME);
  }
}
