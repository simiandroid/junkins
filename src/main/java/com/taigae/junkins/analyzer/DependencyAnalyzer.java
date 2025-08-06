package com.taigae.junkins.analyzer;


import com.taigae.junkins.parser.*;
import com.taigae.junkins.reporter.AnalysisReporter;
import com.taigae.junkins.visitor.MethodAnalysisVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class DependencyAnalyzer {
    private final Path libRoot;
    private final Map<String, Set<String>> dependencies = new HashMap<>();
    private final Map<String, Set<String>> fileImports = new HashMap<>();
    private final Map<String, String> packageStructure = new TreeMap<>();

    public DependencyAnalyzer(Path libRoot) {
        this.libRoot = libRoot;
    }

    public void analyze() throws IOException {
        analyzePackageStructure();
        analyzeDependencies();
    }

    public Map<String, String> getPackageStructure() {
        return Collections.unmodifiableMap(packageStructure);
    }

     private void analyzePackageStructure() throws IOException {
        Files.walk(libRoot)
            .filter(this::isGroovyFile)
            .forEach(file -> {
                String relativePath = libRoot.relativize(file).toString();
                String packageName = extractPackageName(file);
                packageStructure.put(relativePath, packageName);
            });
    }




    private void analyzeFile(Path file) {
        try {
            String packageName = extractPackageName(file);
            String fileName = libRoot.relativize(file).toString();
            
            // Registrar no diagrama de pacotes
            packageStructure.put(fileName, packageName);
            
            // Analisar dependências
            analyzeDependencies(file, fileName);
        } catch (IOException e) {
            System.err.println("Error analyzing " + file + ": " + e.getMessage());
        }
    }

    private String extractPackageName(Path file) {
        try {
            // Tentar extrair do conteúdo do arquivo
            String content = Files.readString(file);
            Pattern packagePattern = Pattern.compile("package\\s+([a-zA-Z0-9_.]+)");
            Matcher matcher = packagePattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
            
            // Fallback: usar estrutura de diretórios
            return file.getParent().equals(libRoot) ? 
                "<root>" : 
                libRoot.relativize(file.getParent()).toString().replace(File.separator, ".");
        } catch (IOException e) {
            return "<error>";
        }
    }

 // 2. Implementação do getDependencies
    public Map<String, Set<String>> getDependencies() {
        return Collections.unmodifiableMap(dependencies);
    }

    private void analyzeDependencies(Path file, String fileName) throws IOException {
        String content = Files.readString(file);
        // Implementar análise de imports e referências
        // Exemplo simplificado:
        Pattern importPattern = Pattern.compile("import\\s+([a-zA-Z0-9_.]+)");
        Matcher matcher = importPattern.matcher(content);
        
        while (matcher.find()) {
            String imported = matcher.group(1);
            dependencies.computeIfAbsent(fileName, k -> new HashSet<>()).add(imported);
        }
    }

    private void analyzeDependencies() throws IOException {
        // Primeiro passada: coletar todos os arquivos e seus imports
        Files.walk(libRoot)
            .filter(this::isGroovyFile)
            .forEach(file -> {
                try {
                    String fileName = libRoot.relativize(file).toString();
                    fileImports.put(fileName, extractImports(file));
                } catch (IOException e) {
                    System.err.println("Error analyzing imports in " + file + ": " + e.getMessage());
                }
            });

        // Segunda passada: mapear dependências reais
        fileImports.forEach((file, imports) -> {
            imports.forEach(imp -> {
                resolveImportToFile(imp).ifPresent(depFile -> {
                    dependencies.computeIfAbsent(file, k -> new HashSet<>())
                              .add(depFile);
                });
            });
        });
    }

    private Set<String> extractImports(Path file) throws IOException {
        Set<String> imports = new HashSet<>();
        String content = Files.readString(file);

        // Padrão para imports padrão
        Pattern importPattern = Pattern.compile("import\\s+([a-zA-Z0-9_.*]+)");
        Matcher matcher = importPattern.matcher(content);

        while (matcher.find()) {
            String importStr = matcher.group(1);
            if (!importStr.endsWith(".*")) { // Ignorar imports com wildcard
                imports.add(importStr);
            }
        }

        // Extrair imports estáticos (opcional)
        Pattern staticImportPattern = Pattern.compile("import\\s+static\\s+([a-zA-Z0-9_.]+)");
        matcher = staticImportPattern.matcher(content);
        
        while (matcher.find()) {
            imports.add(matcher.group(1));
        }

        return imports;
    }

     private Optional<String> resolveImportToFile(String importStr) {
        // 1. Verificar se é um import da própria library
        String importPath = importStr.replace('.', File.separatorChar) + ".groovy";
        
        try {
            Optional<Path> foundFile = Files.walk(libRoot)
                .filter(p -> p.toString().endsWith(importPath))
                .findFirst();
                
            if (foundFile.isPresent()) {
                return Optional.of(libRoot.relativize(foundFile.get()).toString());
            }
            
            // 2. Verificar se é um import de classes Java (java., javax., etc.)
            if (importStr.startsWith("java.") || importStr.startsWith("javax.")) {
                return Optional.of("[JRE] " + importStr);
            }
            
            // 3. Verificar outros imports (Jenkins, plugins, etc.)
            return Optional.of("[EXT] " + importStr);
            
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private boolean isGroovyFile(Path path) {
        return Files.isRegularFile(path) && 
               path.toString().endsWith(".groovy") &&
               !path.toString().contains("test"); // Ignorar testes
    }

    
    public void analyzeMethods(Path outputDir) throws IOException {
        MethodAnalysisVisitor methodVisitor = new MethodAnalysisVisitor();
        
        Files.walk(libRoot)
            .filter(p -> p.toString().endsWith(".groovy"))
            .forEach(file -> {
                try {
                    String content = Files.readString(file);
                    ParseTree tree = parseGroovy(content);
                    methodVisitor.visit(tree);
                } catch (IOException e) {
                    System.err.println("Error analyzing methods in " + file + ": " + e.getMessage());
                }
            });
        
        // Gerar relatórios
        String callGraph = AnalysisReporter.generateCallGraph(methodVisitor.getGraph());
        String dataFlowReport = AnalysisReporter.generateDataFlowReport(methodVisitor.getGraph());
        
        Files.write(outputDir.resolve("call_graph.dot"), callGraph.getBytes());
        Files.write(outputDir.resolve("data_flow.txt"), dataFlowReport.getBytes());
    }
    
    private ParseTree parseGroovy(String content) {
        JenkinsGroovyLexer lexer = new JenkinsGroovyLexer(CharStreams.fromString(content));
        JenkinsGroovyParser parser = new JenkinsGroovyParser(new CommonTokenStream(lexer));
        return parser.compilationUnit();
    }
}
