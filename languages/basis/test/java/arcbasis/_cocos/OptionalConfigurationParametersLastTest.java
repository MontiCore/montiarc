/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link OptionalConfigurationParametersLast}
 */
public class OptionalConfigurationParametersLastTest extends AbstractTest {

  protected static final String INT_FIELD_NAME = "anInt";

  @BeforeEach
  protected void addIntFieldToGlobalScope() {
    FieldSymbol intField = ArcBasisMill.fieldSymbolBuilder()
      .setName(INT_FIELD_NAME)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();

    ArcBasisMill.globalScope().add(intField);
  }

  @Test
  public void shouldFindCorrectOrderWithNoParameters() {
    // Given
    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName("NoParameters")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    OptionalConfigurationParametersLast coco = new OptionalConfigurationParametersLast();
    coco.check(comp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindCorrectOrderWithMandatoryParameters() {
    // Given
    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName("WithMandatoryParams")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(0, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt1")
          .setMCType(createQualifiedType("int"))
          .build())
        .addArcParameter(1, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt2")
          .setMCType(createQualifiedType("int"))
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    OptionalConfigurationParametersLast coco = new OptionalConfigurationParametersLast();
    coco.check(comp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindCorrectOrderWithMandatoryParametersThenOptionalParameters() {
    // Given
    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName("WithMandatoryThenOptionalParams")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(0, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt1")
          .setMCType(createQualifiedType("int"))
          .build())
        .addArcParameter(1, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt2")
          .setMCType(createQualifiedType("int"))
          .build())
        .addArcParameter(2, ArcBasisMill.arcParameterBuilder()
          .setName("optInt1")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .addArcParameter(3, ArcBasisMill.arcParameterBuilder()
          .setName("optInt2")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    OptionalConfigurationParametersLast coco = new OptionalConfigurationParametersLast();
    coco.check(comp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindCorrectOrderWithOptionalParameters() {
    // Given
    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName("WithOptionalParams")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(0, ArcBasisMill.arcParameterBuilder()
          .setName("optInt1")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .addArcParameter(1, ArcBasisMill.arcParameterBuilder()
          .setName("optInt2")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    OptionalConfigurationParametersLast coco = new OptionalConfigurationParametersLast();
    coco.check(comp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindWrongOrderWithMixedParameters1() {
    // Given
    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName("WithMixedParams1")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(0, ArcBasisMill.arcParameterBuilder()
          .setName("optInt1")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .addArcParameter(1, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt1")
          .setMCType(createQualifiedType("int"))
          .build())
        .addArcParameter(2, ArcBasisMill.arcParameterBuilder()
          .setName("optInt2")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    OptionalConfigurationParametersLast coco = new OptionalConfigurationParametersLast();
    coco.check(comp);

    // Then
    this.checkOnlyExpectedErrorsPresent(
      ArcError.OPTIONAL_PARAMS_LAST
    );
  }

  @Test
  public void shouldFindWrongOrderWithMixedParameters2() {
    // Given
    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName("WithMixedParams2")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(0, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt1")
          .setMCType(createQualifiedType("int"))
          .build())
        .addArcParameter(1, ArcBasisMill.arcParameterBuilder()
          .setName("optInt1")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .addArcParameter(2, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt2")
          .setMCType(createQualifiedType("int"))
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    OptionalConfigurationParametersLast coco = new OptionalConfigurationParametersLast();
    coco.check(comp);

    // Then
    this.checkOnlyExpectedErrorsPresent(
      ArcError.OPTIONAL_PARAMS_LAST
    );
  }

  @Test
  public void shouldFindWrongOrderWithOptionalThenMandatoryParameters() {
    // Given
    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName("WithOptionalThenMandatoryParams")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(0, ArcBasisMill.arcParameterBuilder()
          .setName("optInt1")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .addArcParameter(1, ArcBasisMill.arcParameterBuilder()
          .setName("optInt2")
          .setMCType(createQualifiedType("int"))
          .setDefault(ArcBasisMill.nameExpressionBuilder().setName(INT_FIELD_NAME).build())
          .build())
        .addArcParameter(2, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt1")
          .setMCType(createQualifiedType("int"))
          .build())
        .addArcParameter(3, ArcBasisMill.arcParameterBuilder()
          .setName("mandatoryInt2")
          .setMCType(createQualifiedType("int"))
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    OptionalConfigurationParametersLast coco = new OptionalConfigurationParametersLast();
    coco.check(comp);

    // Then
    this.checkOnlyExpectedErrorsPresent(
      ArcError.OPTIONAL_PARAMS_LAST, ArcError.OPTIONAL_PARAMS_LAST
    );
  }
}
