/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;

import java.util.List;

public class ConstraintsOmitFieldReferencesTest extends VariableArcAbstractTest {

  protected static String featureName = "someFeature";

  protected static String fieldName = "someField";

  /**
   * Provides a component type with specified name, one feature, one field, and the provided elements
   */
  protected ASTComponentType provideComponentWithFieldAndFeature(@NotNull String compName,
                                                                 List<ASTArcElement> elements) {
    Preconditions.checkNotNull(compName);

    ASTComponentType comp = VariableArcMill.componentTypeBuilder().setName(compName)
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(VariableArcMill.componentBodyBuilder()
        .addArcElement(
          VariableArcMill.arcFeatureDeclarationBuilder()
            .setArcFeaturesList(List.of(VariableArcMill.arcFeatureBuilder()
              .setName(featureName)
              .build()))
            .build())
        .addArcElement(
          VariableArcMill.arcFieldDeclarationBuilder()
            .setMCType(VariableArcMill.mCPrimitiveTypeBuilder()
              .setPrimitive(ASTConstantsMCBasicTypes.BYTE)
              .build())
            .setArcFieldsList(List.of(
              VariableArcMill.arcFieldBuilder()
                .setName(fieldName)
                .setInitial(Mockito.mock(ASTExpression.class))
                .build()))
            .build())
        .addAllArcElements(elements)
        .build())
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(comp);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(comp);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(comp);

    return comp;
  }

  /**
   * Provides a component type that references a feature in a constraint
   */
  protected ASTComponentType provideCompWithFeatureRef() {
    ASTArcConstraintDeclaration constraint = VariableArcMill.arcConstraintDeclarationBuilder()
      .setExpression(VariableArcMill.nameExpressionBuilder()
        .setName(featureName)
        .build())
      .build();

    return provideComponentWithFieldAndFeature("WithoutFieldRef", List.of(constraint));
  }

  /**
   * Provides a component type that references a feature and a field in constraints
   */
  protected ASTComponentType provideCompWithFeatureAndFieldRef() {
    ASTArcConstraintDeclaration firstConstraint =
      VariableArcMill.arcConstraintDeclarationBuilder()
        .setExpression(VariableArcMill.nameExpressionBuilder()
          .setName(fieldName)
          .build())
        .build();

    ASTArcConstraintDeclaration secondConstraint = VariableArcMill.arcConstraintDeclarationBuilder()
      .setExpression(VariableArcMill.nameExpressionBuilder()
        .setName(featureName)
        .build())
      .build();

    return provideComponentWithFieldAndFeature("WithFieldRef", List.of(firstConstraint, secondConstraint));
  }

  @Test
  public void shouldNotFindFieldReference() {
    // Given
    ASTComponentType comp = provideCompWithFeatureRef();

    // When
    new ConstraintsOmitFieldReferences().check(comp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindFieldReference() {
    // Given
    ASTComponentType comp = provideCompWithFeatureAndFieldRef();

    // When
    new ConstraintsOmitFieldReferences().check(comp);

    // Then
    this.checkOnlyExpectedErrorsPresent(VariableArcError.FIELD_REFERENCE_IN_CONSTRAINT_ILLEGAL);
  }
}
