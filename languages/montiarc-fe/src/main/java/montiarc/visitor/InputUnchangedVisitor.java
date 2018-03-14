package montiarc.visitor;

import java.util.ArrayList;
import java.util.List;

import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTPrefixExpression;
import de.monticore.mcexpressions._ast.ASTSuffixExpression;
import montiarc._visitor.MontiArcVisitor;

/**
 * Visitor that is used to check whether input ports are changed in a compute block.
 *
 * @author Michael Mutert
 */
public class InputUnchangedVisitor implements MontiArcVisitor {
  
  private List<String> possiblePorts = new ArrayList<>();
  
  @Override
  public void visit(ASTExpression node) {
//  TODO@AB  
//    // Check if the current expression is a 'regular' assignment,
//    // i.e. =, +=, -=
//    if (node.getLeftExpression().isPresent() && node.getRightExpression().isPresent()) {
//      if (node.getAssignment().isPresent()) {
//        // It actually is a 'regular' assignment
//        // We save the name of the lefthand side if it is a primary expression
//        // Otherwise it can not be a port
//        ASTPrimaryExpression primaryExpression;
//        if (node.getLeftExpression().get().getPrimaryExpression().isPresent()) {
//          primaryExpression = node.getLeftExpression().get().getPrimaryExpression().get();
//          if (primaryExpression.getName().isPresent()) {
//            possiblePorts.add(primaryExpression.getName().get());
//          }
//        }
//      }
//    }
//    
//    // Check if the current expressiosn is a prefix/suffix operator,
//    // i.e. ++, --
//    if (node instanceof ASTSuffixExpression) {
//      if (node.getExpression().get().getPrimaryExpression().isPresent()) {
//        ASTPrimaryExpression primaryExpression = node.getExpression().get().getPrimaryExpression()
//            .get();
//        if (primaryExpression.getName().isPresent()) {
//          possiblePorts.add(primaryExpression.getName().get());
//        }
//      }
//    }
//    if (node instanceof ASTPrefixExpression) {
//      if (node.getExpression().get().getPrimaryExpression().isPresent()) {
//        ASTPrimaryExpression primaryExpression = node.getExpression().get().getPrimaryExpression()
//            .get();
//        if (primaryExpression.getName().isPresent()) {
//          possiblePorts.add(primaryExpression.getName().get());
//        }
//      }
//    }
  }
  
  public List<String> getPossiblePorts() {
    return possiblePorts;
  }
}
