/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisDeriveType;
import arcbasis.check.ArcTypeCheck;
import arcbasis.check.ArcBasisSynthesizeType;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._ast.ASTVariable;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

/**
 * Checks coco R10 of [Hab16]: If a configurable component is instantiated as a subcomponent, all configuration
 * parameters have to be assigned. This coco is extended by coco MR1 of [Wor16]: Arguments of configuration parameters
 * with default values may be omitted during subcomponent declaration.
 * <p>
 * In short: * [binding]: When a component type is instantiated, all it's configuration parameters without specified
 * default values must be bound with values or expressions. When default values are overwritten, then in order of their
 * appearance in the components signature. * [typecheck]: The configuration parameters must be assignable from the
 * specified value bindings.
 * <p>
 * This coco must be checked AFTER symbol table creation! This coco can be reused by grammars that extend ArcBasis and
 * introduce new expressions by calling a constructor with a configured {@link ArcTypeCheck} or an {@link IDerive} to
 * use.
 */
public class ConfigurationParameterAssignment implements ArcBasisASTComponentInstanceCoCo {

  /**
   * Used to check whether component instantiation arguments match a component types signature.
   */
  protected final ArcTypeCheck typeChecker;

  /**
   * Creates this coco with an ArcTypeCheck, combined with {@link ArcBasisDeriveType} to check whether instantiation
   * arguments match a component types signature.
   */
  public ConfigurationParameterAssignment() {
    this(new ArcTypeCheck(new ArcBasisSynthesizeType(), new ArcBasisDeriveType(new TypeCheckResult())));
  }

  /**
   * Creates this coco with a custom ArcTypeCheck to use to check if instantiation arguments match a component types
   * signature.
   */
  public ConfigurationParameterAssignment(@NotNull ArcTypeCheck typeChecker) {
    this.typeChecker = checkNotNull(typeChecker);
  }

  /**
   * Creates this coco with a custom IDerive to use to check if instantiation arguments match a component types
   * signature.
   */
  public ConfigurationParameterAssignment(@NotNull IDerive deriverFromExpr) {
    this(new ArcTypeCheck(new ArcBasisSynthesizeType(), checkNotNull(deriverFromExpr)));
  }

  @Override
  public void check(ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "Could not perform coco check '%s'. Perhaps you missed the " +
      "symbol table creation.", this.getClass().getSimpleName());
    Preconditions.checkArgument(node.getSymbol().getType() != null);
    Preconditions.checkArgument(node.getSymbol().getType().getTypeInfo() != null);

    ComponentInstanceSymbol compInstance = node.getSymbol();
    ComponentTypeSymbol typeOfCompInstance = compInstance.getType().getTypeInfo();

    List<ASTExpression> paramBindings = compInstance.getArguments();
    List<SymTypeExpression> bindingSignature =
      paramBindings.stream()
        .map(typeChecker::typeOf)
        .collect(toList());

    List<VariableSymbol> configParamsOfType = typeOfCompInstance.getParameters();
    List<SymTypeExpression> signatureOfCompType =
      configParamsOfType.stream()
        .map(VariableSymbol::getType)
        .collect(toList());

    // checking that not too many arguments were provided during instantiation
    if (bindingSignature.size() > signatureOfCompType.size()) {
      this.logCocoViolation(bindingSignature, compInstance);
      return; // to terminate when fail-fast of logger is off (e.g. during tests)
    }

    // checking that configuration parameters are assignable from arguments
    for (int i = 0; i < bindingSignature.size(); i++) {
      if (!ArcTypeCheck.compatible(signatureOfCompType.get(i), bindingSignature.get(i))) {
        this.logCocoViolation(bindingSignature, compInstance);
        return; // to terminate when fail-fast of logger is off (e.g. during tests)
      }
    }

    // checking that all parameters left have default values
    if (!configParamsOfType.stream()
      .skip(bindingSignature.size())
      .map(VariableSymbol::getAstNode)
      .peek(astVar -> assertVarIsArcParameter(astVar, typeOfCompInstance))
      .map(astVar -> (ASTArcParameter) astVar)
      .allMatch(ASTArcParameter::isPresentDefault)) {
      this.logCocoViolation(bindingSignature, compInstance);
      return; // to terminate when fail-fast of logger is off (e.g. during tests)
    }
  }

  protected void assertVarIsArcParameter(ASTVariable configParam, ComponentTypeSymbol fromType) {
    Preconditions.checkArgument(configParam instanceof ASTArcParameter, "Could not check coco '%s', because " +
        "configuration parameter '%s' of component type '%s' is not of type '%s", this.getClass().getSimpleName(),
      configParam.getName(), fromType.getFullName(), ASTArcParameter.class.getSimpleName());
  }

  protected void logCocoViolation(
    List<SymTypeExpression> bindingSignature, ComponentInstanceSymbol compInstance) {
    Log.error(ArcError.CONFIG_PARAMETER_BINDING.format(compInstance.getType().printFullName(),
      printSignature(bindingSignature), compInstance.getFullName()));
  }

  protected String printSignature(List<SymTypeExpression> signature) {
    List<String> signatureParts = signature.stream().map(SymTypeExpression::print).collect(toList());
    return "(" + String.join(", ", signatureParts) + ")";
  }
}
