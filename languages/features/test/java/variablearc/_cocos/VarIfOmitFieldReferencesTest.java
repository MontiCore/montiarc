/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentHead;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcMill;
import variablearc.VariableArcTestBase;
import variablearc._ast.ASTArcVarIf;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VarIfOmitFieldReferencesTest extends VariableArcTestBase {

  protected static String featureName = "someFeature";

  protected static String fieldName = "someField";

  /**
   * Provides a component type with specified name, one feature, one field, and the provided elements
   */
  protected ASTArcComponentType provideComponentWithFieldAndFeature(@NotNull String compName,
                                                                    List<ASTArcElement> elements) {
    Preconditions.checkNotNull(compName);

    ASTArcComponentType comp = VariableArcMill.arcComponentTypeBuilder().setName(compName)
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(VariableArcMill.componentBodyBuilder()
        .addArcElement(
          VariableArcMill.arcFeatureDeclarationBuilder()
            .addArcFeature(VariableArcMill.arcFeatureBuilder()
              .setName(featureName)
              .build())
            .build())
        .addArcElement(
          VariableArcMill.arcFieldDeclarationBuilder()
            .setMCType(VariableArcMill.mCPrimitiveTypeBuilder()
              .setPrimitive(ASTConstantsMCBasicTypes.BYTE)
              .build())
            .addArcField(VariableArcMill.arcFieldBuilder()
              .setName(fieldName)
              .setInitial(Mockito.mock(ASTExpression.class))
              .build())
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
   * Provides a component type that references a feature in a varif
   */
  protected ASTArcComponentType provideCompWithFeatureRef() {
    ASTArcVarIf varif = VariableArcMill.arcVarIfBuilder()
      .setCondition(VariableArcMill.nameExpressionBuilder().setName(featureName).build())
      .setThen(Mockito.mock(ASTArcElement.class)).build();

    return provideComponentWithFieldAndFeature("WithoutFieldRef", Collections.singletonList(varif));
  }

  /**
   * Provides a component type that references a field and a feature in varifs
   */
  protected ASTArcComponentType provideCompWithFieldAndFeatureRef() {
    ASTArcVarIf varif1 =
      VariableArcMill.arcVarIfBuilder()
        .setCondition(VariableArcMill.nameExpressionBuilder().setName(fieldName).build())
        .setThen(Mockito.mock(ASTArcElement.class)).build();

    ASTArcVarIf varif2 = VariableArcMill.arcVarIfBuilder()
      .setCondition(VariableArcMill.nameExpressionBuilder().setName(featureName).build())
      .setThen(Mockito.mock(ASTArcElement.class)).build();

    return provideComponentWithFieldAndFeature("WithoutFieldRef", List.of(varif1, varif2));
  }

  @Test
  public void shouldNotFindFieldReference() {
    // Given
    ASTArcComponentType comp = provideCompWithFeatureRef();

    // When
    new VarIfOmitFieldReferences().check(comp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindFieldReference() {
    // Given
    ASTArcComponentType comp = provideCompWithFieldAndFeatureRef();

    // When
    new VarIfOmitFieldReferences().check(comp);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(
        getErrorCodes(
          VariableArcError.FIELD_REFERENCE_IN_IF_STATEMENT_ILLEGAL
        )
      );
  }
}
