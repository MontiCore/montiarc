/* (c) https://github.com/MontiCore/monticore */
package mceffect;

import arcbasis._symboltable.ArcComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import mceffect._ast.ASTMCEffect;
import mceffect._parser.MCEffectParser;
import montiarc.MontiArcMill;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class EffectAbstractTest {
  protected final String modelPath = "test/resources/mceffect/";
  protected Function<String, Optional<PortSymbol>> portResolver =
      s -> MontiArcMill.globalScope().resolvePort(s);
  protected Function<String, Optional<ArcComponentTypeSymbol>> compResolver =
      s -> MontiArcMill.globalScope().resolveArcComponentType(s);

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
