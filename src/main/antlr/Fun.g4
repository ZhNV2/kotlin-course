grammar Fun;

file: block;

block: (statement)*;

blockWithBraces: '{' block '}';

statement
    :   function
    |   variable
    |   expression
    |   whileT
    |   ifT
    |   assignment
    |   returnT
    |   println
    ;

println: 'println' '(' arguments ')';

function: 'fun' Identifier '(' ((Identifier ',')* Identifier)? ')' blockWithBraces;

variable: 'var' Identifier ('=' expression)?;

whileT: 'while' '(' expression ')' blockWithBraces;

ifT: 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?;

assignment: Identifier '=' expression;

returnT: 'return' expression;

expression
    :   expressionInBrackets
    |   functionCall
    |   literal
    |   expression op=('*' | '/' | '%') expression
    |   expression op=('+' | '-' ) expression
    |   expression op=('>' | '<' | '<=' | '>='| '==' | '!=') expression
    |   expression op=('&&'| '||') expression
    |   var
    ;

var: Identifier;

expressionInBrackets: '(' expression ')';

functionCall: Identifier '(' arguments ')';

arguments: ((expression ',')* expression)?;

literal: Literal;

Identifier
    :   Nondigit
        (   Nondigit
        |   Digit
        )*
    ;

Nondigit:   [a-zA-Z_];

Literal: [0] | [1-9] Digit*;

Digit: [0-9];

WS : (' ' | '\t' | '\r'| '\n') -> skip;
