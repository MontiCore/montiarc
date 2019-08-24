/* (c) https://github.com/MontiCore/monticore */
package montiarc.helper;

import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class SymbolPrinterTest extends AbstractCoCoTest {

  @Test
  public void printArrayDimensions() {
    final ComponentSymbol componentSymbol
        = loadComponentSymbol("components.body.ports", "CompWithArrays");
    final Optional<PortSymbol> stringArrayIn
        = componentSymbol.getPort("stringArrayIn");
    assertTrue(stringArrayIn.isPresent());
    String result = SymbolPrinter.printArrayDimensions(stringArrayIn.get().getTypeReference());
    assertEquals("[][]", result);

    final Optional<PortSymbol> intNoArrayIn
        = componentSymbol.getPort("intNoArrayIn");
    assertTrue(intNoArrayIn.isPresent());
    result = SymbolPrinter.printArrayDimensions(intNoArrayIn.get().getTypeReference());
    assertEquals("", result);
  }

  @Test
  public void printTypeArguments() {
    final ComponentSymbol componentSymbol
        = loadComponentSymbol("types", "GenericComponent");
    final Optional<PortSymbol> myListKIn1 = componentSymbol.getPort("myListKIn1");
    assertTrue(myListKIn1.isPresent());
    final JTypeReference<? extends JTypeSymbol> typeReference1
        = myListKIn1.get().getTypeReference();
    String result = SymbolPrinter.printTypeArguments(typeReference1.getActualTypeArguments());
    assertEquals("<K>", result);

    final Optional<PortSymbol> myListMapKVIn1
        = componentSymbol.getPort("myListMapKVIn1");
    assertTrue(myListMapKVIn1.isPresent());
    final JTypeReference<? extends JTypeSymbol> typeReference
        = myListMapKVIn1.get().getTypeReference();
    result = SymbolPrinter.printTypeArguments(typeReference.getActualTypeArguments());
    assertEquals("<java.util.Map<K,V>>", result);
  }

  @Test
  public void printTypeParameterWithInterfaces() {
    ComponentSymbol componentSymbol = loadComponentSymbol("types", "GenericComponent");
    List<JTypeSymbol> formalTypeParameters = componentSymbol.getFormalTypeParameters();
    assertTrue(!formalTypeParameters.isEmpty());
    JTypeSymbol param1 = formalTypeParameters.get(0);
    String result = SymbolPrinter.printTypeParameterWithInterfaces(param1);
    assertEquals("K", result);

    componentSymbol = loadComponentSymbol("components.body.connectors", "GenericSourceTypeIsSubtypeOfTargetType");
    formalTypeParameters = componentSymbol.getFormalTypeParameters();
    assertTrue(!formalTypeParameters.isEmpty());
    param1 = formalTypeParameters.get(0);
    result = SymbolPrinter.printTypeParameterWithInterfaces(param1);
    assertEquals("T extends java.lang.Number", result);

    componentSymbol = loadComponentSymbol("components.head.generics", "SubCompExtendsGenericComparableCompValid");
    formalTypeParameters = componentSymbol.getFormalTypeParameters();
    assertTrue(!formalTypeParameters.isEmpty());
    param1 = formalTypeParameters.get(0);
    result = SymbolPrinter.printTypeParameterWithInterfaces(param1);
    assertEquals("K extends java.lang.Comparable<K>", result);
  }
}

