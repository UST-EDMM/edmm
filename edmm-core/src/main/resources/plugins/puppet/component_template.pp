class ${component} {
<#list tasks as task>
    include ${component}::${task.name}
</#list>
}
