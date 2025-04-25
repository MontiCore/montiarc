/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisTypeCheck;
import arcbasis.check.ArcBasisTypeContextCalculator;
import arcbasis.check.util.ArcBasisTypeVisitorOperatorCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsCTTIVisitor;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.ocl.setexpressions.types3.SetExpressionsCTTIVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.VariableArcTypeCheck;
import variablearc.check.VariableArcVariantWithinScopeBasicSymbolsResolver;
import variablearc.check.VariableArcVariantWithinTypeBasicSymbolsResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * TypeCheck3 implementation for Montiarc. After calling {@link #init()}, this
 * implementation will be available through the TypeCheck3 interface.
 */
public class MontiArcTypeCheck extends VariableArcTypeCheck {

  private static final String LOG_NAME = MontiArcTypeCheck.class.getSimpleName();

  static CommonExpressionsCTTIVisitor commonExpressions;
  static AssignmentExpressionsCTTIVisitor assignmentExpressions;
  static BitExpressionsTypeVisitor bitExpressions;
  static MCCollectionTypesTypeVisitor mcCollectionTypes;
  static MCSimpleGenericTypesTypeVisitor mcGenericTypes;
  static SetExpressionsCTTIVisitor setExpressions;

  /**
   * @see MapBasedTypeCheck3(ITraverser, Type4Ast, InferenceContext4Ast)
   */
  protected MontiArcTypeCheck(MontiArcTraverser typeTraverser,
                              Type4Ast type4Ast,
                              InferenceContext4Ast ctx4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
  }

  public static void init() {
    Log.trace(() -> "Start initializing the type-check", LOG_NAME);
    initTC3Delegate();
    Log.trace(() -> "Finished initializing the type-check", LOG_NAME);
  }

  protected static void initTC3Delegate() {
    initTC3Delegate(
      MontiArcMill.inheritanceTraverser(),
      new Type4Ast(),
      new InferenceContext4Ast()
    );
  }

  protected static void initTC3Delegate(@NotNull MontiArcTraverser traverser,
                                        @NotNull Type4Ast type4Ast,
                                        @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the type-check delegate", LOG_NAME);
    VariableArcVariantWithinScopeBasicSymbolsResolver.init();
    VariableArcVariantWithinTypeBasicSymbolsResolver.init();
    MAOOWithinTypeBasicSymbolsResolver.init();
    ArcBasisTypeContextCalculator.init();
    ArcBasisTypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    initTypeVisitors(traverser, type4Ast, ctx4Ast);
    Log.trace(() -> "Set the type-check delegate as global type-check delegate", LOG_NAME);
    MontiArcTypeCheck delegate = new MontiArcTypeCheck(traverser, type4Ast, ctx4Ast);
    setMADelegate(delegate);
    setDelegate(delegate);
    Log.trace(() -> "Finish initializing the type-check delegate", LOG_NAME);
  }

  protected static void initTypeVisitors(@NotNull MontiArcTraverser traverser,
                                         @NotNull Type4Ast type4Ast,
                                         @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the visitors of the type-check delegate", LOG_NAME);
    defaultContext = MontiArcMill.componentTypeSymbolBuilder()
      .setName("?DEFAULT_CONTEXT?").setSpannedScope(MontiArcMill.scope()).build();
    context2Type4AST = new HashMap<>();
    context2Type4AST.put(defaultContext, type4Ast);
    ArcBasisTypeCheck.initTypeVisitors(traverser, type4Ast, ctx4Ast);
    initCommonExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initAssignmentExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initBitExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initMCCollectionTypesTypeVisitor(traverser, type4Ast, ctx4Ast);
    initMCSimpleGenericTypesTypeVisitor(traverser, type4Ast, ctx4Ast);
    initSetExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    Log.trace(() -> "Finish initializing the visitors of the type-check delegate", LOG_NAME);
  }

  protected static void initCommonExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                         @NotNull Type4Ast type4Ast,
                                                         @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the CommonExpressions visitor of the type-check delegate", LOG_NAME);
    commonExpressions = new CommonExpressionsCTTIVisitor();
    commonExpressions.setType4Ast(type4Ast);
    commonExpressions.setContext4Ast(ctx4Ast);
    traverser.add4CommonExpressions(commonExpressions);
    traverser.setCommonExpressionsHandler(commonExpressions);
    Log.trace(() -> "Finish initializing the CommonExpressions visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initAssignmentExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                             @NotNull Type4Ast type4Ast,
                                                             @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the AssignmentExpressions visitor of the type-check delegate", LOG_NAME);
    assignmentExpressions = new AssignmentExpressionsCTTIVisitor();
    assignmentExpressions.setType4Ast(type4Ast);
    assignmentExpressions.setContext4Ast(ctx4Ast);
    traverser.add4AssignmentExpressions(assignmentExpressions);
    traverser.setAssignmentExpressionsHandler(assignmentExpressions);
    Log.trace(() -> "Finish initializing the AssignmentExpressions visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initBitExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                      @NotNull Type4Ast type4Ast,
                                                      @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the BitExpressions visitor of the type-check delegate", LOG_NAME);
    bitExpressions = new BitExpressionsTypeVisitor();
    bitExpressions.setType4Ast(type4Ast);
    bitExpressions.setContext4Ast(ctx4Ast);
    traverser.add4BitExpressions(bitExpressions);
    Log.trace(() -> "Finish initializing the BitExpressions visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initMCCollectionTypesTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                         @NotNull Type4Ast type4Ast,
                                                         @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the MCCollectionTypes visitor of the type-check delegate", LOG_NAME);
    mcCollectionTypes = new MCCollectionTypesTypeVisitor();
    mcCollectionTypes.setType4Ast(type4Ast);
    mcCollectionTypes.setContext4Ast(ctx4Ast);
    traverser.add4MCCollectionTypes(mcCollectionTypes);
    Log.trace(() -> "Finish initializing the MCCollectionTypes visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initMCSimpleGenericTypesTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                            @NotNull Type4Ast type4Ast,
                                                            @NotNull InferenceContext4Ast ctx4Ast) {
    Log.trace(() -> "Start initializing the SimpleGenericTypes visitor of the type-check delegate", LOG_NAME);
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    mcGenericTypes = new MCSimpleGenericTypesTypeVisitor();
    mcGenericTypes.setType4Ast(type4Ast);
    mcGenericTypes.setContext4Ast(ctx4Ast);
    traverser.add4MCSimpleGenericTypes(mcGenericTypes);
    Log.trace(() -> "Finish initializing the SimpleGenericTypes visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initSetExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                      @NotNull Type4Ast type4Ast,
                                                      @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the SetExpressionsType visitor of the type-check delegate", LOG_NAME);
    setExpressions = new SetExpressionsCTTIVisitor();
    setExpressions.setType4Ast(type4Ast);
    setExpressions.setContext4Ast(ctx4Ast);
    traverser.add4SetExpressions(setExpressions);
    traverser.setSetExpressionsHandler(setExpressions);
    Log.trace(() -> "Finish initializing the SetExpressionsType visitor of the type-check delegate", LOG_NAME);
  }

  static Map<ComponentTypeSymbol, Type4Ast> context2Type4AST;
  static ComponentTypeSymbol defaultContext;
  static MontiArcTypeCheck maDelegate;

  protected static Map<ComponentTypeSymbol, Type4Ast> getContext2Type4AST() {
    return context2Type4AST;
  }

  protected static ComponentTypeSymbol getDefaultContext() {
    return defaultContext;
  }

  protected static void setMADelegate(@NotNull MontiArcTypeCheck delegate) {
    Preconditions.checkNotNull(delegate);
    maDelegate = delegate;
  }

  protected static MontiArcTypeCheck getMADelegate() {
    return maDelegate;
  }

  protected void setType4AST(@NotNull Type4Ast type4Ast) {
    Preconditions.checkNotNull(type4Ast);
    this.type4Ast = type4Ast;
  }

  public static void enterContext(ComponentTypeSymbol context) {
    if (!getContext2Type4AST().containsKey(context)) {
      Type4Ast type4Ast = new Type4Ast();
      getContext2Type4AST().put(context, type4Ast);
    }
    setTyp4AST(getContext2Type4AST().get(context));
    VariableArcTypeCheck.setCurrentVariant(context);
  }

  public static void leaveContext() {
    setTyp4AST(getContext2Type4AST().get(getDefaultContext()));
    VariableArcTypeCheck.setCurrentVariant(null);
  }

  protected static void setTyp4AST(@NotNull Type4Ast type4Ast) {
    Preconditions.checkNotNull(type4Ast);
    expressionBasis.setType4Ast(type4Ast);
    mcBasicTypes.setType4Ast(type4Ast);
    mcCommonLiterals.setType4Ast(type4Ast);
    commonExpressions.setType4Ast(type4Ast);
    assignmentExpressions.setType4Ast(type4Ast);
    bitExpressions.setType4Ast(type4Ast);
    mcCollectionTypes.setType4Ast(type4Ast);
    mcGenericTypes.setType4Ast(type4Ast);
    setExpressions.setType4Ast(type4Ast);
    getMADelegate().setType4AST(type4Ast);
  }
}
