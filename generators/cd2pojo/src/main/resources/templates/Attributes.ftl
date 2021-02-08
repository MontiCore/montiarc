<#-- (c) https://github.com/MontiCore/monticore -->

<#macro printAttributesAndGetterSetter type helper>
    <#list helper.getAttributes(type) as attribute>
        ${attribute.getModifier()} ${attribute.getType()} ${attribute.getName()};

    </#list>
    <#list helper.getAttributes(type) as attribute>
        <@printGetterSetter attribute=attribute/>
    </#list>
</#macro>

<#macro printAttributesForInterface type helper>
    <#list helper.getAttributes(type) as attribute>
        ${attribute.getModifier()} ${attribute.getType()} ${attribute.getName()};

    </#list>
</#macro>

<#macro printGetterSetter attribute>
  public ${attribute.getType()} get${attribute.getName()?cap_first}() {
  return this.${attribute.getName()};
  }

    <#if !attribute.isReadOnly()>
      public void set${attribute.getName()?cap_first}(
        ${attribute.getType()} ${attribute.getName()}) {
      this.${attribute.getName()} = ${attribute.getName()};
      }

    </#if>
</#macro>