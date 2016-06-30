${tc.signature("packageName", "schemaName", "tagTypeName", "dataType", "isUnit")}

package ${packageName}.${schemaName};

import de.monticore.lang.montiarc.tagging._symboltable.TagKind;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;

  <#if isUnit>
import javax.measure.Measure;
import javax.measure.quantity.${dataType};
import javax.measure.unit.Unit;
  </#if>

/**
 * Created by ValuedTagType.ftl
 */
public class ${tagTypeName}Symbol extends TagSymbol {
  public static final ${tagTypeName}Kind KIND = ${tagTypeName}Kind.INSTANCE;

<#if isUnit>
  public ${tagTypeName}Symbol(Measure<? extends Number, ${dataType}> value) {
    this(KIND, value);
  }

  public ${tagTypeName}Symbol(Number number, Unit<${dataType}> unit) {
    this(KIND, number, unit);
  }

  protected ${tagTypeName}Symbol(${tagTypeName}Kind kind, Measure<? extends Number, ${dataType}> value) {
    super(kind, value);
  }

  protected ${tagTypeName}Symbol(${tagTypeName}Kind kind, Number number, Unit<${dataType}> unit) {
    this(kind, number.toString().contains(".") ?
      Measure.valueOf(number.doubleValue(), unit) :
      Measure.valueOf(number.intValue(),
          unit));
  }

  public Measure<? extends Number, ${dataType}> getValue() {
     return getValue(0);
  }

  public Number getNumber() {
    return getValue().getValue();
  }

  public Unit<${dataType}> getUnit() {
    return getValue().getUnit();
  }

  @Override
  public String toString() {
    return String.format("${tagTypeName} = %s %s",
      getNumber(), getUnit());
  }
<#else>
  public ${tagTypeName}Symbol(${dataType} value) {
    super(KIND, value);
  }

  protected ${tagTypeName}Symbol(${tagTypeName}Kind kind, ${dataType} value) {
    super(kind, value);
  }

  public ${dataType} getValue() {
     return getValue(0);
  }

  @Override
  public String toString() {
    <#if dataType = "String">
    return String.format("${tagTypeName} = \"%s\"",
      getValue().toString());
    <#else>
    return String.format("${tagTypeName} = %s",
      getValue().toString());
    </#if>
  }
</#if>

  public static class ${tagTypeName}Kind extends TagKind {
    public static final ${tagTypeName}Kind INSTANCE = new ${tagTypeName}Kind();

    protected ${tagTypeName}Kind() {
    }
  }
}