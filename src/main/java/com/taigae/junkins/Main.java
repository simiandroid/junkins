package com.taigae.junkins;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.Callable;

import com.taigae.junkins.analyzer.DependencyAnalyzer;

@Command(name = "junkins-cli", mixinStandardHelpOptions = true,
        version = "junkins-analyzer 1.0",
        description = "Analisador de Shared Libraries do Jenkins")
public class Main implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Path to Shared Library")
    private Path libPath;

    @Option(names = {"-g", "--graph"}, description = "Generate dependency graph (DOT format)")
    private boolean genGraph;
    
    @Option(names = {"-p", "--packages"}, description = "Generate package diagram (DOT format)")
    private boolean genPackages;
    
    @Option(names = {"-o", "--output"}, description = "Arquivo de saída (opcional)")
    private File outputFile;

    @Option(names = {"-o", "--output"}, description = "Output directory")
    private Path outputDir = Paths.get("output");
    
    @Option(names = {"-v", "--verbose"}, description = "Modo verboso")
    private boolean verbose;

    @Option(names = {"-m", "--methods"}, description = "Analyze method calls and data flow")
    private boolean analyzeMethods;
    
    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
    
    @Override
    public Integer call() throws Exception {

        if (verbose) {
            System.out.println("Analisando: " + libPath);
        }

        Files.createDirectories(outputDir);
        
        DependencyAnalyzer analyzer = new DependencyAnalyzer(libPath);
        analyzer.analyze();
        

        if (genGraph) {
            String graph = VisualizationGenerator.generateDependencyGraph(analyzer.getDependencies());
            Files.write(outputDir.resolve("dependencies.dot"), graph.getBytes());
        }
        
        if (genPackages) {
            String packages = VisualizationGenerator.generatePackageDiagram(analyzer.getPackageStructure());
            Files.write(outputDir.resolve("packages.dot"), packages.getBytes());
        }

        if (analyzeMethods) {
            analyzer.analyzeMethods(outputDir);
        }
        
        return 0;
        
    }
    
}