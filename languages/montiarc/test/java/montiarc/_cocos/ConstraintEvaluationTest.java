/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc.util.VariableArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._cocos.ConstraintEvaluation;
import variablearc.check.TypeExprOfVariableComponent;

import java.util.Collections;

public class ConstraintEvaluationTest extends AbstractTest {

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

    ConstraintEvaluation coco = new ConstraintEvaluation();

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

    ConstraintEvaluation coco = new ConstraintEvaluation();

    // When
    coco.check(instance);

    // Then
    this.checkOnlyExpectedErrorsPresent();
  }

}
