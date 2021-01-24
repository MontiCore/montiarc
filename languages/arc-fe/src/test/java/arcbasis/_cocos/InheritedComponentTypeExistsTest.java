/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for the handwritten methods of {@link InheritedComponentTypeExists}.
 */
public class InheritedComponentTypeExistsTest extends AbstractTest {

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType parent = createQualifiedType("A");
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("B")
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setParent(parent).build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(ast);
    InheritedComponentTypeExists coco = new InheritedComponentTypeExists();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_INHERITED_COMPONENT });
  }
}