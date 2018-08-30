package de.montiarcautomaton.generator.codegen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import de.montiarcautomaton.generator.codegen.xtend.ComponentGenerator;
import de.montiarcautomaton.generator.helper.AutomatonHelper;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.expressions.prettyprint.MCExpressionsPrettyPrinter;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.IterablePath;
import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._ast.ASTValueInitialization;
import montiarc._symboltable.ComponentSymbol;

/**
 * This class generates code for an maa model.
 * 
 * @author Andreas Wortmann, Jerome Pfeiffer, Gerrit Leonhardt
 */
public class MAAGenerator {
  
  /**
   * Computes the target path of the generated java file.
   * 
   * @param targetPath the path of the target folder
   * @param packageName the package name of the model
   * @param name the model name
   * @return
   */
  private static Path getPath(String targetPath, String packageName, String name) {
    return Paths.get(targetPath, Names.getPathFromPackage(packageName), "");
  }
  
  /**
   * Generates maa code for the given model.
   * 
   * @param simpleName the simple model name e.g. BumperControl
   * @param packageName the package name e.g. bumperbot
   * @param modelPath Path of models e.g. src/main/resources/models
   * @param fqnModelName full qualified name of model e.g.
   * /bumperbot/BumpControl.maa
   * @param targetPath Path where the models should be generated to e.g.
   * target/generated-source/
   * @param hwcPath
   */
  public static void generateModel(File targetPath, File hwcPath, ComponentSymbol compSym) {
    ComponentGenerator gen = new ComponentGenerator();
    gen.generateAll(getPath(targetPath.getAbsolutePath(), compSym.getPackageName(), "").toFile(), compSym);
    
    // Generator setup
    
//    
//    
//    GeneratorSetup setup = new GeneratorSetup();
//    GeneratorEngine generator = new GeneratorEngine(setup);
//    
//    ComponentSymbol comp = compSym;
//    
//    String packageName = comp.getPackageName();
//    String targetPathName = targetPath.getAbsolutePath();
//    
//    final ComponentHelper compHelper = new ComponentHelper(comp);
//    Path filePath;
//    
//    // gen component input
//    String inputName = comp.getName() + "Input";
//    filePath = getPath(targetPathName, packageName, inputName);
//    // pass all arguments instead of comp for better readability in the template
//    generator.generate("de/montiarcautomaton/lib/ComponentInput.ftl", filePath,
//        comp.getAstNode().get(), compHelper, comp.getPackageName(),
//        comp.getImports(),
//        comp.getName(), inputName, comp.getIncomingPorts());
//    
//    // gen component result
//    String resultName = comp.getName() + "Result";
//    filePath = getPath(targetPathName, packageName, resultName);
//    // pass all arguments instead of comp for better readability in the template
//    generator.generate("de/montiarcautomaton/lib/ComponentResult.ftl", filePath,
//        comp.getAstNode().get(), compHelper, comp.getPackageName(),
//        comp.getImports(),
//        comp.getName(), resultName, comp.getOutgoingPorts());
//    
//    // gen behavior implementations
//    boolean existsHWC = false;
//    String implName = comp.getName() + "Impl";
//    
//    existsHWC = TransformationHelper.existsHandwrittenClass(IterablePath.from(hwcPath, "java"),
//        packageName + "." + implName);
//    
//    filePath = getPath(targetPathName, packageName, implName);
//    
//    /* ${tc.
//     * params("de.montiarcautomaton.generator.helper.ComponentHelper helper",
//     * "String _package",
//     * "java.util.Collection<de.monticore.symboltable.ImportStatement> imports",
//     * "String name", "String resultName", "String inputName",
//     * "String implName",
//     * "java.util.Collection<montiarc._symboltable.PortSymbol> portsIn",
//     * "java.util.Collection<montiarc._symboltable.PortSymbol> portsOut",
//     * "java.util.Collection<de.monticore.symboltable.types.JFieldSymbol> configParams"
//     * ,
//     * "java.util.Collection<montiarc._symboltable.VariableSymbol> compVariables"
//     * , "String ajava",
//     * "java.util.List<montiarc._ast.ASTValueInitialization> initializations")} */
//    
//    ASTComponent compAST = (ASTComponent) comp.getAstNode().get();
//    Optional<ASTBehaviorElement> behaviorEmbedding = getBehaviorEmbedding(compAST);
//    if (behaviorEmbedding.isPresent()) {
//      if (behaviorEmbedding.get() instanceof ASTJavaPBehavior) {
//        
//        Optional<ASTJavaPInitializer> init = ComponentHelper.getComponentInitialization(comp);
//        List<ASTValueInitialization> varInits = new ArrayList<>();
//        
//        if (init.isPresent()) {
//          varInits = init.get().getValueInitializationList();
//        }
//        JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());
//        StringBuilder sb = new StringBuilder();
//        for (ASTBlockStatement s : ((ASTJavaPBehavior) behaviorEmbedding.get())
//            .getBlockStatementList()) {
//          sb.append(prettyPrinter.prettyprint(s));
//        }
//        
//        generator.generate("de/montiarcautomaton/lib/ajava/AJavaMain.ftl", filePath, compAST,
//            compHelper,
//            comp.getPackageName(), comp.getImports(), comp.getName(), resultName,
//            inputName, implName, comp.getIncomingPorts(), comp.getOutgoingPorts(),
//            comp.getConfigParameters(), comp.getVariables(), sb.toString(), varInits);
//      }
//      
//      if (behaviorEmbedding.get() instanceof ASTAutomatonBehavior) {
//        AutomatonHelper automatonHelper = new AutomatonHelper(comp);
//        generator.generate("de/montiarcautomaton/lib/automaton/AutomatonImplMain.ftl", filePath,
//            compAST, automatonHelper, comp.getPackageName(), comp.getImports(), comp.getName(),
//            resultName, inputName, implName, comp.getIncomingPorts(), compHelper,
//            comp.getVariables(), automatonHelper.getStates(), comp.getConfigParameters());
//      }
//    }
//    
//    // gen component
//    filePath = getPath(targetPathName, packageName, comp.getName());
//    if (comp.isAtomic()) {
//      Path implPath = getPath(targetPathName, packageName, implName);
//
//      // default implementation
//      if (!existsHWC && !behaviorEmbedding.isPresent()) {
//        generator.generate("de/montiarcautomaton/lib/AbstractAtomicComponent.ftl", implPath, compAST,
//            compHelper, packageName, implName,
//            inputName, resultName, comp.getConfigParameters(), comp.getImports());
//      }
//      
//      // pass all arguments instead of comp for better readability in the
//      // template
//      generator.generate("de/montiarcautomaton/lib/AtomicComponent.ftl", filePath,
//          comp.getAstNode().get(),
//          compHelper,
//          comp.getPackageName(),
//          comp.getImports(),
//          comp.getName(),
//          resultName,
//          inputName,
//          implName,
//          comp.getVariables(),
//          comp.getIncomingPorts(),
//          comp.getOutgoingPorts(),
//          comp.getAllOutgoingPorts(),
//          comp.getConfigParameters());
//    }
//    else {
//      // pass all arguments instead of comp for better readability in the
//      // template
//      generator.generate("de/montiarcautomaton/lib/ComposedComponent.ftl", filePath,
//          comp.getAstNode().get(),
//          compHelper,
//          comp,
//          comp.getPackageName(),
//          comp.getImports(),
//          comp.getName(),
//          comp.getIncomingPorts(),
//          comp.getOutgoingPorts(),
//          comp.getSubComponents(),
//          comp.getConnectors(),
//          comp.getConfigParameters());
//    }
//    
//    // gen deploy
//    if (compHelper.isDeploy()) {
//      String deployName = "Deploy" + comp.getName();
//      filePath = getPath(targetPathName, packageName, deployName);
//      generator.generate("de/montiarcautomaton/lib/Deploy.ftl", filePath, comp.getAstNode().get(),
//          compHelper, comp.getPackageName(),
//          comp.getName(), deployName);
//    }
  }
  
  private static Optional<ASTBehaviorElement> getBehaviorEmbedding(ASTComponent cmp) {
    List<ASTElement> elements = cmp.getBody().getElementList();
    for (ASTElement e : elements) {
      if (e instanceof ASTBehaviorElement) {
        return Optional.of((ASTBehaviorElement) e);
      }
    }
    return Optional.empty();
  }
  
}
