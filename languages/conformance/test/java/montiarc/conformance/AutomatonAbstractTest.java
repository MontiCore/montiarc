/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance;

import arcbasis._ast.ASTComponentType;
import com.microsoft.z3.Context;
import montiarc.conformance.automaton2smt.smtAutomaton.SMTAutomaton;
import montiarc.conformance.util.AutomataLoader;
import montiarc.conformance.util.AutomataUtils;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;

public class AutomatonAbstractTest {

  protected ASTComponentType refAut;
  protected ASTComponentType conAut;

  protected ASTCDCompilationUnit refCD;

  protected ASTCDCompilationUnit conCD;

  protected SMTAutomaton con;
  protected SMTAutomaton ref;

  public static Context buildContext() {
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    return new Context(cfg);
  }

  public void initMills() {

    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();

    MontiArcMill.reset();
    MontiArcMill.init();
    MontiArcMill.globalScope().clear();
  }

  protected void loadModels(File refAutFile, File refCdFile, File conAutFile, File conCDFile) {

    Pair<ASTCDCompilationUnit, ASTMACompilationUnit> ref =
        AutomataLoader.loadModels(refAutFile, refCdFile);
    refAut = ref.getValue().getComponentType();
    refCD = ref.getKey();

    initMills();

    Pair<ASTCDCompilationUnit, ASTMACompilationUnit> con =
        AutomataLoader.loadModels(conAutFile, conCDFile);

    conAut = con.getValue().getComponentType();
    conCD = con.getKey();
  }

  protected ASTSCTransition getTransition(String number, ASTComponentType ma) {
    for (ASTSCTransition trans : AutomataUtils.getTransitions(ma)) {
      if (trans.getStereotype().getValue("n").equals(number)) {
        return trans;
      }
    }
    return null;
  }

  public SCStateSymbol getState(String stateName, ASTComponentType ma) {
    Optional<SCStateSymbol> state =
        AutomataUtils.getStateList(ma).stream()
            .filter(st -> st.getName().equals(stateName))
            .findFirst();
    Assertions.assertTrue(state.isPresent());
    return state.get();
  }

  public PortSymbol getInputPort(String portName, ASTComponentType ma) {
    Optional<PortSymbol> res =
        AutomataUtils.getInPorts(ma).stream().filter(p -> p.getName().equals(portName)).findFirst();
    Assertions.assertTrue(res.isPresent());
    return res.get();
  }

  public VariableSymbol getGlobalVariable(String varName, ASTComponentType aut) {
    Optional<VariableSymbol> var =
        AutomataUtils.getGlobalVariables(aut).stream()
            .filter(v -> v.getName().equals(varName))
            .findAny();
    Assertions.assertTrue(var.isPresent());
    return var.get();
  }

  public PortSymbol getOutputPort(String portName, ASTComponentType ma) {
    Optional<PortSymbol> res =
        AutomataUtils.getOutPorts(ma).stream()
            .filter(p -> p.getName().equals(portName))
            .findFirst();
    Assertions.assertTrue(res.isPresent());
    return res.get();
  }

  public FieldSymbol getEnumConst(String enumConstName, String enumName, ASTCDCompilationUnit cd) {
    String fullName =
        "conformance.automaton2smt.concrete." + enumName + "." + enumConstName;
    Optional<FieldSymbol> f = cd.getEnclosingScope().resolveField(fullName);
    Assertions.assertTrue(f.isPresent());
    return f.get();
  }
}
