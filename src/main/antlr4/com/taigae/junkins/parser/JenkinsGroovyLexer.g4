lexer grammar JenkinsGroovyLexer;

// Palavras-chave da DSL Jenkins
PIPELINE: 'pipeline';
AGENT: 'agent';
STAGES: 'stages';
STAGE: 'stage';
STEPS: 'steps';
SCRIPT: 'script';
ENVIRONMENT: 'environment';
PARALLEL: 'parallel';
POST: 'post';
ALWAYS: 'always';
SUCCESS: 'success';
FAILURE: 'failure';
UNSTABLE: 'unstable';
CHANGED: 'changed';
OPTIONS: 'options';
PARAMETERS: 'parameters';
TRIGGERS: 'triggers';
TOOLS: 'tools';
INPUT: 'input';
WHEN: 'when';
BRANCH: 'branch';
EXPRESSION: 'expression';
ALL_OF: 'allOf';
ANY_OF: 'anyOf';
ANY: 'any';
NONE: 'none';
DOCKER: 'docker';
LABEL: 'label';
ARGS: 'args';
REUSE_NODE: 'reuseNode';
CUSTOM_WORKSPACE: 'customWorkspace';

// Palavras-chave do Groovy
PACKAGE: 'package';
IMPORT: 'import';
STATIC: 'static';
DEF: 'def';
CLASS: 'class';
INTERFACE: 'interface';
ENUM: 'enum';
EXTENDS: 'extends';
IMPLEMENTS: 'implements';
NEW: 'new';
AS: 'as';
PRIVATE: 'private';
PROTECTED: 'protected';
PUBLIC: 'public';
ABSTRACT: 'abstract';
FINAL: 'final';
VOID: 'void';
BOOLEAN: 'boolean';
BYTE: 'byte';
CHAR: 'char';
SHORT: 'short';
INT: 'int';
LONG: 'long';
FLOAT: 'float';
DOUBLE: 'double';
NULL: 'null';
THIS: 'this';
SUPER: 'super';
RETURN: 'return';
THROWS: 'throws';
THROW: 'throw';
TRY: 'try';
CATCH: 'catch';
FINALLY: 'finally';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
FOR: 'for';
IN: 'in';
SWITCH: 'switch';
CASE: 'case';
BREAK: 'break';
DEFAULT: 'default';
CONTINUE: 'continue';
INSTANCEOF: 'instanceof';
ASSERT: 'assert';
SYNCHRONIZED: 'synchronized';
TRANSIENT: 'transient';
VOLATILE: 'volatile';
NATIVE: 'native';
STRICTFP: 'strictfp';

// Shared Libraries
LIBRARY: '@Library';

// Tipos literais
STRING: '"' .*? '"' | '\'' .*? '\'' | '"""' .*? '"""' | '\'\'\'' .*? '\'\'\'';
NUMBER: [0-9]+ ('.' [0-9]+)? ([eE][+-]?[0-9]+)?;
REGEX: '/' (~[/\n\r] | '\\/')* '/' [a-z]*;

// Booleanos
TRUE: 'true';
FALSE: 'false';

// Parameter types (fixed conflict)
PARAM_STRING: 'string';
PARAM_CHOICE: 'choice';
PARAM_PASSWORD: 'password';
PARAM_TEXT: 'text';

// Identificadores
ID: [a-zA-Z_$][a-zA-Z0-9_$]*;

// Espaços em branco
WS: [ \t\r\n]+ -> skip;
COMMENT: '/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);

// Símbolos
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
LBRACK: '[';
RBRACK: ']';
DOT: '.';
COMMA: ',';
COLON: ':';
SEMI: ';';
EQUALS: '=';
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
MOD: '%';
POW: '**';
NOT: '!';
AND: '&&';
OR: '||';
QUESTION: '?';
DOLLAR: '$';
AT: '@';
HASH: '#';
ELLIPSIS: '...';
RANGE: '..';
SPREAD: '*.';
SAFE_NAVIGATION: '?.';
METHOD_REF: '::';

// Operadores (added missing ones)
PLUS_EQ: '+=';
MINUS_EQ: '-=';
MULT_EQ: '*=';
DIV_EQ: '/=';
MOD_EQ: '%=';
POW_EQ: '**=';
ARROW: '->';
INC: '++';
DEC: '--';
EQ: '==';
NEQ: '!=';
LT: '<';
LE: '<=';
GT: '>';
GE: '>=';
IDENTICAL: '===';
NOT_IDENTICAL: '!==';
REGEX_MATCH: '=~';
REGEX_FIND: '==~';
ELVIS: '?:';

// Steps do Jenkins
SH: 'sh';
BAT: 'bat';
POWERSHELL: 'powershell';
ECHO: 'echo';
ERROR: 'error';
WITH_CREDS: 'withCredentials';
WITH_ENV: 'withEnv';
TIMEOUT: 'timeout';
WAIT_UNTIL: 'waitUntil';
RETRY: 'retry';
SLEEP: 'sleep';
LOCK: 'lock';
WRAP: 'wrap';
ARCHIVE: 'archive';
UNARCHIVE: 'unarchive';
READ_FILE: 'readFile';
WRITE_FILE: 'writeFile';
FILE_EXISTS: 'fileExists';
DIR_EXISTS: 'dirExists';
DELETE_DIR: 'deleteDir';
DELETE_FILE: 'deleteFile';
MAIL: 'mail';
BUILD: 'build';
CATCH_ERROR: 'catchError';
NODE: 'node';
CHECKOUT: 'checkout';
GIT: 'git';
DIR: 'dir';
PWD: 'pwd';