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

function: 'fun' Identifier '(' parameterNames ')' blockWithBraces;

parameterNames: ((Identifier ',')* Identifier)?;

variable: 'var' Identifier ('=' expression)?;

whileT: 'while' expressionInBrackets blockWithBraces;

ifT: 'if' expressionInBrackets blockWithBraces ('else' blockWithBraces)?;

assignment: Identifier '=' expression;

returnT: 'return' expression;

expression
    :   functionCall
    |   eval
    |   expressionInBrackets
    |   Identifier
    ;

expressionInBrackets
    :   '(' (   expression
            |   binaryExpression
            )
        ')'
    ;

functionCall: Identifier '(' arguments ')';

arguments: ((expression ',')* expression)?;

binaryExpression
    :   expression sign=(    '>'
                    |   '<'
                    |   '<='
                    |   '>='
                    |   '=='
                    |   '!='
                    |   '||'
                    |   '&&'
                    )
        expression
    ;


Identifier
    :   Nondigit
        (   Nondigit
        |   Digit
        )*
    ;

Nondigit:   [a-zA-Z_];

Literal: [0] | [1-9] Digit*;

Digit: [0-9];

eval: additionExp;

additionExp: multiplyExp (multiplyExpWithSign)*;

multiplyExpWithSign: plusMultiplyExp | minusMultiplyExp;
plusMultiplyExp: '+' multiplyExp;
minusMultiplyExp: '-' multiplyExp;

multiplyExp: atomExp (atomExpWithSign)*;

atomExpWithSign: mulAtomExp | divAtomExp | modAtomExp;
mulAtomExp: '*' atomExp;
divAtomExp: '/' atomExp;
modAtomExp: '%' atomExp;

atomExp
    :   Literal
    |   functionCall
    |   Identifier
    |   expressionInBrackets
    ;

WS : (' ' | '\t' | '\r'| '\n') -> skip;
