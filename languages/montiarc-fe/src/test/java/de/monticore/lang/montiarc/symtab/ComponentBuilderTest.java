package de.monticore.lang.montiarc.symtab;

import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.addConnectors;
import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.addInnerComponents;
import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.addPort;
import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.addPorts;
import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.addSubComponent;
import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.removeConnectors;
import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.removeInnerComponents;
import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.removePorts;
import static de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder.removeSubComponent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.lang.montiarc.AbstractSymtabTest;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.Scope;
import org.junit.Test;

/**
 * Created by MichaelvonWenckstern on 13.06.2016.
 */
public class ComponentBuilderTest extends AbstractSymtabTest {

  @Test
  public void testPorts() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve(
        "a.TypesTest", ComponentSymbol.KIND).orElse(null);
    assertNotNull(cmp);

    addPort(cmp, PortSymbol.builder().setName("newPort1").setDirection(true)
        .setTypeReference(new JavaTypeSymbolReference("Boolean", cmp.getSpannedScope(), 0)).build());
    addPorts(cmp,
        PortSymbol.builder().setName("newPort2").setDirection(false)
            .setTypeReference(new JavaTypeSymbolReference("Integer", cmp.getSpannedScope(), 2)).build(),
        PortSymbol.builder().setName("newPort3").setDirection(true)
            .setTypeReference(new JavaTypeSymbolReference("String", cmp.getSpannedScope(), 1)).build());

    PortSymbol ps = cmp.getIncomingPort("newPort1").orElse(null);
    assertNotNull(ps);
    assertEquals("Boolean", ps.getTypeReference().getName());

    ps = cmp.getOutgoingPort("newPort2").orElse(null);
    assertNotNull(ps);
    assertEquals("Integer", ps.getTypeReference().getName());

    ps = cmp.getIncomingPort("newPort3").orElse(null);
    assertNotNull(ps);
    assertEquals("String", ps.getTypeReference().getName());

    removePorts(cmp, cmp.getIncomingPort("incoming").get(),
        cmp.getOutgoingPort("outgoing").get());

    assertFalse(cmp.getIncomingPort("incoming").isPresent());
    assertFalse(cmp.getOutgoingPort("outgoing").isPresent());
  }

  @Test
  public void testConnectors() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve(
        "a.TypesTest", ComponentSymbol.KIND).orElse(null);
    assertNotNull(cmp);

    assertTrue(cmp.getConnector("gen.incoming").isPresent());
    assertTrue(cmp.getConnector("gen.nt").isPresent());

    removeConnectors(cmp, cmp.getConnector("gen.incoming").get(),
        cmp.getConnector("gen.nt").get());

    assertFalse(cmp.getConnector("gen.incoming").isPresent());
    assertFalse(cmp.getConnector("gen.nt").isPresent());

    addConnectors(cmp,
        ConnectorSymbol.builder().setSource("incoming").setTarget("gen.incoming").build(),
        ConnectorSymbol.builder().setSource("nt").setTarget("gen.nt").build());

    assertTrue(cmp.getConnector("gen.incoming").isPresent());
    assertTrue(cmp.getConnector("gen.nt").isPresent());
  }

  @Test
  public void testInnerComponents() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve(
        "a.TypesTest", ComponentSymbol.KIND).orElse(null);
    assertNotNull(cmp);

    assertFalse(cmp.getInnerComponent("Sub1").isPresent());
    assertFalse(cmp.getInnerComponent("Sub2").isPresent());

    ComponentSymbol sub1 = symTab.<ComponentSymbol>resolve(
        "a.Sub1", ComponentSymbol.KIND).orElse(null);
    assertNotNull(sub1);

    ComponentSymbol sub2 = symTab.<ComponentSymbol>resolve(
        "a.Sub2", ComponentSymbol.KIND).orElse(null);
    assertNotNull(sub2);

    addInnerComponents(cmp, sub1, sub2);

    assertTrue(cmp.getInnerComponent("Sub1").isPresent());
    assertTrue(cmp.getInnerComponent("Sub2").isPresent());

    removeInnerComponents(cmp, sub1, sub2);

    assertFalse(cmp.getInnerComponent("Sub1").isPresent());
    assertFalse(cmp.getInnerComponent("Sub2").isPresent());
  }

  @Test
  public void testSubComponents() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve(
        "a.TypesTest", ComponentSymbol.KIND).orElse(null);
    assertNotNull(cmp);

    assertTrue(cmp.getSubComponent("gen").isPresent());
    ComponentInstanceSymbol gen =  cmp.getSubComponent("gen").get();

    removeSubComponent(cmp, gen);
    assertFalse(cmp.getSubComponent("gen").isPresent());

    addSubComponent(cmp, gen);
    assertTrue(cmp.getSubComponent("gen").isPresent());
  }
}
