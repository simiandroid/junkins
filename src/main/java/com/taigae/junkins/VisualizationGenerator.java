package com.taigae.junkins;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class VisualizationGenerator {
    
    public static String generateDependencyGraph(Map<String, Set<String>> dependencies) {
        StringBuilder dot = new StringBuilder("digraph G {\n");
        
        dependencies.forEach((source, targets) -> {
            String cleanSource = cleanNodeName(source);
            dot.append(String.format("  \"%s\" [shape=box];\n", cleanSource));
            
            targets.forEach(target -> {
                String cleanTarget = cleanNodeName(target);
                dot.append(String.format("  \"%s\" -> \"%s\";\n", cleanSource, cleanTarget));
            });
        });
        
        dot.append("}");
        return dot.toString();
    }

    public static String generatePackageDiagram(Map<String, String> packageStructure) {
        Map<String, Set<String>> packages = new TreeMap<>();
        
        packageStructure.forEach((file, pkg) -> 
            packages.computeIfAbsent(pkg, k -> new TreeSet<>()).add(file));
        
        StringBuilder dot = new StringBuilder("digraph PackageStructure {\n");
        dot.append("  rankdir=LR;\n  node [shape=folder];\n");
        
        packages.forEach((pkg, files) -> {
            String cleanPkg = cleanNodeName(pkg.isEmpty() ? "<root>" : pkg);
            dot.append(String.format("  \"%s\" [shape=folder];\n", cleanPkg));
            
            files.forEach(file -> {
                String cleanFile = cleanNodeName(file);
                dot.append(String.format("  \"%s\" [shape=note];\n", cleanFile));
                dot.append(String.format("  \"%s\" -> \"%s\";\n", cleanPkg, cleanFile));
            });
        });
        
        dot.append("}");
        return dot.toString();
    }

    private static String cleanNodeName(String name) {
        return name.replace("\"", "\\\"")
                  .replace("\\", "\\\\");
    }
}
