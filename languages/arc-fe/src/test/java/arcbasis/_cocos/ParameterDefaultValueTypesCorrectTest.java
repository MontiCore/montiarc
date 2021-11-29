/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link ParameterDefaultValueTypesCorrect}
 */
public class ParameterDefaultValueTypesCorrectTest extends AbstractTest {

  private static final String INT_VAR_NAME = "anInt";
  private static final String BOOL_VAR_NAME = "aBool";
  private static final String DOUBLE_VAR_NAME = "aDouble";

  @Override
  @BeforeEach
  public void init() {
    super.init();
    this.addFieldsToScope();
  }

  public void addFieldsToScope() {
    FieldSymbol anInt = ArcBasisMill.fieldSymbolBuilder().setName(INT_VAR_NAME)
      .setType(SymTypeExpressionFactory.createTypeConstant("int")).build();
    FieldSymbol aBool = ArcBasisMill.fieldSymbolBuilder().setName(BOOL_VAR_NAME)
      .setType(SymTypeExpressionFactory.createTypeConstant("boolean")).build();
    FieldSymbol aDouble = ArcBasisMill.fieldSymbolBuilder().setName(DOUBLE_VAR_NAME)
      .setType(SymTypeExpressionFactory.createTypeConstant("double")).build();

    ArcBasisMill.globalScope().add(anInt);
    ArcBasisMill.globalScope().add(aBool);
    ArcBasisMill.globalScope().add(aDouble);
  }

  @Test
  public void shouldApproveParamsWithoutDefaultValues() {
    //Given
    ASTArcParameter param = ArcBasisMill.arcParameterBuilder()
      .setName("fooField")
      .setMCType(createQualifiedType("int"))
      .build();

    ASTComponentType enclComp = encloseParamInCompType(param);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(enclComp);

    ParameterDefaultValueTypesCorrect coco = new ParameterDefaultValueTypesCorrect();

    //When
    coco.check(param);

    //Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindCorrectType() {
    //Given
    ASTArcParameter param = ArcBasisMill.arcParameterBuilder()
      .setName("fooField")
      .setMCType(createQualifiedType("int"))
      .setDefault(doBuildNameExpressionInGlobalScope(INT_VAR_NAME))
      .build();

    ASTComponentType enclComp = encloseParamInCompType(param);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(enclComp);

    ParameterDefaultValueTypesCorrect coco = new ParameterDefaultValueTypesCorrect();

    //When
    coco.check(param);

    //Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindCorrectTypeAutoCasted() {
    //Given
    ASTArcParameter param = ArcBasisMill.arcParameterBuilder()
      .setName("fooField")
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
      .setDefault(doBuildNameExpressionInGlobalScope(INT_VAR_NAME))
      .build();

    ASTComponentType enclComp = encloseParamInCompType(param);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(enclComp);

    ParameterDefaultValueTypesCorrect coco = new ParameterDefaultValueTypesCorrect();

    //When
    coco.check(param);

    //Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindWrongType() {
    //Given
    ASTArcParameter param = ArcBasisMill.arcParameterBuilder()
      .setName("fooField")
      .setMCType(createQualifiedType("int"))
      .setDefault(doBuildNameExpressionInGlobalScope(DOUBLE_VAR_NAME))
      .build();

    ASTComponentType enclComp = encloseParamInCompType(param);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(enclComp);

    ParameterDefaultValueTypesCorrect coco = new ParameterDefaultValueTypesCorrect();

    //When
    coco.check(param);

    //Then
    this.checkOnlyExpectedErrorsPresent(ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE);
  }

  protected ASTComponentType encloseParamInCompType(@NotNull ASTArcParameter param) {
    Preconditions.checkNotNull(param);

    return ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().addArcParameter(param).build())
      .build();
  }

  protected ASTExpression doBuildNameExpressionInGlobalScope(@NotNull String expression) {
    Preconditions.checkNotNull(expression);
    ASTExpression result = ArcBasisMill.nameExpressionBuilder().setName(expression).build();
    result.setEnclosingScope(ArcBasisMill.globalScope());
    return result;
  }
}
