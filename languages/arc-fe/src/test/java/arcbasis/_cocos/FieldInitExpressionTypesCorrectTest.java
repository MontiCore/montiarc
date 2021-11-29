/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis.check.ArcBasisDeriveTypeTest;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link FieldInitExpressionTypesCorrect}
 */
public class FieldInitExpressionTypesCorrectTest extends ArcBasisDeriveTypeTest {

  @Override
  public void setUpFields() {
    super.setUpFields();

    FieldSymbol anInt = ArcBasisMill.fieldSymbolBuilder().setName("anInt")
      .setType(SymTypeExpressionFactory.createTypeConstant("int")).build();
    FieldSymbol aBool = ArcBasisMill.fieldSymbolBuilder().setName("aBool")
      .setType(SymTypeExpressionFactory.createTypeConstant("boolean")).build();
    FieldSymbol aDouble = ArcBasisMill.fieldSymbolBuilder().setName("aDouble")
      .setType(SymTypeExpressionFactory.createTypeConstant("double")).build();

    this.add2Scope(this.scope, anInt, aBool, aDouble);
  }

  @Test
  public void shouldFindCorrectType() {
    ASTArcField field = ArcBasisMill.arcFieldBuilder()
      .setName("fooField")
      .setInitial(doBuildNameExpressionInScope("anInt"))
      .build();

    ASTArcFieldDeclaration fieldDecl = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(createQualifiedType("int"))
      .addArcField(field)
      .build();

    ASTComponentType enclComp = encloseFieldInCompType(fieldDecl);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(enclComp);

    FieldInitExpressionTypesCorrect coco = new FieldInitExpressionTypesCorrect();
    coco.check(field);

    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindCorrectTypeAutoCasted() {
    ASTArcField field = ArcBasisMill.arcFieldBuilder()
      .setName("fooField")
      .setInitial(doBuildNameExpressionInScope("anInt"))
      .build();

    ASTArcFieldDeclaration fieldDecl = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
      .addArcField(field)
      .build();

    ASTComponentType enclComp = encloseFieldInCompType(fieldDecl);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(enclComp);

    FieldInitExpressionTypesCorrect coco = new FieldInitExpressionTypesCorrect();
    coco.check(field);

    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindWrongType() {
    ASTArcField field = ArcBasisMill.arcFieldBuilder()
      .setName("foofield")
      .setInitial(doBuildNameExpressionInScope("aBool"))
      .build();

    ASTArcFieldDeclaration fieldDecl = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(createQualifiedType("int"))
      .addArcField(field)
      .build();

    ASTComponentType enclComp = encloseFieldInCompType(fieldDecl);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(enclComp);

    FieldInitExpressionTypesCorrect coco = new FieldInitExpressionTypesCorrect();
    coco.check(field);

    this.checkOnlyExpectedErrorsPresent(ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE);
  }

  protected ASTComponentType encloseFieldInCompType(@NotNull ASTArcFieldDeclaration field) {
    Preconditions.checkArgument(field != null);

    return ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder().addArcElement(field).build())
      .build();
  }

  protected ASTExpression doBuildNameExpressionInScope(@NotNull String expression) {
    Preconditions.checkArgument(expression != null);
    ASTExpression result = this.doBuildNameExpression(expression);
    this.getScopeSetter().handle(result);
    return result;
  }
}
