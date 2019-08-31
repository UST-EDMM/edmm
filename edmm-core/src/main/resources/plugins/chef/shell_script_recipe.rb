<#if properties??>
<#list properties as k, property>
ENV['${k}'] = '${property}'
</#list>
</#if>

<#if tasks??>
<#list tasks as task>
cookbook_file '${task.targetPath}' do
  source '${task.sourcePath}'
  mode 0755
end

execute '${task.name}' do
  command 'sh ${task.targetPath}'
end

</#list>
</#if>


