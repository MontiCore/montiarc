/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util;

import arcbasis._ast.*;
import arcbasis._symboltable.Port2VariableAdapter;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._ast.ASTStatechart;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import java.util.*;
import java.util.stream.Collectors;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import scmapping.util.MappingUtil;

public class AutomataUtils {

  public static String getName(ASTComponentType ast) {
    return ast.getName();
  }

  public static PortSymbol getPortSymbol(Port2VariableAdapter symbol, ASTComponentType comp) {
    Optional<PortSymbol> portSymbol = comp.getEnclosingScope().resolvePort(symbol.getFullName());
    assert portSymbol.isPresent();
    return portSymbol.get();
  }

  public static List<SCStateSymbol> getStateList(ASTComponentType ast) {
    ASTStatechart sc = getAutomaton(ast);

    return sc.getSCStatechartElementList().stream()
        .filter(elem -> elem instanceof ASTSCState)
        .map(elem -> (ASTSCState) elem)
        .map(ASTSCState::getSymbol)
        .collect(Collectors.toList());
  }


  public static List<ASTSCTransition> getTransitions(ASTComponentType ast) {
    ASTStatechart sc = getAutomaton(ast);

    return sc.getSCStatechartElementList().stream()
        .filter(elem -> elem instanceof ASTSCTransition)
        .map(elem -> (ASTSCTransition) elem)
        .collect(Collectors.toList());
  }

  public static List<PortSymbol> getInPorts(ASTComponentType ast) {
    return ast.getPorts().stream()
        .map(ASTArcPort::getSymbol)
        .filter(PortSymbol::isIncoming)
        .collect(Collectors.toList());
  }

  public static List<PortSymbol> getOutPorts(ASTComponentType ast) {
    return ast.getPorts().stream()
        .map(ASTArcPort::getSymbol)
        .filter(PortSymbol::isOutgoing)
        .collect(Collectors.toList());
  }

  public static ASTStatechart getAutomaton(ASTComponentType ma) {
    return (ASTStatechart)
        ma.getBody().getArcElementList().stream()
            .filter(x -> x instanceof ASTStatechart)
            .findFirst()
            .orElse(null);
  }

  public static List<VariableSymbol> getGlobalVariables(ASTComponentType comp) {
    return comp.getFields().stream().map(ASTArcField::getSymbol).collect(Collectors.toList());
  }

  public static List<ASTAssignmentExpression> getActionList(ASTSCTransition trans) {

    List<ASTAssignmentExpression> res = new ArrayList<>();
    AssignmentExpressionsVisitor2 assigmentCollector =
        new AssignmentExpressionsVisitor2() {
          @Override
          public void visit(ASTAssignmentExpression node) {
            res.add(node);
          }
        };

    MontiArcTraverser traverser = MontiArcMill.traverser();
    traverser.add4AssignmentExpressions(assigmentCollector);
    trans.accept(traverser);

    return res;
  }

  public static String print(ASTNode node) {
      return MappingUtil.print(node);
  }
}
