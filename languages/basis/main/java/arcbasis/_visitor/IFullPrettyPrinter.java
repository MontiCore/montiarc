/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * An interface that allows polymorphism for FullPrettyPrinters
 */
public interface IFullPrettyPrinter {

  String prettyprint(@NotNull ASTExpression a);

}
