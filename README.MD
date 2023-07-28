# Testando: maven-plugin-release

Este projeto tem como objetivo executar um fluxo de entrega continua para um projeto maven integrando GitHub Actions e maven-release-plugin, um plugin fornecido pelo projeto maven que automatiza a tarefa de realizar um lançamento, incluindo as etapas de incremento da versão, criação da tag do lançamento, geração e envio do artefato para o repositório de artefatos.

## Configurando

Para usarmos o plugin precisamos primeiro precisamos informar o endereço do repositório, na seção SCM, Source Code Management.
Aqui iremos informar como endereço a pasta atual do projeto.

```xml

<project>

    ...

    <scm>
        <developerConnection>scm:git:file:///${project.basedir}</developerConnection>
        <tag>HEAD</tag>
    </scm>

    ...

</project>
```

Abaixo configuramos alguns aspectos sobre o lançamento, como o formado da tag de lançamento e mensagens.
Observe que @ é usado no lugar de $ nas funções de substituição.

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <tagNameFormat>v@{project.version}</tagNameFormat>
        <scmCommentPrefix>release:</scmCommentPrefix>
        <scmReleaseCommitComment>@{prefix} @{releaseLabel}</scmReleaseCommitComment>
        <scmDevelopmentCommitComment>@{prefix} incrementando para nova versão de desenvolvimento
        </scmDevelopmentCommitComment>
    </configuration>
</plugin>
```

## Fazendo um lançamento

O maven-release-plugin divide o lançamento em duas etapas: preparar e executar, cada uma executada pelo seu respectivo 
Goal: prepare e perform.

### Preparar ([***release:prepare***](https://maven.apache.org/maven-release/maven-release-plugin/usage/prepare-release.html))

Esse Goal executa a maioria do trabalho de um lançamento, nas seguintes etapas:

* Verifica alterações não confirmadas
* Verifica se não existem dependências SNAPSHOT
* Altera a versão de SNAPSHOT para versão final (ex: de 1.0.0-SNAPSHOT para 1.0.0)
* Adiciona/Alterar a tag no pom.xml, dentro da seção SCM
* Executa os Testes
* Confirma as modificação
* Cria a tag de lançamento da versão x (ex: da 1.0.0)
* Incrementa para a próxima versão SNAPSHOT (ex: de 1.0.0 para 1.0.1-SNAPSHOT)
* Confirma as modificações

Note que o plugin espera que não haja modificações não confirmadas e que o projeto esteja na versão SNAPSHOT antes de fazer o lançamento, e a após o mesmo. Ao executar será solicitado para três parâmetros: a versão do lançamento, o nome da tag, a próxima versão.
Para todos os parâmetros existem valores predefinidos, que serão usados caso o usuário não informar o valor, ou em
modo de execução em lote.


### Executar ([***release:perform***](https://maven.apache.org/maven-release/maven-release-plugin/usage/perform-release.html))

Executa os Goal deploy e site-deploy para implantação dos artefatos.

## Testando um lançamento

O maven-release-plugin possui muitas configurações para controlar vários aspectos do lançamento, para testar se tudo está configurado corretamente podemos usar o parâmetro ```-DdryRun```, dessa forma podemos simular um lançamento sem que nenhuma alteração seja aplicada.

```shell
mvn -B release:clean release:prepare -DdryRun -DcheckModificationExcludeList=*
```

Nesse comando é usado o Goal [
***release:clean***](https://maven.apache.org/maven-release/maven-release-plugin/usage/clean-release.html) para limpar
os resíduos da preparação anterior para que não interferão com a execução da preparação atual.

O parâmetro ```-DcheckModificationExcludeList=*``` ignorar todas as alterações não confirmadas, isso evita que tenhamos
que enviar as
modificações que ainda estamos testando para o repositório cada vez que tentamos simular um lançamento.

## Incrementando versão

Durante a etapa de preparação, por padrão, o último digito da versão é incrementada, esse comportamento pode ser alterado a partir da configuração ```projectVersionPolicyId```. Porém, essa configuração tem certas limitações, portanto vamos ignora-la e fazer o incremento da seguinte forma:

Primeiro será usado o plugin [Maven Build Helper](https://www.mojohaus.org/build-helper-maven-plugin/) para extrair a versão do projeto e prepara as variáveis com as versões.

```shell
majorNumber=$(mvn build-helper:parse-version help:evaluate -Dexpression=parsedVersion.majorVersion -q -DforceStdout)
nextMinorNumber=$(mvn build-helper:parse-version help:evaluate -Dexpression=parsedVersion.nextMinorVersion -q -DforceStdout)
nextMinorVersion=$majorNumber.$nextMinorNumber.0
```

Depois vamos fornecer a próxima versão através do parâmetro ``developmentVersion``.

```shell
mvn -B release:prepare -DdevelopmentVersion=$nextMinorVersion-SNAPSHOT
```




## Referências

[Releasing](https://maven.apache.org/guides/mini/guide-releasing.html)  
[Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/index.html)  
[Semantic Versioning](https://semver.org/)  
[Maven Build Helper](https://www.mojohaus.org/build-helper-maven-plugin/)
