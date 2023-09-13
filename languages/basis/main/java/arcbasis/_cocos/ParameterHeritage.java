/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParameterHeritage implements ArcBasisASTComponentTypeCoCo {

  protected final IArcTypeCalculator tc;

  public ParameterHeritage(@NotNull IArcTypeCalculator tc) {
    this.tc = Preconditions.checkNotNull(tc);
  }

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();

    if (component.isPresentParent()) {
      CompTypeExpression parent = component.getParent();

      checkParentInstantiationArgsAreNotTooMany(component, parent);
      if( checkParentKeywordsMustBeParameters(component, parent) &
        checkParentKeywordArgsLast(component, parent)) {
        checkParentInstantiationArgsBindAllMandatoryParams(component, parent);
        if(checkParentArgValuesUnique(component, parent) & checkParentKeywordArgsUnique(component, parent)) {
          checkParentInstantiationArgsHaveCorrectTypes(component, parent);
        }
      }
    }
  }

  /**
   * Checks that there are not more parent configuration arguments provided than there are parent configuration parameters in the
   * component type that should be instantiated.
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected void checkParentInstantiationArgsAreNotTooMany(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    List<ASTArcArgument> parentArgs = comp.getParentConfiguration();
    List<VariableSymbol> paramsOfParentCompType = parent.getTypeInfo().getParameters();

    if (parentArgs.size() > paramsOfParentCompType.size()) {
      ASTArcArgument firstIllegalArg = parentArgs.get(paramsOfParentCompType.size());
      ASTArcArgument lastIllegalArg = parentArgs.get(parentArgs.size() - 1);

      Log.error(ArcError.HERITAGE_TOO_MANY_ARGUMENTS.format(
          paramsOfParentCompType.size(), parentArgs.size()
      ), firstIllegalArg.get_SourcePositionStart(), lastIllegalArg.get_SourcePositionEnd());
    }
  }

  /**
   * Checks that there are enough parent configuration arguments provided to bind all mandatory parent configuration parameters of the
   * component type that should be instantiated.
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected void checkParentInstantiationArgsBindAllMandatoryParams(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());


    List<ASTArcArgument> parentArgs = comp.getParentConfiguration();
    List<VariableSymbol> paramsOfParentCompType = parent.getTypeInfo().getParameters();

    long mandatoryParamsAmount = paramsOfParentCompType.stream()
        .map(VariableSymbol::getAstNode)
        .map(ASTArcParameter.class::cast)
        .filter(param -> !param.isPresentDefault())
        .count();

    List<String> paramNames = paramsOfParentCompType.stream()
      .map(VariableSymbol::getName).collect(Collectors.toList());
    Map<String,Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    List<ASTArcArgument> keywordArgs = parentArgs.stream()
      .filter(ASTArcArgument::isPresentName)
      .collect(Collectors.toList());

    int defaultAssignedByKey = 0;

    for (ASTArcArgument keywordArg : keywordArgs) {
      String argumentKey = keywordArg.getName();
      int paramIndex = paramIndices.get(argumentKey);
      if (paramIndex >= mandatoryParamsAmount) {
        defaultAssignedByKey++;
      }
    }

    if (mandatoryParamsAmount + defaultAssignedByKey > parentArgs.size()) {
      SourcePosition lastArgumentPos = !parentArgs.isEmpty() ?
        parentArgs.get(parentArgs.size() - 1).get_SourcePositionEnd() :
        comp.getAstNode().get_SourcePositionEnd();

      Log.error(ArcError.HERITAGE_TOO_FEW_ARGUMENTS.format(
        mandatoryParamsAmount, parentArgs.size()
      ), lastArgumentPos);
    }

  }

  /**
   * Checks that keyword instantiations only use keywords referring to parameters of the component instance type
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected boolean checkParentKeywordsMustBeParameters(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent){
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    List<ASTArcArgument> keywordArgs =  comp.getParentConfiguration().stream()
      .filter(ASTArcArgument::isPresentName)
      .collect(Collectors.toList());

    boolean keysAreParams = true;

    for (ASTArcArgument argument : keywordArgs){
      String paramName = argument.getName();

      if (parent.getTypeInfo().getParameters().stream()
        .noneMatch(param -> param.getName().equals(paramName))){
        Log.error(ArcError.HERITAGE_COMP_ARG_KEY_INVALID.format(
            comp.getName()), argument.get_SourcePositionStart(),
          argument.get_SourcePositionEnd()
        );
        keysAreParams = false;
      }

    }
    return keysAreParams;
  }

  /**
   * Checks that keyword based parent configuration arguments are only used once
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected boolean checkParentKeywordArgsUnique(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    List<ASTArcArgument> parentArgs = comp.getParentConfiguration();
    Set<String> keywordArguments = new HashSet<>();
    int keywordCounter;

    boolean isUnique = true;
    for (ASTArcArgument argument : parentArgs) {
      if (argument.isPresentName()) {
        keywordCounter = keywordArguments.size();
        keywordArguments.add(argument.getName());
        if (keywordCounter == keywordArguments.size()) {
          Log.error(ArcError.HERITAGE_KEY_NOT_UNIQUE.format(
              argument.getName(), comp.getName()), argument.get_SourcePositionStart(),
            argument.get_SourcePositionEnd()
          );
          isUnique = false;
        }
      }
    }

    return isUnique;
  }

  /**
   * Checks that keyword based parent configuration arguments do not overwrite position based parameter values
   * set position based Keywords.
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected boolean checkParentArgValuesUnique(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    List<ASTArcArgument> parentArgs = comp.getParentConfiguration();
    long posArgsAmount = parentArgs.stream()
      .filter(Predicate.not(ASTArcArgument::isPresentName))
      .count();

    List<String> paramNames = parent.getTypeInfo().getParameters()
      .stream().map(VariableSymbol::getName).collect(Collectors.toList());

    Map<String,Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    boolean isUnique = true;
    for (ASTArcArgument argument : parentArgs) {
      if (argument.isPresentName()) {
        String argumentKey = argument.getName();
        int paramIndex = paramIndices.get(argumentKey);
        if (paramIndex < posArgsAmount) {
          Log.error(ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES.format(argumentKey),
            argument.get_SourcePositionStart(), argument.get_SourcePositionEnd()
          );
          isUnique = false;
        }
      }
    }

    return isUnique;
  }

  /**
   * Checks that keyword based instantiation arguments of a component instance come after positional arguments.
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected boolean checkParentKeywordArgsLast(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    boolean keywordAssignmentPresent=false;
    boolean rightArgumentOrder=true;

    List<ASTArcArgument> instantiationArgs = comp.getParentConfiguration();

    for (ASTArcArgument argument : instantiationArgs) {
      if (argument.isPresentName()){
        keywordAssignmentPresent=true;
      }else{
        if(keywordAssignmentPresent) {
          Log.error(ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY.format(
              comp.getName()), argument.get_SourcePositionStart(),
            argument.get_SourcePositionEnd());
          rightArgumentOrder=false;
        }
      }
    }
    return rightArgumentOrder;
  }

  /**
   * Checks that all arguments have compatible types with the parent's configurations arguments.
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected void checkParentInstantiationArgsHaveCorrectTypes(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {

    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    List<ASTArcArgument> parentArgs = comp.getParentConfiguration();

    List<Optional<SymTypeExpression>> parentSignature = parent.getTypeInfo()
      .getParameters().stream()
      .map(ISymbol::getName)
      .map(parent::getParameterType)
      .collect(Collectors.toList());

    List<SymTypeExpression> parentArgsCheck = parentArgs.stream()
      .map(ASTArcArgument::getExpression).map(this.tc::typeOf)
      .collect(Collectors.toList());

    List<String> paramNames = parent.getTypeInfo().getParameters().stream()
      .map(VariableSymbol::getName).collect(Collectors.toList());
    Map<String,Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    for (int i = 0; i < Math.min(parentArgsCheck.size(), parentSignature.size()); i++) {
      if (parentArgsCheck.get(i).isObscureType()) {
        Log.debug(parent.getArcArguments().get(i).get_SourcePositionStart()
            + ": Skip execution of CoCo, could not calculate the expression's type.",
          this.getClass().getCanonicalName()
        );
      } else if (parentArgs.get(i).isPresentName()) {
        String argumentKey = parentArgs.get(i).getName();
        int paramIndex = paramIndices.get(argumentKey);
        if (parentSignature.get(paramIndex).isEmpty() || !SymTypeRelations.isCompatible(parentSignature.get(paramIndex).get(), parentArgsCheck.get(i))) {
          ASTArcArgument incompatibleArgument = comp.getParentConfiguration().get(i);

          Log.error(ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH.format(parentArgsCheck.get(i).printFullName(),
              parentSignature.get(i).map(SymTypeExpression::printFullName).orElse("UNKNOWN")),
            incompatibleArgument.get_SourcePositionStart(), incompatibleArgument.get_SourcePositionEnd()
          );
        }
      } else {
        if (parentSignature.get(i).isEmpty() || !SymTypeRelations.isCompatible(parentSignature.get(i).get(), parentArgsCheck.get(i))) {
          ASTArcArgument incompatibleArgument = comp.getParentConfiguration().get(i);

          Log.error(ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH.format(parentArgsCheck.get(i).printFullName(),
              parentSignature.get(i).map(SymTypeExpression::printFullName).orElse("UNKNOWN")),
            incompatibleArgument.get_SourcePositionStart(), incompatibleArgument.get_SourcePositionEnd());
        }
      }
    }
  }
}
