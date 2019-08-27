/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._cocos.DynamicMontiArcCoCoChecker;
import dynamicmontiarc._visitor.DynamicMontiArcVisitor;
import montiarc._ast.ASTComponent;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.CircularInheritance;

import java.util.List;

public class IdentifiersAreUnique extends montiarc.cocos.IdentifiersAreUnique {

  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }

    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();

    // In case the model is faulty and there are inheritance cycles, we have to
    // check for those before actually trying to check the uniqueness of the
    // connector names
    // Findings up to this point are saved to not alter the results
    final List<Finding> findings = Lists.newArrayList(Log.getFindings());
    Log.getFindings().clear();
    CircularInheritance cycleCoCo = new CircularInheritance();
    DynamicMontiArcCoCoChecker checker = new DynamicMontiArcCoCoChecker();
    checker.addCoCo(cycleCoCo);
    checker.checkAll(node);
    final boolean inheritanceCycle
        = Log.getFindings().stream()
              .map(Finding::getMsg)
              .anyMatch(m -> m.contains("xMA017"));
    if(inheritanceCycle){
      Log.warn("Could not check for uniqueness of names in inherited " +
                   "ports due to an inheritance cycle.");
      // Restore the saved findings
      Log.getFindings().clear();
      Log.getFindings().addAll(findings);
      return;
    }
    // Restore the saved findings
    Log.getFindings().clear();
    Log.getFindings().addAll(findings);

    doCheck(node, comp);
  }
}
