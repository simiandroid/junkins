package com.taigae.junkins.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodInfo {
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    private String className;
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    private List<String> parameters;
    public List<String> getParameters() {
        return parameters;
    }
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
    private Set<String> calledMethods = new HashSet<>();
    public Set<String> getCalledMethods() {
        return calledMethods;
    }
    public void setCalledMethods(Set<String> calledMethods) {
        this.calledMethods = calledMethods;
    }
    private Set<String> variablesRead = new HashSet<>();
    public Set<String> getVariablesRead() {
        return variablesRead;
    }
    public void setVariablesRead(Set<String> variablesRead) {
        this.variablesRead = variablesRead;
    }
    private Set<String> variablesWritten = new HashSet<>();
    public Set<String> getVariablesWritten() {
        return variablesWritten;
    }
    public void setVariablesWritten(Set<String> variablesWritten) {
        this.variablesWritten = variablesWritten;
    }
    private int startLine;
    public int getStartLine() {
        return startLine;
    }
    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }
    private int endLine;
    public int getEndLine() {
        return endLine;
    }
    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
    public String getFullName() {
       return getClassName().concat("/").concat(getName());
    }
    public void addVariableWritten(String varName) {
        variablesWritten.add(varName);
    }
    public void addVariableRead(String varName) {
       variablesRead.add(varName);
    }
    
}