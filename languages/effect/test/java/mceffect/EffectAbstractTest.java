/* (c) https://github.com/MontiCore/monticore */
package mceffect;

import arcbasis._symboltable.ComponentTypeSymbol;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import mceffect._ast.ASTMCEffect;
import mceffect._parser.MCEffectParser;
import montiarc.MontiArcMill;
import org.junit.jupiter.api.Assertions;

public class EffectAbstractTest {
  protected final String modelPath = "test/resources/mceffect/";
  protected Function<String, Optional<PortSymbol>> portResolver =
      s -> MontiArcMill.globalScope().resolvePort(s);
  protected Function<String, Optional<ComponentTypeSymbol>> compResolver =
      s -> MontiArcMill.globalScope().resolveComponentType(s);

  public ASTMCEffect parseEffect(String path) {
    Optional<ASTMCEffect> ast;
    try {
      ast = new MCEffectParser().parse(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Assertions.assertTrue(ast.isPresent());
    return ast.get();
  }
}
