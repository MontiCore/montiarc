/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * Holds tests for the handwritten methods of {@link ParameterTypeExists}.
 */
public class ParameterTypeExistsTest extends AbstractTest {

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType type = createQualifiedType("Integer");
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().setArcParameterList(
        Collections.singletonList(arcbasis.ArcBasisMill.arcParameterBuilder().setName("p").setMCType(type)
          .build()))
        .build())
      .build();
    ArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(ast);
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(ast.getHead().getArcParameter(0));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_PARAMETER });
  }
}