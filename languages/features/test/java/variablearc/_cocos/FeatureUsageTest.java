/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FeatureUsageTest extends VariableArcAbstractTest {

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
              VariableArcMill.arcFeatureDeclarationBuilder()
                .setArcFeaturesList(Collections.singletonList(
                  VariableArcMill.arcFeatureBuilder().setName("f1").build()
                ))
                .build()
            ))
            .build())
          .build(),
        new Error[]{}
      ),
      Arguments.of(VariableArcMill.componentTypeBuilder()
          .setName("comp3")
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(VariableArcMill.componentBodyBuilder()
            .setArcElementsList(List.of(
              VariableArcMill.arcFeatureDeclarationBuilder()
                .setArcFeaturesList(Collections.singletonList(
                  VariableArcMill.arcFeatureBuilder().setName("f1").build()
                ))
                .build(),
              VariableArcMill.componentInstantiationBuilder()
                .setComponentInstanceList("i")
                .setMCType(VariableArcMill.mCPrimitiveTypeBuilder().build())
                .build()
            ))
            .build())
          .build(),
        new Error[]{ VariableArcError.FEATURE_UNUSED }
      )
    );
  }

  @ParameterizedTest
  @MethodSource("provideComponentAndError")
  public void testComponentType(ASTComponentType component, Error[] errorList) {
    // Given
    FeatureUsage coco = new FeatureUsage();
    VariableArcMill.scopesGenitorDelegator().createFromAST(component);

    // When
    coco.check(component);

    // Then
    this.checkOnlyExpectedErrorsPresent(errorList);
  }
}
