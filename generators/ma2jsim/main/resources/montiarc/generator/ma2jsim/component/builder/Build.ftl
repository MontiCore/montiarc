<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
public ${ast.getName()}${suffixes.component()} build() {
  if(!isValid()) throw new java.lang.IllegalStateException(
    "Illegal builder configuration for component " + ((name == null || name.isBlank())? "ERR: no name given" : name)
  );

  return new ${ast.getName()}${suffixes.component()}(
    getName()
    <#list ast.getHead().getArcParameterList()>, <#items as param>${prefixes.getterMethod()}${prefixes.parameter()}${param.getName()}()<#sep>, </#sep></#items></#list>
    <#list helper.getFeatures(ast)>, <#items as feature>${prefixes.getterMethod()}${prefixes.feature()}${feature.getName()}()<#sep>, </#sep></#items></#list>
  );
}