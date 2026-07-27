/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdgen.CDGenTool;
import de.monticore.cdgen.cocos.CD2JavaGenCoCos;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.Names;

import java.util.Arrays;
import java.util.List;

public class CD2PojoTool extends CDGenTool {

  public static void main(String[] args) {
    CD2PojoTool tool = new CD2PojoTool();
    String[] augmentedArgs = augmentWithNewDefaultConfigTemplate(args);
    tool.run(augmentedArgs);
  }

  @Override
  public void storeSymTab(ICD4CodeArtifactScope scope, String path) {
    for (ICD4AnalysisScope subscope : scope.getSubScopes()) {
      // store each type symbol in its own symbol-table
      ISymbol symbol = subscope.getSpanningSymbol();
      if (symbol instanceof CDTypeSymbol) {
        ICD4CodeArtifactScope as2store = CD4CodeMill.artifactScope();
        as2store.setPackageName(Names.getQualifiedName(scope.getPackageName(), scope.getName()));
        as2store.setName(subscope.getName());
        as2store.add((CDTypeSymbol) subscope.getSpanningSymbol());
        super.storeSymTab(as2store, path);
      }
    }
  }

  /**
   * Sets the CD2Pojo specific configuration template as the CD2Code
   * configuration template to use if the user did not specify a configuration
   * template in {@code userArgs}.
   * <p>
   * Setting the CD2Pojo specific configuration template is achieved by
   * appending it over the '-ct' CLI option to a copy of {@code userArgs} and
   * returning it.
   * <p>
   * If the user already specifies a configuration template with a CLI argument,
   * the returned arguments are the input. The CLI arguments that can be used to
   * achieve this are {@code -ct} or {@code --configtemplate}.
   * <p>
   * If a configuration template is already set via the CLI, it should be
   * considered to execute the {@code cd2pojo.init.CD2Java.ftl} because
   * else wise compatibility with MontiArc can not be guaranteed.
   *
   * @param userArgs command line arguments from the user
   */
  protected static String[] augmentWithNewDefaultConfigTemplate(String[] userArgs) {
    List<String> argsAsList = Arrays.asList(userArgs);

    if (!argsAsList.contains("--configtemplate") && !argsAsList.contains("-ct")) {

      String[] augmentedList = Arrays.copyOf(userArgs, userArgs.length + 2);
      augmentedList[userArgs.length] = "-ct";
      augmentedList[userArgs.length + 1] = "cd2pojo.init.CD2Java";

      return augmentedList;

    } else {
      return userArgs;
    }
  }

  @Override
  public void runCoCos(ASTCDCompilationUnit ast) {
    CD4CodeCoCoChecker checker = new CD2JavaGenCoCos().getCheckerForAllCoCos();
    checker.checkAll(ast);
  }
}
