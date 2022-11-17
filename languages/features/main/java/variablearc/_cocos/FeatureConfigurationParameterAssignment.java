/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc.check.VariableArcTypeCalculator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Checks that named parameter of component instantiations are: 1. Have only a
 * name on the left side (i.e. not {@code f1 + f2 = ...}) 2. The name is an
 * existing feature 3. The right side is of type boolean
 */
public class FeatureConfigurationParameterAssignment implements ArcBasisASTComponentInstanceCoCo {

  /**
   * Used to extract the type to which instantiation arguments evaluate to.
   */
  protected final IDerive typeCalculator;

  /**
   * Creates this coco with an {@link VariableArcTypeCalculator}.
   *
   * @see #FeatureConfigurationParameterAssignment(IDerive)
   */
  public FeatureConfigurationParameterAssignment() {
    this(new VariableArcTypeCalculator());
  }

  /**
   * Creates this coco with a custom {@link IDerive} to extract the types to
   * which instantiation arguments evaluate to.
   */
  public FeatureConfigurationParameterAssignment(@NotNull IDerive typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  protected List<ASTAssignmentExpression> getNamedInstantiationArguments(@NotNull ASTComponentInstance instance) {
    return instance.getSymbol().getArguments().stream()
      .filter(astExpression -> astExpression instanceof ASTAssignmentExpression)
      .map(e -> (ASTAssignmentExpression) e).collect(Collectors.toList());
  }

  protected Map<String, ASTExpression> getNamedArgumentsMap(@NotNull ASTComponentInstance instance) {
    ImmutableMap.Builder<String, ASTExpression> argMap = ImmutableMap.builder();
    for (ASTAssignmentExpression namedParameterArgument : getNamedInstantiationArguments(instance)) {
      if (namedParameterArgument.getLeft() instanceof ASTNameExpression) {
        String name = ((ASTNameExpression) namedParameterArgument.getLeft()).getName();
        argMap.put(name, namedParameterArgument.getRight());
      } else {
        // All left parts of the assignment should be name expression. This should be tested before calling this method
        throw new IllegalStateException();
      }
    }
    return argMap.build();
  }

  @Override
  public void check(@NotNull ASTComponentInstance instance) {
    Preconditions.checkNotNull(instance);
    Preconditions.checkArgument(instance.isPresentSymbol(), "Could not perform coco check '%s'. Perhaps you missed the " + "symbol table creation.",
      this.getClass().getSimpleName());

    if (!instance.getSymbol().isPresentType()) return;

    List<ASTAssignmentExpression> instantiationArgs = getNamedInstantiationArguments(instance);

    CompTypeExpression toInstantiate = instance.getSymbol().getType();
    List<ArcFeatureSymbol> featuresOfCompType = ((IVariableArcScope) toInstantiate.getTypeInfo()
      .getSpannedScope()).getLocalArcFeatureSymbols();

    if (instantiationArgs.size() > featuresOfCompType.size()) {
      ASTExpression firstIllegalArg = instantiationArgs.get(featuresOfCompType.size());
      ASTExpression lastIllegalArg = instantiationArgs.get(instantiationArgs.size() - 1);

      Log.error(VariableArcError.TOO_MANY_INSTANTIATION_FEATURE_ARGUMENTS.format(instantiationArgs.size(), featuresOfCompType.size()),
        firstIllegalArg.get_SourcePositionStart(), lastIllegalArg.get_SourcePositionEnd());
    }
    for (int i = 0; i < instantiationArgs.size(); i++) {
      ASTAssignmentExpression assignmentExpression = instantiationArgs.get(i);
      String name = "UNKNOWN";
      if (!(assignmentExpression.getLeft() instanceof ASTNameExpression)) {
        Log.error(VariableArcError.NAMED_ARGUMENT_NO_NAME_LEFT.format(i + 1), assignmentExpression.get_SourcePositionStart(),
          assignmentExpression.get_SourcePositionEnd());
      } else {
        name = ((ASTNameExpression) assignmentExpression.getLeft()).getName();
        String finalName = name; // needed for Lambda expression
        if (featuresOfCompType.stream()
          .noneMatch(feature -> feature.getName().equals(finalName))) {
          Log.error(VariableArcError.NAMED_ARGUMENT_FEATURE_NOT_EXIST.format(name),
            assignmentExpression.get_SourcePositionStart(), assignmentExpression.get_SourcePositionEnd());
        }
      }

      TypeCheckResult typeCheckResult = typeCalculator.deriveType(assignmentExpression.getRight());

      if (typeCheckResult.isPresentResult() && !TypeCheck.isBoolean(typeCheckResult.getResult())) {
        Log.error(VariableArcError.NAMED_ARGUMENT_EXPRESSION_WRONG_TYPE.format(typeCheckResult.getResult()
            .print()),
          assignmentExpression.get_SourcePositionStart(), assignmentExpression.get_SourcePositionEnd());
      }
      if (!typeCheckResult.isPresentResult()) {
        Log.debug(String.format(
          "Checking coco '%s' is skipped for the if-statement condition, as the type of the initialization " + "expression could not be calculated. "
            + "Position: '%s'.", this.getClass()
            .getSimpleName(), assignmentExpression.get_SourcePositionStart()), "CoCos");
      }
    }
  }
}
