<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
public boolean isValid() {
  return !(name == null || "".equals(name)) &&
  <#list helper.getNonPrimitiveParameters(ast)>
      <#items as param>${prefixes.parameter()}${param.getName()} != null<#sep>
      &&</#sep></#items>
  <#else>true</#list>;
  }
