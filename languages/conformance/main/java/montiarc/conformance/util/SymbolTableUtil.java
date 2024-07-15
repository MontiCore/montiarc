/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.Port2VariableAdapter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;

import java.util.Optional;

public class SymbolTableUtil {
  public static Optional<FieldSymbol> resolveEnumConst(String name, ASTCDCompilationUnit cd) {
    return cd.getEnclosingScope().resolveField(name);
  }

  public static Optional<ASTCDEnum> resolveEnum(TypeSymbol symbol, ASTCDCompilationUnit cd) {
    Optional<CDTypeSymbol> cdTypeSymbol =
        cd.getEnclosingScope().resolveCDType(symbol.getFullName());
    if (cdTypeSymbol.isPresent() && cdTypeSymbol.get().isIsEnum()) {
      return Optional.ofNullable((ASTCDEnum) cdTypeSymbol.get().getAstNode());
    }
    return Optional.empty();
  }

  public static boolean isInPortVar(ISymbol symbol, ASTComponentType comp) {
    if (symbol instanceof Port2VariableAdapter) {
      Optional<PortSymbol> sym = comp.getEnclosingScope().resolvePort(symbol.getFullName());
      return sym.isPresent() && sym.get().isIncoming();
    }
    return false;
  }

  public static boolean isInPort(ISymbol symbol) {
    return (symbol instanceof PortSymbol && ((PortSymbol) symbol).isIncoming());
  }

  public static boolean isOutPort(ISymbol symbol) {
    return (symbol instanceof PortSymbol && ((PortSymbol) symbol).isOutgoing());
  }

  public static boolean isOutPortVar(ISymbol symbol, ASTComponentType comp) {
    if (symbol instanceof Port2VariableAdapter) {
      Optional<PortSymbol> sym = comp.getEnclosingScope().resolvePort(symbol.getFullName());
      return sym.isPresent() && sym.get().isOutgoing();
    }
    return false;
  }

  public static boolean isGlobalVar(ISymbol symbol) {
    return symbol instanceof VariableSymbol && !(symbol instanceof Port2VariableAdapter);
  }

  public static Optional<PortSymbol> resolvePort(String portName, ASTComponentType comp) {

    String symName = comp.getSymbol().getFullName() + "." + portName;

    return comp.getEnclosingScope().resolvePort(symName);
  }

  public static Optional<VariableSymbol> resolveGlobalVar(String varName, ASTComponentType comp) {

    String symName = comp.getSymbol().getFullName() + "." + varName;

    return comp.getEnclosingScope().resolveVariable(symName);
  }

  public static Optional<SCStateSymbol> resolveState(String stateName, ASTComponentType comp) {
    String scName = AutomataUtils.getAutomaton(comp).getSCName().orElse("");
    String symName = comp.getSymbol().getFullName() + "." + scName + stateName;

    return ((IMontiArcScope) comp.getEnclosingScope()).resolveSCState(symName);
  }

  public static ISymbol resolveSymbol(String symbolName, ASTComponentType comp) {
    Optional<PortSymbol> result = resolvePort(symbolName, comp);
    if (result.isPresent()) {
      return result.get();
    }
    Optional<SCStateSymbol> res = resolveState(symbolName, comp);
    if (res.isPresent()) {
      return res.get();
    }

    Optional<VariableSymbol> var = resolveGlobalVar(symbolName, comp);
    if (var.isPresent()) {
      return var.get();
    }

    Log.error("Unable to resolve symbol " + symbolName);
    Log.error("Transformation of Automaton to SMT must be stop ");
    assert false;
    return null;
  }
}
