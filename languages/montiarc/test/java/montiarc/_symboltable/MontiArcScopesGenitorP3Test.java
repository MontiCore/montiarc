/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.collect.Multimap;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The class under test is {@link MontiArcScopesGenitor}.
 */
public class MontiArcScopesGenitorP3Test extends MontiArcTestBase {

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    this.setUpComponents();
  }

  protected void setUpComponents() {
    compile("""
      component Medium{
        port in int in;
        port out int out;
      
        in -> out;
      }
      """);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    """
      component Empty {
        port in int in1;
        port in int in2;
        port out int out1;
        port out int out2;
      }
      """,
    """
      component StateChart {
        port in int in1;
        port in int in2;
        port out int out1;
        port out int out2;
      
        automaton {
          initial state S;
        }
      }
      """,
    """
      component Compute {
        port in int in1;
        port in int in2;
        port out int out1;
        port out int out2;
        init {}
        compute {}
      }
      """,
  })
  void testAtomicComponents_EffectAllPorts(@NotNull String model) {
    //given model
    //when
    ASTMACompilationUnit unit = compile(model);

    //then
    ComponentTypeSymbol symbol = unit.getArcComponentType().getSymbol();
    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    assertEquals(4, chain.size());
    List<PortSymbol> inPorts = symbol.getPorts(true, false);
    List<PortSymbol> outPorts = symbol.getPorts(false, true);
    for (PortSymbol inPort : inPorts) {
      for (PortSymbol outPort : outPorts) {
        assertTrue(chain.get(inPort).contains(outPort));
      }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {

    """
      component StateChart {
        port in int in1;
        port in int in2;
        port out int out1;
        port out int out2;
      
        <<delayed>> automaton {
          initial state S;
        }
      }
      """,
    """
      component Compute {
        port in int in1;
        port in int in2;
        port out int out1;
        port out int out2;
        init {}
        <<delayed>> compute {}
      }
      """,
    """
      component inPortsNotAffected {
        port in int input;
        port in int output;
      
        input ->  output;
      }
      """,
  })
  void testDelayedComponents_noEffect(@NotNull String model) {
    //given model
    //when
    ASTMACompilationUnit unit = compile(model);

    //then
    ComponentTypeSymbol symbol = unit.getArcComponentType().getSymbol();
    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    assertTrue(chain.isEmpty());

  }

  @Test
  void test_sameNameComponent() {
    //given
    String model = """
      component WithSubcomponent {
        port in int input;
        port out int output1;
        port out int output2;
      
        Medium m;
        Medium m;
      
        input -> m.in;
        m.out -> output1;
        m.out -> output2;
      }
      """;
    //when
    ASTMACompilationUnit unit = compile(model);

    //then
    ComponentTypeSymbol symbol = unit.getArcComponentType().getSymbol();
    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    List<PortSymbol> outputPorts = symbol.getPorts(false, true);
    PortSymbol inputPort = symbol.getPort("input").orElseThrow();

    assertEquals(2, chain.size());
    assertTrue(chain.get(inputPort).containsAll(outputPorts));
  }

  @Test
  void test_goThroughSameComponentTwice() {
    //given
    String model = """
      component WithSubcomponent {
        component Medium{
          port in int in1;
          port in int in2;
          port out int out1;
          port out int out2;
      
          in1 -> out1;
          in2 -> out2;
        }
        port in int input;
        port out int output;
        port out int noOutput;
      
        Medium m;
      
        input -> m.in1;
        m.out1 -> m.in2;
        m.out2 -> output;
      }
      """;

    //when
    ASTMACompilationUnit unit = compile(model);

    //then
    ComponentTypeSymbol symbol = unit.getArcComponentType().getSymbol();
    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    PortSymbol outputPort = symbol.getPort("output").orElseThrow();
    PortSymbol inputPort = symbol.getPort("input").orElseThrow();

    assertEquals(1, chain.size());
    assertTrue(chain.get(inputPort).contains(outputPort));
  }

  @Test
  void test_outPortWithTheSameName() {
    //given
    String model = """
      component inPorts {
        port in int input;
        port out int output;
        port out int output;
      
        input ->  output;
      }
      """;

    //when
    ASTMACompilationUnit unit = compile(model);

    //then
    ComponentTypeSymbol symbol = unit.getArcComponentType().getSymbol();
    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    PortSymbol inputPort = symbol.getPort("input").orElseThrow();
    List<PortSymbol> outPorts = symbol.getSpannedScope().resolvePortMany("output");

    assertEquals(2, chain.size());
    assertTrue(chain.get(inputPort).containsAll(outPorts));
  }

  @Test
  void test_allPortsNamedTheSame() {
    //given
    String model = """
      component SameNames {
        component Medium{
          port in int input;
          port out int output;
      
          input -> output;
        }
      
        port in int input;
        port in int input;
        port in int input2;
        port out int output;
        port out int output;
        port out int output2;
      
        Medium m;
      
        input -> m.input;
        m.output -> output;
        input2 -> output;
        input2 -> output2;
      }
      """;

    //when
    ASTMACompilationUnit unit = compile(model);

    //then
    ComponentTypeSymbol symbol = unit.getArcComponentType().getSymbol();
    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    List<PortSymbol> inputPorts = symbol.getSpannedScope().resolvePortMany("input");
    PortSymbol input2Port = symbol.getSpannedScope().resolvePortMany("input2").getFirst();
    List<PortSymbol> outputPorts = symbol.getSpannedScope().resolvePortMany("output");
    PortSymbol output2Port = symbol.getSpannedScope().resolvePortMany("output2").getFirst();

    assertEquals(7, chain.size());
    assertTrue(chain.get(inputPorts.getFirst()).containsAll(outputPorts));
    assertTrue(chain.get(inputPorts.get(1)).containsAll(outputPorts));
    assertTrue(chain.get(input2Port).containsAll(outputPorts));
    assertTrue(chain.get(input2Port).contains(output2Port));
  }

  @Test
  void test_pathEndingWithoutFinalPort() {
    //given
    String model = """
      component EndingPath {
        port in int input;
        port out int output;
        port out int noOutput;
      
        Medium m1;
        Medium m2;
      
        input -> m1.in;
        m1.out -> output;
      
        input -> m2.in;
      }
      """;

    //when
    ASTMACompilationUnit unit = compile(model);

    //then
    ComponentTypeSymbol symbol = unit.getArcComponentType().getSymbol();
    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    PortSymbol outputPort = symbol.getPort("output").orElseThrow();
    PortSymbol inputPort = symbol.getPort("input").orElseThrow();

    assertEquals(1, chain.size());
    assertTrue(chain.get(inputPort).contains(outputPort));
  }

  @Test
  void test_loop() {
    //given
    String model = """
      component WithSubcomponent {
        port in int input;
        port out int output;
      
        Medium m;
      
        input -> m.in;
        m.out -> output;
        m.out -> m.in;
      }
      """;

    //when
    ASTMACompilationUnit unit = compile(model);

    //then
    ComponentTypeSymbol symbol = unit.getArcComponentType().getSymbol();
    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    PortSymbol outputPort = symbol.getPort("output").orElseThrow();
    PortSymbol inputPort = symbol.getPort("input").orElseThrow();

    assertEquals(1, chain.size());
    assertTrue(chain.get(inputPort).contains(outputPort));
  }

  @ParameterizedTest
  @ValueSource(strings = {"""
    component ConnectorsAndBehaviour {
      port in int input;
      port out int output;
    
      input -> output;
    
      automaton {
        initial state S;
      }
    }
    """,
    """
      component ConnectedToUndefined {
        port in int input;
        port out int output;
      
        input -> undefined;
        undefined -> output;
      }
      """,
    """
      component InToInPort {
        port in int input1;
        port in int input2;
        port out int output;
      
        input2 -> input1;
        input1 -> output;
      }
      """,
    """
      component OutToOutPort {
        port in int input;
        port out int output1;
        port out int output2;
      
        input -> output1;
        output1 -> output2;
      }
      """,
    """
      component InToInPort {
        port in int input;
        port out int output1;
        port out int output2;
      
        Medium m;
      
        input -> m.in;
        m.in -> output1;
        m.out -> output2;
      }
      """,
    """
      component OutToOutPort {
        port in int input;
        port out int output1;
        port out int output2;
      
        Medium m1;
        Medium m2;
      
        input -> m1.in;
        m1.out -> m2.out;
        m2.out -> output2;
      }
      """,
    """
      component Self {
        port in int input;
        port out int output;
      
        Self s;
      
        input -> s.input;
        s.output -> output;
      }
      """,
  })
  void test_invalid_stillCompiles(@NotNull String model) {
    //when
    compile(model);

    //then
    // undefined behavior, this connection will be disallowed by a following coco
    // we only check that it terminates
    assertTrue(true);
  }
}
