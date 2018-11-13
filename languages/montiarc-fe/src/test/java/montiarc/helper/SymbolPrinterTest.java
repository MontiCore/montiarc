package montiarc.helper;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.TypeReference;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class SymbolPrinterTest extends AbstractCoCoTest {

  @Test
  public void printArrayDimensions() {
    final ComponentSymbol componentSymbol
        = loadComponentSymbol("components.body.ports", "CompWithArrays");
    final Optional<PortSymbol> stringArrayIn = componentSymbol.getPort("stringArrayIn");
    assertTrue(stringArrayIn.isPresent());
    String result = SymbolPrinter.printArrayDimensions(stringArrayIn.get().getTypeReference());
    assertEquals("[][]", result);

    final Optional<PortSymbol> intNoArrayIn = componentSymbol.getPort("intNoArrayIn");
    assertTrue(intNoArrayIn.isPresent());
    result = SymbolPrinter.printArrayDimensions(intNoArrayIn.get().getTypeReference());
    assertEquals("", result);
  }
}

