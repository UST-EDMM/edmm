require 'chef/provisioning/docker_driver'

<#if name??>
machine '${name}' do
    <#if recipes??>
    <#list recipes as recipe>
    recipe '${recipe}'
    </#list>
    </#if>
    <#if image??>
    machine_options :docker_options => {
        :base_image => {
            :name => '${image}',
            :repository => '${image}'<#if imageVersion??>,
            :tag => '${imageVersion}'
            </#if>
        }
    }
    </#if>

end
</#if>
