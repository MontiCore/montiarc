<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
public boolean isValid() {
  return !(name == null || "".equals(name)) &&
  <#list helper.getNonPrimitiveParameters(ast)>
      <#items as param>${prefixes.parameter()}${param.getName()} != null<#sep>
      &&</#sep></#items>
  <#else>true</#list>
  <#list helper.getFeatures(ast)>
      && <#items as feature>${prefixes.feature()}${feature.getName()} != null<#sep>
      &&</#sep></#items>
  </#list>
  && checkConstraintsFulfilled();
  }

public boolean checkConstraintsFulfilled() {
  <#list helper.getFeatures(ast) as feature>
      boolean ${feature.getName()} = ${prefixes.getterMethod()}${prefixes.feature()}${feature.getName()}();
  </#list>

  return <#list helper.getConstraintExpressions(ast) as constraint>
      (${prettyPrinter.prettyprint(constraint)}) <#sep>
      &&</#sep>
  <#else>true</#list>;
}