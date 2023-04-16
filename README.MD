# Testando: maven-plugin-release

<p style="text-align: justify">
Este projeto tem como objetivo executar um fluxo de versionamento, usando maven-release-plugin. Ele seguirá o 
padrão de versionamento semântico, portando a versão da aplicação seguira o formado PRINCIPAL.SECUNDARIO.CORRECAO, 
por exemplo, no número de versão 2.0.1: 2 é o número de versão principal (Maior ou Major, em inglês), o incremento 
desse número representa uma mudança na aplicação que a torna completa ou parcialmente incompatível com versões 
principais anteriores; 0 é o número da versão secundária (Menor ou Minor, em inglês), e representa o incremento de novas
funcionalidades dentro de uma mesma versão principal, sem perda de compatibilidade com versões anteriores da mesma
versão principal; e 1 é o número de versão de correção (Patch, em inglês), ele representa a correção de bug dentro 
de uma mesma verão secundária. Note que o número mais a direta está ligado ao número que está imediatamenta a sua 
esquerda, portando, na versão 1.2.3, o número 3 é uma correção na versão secundária 2 da verão principal 1, e na versão 
2.4.3, 3 é uma correção na versão secundária 4 da verão principal 2, e elas são correções diferentes. Assim sendo, o 
incremento de uma versão tem como efeito retornar para 0 os número de versão a sua direta, por exemplo, incrementar 
a versão secundária de 1.2.3 resulta na versão 1.3.0.
</p>



Pare fazer um lançamento vamos executar os seguintes comandos:

* [***release:prepare***](https://maven.apache.org/maven-release/maven-release-plugin/usage/prepare-release.html):  
  prepara um lançamento, a maior parte do trabalho é executada nessa etapa, incluindo o incremento da
  versão. Ao executar será solicitado para três parâmetros: a versão do lançamento, o nome da tag, a próxima versão.
  Para todos os parâmetros existem valores sugeridos, que serão usados caso o usuário não informar nenhum valor, ou em
  modo de operação em lote.
* [***release:perform***](https://maven.apache.org/maven-release/maven-release-plugin/usage/perform-release.html):  
  executa a implantação dos artefatos.

## Configurando

Para usarmos o plugin precisamos primeiro precisamos informar o endereço do repositório (SCM, Source Code Management).
Aqui iremos informar como endereço a pasta atual do projeto.

```xml

<project>

    ...

    <scm>
        <developerConnection>scm:git:file:///${project.basedir}</developerConnection>
    </scm>

    ...

</project>
```

## Testando um lançamento

Existem muitas configurações que podem ser ajustadas e antes de fazer o lançamento podemos testar esses ajustes usando o
parâmetro ```-DdryRun```, dessa forma nenhuma alteração é aplicada.

```
mvn -B release:clean release:prepare -DdryRun -DcheckModificationExcludeList=*
```

Nesse comando é usado [
***release:clean***](https://maven.apache.org/maven-release/maven-release-plugin/usage/clean-release.html) para limpar
os resíduos da preparação anterior para que não interferirão com a execução da preparação atual. O
parâmetro ```-DcheckModificationExcludeList=*``` ignorar todas as alterações não confirmadas, isso evita que tenhamos
que enviar as
modificações que ainda estamos testando para o repositório cada vez que tentamos testa-las.

## Configurando o lançamento

Abaixo configuramos alguns aspectos sobre o lançamento, como o formado da tag de lançamento e mensagens.
Observe que @ é usado no lugar de $ nas funções de substituição, para o maven faça a subtituição durante a execuçao do
plugin e não antes.

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

## Incrementando versão

Durante a etapa de preparação, por padrão, o último digito da versão é incrementada, esse comportamento pode ser
alterado
a partir da configuração ```projectVersionPolicyId```. Porém, essa configuração tem certas limitações, portanto vamos
ignora-la e
fornecer a próxima versão através do parâmetro ``developmentVersion`` usando o
plugin [Maven Build Helper](https://www.mojohaus.org/build-helper-maven-plugin/).
Da seguinte forma:

Incrementando versão principal, ou maior (major)

```
mvn -B build-helper:parse-version release:prepare -DdevelopmentVersion=${parsedVersion.nextMajorVersion}.0.0-SNAPSHOT
```

Incrementando versão secundária, ou menor (minor)

```
mvn -B build-helper:parse-version release:prepare -DdevelopmentVersion=${parsedVersion.majorVersion}.${parsedVersion.nextMinorVersion}.0-SNAPSHOT
```

Incrementando versão de correção (patch)

```
mvn -B build-helper:parse-version release:prepare -DdevelopmentVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion}-SNAPSHOT
```

## Referências

[Releasing](https://maven.apache.org/guides/mini/guide-releasing.html)  
[Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/index.html)  
[Semantic Versioning](https://semver.org/)
[Maven Build Helper](https://www.mojohaus.org/build-helper-maven-plugin/)