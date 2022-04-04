/* (c) https://github.com/MontiCore/monticore */
package arcautomaton;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.scbasis._ast.ASTSCModifier;
import de.monticore.scbasis._ast.ASTSCSBody;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;

import java.util.regex.Pattern;

public class AbstractTest extends montiarc.util.AbstractTest{
  @BeforeEach
  public void init() {
    ArcAutomatonMill.globalScope().clear();
    ArcAutomatonMill.reset();
    ArcAutomatonMill.init();
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

    ASTComponentType comp = ArcAutomatonMill.componentTypeBuilder()
      .setName(name4Comp)
      .setHead(ArcAutomatonMill.componentHeadBuilder().build())
      .setBody(ArcAutomatonMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol sym = ArcAutomatonMill.componentTypeSymbolBuilder()
      .setName(name4Comp)
      .setSpannedScope(ArcAutomatonMill.scope())
      .build();

    comp.setSymbol(sym);
    comp.setSpannedScope(sym.getSpannedScope());
    sym.setAstNode(comp);

    return comp;
  }

  /**
   * Creates an ASTSCState with empty body, no modifiers and the given name that is linked to a SCStateSymbol with the
   * same name. Beware that the SCStateSymbol is not part of any scope.
   */
  protected static ASTSCState provideEmptyStateWithSymbol(@NotNull String name4State) {
    Preconditions.checkNotNull(name4State);

    ASTSCState astState = ArcAutomatonMill.sCStateBuilder()
      .setName(name4State)
      .setSCModifier(ArcAutomatonMill.sCModifierBuilder().build())
      .setSCSAnte(ArcAutomatonMill.sCEmptyAnteBuilder().build())
      .setSCSBody(ArcAutomatonMill.sCEmptyBodyBuilder().build()).build();

    SCStateSymbol symState = ArcAutomatonMill.sCStateSymbolBuilder()
      .setName(name4State)
      .build();

    astState.setSymbol(symState);
    symState.setAstNode(astState);

    return astState;
  }
}
