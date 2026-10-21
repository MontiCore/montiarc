/* (c) https://github.com/MontiCore/monticore */
package variablearc.presence;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeSort;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Symbol;
import com.microsoft.z3.Z3Exception;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.smt.FormulaSolver;
import variablearc.smt.VariableArcDeriveSMTSort;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * (De-)serializes the {@link PresenceFormulas} of a component type as SMT-LIB
 * terms. The symbols the component type declares that are not always present,
 * e.g., ports, subcomponents, fields, features, and inner component types,
 * are serialized with their presence, before and instead of the spanned
 * scope; as the spanned scope is serialized after them and skips the symbols
 * that are already serialized, they are serialized once.
 * Loading parses the terms of a component type once into the context of the
 * presence formulas of the global scope, unless it provides none. The
 * enumerations of the terms are serialized by their constants and shared with
 * the conversion of expressions in the context, see
 * {@link VariableArcDeriveSMTSort#enumSort}.
 */
public class PresenceFormulasDeSer {

  protected final WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();

  /**
   * The version of the format, which is incremented on incompatible changes
   */
  public static final int VERSION = 1;

  public static final String PRESENCE = "presence";
  public static final String FORMAT_VERSION = "version";
  public static final String ENUMERATIONS = "enumerations";
  public static final String DECLARATIONS = "declarations";
  public static final String CONSTRAINTS = "constraints";
  public static final String DELAYED = "delayed";
  public static final String DEFAULTS = "defaults";
  public static final String ARGUMENTS = "arguments";
  public static final String SUPER_ARGUMENTS = "superArguments";
  public static final String CONDITIONAL_SYMBOLS = "conditionalSymbols";
  public static final String SYMBOLS = "symbols";

  public void serialize(@NotNull ComponentTypeSymbol component, @NotNull CompSymbolsSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);
    if (!(component instanceof IPresenceFormulasOwner owner) || owner.getPresenceFormulas().isEmpty()) return;
    PresenceFormulas formulas = owner.getPresenceFormulas().get();
    Map<String, List<ISymbol>> conditional = this.conditionalSymbolsOf(component, formulas);
    List<Expr<?>> terms = new ArrayList<>();
    terms.add(formulas.getConstraints());
    terms.add(formulas.getDelayed());
    terms.addAll(formulas.getDefaults().values());
    formulas.getArguments().values().forEach(arguments -> terms.addAll(arguments.values()));
    formulas.getSuperArguments().forEach(arguments -> terms.addAll(arguments.values()));
    conditional.values().forEach(symbols -> terms.add(formulas.of(symbols.get(0)).orElseThrow()));

    JsonPrinter printer = s2j.getJsonPrinter();
    printer.beginObject(PRESENCE);
    printer.member(FORMAT_VERSION, VERSION);
    printer.beginObject(ENUMERATIONS);
    this.enumerationsOf(terms).forEach(enumeration -> {
      String name = enumeration.getName().toString();
      printer.beginArray(name);
      Arrays.stream(enumeration.getConstructors())
        .map(constant -> constant.getName().toString().substring(name.length() + 1))
        .forEach(printer::value);
      printer.endArray();
    });
    printer.endObject();
    printer.member(DECLARATIONS, this.declarationsOf(terms));
    printer.member(CONSTRAINTS, formulas.getConstraints().toString());
    printer.member(DELAYED, formulas.getDelayed().toString());
    this.printValues(printer, DEFAULTS, formulas.getDefaults());
    printer.beginObject(ARGUMENTS);
    formulas.getArguments().forEach((subcomponent, arguments) -> {
      if (formulas.of(subcomponent).isEmpty()) {
        this.printValues(printer, subcomponent.getName(), arguments);
      }
    });
    printer.endObject();
    printer.beginObject(SUPER_ARGUMENTS);
    for (int i = 0; i < formulas.getSuperArguments().size(); i++) {
      this.printValues(printer, String.valueOf(i), formulas.getSuperArguments().get(i));
    }
    printer.endObject();
    printer.endObject();

    if (conditional.isEmpty()) return;
    printer.beginArray(CONDITIONAL_SYMBOLS);
    conditional.forEach((presence, symbols) -> {
      printer.beginObject();
      printer.member(PRESENCE, presence);
      printer.beginArray(SYMBOLS);
      symbols.forEach(symbol -> symbol.accept(s2j.getTraverser()));
      printer.endArray();
      printer.beginObject(ARGUMENTS);
      symbols.stream()
        .filter(SubcomponentSymbol.class::isInstance)
        .map(SubcomponentSymbol.class::cast)
        .forEach(subcomponent -> this.printValues(printer, subcomponent.getName(),
          formulas.getArguments().getOrDefault(subcomponent, Map.of())));
      printer.endObject();
      printer.endObject();
    });
    printer.endArray();
  }

  /**
   * @return the symbols the component type declares that are not always
   * present by their presence
   */
  protected Map<String, List<ISymbol>> conditionalSymbolsOf(@NotNull ComponentTypeSymbol component,
                                                           @NotNull PresenceFormulas formulas) {
    Map<String, List<ISymbol>> conditional = new LinkedHashMap<>();
    this.withinComponentResolver.getLocalSymbols(component, ISymbol.class)
      .forEach(symbol -> formulas.of(symbol).ifPresent(presence ->
        conditional.computeIfAbsent(presence.toString(), p -> new ArrayList<>()).add(symbol)));
    return conditional;
  }

  protected void printValues(@NotNull JsonPrinter printer, @NotNull String name, @NotNull Map<String, Expr<?>> values) {
    printer.beginObject(name);
    values.forEach((parameter, value) -> printer.member(parameter, value.toString()));
    printer.endObject();
  }

  /**
   * @return the SMT-LIB declarations of the constants of the terms
   */
  protected String declarationsOf(@NotNull List<Expr<?>> terms) {
    Set<Expr<?>> constants = new LinkedHashSet<>();
    FormulaSolver formulas = new FormulaSolver();
    terms.forEach(term -> constants.addAll(formulas.constantsOf(term)));
    Set<String> declarations = new LinkedHashSet<>();
    for (Expr<?> constant : constants) {
      declarations.add("(declare-const " + constant + " " + constant.getSort() + ")");
    }
    return String.join("\n", declarations);
  }

  /**
   * @return the enumeration sorts of the subterms of the terms, including
   * those of constants of enumerations without variables of their sort; Z3
   * provides the sorts of terms as datatype sorts
   */
  protected Set<DatatypeSort<?>> enumerationsOf(@NotNull List<Expr<?>> terms) {
    Set<DatatypeSort<?>> enumerations = new LinkedHashSet<>();
    Set<Expr<?>> visited = new HashSet<>();
    Deque<Expr<?>> pending = new ArrayDeque<>(terms);
    while (!pending.isEmpty()) {
      Expr<?> term = pending.pop();
      if (!visited.add(term)) continue;
      if (term.getSort() instanceof DatatypeSort<?> enumeration) {
        enumerations.add(enumeration);
      }
      if (term.isApp()) {
        pending.addAll(Arrays.asList(term.getArgs()));
      }
    }
    return enumerations;
  }

  /**
   * Adds the conditional symbols of the component type to its spanned scope
   * and parses its presence formulas into the context.
   */
  public void deserialize(@NotNull IVariableArcComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);
    List<List<ISymbol>> conditional = this.deserializeConditionalSymbols(component, json);
    Optional<Context> context = VariableArcMill.globalScope().getPresenceContext();
    if (context.isEmpty() || !json.hasObjectMember(PRESENCE)
      || !(component instanceof IPresenceFormulasOwner owner)) return;
    JsonObject presence = json.getObjectMember(PRESENCE);
    List<JsonElement> conditionalJson = json.getArrayMemberOpt(CONDITIONAL_SYMBOLS).orElseGet(Collections::emptyList);
    int version = presence.getIntegerMemberOpt(FORMAT_VERSION).orElse(0);
    if (version != VERSION) {
      Log.error(VariableArcError.PRESENCE_NOT_DESERIALIZABLE.format(component.getTypeInfo().getFullName(),
        String.format("the symbol file has format version %d instead of %d and needs to be regenerated", version, VERSION)));
      return;
    }

    List<String> terms = new ArrayList<>();
    terms.add(presence.getStringMember(CONSTRAINTS));
    terms.add(presence.getStringMember(DELAYED));
    this.collectValues(presence.getObjectMemberOpt(DEFAULTS), terms);
    presence.getObjectMemberOpt(ARGUMENTS).ifPresent(arguments -> arguments.getMembers().values()
      .forEach(values -> this.collectValues(Optional.of(values.getAsJsonObject()), terms)));
    presence.getObjectMemberOpt(SUPER_ARGUMENTS).ifPresent(arguments -> arguments.getMembers().values()
      .forEach(values -> this.collectValues(Optional.of(values.getAsJsonObject()), terms)));
    for (JsonElement element : conditionalJson) {
      terms.add(element.getAsJsonObject().getStringMember(PRESENCE));
      element.getAsJsonObject().getObjectMemberOpt(ARGUMENTS).ifPresent(arguments -> arguments.getMembers().values()
        .forEach(values -> this.collectValues(Optional.of(values.getAsJsonObject()), terms)));
    }

    Map<String, List<String>> enumerations = new LinkedHashMap<>();
    presence.getObjectMemberOpt(ENUMERATIONS).ifPresent(enumerationsJson -> enumerationsJson.getMembers()
      .forEach((name, constants) -> enumerations.put(name, constants.getAsJsonArray().getValues().stream()
        .map(constant -> constant.getAsJsonString().getValue())
        .toList())));
    Optional<List<Expr<?>>> parsed = this.parse(context.get(), component, enumerations,
      presence.getStringMemberOpt(DECLARATIONS).orElse(""), terms);
    if (parsed.isEmpty()) return;
    Iterator<Expr<?>> next = parsed.get().iterator();
    BoolExpr constraints = (BoolExpr) next.next();
    BoolExpr delayed = (BoolExpr) next.next();
    Map<String, Expr<?>> defaults = this.readValues(presence.getObjectMemberOpt(DEFAULTS), next);
    Map<SubcomponentSymbol, Map<String, Expr<?>>> arguments = new IdentityHashMap<>();
    presence.getObjectMemberOpt(ARGUMENTS).ifPresent(unconditional -> unconditional.getMembers().forEach((name, values) -> {
      Map<String, Expr<?>> read = this.readValues(Optional.of(values.getAsJsonObject()), next);
      component.getTypeInfo().getSubcomponents().stream()
        .filter(subcomponent -> subcomponent.getName().equals(name))
        .filter(subcomponent -> conditional.stream().noneMatch(symbols -> symbols.contains(subcomponent)))
        .findFirst()
        .ifPresent(subcomponent -> arguments.put(subcomponent, read));
    }));
    List<Map<String, Expr<?>>> superArguments = new ArrayList<>();
    component.getTypeInfo().getSuperComponentsList().forEach(superComponent -> superArguments.add(Map.of()));
    presence.getObjectMemberOpt(SUPER_ARGUMENTS).ifPresent(superJson -> superJson.getMembers().forEach((index, values) -> {
      Map<String, Expr<?>> read = this.readValues(Optional.of(values.getAsJsonObject()), next);
      int position = Integer.parseInt(index);
      if (position < superArguments.size()) {
        superArguments.set(position, read);
      }
    }));
    Map<ISymbol, BoolExpr> symbols = new IdentityHashMap<>();
    for (int i = 0; i < conditionalJson.size(); i++) {
      BoolExpr symbolPresence = (BoolExpr) next.next();
      List<ISymbol> group = conditional.get(i);
      group.forEach(symbol -> symbols.put(symbol, symbolPresence));
      conditionalJson.get(i).getAsJsonObject().getObjectMemberOpt(ARGUMENTS).ifPresent(argumentsJson -> argumentsJson.getMembers()
        .forEach((name, values) -> {
          Map<String, Expr<?>> read = this.readValues(Optional.of(values.getAsJsonObject()), next);
          group.stream()
            .filter(SubcomponentSymbol.class::isInstance)
            .map(SubcomponentSymbol.class::cast)
            .filter(subcomponent -> subcomponent.getName().equals(name))
            .findFirst()
            .ifPresent(subcomponent -> arguments.put(subcomponent, read));
        }));
    }
    owner.setPresenceFormulas(new PresenceFormulas(constraints, delayed, symbols, defaults, arguments,
      superArguments));
  }

  protected void collectValues(@NotNull Optional<JsonObject> values, @NotNull List<String> terms) {
    values.ifPresent(json -> json.getMembers().values().forEach(value -> terms.add(value.getAsJsonString().getValue())));
  }

  protected Map<String, Expr<?>> readValues(@NotNull Optional<JsonObject> values, @NotNull Iterator<Expr<?>> next) {
    Map<String, Expr<?>> read = new LinkedHashMap<>();
    values.ifPresent(json -> json.getMembers().keySet().forEach(name -> read.put(name, next.next())));
    return read;
  }

  /**
   * @param enumerations the constants of the enumerations of the terms by the
   *                     names of the enumerations
   * @return the terms parsed with a single call, each wrapped into an
   * assertion of its equality with itself to also parse terms that are no
   * formulas, or empty if they cannot be parsed, which is reported
   */
  protected Optional<List<Expr<?>>> parse(@NotNull Context context,
                                          @NotNull IVariableArcComponentTypeSymbol component,
                                          @NotNull Map<String, List<String>> enumerations,
                                          @NotNull String declarations,
                                          @NotNull List<String> terms) {
    List<Symbol> sortNames = new ArrayList<>();
    List<Sort> sorts = new ArrayList<>();
    StringBuilder script = new StringBuilder(declarations);
    terms.forEach(term -> script.append("\n(assert (= ").append(term).append(' ').append(term).append("))"));
    try {
      enumerations.forEach((name, values) -> {
        // the parser knows the constants of the sorts
        EnumSort<?> enumeration = VariableArcDeriveSMTSort.enumSort(context, name, values);
        sortNames.add(enumeration.getName());
        sorts.add(enumeration);
      });
      List<Expr<?>> parsed = new ArrayList<>();
      for (BoolExpr assertion : context.parseSMTLIB2String(script.toString(), sortNames.toArray(Symbol[]::new),
        sorts.toArray(Sort[]::new), null, null)) {
        parsed.add(assertion.getArgs()[0]);
      }
      if (parsed.size() != terms.size()) {
        Log.error(VariableArcError.PRESENCE_NOT_DESERIALIZABLE.format(component.getTypeInfo().getFullName(),
          String.format("expected %d terms, but parsed %d", terms.size(), parsed.size())));
        return Optional.empty();
      }
      return Optional.of(parsed);
    } catch (Z3Exception e) {
      Log.error(VariableArcError.PRESENCE_NOT_DESERIALIZABLE.format(component.getTypeInfo().getFullName(), e.getMessage()));
      return Optional.empty();
    }
  }

  /**
   * @return the conditional symbols, which are added to the spanned scope of
   * the component type, grouped as serialized
   */
  protected List<List<ISymbol>> deserializeConditionalSymbols(@NotNull IVariableArcComponentTypeSymbol component,
                                                              @NotNull JsonObject json) {
    IVariableArcScope scope = (IVariableArcScope) component.getTypeInfo().getSpannedScope();
    List<List<ISymbol>> conditional = new ArrayList<>();
    for (JsonElement element : json.getArrayMemberOpt(CONDITIONAL_SYMBOLS).orElseGet(Collections::emptyList)) {
      List<ISymbol> symbols = new ArrayList<>();
      for (JsonElement symbolJson : element.getAsJsonObject().getArrayMemberOpt(SYMBOLS).orElseGet(Collections::emptyList)) {
        ISymbol symbol = VariableArcMill.globalScope().getSymbolDeSer(JsonDeSers.getKind(symbolJson.getAsJsonObject()))
          .deserialize(scope, symbolJson.getAsJsonObject());
        switch (symbol) {
          case ComponentTypeSymbol type -> {
            scope.add(type);
            scope.addSubScope((IVariableArcScope) type.getSpannedScope());
          }
          case ArcFeatureSymbol feature -> scope.add(feature);
          case PortSymbol port -> scope.add(port);
          case SubcomponentSymbol subcomponent -> scope.add(subcomponent);
          case VariableSymbol field -> scope.add(field);
          default -> Log.error(String.format("Could not add symbol '%s' of kind '%s' to component '%s'",
            symbol.getName(), JsonDeSers.getKind(symbolJson.getAsJsonObject()), component.getTypeInfo().getName()));
        }
        symbols.add(symbol);
      }
      conditional.add(symbols);
    }
    return conditional;
  }
}
