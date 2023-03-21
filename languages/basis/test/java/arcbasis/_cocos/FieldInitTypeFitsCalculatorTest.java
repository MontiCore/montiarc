/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculatorTest;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeRelations;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link FieldInitTypeFits}
 */
public class FieldInitTypeFitsCalculatorTest extends ArcBasisTypeCalculatorTest {

  @Override
  public void setUpFields() {
    super.setUpFields();

    FieldSymbol anInt = ArcBasisMill.fieldSymbolBuilder().setName("anInt")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol aBool = ArcBasisMill.fieldSymbolBuilder().setName("aBool")
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build();
    FieldSymbol aDouble = ArcBasisMill.fieldSymbolBuilder().setName("aDouble")
      .setType(SymTypeExpressionFactory.createPrimitive("double")).build();

    SymbolService.link(this.scope, anInt, aBool, aDouble);
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

    FieldInitTypeFits coco = new FieldInitTypeFits(new ArcBasisTypeCalculator(), new TypeRelations());
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

    FieldInitTypeFits coco = new FieldInitTypeFits(new ArcBasisTypeCalculator(), new TypeRelations());
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

    FieldInitTypeFits coco = new FieldInitTypeFits(new ArcBasisTypeCalculator(), new TypeRelations());
    coco.check(field);

    this.checkOnlyExpectedErrorsPresent(ArcError.FIELD_INIT_TYPE_MISMATCH);
  }

  @Test
  public void shouldFindTypeReference() {
    ASTArcField field = ArcBasisMill.arcFieldBuilder()
      .setName("foofield")
      .setInitial(doBuildNameExpressionInScope("Person"))
      .build();

    ASTArcFieldDeclaration fieldDecl = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(createQualifiedType("int"))
      .addArcField(field)
      .build();

    ASTComponentType enclComp = encloseFieldInCompType(fieldDecl);
    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclComp);
    ArcBasisMill.symbolTableCompleterDelegator().createFromAST(enclComp);

    FieldInitTypeFits coco = new FieldInitTypeFits(new ArcBasisTypeCalculator(), new TypeRelations());
    coco.check(field);

    this.checkOnlyExpectedErrorsPresent(ArcError.FIELD_INIT_TYPE_REF);
  }

  protected ASTComponentType encloseFieldInCompType(@NotNull ASTArcFieldDeclaration field) {
    Preconditions.checkNotNull(field);

    return ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder().addArcElement(field).build())
      .build();
  }

  protected ASTExpression doBuildNameExpressionInScope(@NotNull String expression) {
    Preconditions.checkNotNull(expression);
    ASTExpression result = doBuildNameExpression(expression);
    this.getScopeSetter().setScope(result, this.getScope());
    return result;
  }
}
