/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.TransitiveNameExpressionScopeSetter;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.compsymbols._ast.ASTSubcomponentArgument;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * (De-)serializes the arguments of a component type expression, e.g., of a
 * super component or of the type of a subcomponent, which the serialization
 * of component type expressions omits. The arguments are bound to the
 * parameters when they are first needed, as the component type may not be
 * loaded yet.
 */
public class ComponentArgumentsDeSer {

  public static final String NAME = "name";
  public static final String EXPRESSION = "expression";

  protected final Function<String, Optional<ASTExpression>> parseExpression;

  public ComponentArgumentsDeSer(@NotNull Function<String, Optional<ASTExpression>> parseExpression) {
    this.parseExpression = Preconditions.checkNotNull(parseExpression);
  }

  /**
   * Prints the arguments of the component type expression as the elements of
   * the JSON array the printer is currently in.
   */
  public void serialize(@NotNull JsonPrinter printer, @NotNull CompKindExpression kind) {
    Preconditions.checkNotNull(printer);
    Preconditions.checkNotNull(kind);
    for (ASTSubcomponentArgument argument : kind.getArguments()) {
      printer.beginObject();
      if (argument.isPresentName()) {
        printer.member(NAME, argument.getName());
      }
      printer.member(EXPRESSION, MontiArcMill.prettyPrint(argument.getExpression(), false).trim());
      printer.endObject();
    }
  }

  /**
   * Adds the arguments to the component type expression, with names resolved
   * in the given scope.
   */
  public void deserialize(@NotNull List<JsonElement> arguments,
                          @NotNull CompKindExpression kind,
                          @NotNull IArcBasisScope scope) {
    Preconditions.checkNotNull(arguments);
    Preconditions.checkNotNull(kind);
    Preconditions.checkNotNull(scope);
    for (JsonElement element : arguments) {
      JsonObject json = element.getAsJsonObject();
      String value = json.getStringMember(EXPRESSION);
      Optional<ASTExpression> expression = this.parseExpression.apply(value);
      if (expression.isEmpty()) {
        Log.error(String.format("Could not deserialize the argument '%s' of component type '%s'",
          value, kind.printFullName()));
        continue;
      }
      new TransitiveNameExpressionScopeSetter(scope).set(expression.get());
      ASTArcArgument argument = MontiArcMill.arcArgumentBuilder().setExpression(expression.get()).build();
      json.getStringMemberOpt(NAME).ifPresent(argument::setName);
      kind.addArgument(argument);
    }
  }
}
