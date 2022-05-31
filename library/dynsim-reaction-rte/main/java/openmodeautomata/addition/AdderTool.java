/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.addition;

import de.monticore.class2mc.Class2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.MontiArcSymbols2Json;
import org.apache.commons.cli.*;

public class AdderTool {

  public static void main(String[] args) throws ParseException {
    AdderTool tool = new AdderTool();
    tool.run(args);
  }

  public void run(String[] args) throws ParseException {
    init();
    Options options = initOptions();
    CommandLine cmd = new DefaultParser().parse(options, args);

    //help: when --help
    if(cmd.hasOption("h")) {
      printHelp(options);
      //do not continue, when help is printed.
      return;
    }
    if(cmd.hasOption("p")){
      initializePrimitives();
    }
    if(cmd.hasOption("c2mc")){
      initializeClass2MC();
    }
    if(!cmd.hasOption("f")){
      printHelp(options);
      return;
    }
    if(cmd.hasOption("s")){
      storeSymbols(createSymbolTable(cmd), cmd.getOptionValue("s")+cmd.getOptionValue("f"));
    }
  }

  public void init() {
    Log.init();
    MontiArcMill.init();
  }

  public void initializePrimitives(){
    BasicSymbolsMill.initializePrimitives();
  }

  public void printHelp(Options options) {
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("AdderTool", options);
  }

  public void storeSymbols(montiarc._symboltable.IMontiArcArtifactScope scope, String path) {
    new MontiArcSymbols2Json().store(scope, path);
    System.out.println("Stored symbols to "+path);
  }

  public Options initOptions() {
    Options options = new Options();
    options.addOption(org.apache.commons.cli.Option.builder("h")
        .longOpt("help")
        .desc("Prints this help dialog")
        .build());
    options.addOption(org.apache.commons.cli.Option.builder("s")
        .longOpt("symboltable")
        .argName("directory")
        .hasArg()
        .desc("Serialized the Symbol table of the given artifact.")
        .build());
    options.addOption(Option.builder("c2mc")
        .longOpt("class2mc")
        .desc("Enables to resolve java classes in the model path (mandatory)")
        .build());
    options.addOption(Option.builder("p")
        .longOpt("primitives")
        .desc("Adds java's primitives types to the global scope")
        .build());
    options.addOption(Option.builder("f")
        .longOpt("filename")
        .argName("filename")
        .hasArg()
        .desc("Specifies the filename for the file to create (without path, with extension)")
        .build());
    return options;
  }

  public void initializeClass2MC() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new Class2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new Class2MCResolver());
  }

  public IMontiArcArtifactScope createSymbolTable(CommandLine cmd){
    IMontiArcArtifactScope scope = MontiArcMill.artifactScope();
    scope.setName(cmd.getOptionValue("f").split("\\.")[0]);
    MontiArcMill.globalScope().addSubScope(scope);
    new RuntimeSymbolsFinder().createSymbols().forEach(scope::add);
    return scope;
  }
}