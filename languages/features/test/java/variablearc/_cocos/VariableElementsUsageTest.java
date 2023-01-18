/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcBlock;
import variablearc._ast.ASTArcIfStatement;
import variablearc._symboltable.IVariableArcScope;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class VariableElementsUsageTest extends AbstractTest {

  protected static Stream<Arguments> provideComponentAndError() {
    return Stream.of(
      Arguments.of(VariableArcMill.componentTypeBuilder()
          .setName("comp1")
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(VariableArcMill.componentBodyBuilder()
            .setArcElementsList(List.of(
              VariableArcMill.arcFeatureDeclarationBuilder()
                .setArcFeaturesList(Collections.singletonList(
                  VariableArcMill.arcFeatureBuilder().setName("f1").build()
                ))
                .build(),
              VariableArcMill.arcIfStatementBuilder().setCondition(
                  VariableArcMill.nameExpressionBuilder()
                    .setName("f1")
                    .build())
                .setThenStatement(Mockito.mock(ASTArcElement.class))
                .setElseStatementAbsent()
                .build()
            ))
            .build())
          .build(),
        new Error[]{}
      ),
      Arguments.of(VariableArcMill.componentTypeBuilder()
          .setName("comp2")
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(VariableArcMill.componentBodyBuilder()
            .setArcElementsList(List.of(
              VariableArcMill.arcBlockBuilder()
                .setArcElementsList(Collections.emptyList())
                .build()
            ))
            .build())
          .build(),
        new Error[]{ VariableArcError.ILLEGAL_USE }
      )
    );
  }

  protected static Stream<Arguments> provideBlockAndError() {
    return Stream.of(
      Arguments.of(VariableArcMill.arcBlockBuilder()
          .setArcElementsList(List.of(
            VariableArcMill.arcIfStatementBuilder().setCondition(
                VariableArcMill.nameExpressionBuilder()
                  .setName("f1")
                  .build())
              .setThenStatement(Mockito.mock(ASTArcElement.class))
              .setElseStatementAbsent()
              .build()
          ))
          .build(),
        new Error[]{}
      ),
      Arguments.of(VariableArcMill.arcBlockBuilder()
          .setArcElementsList(List.of(
            VariableArcMill.arcFeatureDeclarationBuilder()
              .setArcFeaturesList(Collections.singletonList(
                VariableArcMill.arcFeatureBuilder().setName("f1").build()
              ))
              .build(),
            VariableArcMill.arcIfStatementBuilder().setCondition(
                VariableArcMill.nameExpressionBuilder()
                  .setName("f1")
                  .build())
              .setThenStatement(Mockito.mock(ASTArcElement.class))
              .setElseStatementAbsent()
              .build()
          ))
          .build(),
        new Error[]{ VariableArcError.ILLEGAL_USE }
      ),
      Arguments.of(VariableArcMill.arcBlockBuilder()
          .setArcElementsList(List.of(
            Mockito.mock(ASTArcBehaviorElement.class),
            VariableArcMill.arcIfStatementBuilder().setCondition(
                VariableArcMill.nameExpressionBuilder()
                  .setName("f1")
                  .build())
              .setThenStatement(Mockito.mock(ASTArcElement.class))
              .setElseStatementAbsent()
              .build()
          ))
          .build(),
        new Error[]{ VariableArcError.ILLEGAL_USE }
      )
    );
  }

  protected static Stream<Arguments> provideIfStatementAndError() {
    return Stream.of(
      Arguments.of(VariableArcMill.arcIfStatementBuilder()
          .setCondition(Mockito.mock(ASTExpression.class))
          .setThenStatement(VariableArcMill.arcBlockBuilder().build())
          .build(),
        new Error[]{}
      ),
      Arguments.of(VariableArcMill.arcIfStatementBuilder()
          .setCondition(Mockito.mock(ASTExpression.class))
          .setThenStatement(Mockito.mock(ASTArcBehaviorElement.class))
          .build(),
        new Error[]{ VariableArcError.ILLEGAL_USE }
      ),
      Arguments.of(VariableArcMill.arcIfStatementBuilder()
          .setCondition(Mockito.mock(ASTExpression.class))
          .setThenStatement(VariableArcMill.arcFeatureDeclarationBuilder()
            .setArcFeaturesList(Collections.singletonList(
              VariableArcMill.arcFeatureBuilder().setName("f1").build()
            ))
            .build())
          .build(),
        new Error[]{ VariableArcError.ILLEGAL_USE }
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideBlockAndError")
  public void testBlock(ASTArcBlock block, Error[] errorList) {
    // Given
    VariableElementsUsage coco = new VariableElementsUsage();
    IVariableArcScope scope = VariableArcMill.scope();
    scope.setName("");
    block.setEnclosingScope(scope);

    // When
    coco.check(block);

    // Then
    this.checkOnlyExpectedErrorsPresent(errorList);
  }

  @ParameterizedTest
  @MethodSource("provideIfStatementAndError")
  public void testIfStatement(ASTArcIfStatement ifStatement,
                              Error[] errorList) {
    // Given
    VariableElementsUsage coco = new VariableElementsUsage();
    IVariableArcScope scope = VariableArcMill.scope();
    scope.setName("");
    ifStatement.setEnclosingScope(scope);

    // When
    coco.check(ifStatement);

    // Then
    this.checkOnlyExpectedErrorsPresent(errorList);
  }

  @ParameterizedTest
  @MethodSource("provideComponentAndError")
  public void testComponentType(ASTComponentType component, Error[] errorList) {
    // Given
    VariableElementsUsage coco = new VariableElementsUsage();

    // When
    coco.check(component);

    // Then
    this.checkOnlyExpectedErrorsPresent(errorList);
  }
}
