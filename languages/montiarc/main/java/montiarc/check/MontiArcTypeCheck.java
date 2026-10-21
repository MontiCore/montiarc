/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.ArcBasisTypeCheck;
import arcbasis.check.ArcBasisTypeContextCalculator;
import arcbasis.check.util.ArcBasisTypeVisitorOperatorCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsCTTIVisitor;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor;
import de.monticore.expressions.tupleexpressions.types3.TupleExpressionsCTTIVisitor;
import de.monticore.ocl.setexpressions.types3.SetExpressionsCTTIVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types.mcstructuraltypes.types3.MCStructuralTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.VariableArcTypeCheck;
import variablearc.check.SymbolSelection;
import variablearc.check.VariableArcWithinScopeBasicSymbolsResolver;
import variablearc.check.VariableArcWithinTypeBasicSymbolsResolver;


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
  static StreamExpressionsTypeVisitor streamExpressions;
  static TupleExpressionsCTTIVisitor tupleExpressions;
  static MCStructuralTypesTypeVisitor mcStructuralTypes;

  /**
   * @see de.monticore.types3.util.MapBasedTypeCheck3(de.monticore.visitor.ITraverser, Type4Ast, InferenceContext4Ast)
   */
  protected MontiArcTypeCheck(MontiArcTraverser typeTraverser,
                              Type4Ast type4Ast,
                              InferenceContext4Ast ctx4Ast,
                              SymbolSelection selection) {
    super(typeTraverser, type4Ast, ctx4Ast, selection);
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
    SymbolSelection selection = new SymbolSelection(type4Ast, ctx4Ast);
    VariableArcWithinScopeBasicSymbolsResolver.init(selection);
    VariableArcWithinTypeBasicSymbolsResolver.init(selection);
    MAOOWithinTypeBasicSymbolsResolver.init(selection);
    ArcBasisTypeContextCalculator.init();
    ArcBasisTypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    initTypeVisitors(traverser, type4Ast, ctx4Ast);
    Log.trace(() -> "Set the type-check delegate as global type-check delegate", LOG_NAME);
    setDelegate(new MontiArcTypeCheck(traverser, type4Ast, ctx4Ast, selection));
    Log.trace(() -> "Finish initializing the type-check delegate", LOG_NAME);
  }

  protected static void initTypeVisitors(@NotNull MontiArcTraverser traverser,
                                         @NotNull Type4Ast type4Ast,
                                         @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the visitors of the type-check delegate", LOG_NAME);
    ArcBasisTypeCheck.initTypeVisitors(traverser, type4Ast, ctx4Ast);
    initCommonExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initAssignmentExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initBitExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initMCCollectionTypesTypeVisitor(traverser, type4Ast, ctx4Ast);
    initMCSimpleGenericTypesTypeVisitor(traverser, type4Ast, ctx4Ast);
    initSetExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initStreamExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initTupleExpressionsTypeVisitor(traverser, type4Ast, ctx4Ast);
    initMCStructuralTypesTypeVisitor(traverser, type4Ast, ctx4Ast);
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

  protected static void initStreamExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                         @NotNull Type4Ast type4Ast,
                                                         @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the StreamExpressionsType visitor of the type-check delegate", LOG_NAME);
    streamExpressions = new StreamExpressionsTypeVisitor();
    streamExpressions.setType4Ast(type4Ast);
    streamExpressions.setContext4Ast(ctx4Ast);
    traverser.add4StreamExpressions(streamExpressions);
    Log.trace(() -> "Finish initializing the StreamExpressionsType visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initTupleExpressionsTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                        @NotNull Type4Ast type4Ast,
                                                        @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the TupleExpressions visitor of the type-check delegate", LOG_NAME);
    tupleExpressions = new TupleExpressionsCTTIVisitor();
    tupleExpressions.setType4Ast(type4Ast);
    tupleExpressions.setContext4Ast(ctx4Ast);
    traverser.add4TupleExpressions(tupleExpressions);
    traverser.setTupleExpressionsHandler(tupleExpressions);
    Log.trace(() -> "Finish initializing the TupleExpressions visitor of the type-check delegate", LOG_NAME);
  }

  protected static void initMCStructuralTypesTypeVisitor(@NotNull MontiArcTraverser traverser,
                                                         @NotNull Type4Ast type4Ast,
                                                         @NotNull InferenceContext4Ast ctx4Ast) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Log.trace(() -> "Start initializing the MCStructuralTypes visitor of the type-check delegate", LOG_NAME);
    mcStructuralTypes = new MCStructuralTypesTypeVisitor();
    mcStructuralTypes.setType4Ast(type4Ast);
    traverser.add4MCStructuralTypes(mcStructuralTypes);
    Log.trace(() -> "Finish initializing the MCStructuralTypes visitor of the type-check delegate", LOG_NAME);
  }
}
