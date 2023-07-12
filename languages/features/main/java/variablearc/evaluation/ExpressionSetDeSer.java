/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import arcbasis._symboltable.TransitiveNameExpressionScopeSetter;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.expressions.Expression;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExpressionSetDeSer {

  protected final Function<String, Optional<ASTExpression>> parseExpression;

  public static final String SERIALIZED_KIND = "variablearc.evaluation.ExpressionSet";
  public static final String PREFIX = "prefix";
  public static final String EXPRESSIONS = "expressions";
  public static final String EXPRESSION = "expression";
  public static final String NEGATED_CONJUNCTION = "negatedConjunction";

  public ExpressionSetDeSer(@NotNull Function<String, Optional<ASTExpression>> parseExpression) {
    Preconditions.checkNotNull(parseExpression);
    this.parseExpression = parseExpression;
  }

  /**
   * @param toSerialize the expression set that is serialized
   * @return the expression set encoded as a json string.
   */
  public String serialize(@NotNull ExpressionSet toSerialize) {
    Preconditions.checkNotNull(toSerialize);
    JsonPrinter printer = new JsonPrinter();

    printer.beginObject();
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    printer.beginArray(EXPRESSIONS);
    toSerialize.getExpressions().forEach(e -> {
      printer.beginObject();
      printer.member(PREFIX, e.getPrefix());
      printer.member(EXPRESSION, e.print());
      printer.endObject();
    });
    printer.endArray();
    printer.beginArray(NEGATED_CONJUNCTION);
    toSerialize.getNegatedConjunctions().forEach(l -> {
      printer.beginArray();
      l.forEach(e -> {
        printer.beginObject();
        printer.member(PREFIX, e.getPrefix());
        printer.member(EXPRESSION, e.print());
        printer.endObject();
      });
      printer.endArray();
    });
    printer.endArray();

    printer.endObject();

    return printer.getContent();
  }

  /**
   * @param json  The expression set, encoded as json.
   * @param scope The enclosing scope of the expression set.
   * @return The deserialized expression set.
   */
  public ExpressionSet deserialize(@NotNull JsonObject json, @NotNull IArcBasisScope scope) {
    Preconditions.checkNotNull(json);
    Preconditions.checkNotNull(scope);
    List<JsonElement> expressionsJson = json.getArrayMemberOpt(EXPRESSIONS).orElseGet(Collections::emptyList);
    List<Expression> expressions = deserializeExpressions(expressionsJson, scope);

    List<JsonElement> negatedExpressionsJson = json.getArrayMemberOpt(NEGATED_CONJUNCTION).orElseGet(Collections::emptyList);
    List<List<Expression>> negatedExpressions = negatedExpressionsJson.stream().map(e -> deserializeExpressions(e.getAsJsonArray().getValues(), scope)).collect(Collectors.toList());

    return new ExpressionSet(expressions, negatedExpressions);
  }

  /**
   * @param jsonElements A list of expressions, encoded as json.
   * @param scope        The enclosing scope of the expressions.
   * @return The deserialized list of expressions.
   */
  protected List<Expression> deserializeExpressions(@NotNull List<JsonElement> jsonElements, @NotNull IArcBasisScope scope) {
    Preconditions.checkNotNull(jsonElements);
    Preconditions.checkNotNull(scope);
    return jsonElements.stream().map(e -> deserializeExpression(e.getAsJsonObject(), scope)).collect(Collectors.toList());
  }

  /**
   * @param json  An expressions, encoded as json.
   * @param scope The enclosing scope of the expression.
   * @return The deserialized expression.
   */
  protected Expression deserializeExpression(@NotNull JsonObject json, @NotNull IArcBasisScope scope) {
    Preconditions.checkNotNull(json);
    Preconditions.checkNotNull(scope);
    Optional<String> prefix = json.getStringMemberOpt(PREFIX);
    Optional<ASTExpression> expression = parseExpression.apply(json.getStringMember(EXPRESSION));

    TransitiveNameExpressionScopeSetter scopeSetter = new TransitiveNameExpressionScopeSetter(scope);
    expression.ifPresent(scopeSetter::set);

    return prefix.map(s -> new Expression(expression.orElse(null), s)).orElseGet(() -> new Expression(expression.orElse(null)));
  }

  public String getSerializedKind() {
    return SERIALIZED_KIND;
  }
}
