package com.taigae.junkins.reporter;

import com.taigae.junkins.entity.DataFlowGraph;

public class AnalysisReporter {
    public static String generateCallGraph(DataFlowGraph graph) {
        StringBuilder dot = new StringBuilder("digraph CallGraph {\n");
        dot.append("  node [shape=box];\n");
        
        graph.getMethodCalls().forEach((caller, callees) -> {
            dot.append(String.format("  \"%s\";\n", caller));
            callees.forEach(callee -> {
                dot.append(String.format("  \"%s\" -> \"%s\";\n", caller, callee));
            });
        });
        
        dot.append("}");
        return dot.toString();
    }
    
    public static String generateDataFlowReport(DataFlowGraph graph) {
        StringBuilder sb = new StringBuilder();
        sb.append("Data Flow Analysis Report\n");
        sb.append("========================\n\n");
        
        graph.getMethods().values().forEach(method -> {
            sb.append(String.format("Method: %s\n", method.getFullName()));
            sb.append(String.format("Parameters: %s\n", String.join(", ", method.getParameters())));
            sb.append("Variables Read: " + String.join(", ", method.getVariablesRead()) + "\n");
            sb.append("Variables Written: " + String.join(", ", method.getVariablesWritten()) + "\n");
            sb.append("Calls: " + String.join(", ", method.getCalledMethods()) + "\n\n");
        });
        
        return sb.toString();
    }
}