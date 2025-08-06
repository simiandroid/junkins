package com.taigae.junkins.analyzer;

import com.taigae.junkins.parser.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class JenkinsAnalyzer extends JenkinsGroovyParserBaseVisitor<Void> {
    
    @Override
    public Void visitStepCall(JenkinsGroovyParser.StepCallContext ctx) {
        System.out.println("Step encontrado: " + ctx.getText());
        return super.visitStepCall(ctx);
    }
    
    @Override
    public Void visitMethodDeclaration(JenkinsGroovyParser.MethodDeclarationContext ctx) {
        System.out.println("Método declarado: " + ctx.ID().getText());
        return super.visitMethodDeclaration(ctx);
    }
}