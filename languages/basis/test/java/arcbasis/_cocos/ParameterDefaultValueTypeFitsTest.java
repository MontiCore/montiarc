/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.ArcBasisTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link ParameterDefaultValueTypeFits}
 */
public class ParameterDefaultValueTypeFitsTest extends ArcBasisAbstractTest {

  private static final String INT_VAR_NAME = "anInt";
  private static final String BOOL_VAR_NAME = "aBool";
  private static final String DOUBLE_VAR_NAME = "aDouble";

  private static final String FOO_TYPE_NAME = "FooType";
  private static final String BAR_TYPE_NAME = "BarType";

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    this.addFieldsToScope();
    this.addTypesToScope();
  }

  public void addFieldsToScope() {
    FieldSymbol anInt = ArcBasisMill.fieldSymbolBuilder().setName(INT_VAR_NAME)
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol aBool = ArcBasisMill.fieldSymbolBuilder().setName(BOOL_VAR_NAME)
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build();
    FieldSymbol aDouble = ArcBasisMill.fieldSymbolBuilder().setName(DOUBLE_VAR_NAME)
      .setType(SymTypeExpressionFactory.createPrimitive("double")).build();

    SymbolService.link(ArcBasisMill.globalScope(), anInt);
    SymbolService.link(ArcBasisMill.globalScope(), aBool);
    SymbolService.link(ArcBasisMill.globalScope(), aDouble);
  }

  public void addTypesToScope() {
    OOTypeSymbol fooType = ArcBasisMill.oOTypeSymbolBuilder()
      .setName(FOO_TYPE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    OOTypeSymbol barType = ArcBasisMill.oOTypeSymbolBuilder()
      .setName(BAR_TYPE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    SymbolService.link(ArcBasisMill.globalScope(), fooType);
    SymbolService.link(ArcBasisMill.globalScope(), barType);
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
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(enclComp);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(enclComp);

    ParameterDefaultValueTypeFits coco = new ParameterDefaultValueTypeFits(new ArcBasisTypeCalculator(), new SymTypeRelations());

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
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())
      .setDefault(doBuildNameExpressionInGlobalScope(INT_VAR_NAME))
      .build();

    ASTComponentType enclComp = encloseParamInCompType(param);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(enclComp);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(enclComp);

    ParameterDefaultValueTypeFits coco = new ParameterDefaultValueTypeFits(new ArcBasisTypeCalculator(), new SymTypeRelations());

    //When
    coco.check(param);

    //Then
    Assertions.assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
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
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(enclComp);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(enclComp);

    ParameterDefaultValueTypeFits coco = new ParameterDefaultValueTypeFits(new ArcBasisTypeCalculator(), new SymTypeRelations());

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
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(enclComp);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(enclComp);

    ParameterDefaultValueTypeFits coco = new ParameterDefaultValueTypeFits(new ArcBasisTypeCalculator(), new SymTypeRelations());

    //When
    coco.check(param);

    //Then
    this.checkOnlyExpectedErrorsPresent(ArcError.PARAM_DEFAULT_TYPE_MISMATCH);
  }

  @Test
  @Disabled
  public void shouldFindTypeReference() {
    ASTArcParameter param = ArcBasisMill.arcParameterBuilder()
      .setName("fooField")
      .setMCType(createQualifiedType("int"))
      .setDefault(doBuildNameExpressionInGlobalScope(FOO_TYPE_NAME))
      .build();

    ASTComponentType enclComp = encloseParamInCompType(param);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(enclComp);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(enclComp);

    ParameterDefaultValueTypeFits coco = new ParameterDefaultValueTypeFits(new ArcBasisTypeCalculator(), new SymTypeRelations());

    //When
    coco.check(param);

    //Then
    //this.checkOnlyExpectedErrorsPresent(ArcError.TYPE_REF_DEFAULT_VALUE);
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
