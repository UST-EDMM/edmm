
# EDMM Transformation Framework

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/UST-EDMM/transformation-framework.svg?branch=master)](https://travis-ci.org/UST-EDMM/transformation-framework)
[![](https://jitpack.io/v/UST-EDMM/transformation-framework.svg)](https://jitpack.io/#UST-EDMM/transformation-framework)

> Transformation framework for the Essential Deployment Metamodel.

EDMM provides a declarative model that describes the components to be deployed, their configurations, required artifacts, and relations among them.
The resulting EDMM model is independent of any specific deployment technology and can be exported from an EDMM-enabled modeling tool or created directly using a text editor according to the respective YAML specification.
This model can be fed into the EDMM Transformation Framework, which provides a command-line interface (CLI) that can either be used directly by the user or integrated into any automation workflow.
The CLI can be used to select the desired target deployment technology into which the EDMM model is to be transformed. 
The output is an executable technology-specific deployment model that can be executed with the selected technology.
The following figure shows an high-level overview of the EDMM Transformation Framework.
 
![Framework Architecture](docs/architecture.png)

## Usage

The final distribution package can be downloaded form the [releases](https://github.com/UST-EDMM/transformation-framework/releases) page.
Extract the files to a location on your filesystem and add it to your path variable (Linux: `$PATH`, Windows: `%PATH%`). 
Afterwards you can invoke the `edmm` command from a command prompt.

The transformation framework supports YAML files as input, according to the published [YAML specification](https://github.com/UST-EDMM/spec-yaml). 
Specified components and their respective component types must be supplied in a single file at the moment. 
However, we introduce a couple of built-in component types that can be used to model an application deployment.
An [example](edmm-core/src/test/resources/templates/scenario_iaas.yml) shows the usage of the built-in types to model an application deployment based on virtual compute resources, e.g., virtual machines having some software components installed.  

The transformation can be started by using the `transform` command of the `edmm` tool: `edmm transform <target> <input>`.
You have to specify the `target` technology (e.g., one of "ansible", "azure", "chef", "compose", "heat", "kubernetes", "terraform") and the `input` EDMM YAML model file.
The generated technology-specific deployment models will be stored relative to the YAML input file.

## Supported Scenario

In this demonstration, all listed plugins focus on application deployments that are based on virtual computing resources and the software that needs to be deployed on them including their configuration and orchestration.
The following figure shows the application stacks that is used in our test cases. 
Further, this [example](edmm-core/src/test/resources/templates/scenario_iaas.yml) shows basically the resulting EDMM model.

![Supported Scenario](docs/iaas-scenario.jpg)

## Plugins

Each plugin implements its own transformation logic by providing a respective `Plugin` implementation.
Further, a plugin implements different lifecycle methods, e.g., `checkModel()`, `prepare()`, and `cleanup()`, but must provide an implementation for the `transform()` method.
Currently we support the following list of plugins:

* [Ansible](edmm-core/src/main/java/io/github/edmm/plugins/ansible)
* [Azure Resource Manager](edmm-core/src/main/java/io/github/edmm/plugins/azure)
* [Chef](edmm-core/src/main/java/io/github/edmm/plugins/chef)
* [Docker Compose](edmm-core/src/main/java/io/github/edmm/plugins/compose)
* [Heat Orchestration Template](edmm-core/src/main/java/io/github/edmm/plugins/heat)
* [Kubernetes](edmm-core/src/main/java/io/github/edmm/plugins/kubernetes)
* [Terraform](edmm-core/src/main/java/io/github/edmm/plugins/terraform)
* [Puppet](edmm-core/src/main/java/io/github/edmm/plugins/puppet)
* [AWS CloudFormation](edmm-core/src/main/java/io/github/edmm/plugins/cfn)
* [Cloudify](edmm-core/src/main/java/io/github/edmm/plugins/cloudify)

The corresponding plugin README.md contains detailed information of the transformation rule each plugin employs.

## Built-in Component Types

The following built-in types can be used in an EDMM-based model.
Simply copy and past the following code block to your model file.
An example showing the usage of these types is available [here](edmm-core/src/test/resources/templates/scenario_iaas.yml).

```yaml
component_types:

  ## Generic Types

  base:
    extends: null
    description: The base type
    metadata: {}
    operations:
      create: ~
      configure: ~
      start: ~
      stop: ~
      delete: ~

  software_component:
    extends: base

  compute:
    extends: base
    properties:
      os_family:
        type: string
        description: Specifies the type of operating system
        default_value: linux
      machine_image:
        type: string
        description: The name of the machine image to use
      instance_type:
        type: string
        description: The name of the instance type to provision
      key_name:
        type: string
        description: The name of the key pair to use for authentication
      public_key:
        type: string
        description: The public key of the key pair to use for authentication

  web_server:
    extends: software_component
    properties:
      port:
        type: integer
        default_value: 80

  web_application:
    extends: base

  dbms:
    extends: software_component
    properties:
      port:
        type: integer
      root_password:
        type: string

  database:
    extends: base
    properties:
      schema_name:
        type: string
      user:
        type: string
      password:
        type: string

  ## Technology-specific Types

  tomcat:
    extends: web_server
    properties:
      port:
        type: integer
        default_value: 8080

  mysql_dbms:
    extends: dbms
    properties:
      port:
        type: integer
        default_value: 3306

  mysql_database:
    extends: database
```

## Build the project

We use Maven as our build tool:

```shell
./mvnw clean package
```
