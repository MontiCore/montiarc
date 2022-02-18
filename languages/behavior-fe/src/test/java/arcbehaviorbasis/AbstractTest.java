/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.TypeExprOfComponent;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.regex.Pattern;

public abstract class AbstractTest extends montiarc.util.AbstractTest {

  @BeforeEach
  public void init() {
    ArcBehaviorBasisMill.globalScope().clear();
    ArcBehaviorBasisMill.reset();
    ArcBehaviorBasisMill.init();
    addBasicTypes2Scope();
  }

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  /**
   * Creates an empty ASTComponentType that is linked to a ComponentTypeSymbol with the same name. Beware that the
   * ComponentTypeSymbol is not part of any scope.
   */
  protected static ASTComponentType provideComponentTypeWithSymbol(@NotNull String name4Comp) {
    Preconditions.checkNotNull(name4Comp);

    ASTComponentType comp = ArcBehaviorBasisMill.componentTypeBuilder()
      .setName(name4Comp)
      .setHead(ArcBehaviorBasisMill.componentHeadBuilder().build())
      .setBody(ArcBehaviorBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol sym = ArcBehaviorBasisMill.componentTypeSymbolBuilder()
      .setName(name4Comp)
      .setSpannedScope(ArcBehaviorBasisMill.scope())
      .build();

    comp.setSymbol(sym);
    comp.setSpannedScope(sym.getSpannedScope());
    sym.setAstNode(comp);

    return comp;
  }

  /**
   * Adds a component instance of type {@code instanceType} with name {@code instanceName} as a subcomponent to
   * {@code newOwner}.
   */
  protected static void addInstanceToComponentType(@NotNull ComponentTypeSymbol newOwner,
                                                   @NotNull ComponentTypeSymbol instanceType,
                                                   @NotNull String instanceName) {
    Preconditions.checkNotNull(newOwner);
    Preconditions.checkNotNull(instanceType);
    Preconditions.checkNotNull(instanceName);

    ASTComponentInstance astInst = ArcBehaviorBasisMill.componentInstanceBuilder()
      .setName(instanceName)
      .build();
    ASTComponentInstantiation astInstDecl = ArcBehaviorBasisMill.componentInstantiationBuilder()
      .addComponentInstance(astInst)
      .setMCType(Mockito.mock(ASTMCType.class))
      .build();
    ComponentInstanceSymbol symInst = ArcBehaviorBasisMill.componentInstanceSymbolBuilder()
      .setName(instanceName)
      .setType(new TypeExprOfComponent(instanceType))
      .build();

    astInst.setSymbol(symInst);
    symInst.setAstNode(astInst);

    // Add the instance to its new owner
    newOwner.getSpannedScope().add(symInst);
    astInst.setEnclosingScope(newOwner.getSpannedScope());
    astInstDecl.setEnclosingScope(newOwner.getSpannedScope());

    if(newOwner.isPresentAstNode()) {
      newOwner.getAstNode().getBody().addArcElement(astInstDecl);
    }
  }
}
