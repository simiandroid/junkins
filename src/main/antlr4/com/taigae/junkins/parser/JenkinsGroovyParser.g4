parser grammar JenkinsGroovyParser;

options { tokenVocab=JenkinsGroovyLexer; }

// ------------------------- Regras Auxiliares -------------------------
qualifiedName: ID (DOT ID)*;
typeParameters: LT typeParameter (COMMA typeParameter)* GT;
typeParameter: ID (EXTENDS typeBound)?;
typeBound: qualifiedName (AND qualifiedName)*;
type: qualifiedName typeArguments?;
typeArguments: LT typeArgument (COMMA typeArgument)* GT;
typeArgument: type | QUESTION;
typeList: type (COMMA type)*;

// Declarações de corpo
classBodyDeclaration: 
    ';' 
    | memberDeclaration
    | staticInitializer
    | instanceInitializer;

interfaceBodyDeclaration: 
    ';' 
    | interfaceMemberDeclaration;

memberDeclaration:
    methodDeclaration
    | fieldDeclaration
    | constructorDeclaration;

interfaceMemberDeclaration:
    interfaceMethodDeclaration
    | interfaceFieldDeclaration;

arguments: LPAREN (expression (COMMA expression)*)? RPAREN;
enumConstants: enumConstant (COMMA enumConstant)*;
enumConstant: ID (arguments)? (classBody)?;
enumBodyDeclarations: ';' (classBodyDeclaration)*;

// Expressões
parameterList: parameter (COMMA parameter)*;
parameter: type ID;
binaryOp: AND | OR | EQ | NEQ | LT | LE | GT | GE | PLUS | MINUS | MULT | DIV | MOD | POW;
unaryOp: PLUS | MINUS | NOT | INC | DEC;
assignmentOp: EQUALS | PLUS_EQ | MINUS_EQ | MULT_EQ | DIV_EQ | MOD_EQ | POW_EQ;
lambdaParameters: 
    ID 
    | LPAREN (parameter (COMMA parameter)*)? RPAREN;

// ------------------------- Estrutura Principal -------------------------
compilationUnit: 
    (packageDeclaration)? 
    (importDeclaration)* 
    (typeDeclaration | jenkinsPipeline)*
    EOF;

packageDeclaration: PACKAGE qualifiedName SEMI;
importDeclaration: IMPORT STATIC? qualifiedName (DOT MULT)? SEMI;

// ------------------------- Declarações de Tipo -------------------------
typeDeclaration: 
    classDeclaration 
    | interfaceDeclaration 
    | enumDeclaration;

classDeclaration: CLASS ID typeParameters? (EXTENDS type)? (IMPLEMENTS typeList)? classBody;
interfaceDeclaration: INTERFACE ID typeParameters? (EXTENDS typeList)? interfaceBody;
enumDeclaration: ENUM ID (IMPLEMENTS typeList)? enumBody;

classBody: LBRACE classBodyDeclaration* RBRACE;
interfaceBody: LBRACE interfaceBodyDeclaration* RBRACE;
enumBody: LBRACE enumConstants? COMMA? enumBodyDeclarations? RBRACE;

// ------------------------- Pipeline Jenkins -------------------------
jenkinsPipeline: 
    LIBRARY LPAREN STRING (COMMA STRING)* RPAREN SEMI?
    pipelineDeclaration;

pipelineDeclaration: PIPELINE LBRACE pipelineBody RBRACE;
pipelineBody: 
    (agentSection)?
    (optionsSection)?
    (environmentSection)?
    (parametersSection)?
    (triggersSection)?
    (toolsSection)?
    (stagesSection)
    (postSection)?;

// Seções do pipeline
agentSection: AGENT agentParameters SEMI;
optionsSection: OPTIONS LBRACE option* RBRACE;
environmentSection: ENVIRONMENT LBRACE environmentVariable* RBRACE;
parametersSection: PARAMETERS LBRACE pipelineParameter* RBRACE;
triggersSection: TRIGGERS LBRACE trigger* RBRACE;
toolsSection: TOOLS LBRACE tool* RBRACE;
stagesSection: STAGES LBRACE stage+ RBRACE;
postSection: POST LBRACE postCondition* RBRACE;

// Condições post
postCondition: 
    ALWAYS LBRACE step* RBRACE
    | SUCCESS LBRACE step* RBRACE
    | FAILURE LBRACE step* RBRACE
    | UNSTABLE LBRACE step* RBRACE
    | CHANGED LBRACE step* RBRACE;

// Estruturas do pipeline
agentParameters: expression;
option: ID EQUALS expression SEMI;
environmentVariable: ID EQUALS expression SEMI;
pipelineParameter: parameterType ID (EQUALS expression)? SEMI;
parameterType: PARAM_STRING | BOOLEAN | PARAM_CHOICE | PARAM_PASSWORD | PARAM_TEXT;
trigger: ID argumentList SEMI;
tool: ID EQUALS STRING SEMI;
stage: STAGE LPAREN STRING RPAREN LBRACE (stageBody)* RBRACE;
stageBody: 
    agentSection
    | environmentSection
    | inputSection
    | optionsSection
    | parallelSection
    | stagesSection
    | stepsSection
    | whenSection;

// Seções específicas
inputSection: INPUT LBRACE inputParameter* RBRACE;
inputParameter: ID COLON expression SEMI;
parallelSection: PARALLEL LBRACE parallelBranch+ RBRACE;
parallelBranch: ID LBRACE stageBody* RBRACE;
whenSection: WHEN LBRACE whenCondition* RBRACE;
whenCondition: 
    BRANCH LPAREN STRING RPAREN
    | EXPRESSION LPAREN expression RPAREN
    | ALL_OF LBRACE whenCondition+ RBRACE
    | ANY_OF LBRACE whenCondition+ RBRACE
    | NOT LPAREN whenCondition RPAREN;

// ------------------------- Steps e Expressões -------------------------
stepsSection: STEPS LBRACE step* RBRACE;
step: 
    stepCall SEMI
    | scriptBlock
    | dirBlock
    | withCredentialsBlock
    | withEnvBlock
    | timeoutBlock
    | retryBlock
    | sleepStep
    | lockBlock
    | wrapBlock
    | nodeBlock;

// Steps específicos
stepCall: 
    (SH | BAT | POWERSHELL | ECHO | ERROR | BUILD | MAIL | CHECKOUT | GIT) 
    argumentList;
scriptBlock: SCRIPT LBRACE (statement)* RBRACE;
dirBlock: DIR LPAREN STRING RPAREN LBRACE (statement)* RBRACE;
withCredentialsBlock: WITH_CREDS LBRACE (statement)* RBRACE;
withEnvBlock: WITH_ENV LBRACE environmentVariable* RBRACE (statement)* RBRACE;
timeoutBlock: TIMEOUT LBRACE (statement)* RBRACE;
retryBlock: RETRY LBRACE (statement)* RBRACE;
sleepStep: SLEEP LPAREN NUMBER RPAREN SEMI;
lockBlock: LOCK LPAREN STRING RPAREN LBRACE (statement)* RBRACE;
wrapBlock: WRAP LBRACE (statement)* RBRACE;
nodeBlock: NODE LPAREN STRING RPAREN LBRACE (statement)* RBRACE;

// ------------------------- Fixed Expression Hierarchy -------------------------
expression
    : assignment
    ;

assignment
    : ternaryExpr (assignmentOp assignment)?
    ;

ternaryExpr
    : logicalOrExpr (QUESTION expression COLON ternaryExpr)?
    ;

logicalOrExpr
    : logicalAndExpr (OR logicalAndExpr)*
    ;

logicalAndExpr
    : equalityExpr (AND equalityExpr)*
    ;

equalityExpr
    : relationalExpr ((EQ | NEQ | IDENTICAL | NOT_IDENTICAL) relationalExpr)*
    ;

relationalExpr
    : additiveExpr ((LT | LE | GT | GE) additiveExpr)*
    ;

additiveExpr
    : multiplicativeExpr ((PLUS | MINUS) multiplicativeExpr)*
    ;

multiplicativeExpr
    : unaryExpr ((MULT | DIV | MOD) unaryExpr)*
    ;

unaryExpr
    : (unaryOp)* postfixExpr
    ;

postfixExpr
    : primary
    | postfixExpr DOT ID argumentList
    | postfixExpr LBRACK expression RBRACK
    ;

primary
    : literal
    | ID
    | qualifiedName
    | map
    | list
    | closure
    | castExpr
    | lambdaExpr
    | newExpr
    | thisExpr
    | superExpr
    | LPAREN expression RPAREN
    ;

methodCall
    : postfixExpr
    ;


literal: STRING | NUMBER | TRUE | FALSE | NULL;
map: LBRACE (mapEntry (COMMA mapEntry)*)? RBRACE;
mapEntry: expression COLON expression;
list: LBRACK (expression (COMMA expression)*)? RBRACK;
closure: LBRACE (parameterList)? ARROW? (statement)* RBRACE;

castExpr: LPAREN type RPAREN expression;
lambdaExpr: lambdaParameters ARROW expression;
newExpr: NEW qualifiedName argumentList;
thisExpr: THIS;
superExpr: SUPER;

argumentList: LPAREN (expression (COMMA expression)*)? RPAREN;

// ------------------------- Statements -------------------------
statement: 
    expression SEMI
    | variableDecl SEMI
    | ifStmt
    | forStmt
    | whileStmt
    | tryStmt
    | switchStmt
    | synchronizedStmt
    | returnStmt
    | throwStmt
    | breakStmt
    | continueStmt
    | labeledStmt
    | assertionStmt
    | block;

variableDecl: type variableDeclarator (COMMA variableDeclarator)*;
variableDeclarator: ID (EQUALS expression)?;

ifStmt: IF LPAREN expression RPAREN statement (ELSE statement)?;
forStmt: FOR LPAREN forControl RPAREN statement;
forControl: 
    variableDecl SEMI expression SEMI expressionList
    | expressionList SEMI expression SEMI expressionList
    | expressionList;

whileStmt: WHILE LPAREN expression RPAREN statement;
tryStmt: TRY block (catchClause)* (FINALLY block)?;
catchClause: CATCH LPAREN parameter RPAREN block;
switchStmt: SWITCH LPAREN expression RPAREN LBRACE switchCase* RBRACE;
switchCase: CASE expression COLON statement*;
synchronizedStmt: SYNCHRONIZED LPAREN expression RPAREN block;
returnStmt: RETURN expression? SEMI;
throwStmt: THROW expression SEMI;
breakStmt: BREAK ID? SEMI;
continueStmt: CONTINUE ID? SEMI;
labeledStmt: ID COLON statement;
assertionStmt: ASSERT expression (COLON expression)? SEMI;
block: LBRACE statement* RBRACE;

// ------------------------- Declarações de Membros -------------------------
methodDeclaration: 
    modifiers type ID LPAREN parameterList? RPAREN (THROWS qualifiedNameList)? block;
constructorDeclaration: 
    modifiers ID LPAREN parameterList? RPAREN (THROWS qualifiedNameList)? block;
fieldDeclaration: 
    modifiers type variableDeclarator (COMMA variableDeclarator)* SEMI;

interfaceMethodDeclaration:
    modifiers type ID LPAREN parameterList? RPAREN (THROWS qualifiedNameList)? SEMI;
interfaceFieldDeclaration:
    modifiers type ID EQUALS expression SEMI;

staticInitializer: STATIC block;
instanceInitializer: block;

modifiers: (annotation | modifier)*;
modifier: 
    PUBLIC | PROTECTED | PRIVATE | STATIC | ABSTRACT | FINAL | NATIVE | SYNCHRONIZED 
    | TRANSIENT | VOLATILE | STRICTFP;
annotation: AT qualifiedName (LPAREN (annotationElement (COMMA annotationElement)*)? RPAREN)?;
annotationElement: ID EQUALS expression;

qualifiedNameList: qualifiedName (COMMA qualifiedName)*;
expressionList: expression (COMMA expression)*;