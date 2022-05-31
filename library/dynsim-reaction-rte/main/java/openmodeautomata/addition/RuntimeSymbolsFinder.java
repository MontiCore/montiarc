/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.addition;

import de.monticore.class2mc.adapters.JClass2TypeSymbolAdapter;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import openmodeautomata.runtime.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import openmodeautomata.runtime.ComponentType;
import openmodeautomata.runtime.SubcomponentBuilder;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;

public class RuntimeSymbolsFinder {
  protected List<Class<?>> runtime = new ArrayList<>();

  public RuntimeSymbolsFinder(){
    initList();
  }

  public Stream<OOTypeSymbol> createSymbols(){
    return runtime.stream().map(RuntimeSymbolsFinder::getSymbol);
  }

  protected void initList(){
    runtime.add(ComponentType.class);
    runtime.add(SubcomponentInstance.class);
    runtime.add(SubcomponentBuilder.class);
    runtime.add(PortElement.class);
    runtime.add(SourcePort.class);
    runtime.add(TargetPort.class);
    runtime.add(Connector.class);
    runtime.add(NamedArchitectureElement.class);
    runtime.add(DataType.class);
  }

  /**
   * creates a symbol adapter using MontiCore's Class2MC functionality
   *
   * @return a symbol for that
   */
  public static OOTypeSymbol getSymbol(Class<?> origin) {
    ClassLoaderRepository repository = new ClassLoaderRepository(origin.getClassLoader());
    JavaClass componentRTE;
    try {
      componentRTE = repository.loadClass(origin);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Cannot find class for rte methods.", e);
    }
    return new JClass2TypeSymbolAdapter(componentRTE);
  }
}