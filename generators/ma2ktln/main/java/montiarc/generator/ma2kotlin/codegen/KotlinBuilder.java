/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.codegen;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import montiarc._ast.ASTMACompilationUnit;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

public class KotlinBuilder {

  protected final GeneratorEngine engine;
  protected final GeneratorSetup setup;

  /**
   * creates a generator from a given setup
   * @param setup settings used for the generation
   */
  public KotlinBuilder(GeneratorSetup setup) {
    this.engine = new GeneratorEngine(setup);
    this.setup = setup;
  }

  /**
   * creates a generator with default setup, but some options have to be set manually
   * @param output target directory for the generation
   */
  public KotlinBuilder(String output) {
    this(((Supplier<GeneratorSetup>) () -> {
      Preconditions.checkNotNull(output, "Please state an output path");
      GeneratorSetup setup = new GeneratorSetup();
      setup.setDefaultFileExtension("kt");
      setup.setOutputDirectory(new File(output));
      return setup;
    }).get());
  }

  /**
   * @return a template engine configured for this setup
   */
  public GeneratorEngine getEngine() {
    return engine;
  }

  /**
   * @return a path containing a package and the filename of the component
   */
  public Path getRelativePath(ComponentTypeSymbol component){
    String[] fullName = Preconditions.checkNotNull(component).getFullName().split("\\.");
    Preconditions.checkState(fullName.length != 0, "Found unnamed component");
    fullName[fullName.length-1] += "." + setup.getDefaultFileExtension();
    return Paths.get(fullName[0], Arrays.stream(fullName).skip(1).toArray(String[]::new));
  }

  /**
   * generates a file that contains kotlin code to simulate the given component
   * @param component the entity to simulate
   */
  public void writeComponentCode(ComponentTypeSymbol component){
    Preconditions.checkNotNull(component);
    getEngine().generateNoA(
        "templates.Root.ftl",
        getRelativePath(component),
        component,
        new TemplateUtilities());
  }

  /**
   * generates kotlin code for all given files
   */
  public void writeComponentsCodes(Collection<ASTMACompilationUnit> nodes){
    Preconditions.checkNotNull(nodes);
    nodes.stream()
        .map(ASTMACompilationUnit::getComponentType)
        .peek(node -> Preconditions.checkArgument(node.isPresentSymbol(), node.getName()+" has no Symbol"))
        .map(ASTComponentType::getSymbol)
        .forEach(this::writeComponentCode);
  }
}
