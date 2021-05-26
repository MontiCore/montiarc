/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis.util.ArcError;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for the handwritten methods of {@link FieldTypeExists}.
 */
public class FieldTypeExistsTest extends AbstractTest {

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType type = createQualifiedType("Integer");
    String[] names = new String[] { "v1", "v2", "v3" };
    ASTArcFieldDeclaration ast = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(type).setArcFieldList(names, this.mockValues(names.length)).build();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(ast).setName("Watermelon");
    FieldTypeExists coco = new FieldTypeExists();
    coco.check(ast.getArcField(0));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_FIELD });
  }

  protected ASTExpression[] mockValues(int length) {
    ASTExpression[] values = new ASTExpression[length];
    for (int i = 0; i < length; i++) {
      values[i] = Mockito.mock(ASTExpression.class);
    }
    return values;
  }
}