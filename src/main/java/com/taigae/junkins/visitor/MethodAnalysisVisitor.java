package com.taigae.junkins.visitor;

import java.util.Stack;

import com.taigae.junkins.entity.DataFlowGraph;
import com.taigae.junkins.entity.MethodInfo;
import com.taigae.junkins.parser.JenkinsGroovyParser;
import com.taigae.junkins.parser.JenkinsGroovyParserBaseVisitor;

public class MethodAnalysisVisitor extends JenkinsGroovyParserBaseVisitor<Void> {
    private final DataFlowGraph graph = new DataFlowGraph();
    private final Stack<MethodInfo> currentMethod = new Stack<>();
    private String currentClass;
    
    @Override
    public Void visitMethodDeclaration(JenkinsGroovyParser.MethodDeclarationContext ctx) {
        MethodInfo method = new MethodInfo();
        method.setName(ctx.ID().getText());
        method.setClassName(currentClass);
        method.setStartLine(ctx.start.getLine());
        method.setEndLine(ctx.stop.getLine());
        
        currentMethod.push(method);
        graph.addMethod(ctx.ID().getText(),method);
        
        visitChildren(ctx);
        
        currentMethod.pop();
        return null;
    }
    
@Override
public Void visitMethodCall(JenkinsGroovyParser.MethodCallContext ctx) {
    if (!currentMethod.isEmpty()) {
        String caller = currentMethod.peek().getFullName();
        String callee = extractMethodName(ctx.postfixExpr());
        if (callee != null) {
            graph.addMethodCall(caller, callee);
        }
    }
    return visitChildren(ctx);
}

// Helper to extract method name from postfixExpr
private String extractMethodName(JenkinsGroovyParser.PostfixExprContext ctx) {
    if (ctx == null) return null;
    // If this is a method call like foo.bar(), get the ID
    if (ctx.ID() != null && ctx.argumentList() != null) {
        return ctx.ID().getText();
    }
    // If this is a simple call like foo(), primary will be ID
    if (ctx.primary() != null && ctx.primary().ID() != null) {
        return ctx.primary().ID().getText();
    }
    // Otherwise, recursively check the child postfixExpr
    if (ctx.postfixExpr() != null) {
        return extractMethodName(ctx.postfixExpr());
    }
    return null;
}
    
@Override
public Void visitAssignment(JenkinsGroovyParser.AssignmentContext ctx) {
    if (!currentMethod.isEmpty()) {
        // Only handle simple assignments: var = ...
        JenkinsGroovyParser.TernaryExprContext left = ctx.ternaryExpr();
        String varName = extractVariableName(left);
        if (varName != null) {
            currentMethod.peek().addVariableWritten(varName);
        }
    }
    return visitChildren(ctx);
}

// Helper method to extract variable name from the left side of assignment
private String extractVariableName(JenkinsGroovyParser.TernaryExprContext ctx) {
    // ternaryExpr -> logicalOrExpr
    JenkinsGroovyParser.LogicalOrExprContext orCtx = ctx.logicalOrExpr();
    if (orCtx != null) {
        // logicalOrExpr -> logicalAndExpr
        JenkinsGroovyParser.LogicalAndExprContext andCtx = orCtx.logicalAndExpr(0);
        if (andCtx != null) {
            // logicalAndExpr -> equalityExpr
            JenkinsGroovyParser.EqualityExprContext eqCtx = andCtx.equalityExpr(0);
            if (eqCtx != null) {
                // equalityExpr -> relationalExpr
                JenkinsGroovyParser.RelationalExprContext relCtx = eqCtx.relationalExpr(0);
                if (relCtx != null) {
                    // relationalExpr -> additiveExpr
                    JenkinsGroovyParser.AdditiveExprContext addCtx = relCtx.additiveExpr(0);
                    if (addCtx != null) {
                        // additiveExpr -> multiplicativeExpr
                        JenkinsGroovyParser.MultiplicativeExprContext mulCtx = addCtx.multiplicativeExpr(0);
                        if (mulCtx != null) {
                            // multiplicativeExpr -> unaryExpr
                            JenkinsGroovyParser.UnaryExprContext unaryCtx = mulCtx.unaryExpr(0);
                            if (unaryCtx != null) {
                                // unaryExpr -> postfixExpr
                                JenkinsGroovyParser.PostfixExprContext postCtx = unaryCtx.postfixExpr();
                                if (postCtx != null) {
                                    // postfixExpr -> primary
                                    JenkinsGroovyParser.PrimaryContext primCtx = postCtx.primary();
                                    if (primCtx != null) {
                                        if (primCtx.ID() != null) {
                                            return primCtx.ID().getText();
                                        }
                                                // Optionally handle qualifiedName or other cases here
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    
    @Override
    public Void visitVariableDeclarator(JenkinsGroovyParser.VariableDeclaratorContext ctx) {
        if (!currentMethod.isEmpty()) {
            String varName = ctx.ID().getText();
            currentMethod.peek().addVariableRead(varName);
        }
        return visitChildren(ctx);
    }

    public DataFlowGraph getGraph(){
        return graph;
    }
}