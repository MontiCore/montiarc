<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
public ${ast.getName()}${suffixes.component()}<@Util.printTypeParameters ast false/> build() {
  if(!isValid()) throw new java.lang.IllegalStateException(
    "Illegal builder configuration for component " + ((name == null || name.isBlank())? "ERR: no name given" : name)
  );

  ${ast.getName()}${suffixes.component()}<@Util.printTypeParameters ast false/> component = new ${ast.getName()}${suffixes.component()}<@Util.printTypeParameters ast false/>(
    getName(),
    getScheduler()
    <#list ast.getHead().getArcParameterList()>, <#items as param>${prefixes.getterMethod()}${prefixes.parameter()}${param.getName()}()<#sep>, </#sep></#items></#list>
    <#list helper.getFeatures(ast)>, <#items as feature>${prefixes.getterMethod()}${prefixes.feature()}${feature.getName()}()<#sep>, </#sep></#items></#list>
  );

  return component;
}