${tc.params("java.util.Optional<String> type", "String name")}

<#if type.isPresent()>
    private ${type.get()} ${name};
</#if>