package com.taigae.junkins.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataFlowGraph {
    private Map<String, MethodInfo> methods = new HashMap<>();
    private Map<String, Set<String>> methodCalls = new HashMap<>();

    public void addMethod(String name,MethodInfo method){
        methods.put(name, method);
    }
    
    public void addMethodCall(String caller, String callee) {
        methodCalls.computeIfAbsent(caller, k -> new HashSet<>()).add(callee);
    }

    public Map<String, Set<String>> getMethodCalls() {
        return methodCalls;
    }

    public Map<String, MethodInfo> getMethods() {
       return methods;
    }
    
}