FROM ubuntu:bionic

ENV DEBIAN_FRONTEND noninteractive
<#list stack.vars as key, value>
ENV ${key}=${value
</#list>

RUN apt-get -q -y update && \
    apt-get install -y git

<#list stack.files as file>
ADD ${file.source} /opt/app/init/${file.target}
</#list>

<#list stack.ports as port>
EXPOSE ${port}
</#list>

WORKDIR /opt/app
CMD [<#list stack.cmd as c>${c}<#sep>, </#sep></#list>]
