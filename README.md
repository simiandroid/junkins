#Junkins
A tool to analyze jenkins shared-lib

## Como executar
### Gerar ambos os diagramas
java -jar analyzer.jar /path/to/sharedlib -g -p

### Apenas grafo de dependências
java -jar analyzer.jar /path/to/sharedlib -g

### Apenas diagrama de pacotes
java -jar analyzer.jar /path/to/sharedlib -p

## Como gerar o build 
mvn clean package

ou 

mvn clean compile

## Como gerar o parser manualmente

antlr src\main\antlr4\com\taigae\junkins\parser\JenkinsGroovyLexer.g4 src\main\antlr4\com\taigae\junkins\parser\JenkinsGroovyParser.g4 -package com.taiga.junks.antlr4.generated -o antlr/output -visitor
javac antlr/target/*.java -d output
jar cvf parser.jar output/*

## Para converter os arquivos DOT em imagens:
### Requer GraphViz instalado
dot -Tpng output/dependencies.dot -o dependencies.png
dot -Tpng output/packages.dot -o packages.png

