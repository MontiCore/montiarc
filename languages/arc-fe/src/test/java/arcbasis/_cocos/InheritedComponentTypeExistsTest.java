/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis._symboltable.ArcBasisSymTabMill;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link InheritedComponentTypeExists}.
 */
public class InheritedComponentTypeExistsTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType parent = ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
      ArcBasisMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build()).build();
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("B")
      .setHead(ArcBasisMill.componentHeadBuilder().setParent(parent).build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(ast);
    InheritedComponentTypeExists coco = new InheritedComponentTypeExists();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_INHERITED_COMPONENT });
  }
}