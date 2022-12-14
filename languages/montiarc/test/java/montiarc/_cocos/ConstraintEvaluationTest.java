/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import montiarc.MontiArcMill;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._cocos.ConstraintSatisfied4Comp;
import variablearc._cocos.ConstraintSatisfied4SubComp;
import variablearc.check.TypeExprOfVariableComponent;

import java.util.Collections;
import java.util.stream.Stream;

public class ConstraintEvaluationTest extends AbstractCoCoTest {

  protected ASTComponentInstance getInstanceWithConstraint(
      ASTArcConstraintDeclaration constraint) {
    ASTComponentType componentType = MontiArcMill.componentTypeBuilder()
        .setName("comp1").setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(MontiArcMill.componentBodyBuilder()
            .setArcElementsList(Collections.singletonList(constraint)).build())
        .build();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();

    symTab.createFromAST(componentType);

    ASTComponentInstance instance = MontiArcMill.componentInstanceBuilder()
        .setName("inst1").setArgumentsAbsent().build();
    instance.setSymbol(MontiArcMill.componentInstanceSymbolBuilder()
        .setName("inst1")
        .setType(new TypeExprOfVariableComponent(componentType.getSymbol()))
        .build());
    return instance;
  }

  @Test
  public void constraintFalse() {
    // Given
    ASTArcConstraintDeclaration constraint = MontiArcMill.arcConstraintDeclarationBuilder()
        .setExpression(
            MontiArcMill
                .literalExpressionBuilder()
                .setLiteral(MontiArcMill.booleanLiteralBuilder()
                    .setSource(ASTConstantsMCCommonLiterals.FALSE).build())
                .build())
        .build();

    ASTComponentInstance instance = getInstanceWithConstraint(constraint);

    ConstraintSatisfied4SubComp coco = new ConstraintSatisfied4SubComp();

    // When
    coco.check(instance);

    // Then
    this.checkOnlyExpectedErrorsPresent(VariableArcError.CONSTRAINT_NOT_SATISFIED);
  }

  @Test
  public void constraintTrue() {
    // Given
    ASTArcConstraintDeclaration constraint = MontiArcMill.arcConstraintDeclarationBuilder()
        .setExpression(
            MontiArcMill
                .literalExpressionBuilder()
                .setLiteral(MontiArcMill.booleanLiteralBuilder()
                    .setSource(ASTConstantsMCCommonLiterals.TRUE).build())
                .build())
        .build();

    ASTComponentInstance instance = getInstanceWithConstraint(constraint);

    ConstraintSatisfied4SubComp coco = new ConstraintSatisfied4SubComp();

    // When
    coco.check(instance);

    // Then
    this.checkOnlyExpectedErrorsPresent();
  }

  @BeforeEach
  public void prepareModels() {
    loadComponentsToInstantiate();
  }

  public void loadComponentsToInstantiate() {
    // loading helper models into the symboltable
    this.parseAndCreateAndCompleteSymbols("ComponentWithBooleanParameterAsConstraint.arc");
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
        arg("AllConstraintsSatisfiable.arc"),
        arg("SatisfiedInstanceConstraints.arc"),
        arg("NeverSatisfiedConstraints.arc", VariableArcError.CONSTRAINT_NEVER_SATISFIED),
        arg("NeverSatisfiedConstraintsWithParameter.arc", VariableArcError.CONSTRAINT_NEVER_SATISFIED),
        arg("NeverSatisfiedInstanceConstraints.arc", VariableArcError.CONSTRAINT_NOT_SATISFIED)
    );
  }

  protected static Arguments arg(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    return Arguments.of(model, errors);
  }

  @Override
  protected String getPackage() {
    return "constraintEvaluation";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ConstraintSatisfied4SubComp());
    checker.addCoCo(new ConstraintSatisfied4Comp());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void testInvalidModelHasErrors(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    testModel(model, errors);
  }
}
