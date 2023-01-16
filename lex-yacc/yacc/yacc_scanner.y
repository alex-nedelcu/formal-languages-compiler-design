%{
    /* C declarations */

    #include <stdio.h>
    #include <stdlib.h>
    #include <string.h>
    #define YYDEBUG 1

    int yylex();
    void yyerror(char *error) {
      printf("[ERROR] %s\n", error);
    }
%}

/* YACC declarations */

%token START
%token END
%token DEFINE
%token INTEGER_TYPE
%token STRING_TYPE
%token BOOL_TYPE
%token READ
%token DISPLAY
%token IF
%token ELSE
%token STOP
%token LOOP
%token ITERATOR
%token FROM
%token STEP
%token UNTIL

%token OPEN_ROUND_BRACKET
%token CLOSED_ROUND_BRACKET
%token OPEN_SQUARE_BRACKET
%token CLOSED_SQUARE_BRACKET
%token OPEN_CURLY_BRACKET
%token CLOSED_CURLY_BRACKET
%token COMMA
%token SEMICOLON

%token PLUS
%token MINUS
%token SUBTRACT
%token DIVIDE
%token MULTIPLY
%token ASSIGN
%token EQUALS
%token DOES_NOT_EQUAL
%token GREATER
%token GREATER_OR_EQUAL
%token LESS
%token LESS_OR_EQUAL
%token PERCENT

%token IDENTIFIER
%token INTEGER_CONST
%token BOOL_CONST
%token STRING_CONST
%token DIGIT
%token NON_ZERO_DIGIT

/* Operators associativity */
%left PLUS MINUS MULTIPLY SUBTRACT

%start program

%%

program: START statementList END  { printf("<program> ::= START <statementList> END\n"); }

statementList: statement                 { printf("<statementList> ::= <statement>\n"); }
               | statement statementList { printf("<statementList> ::= <statement><statementList>\n"); };

statement: simpleStatement   { printf("<statement> ::= <simpleStatement>\n"); }
           | structStatement { printf("<statement> ::= <structStatement>\n"); }

simpleStatement: defineStatement { printf("<simpleStatement> ::= <defineStatement>\n"); }
            | ioStatement        { printf("<simpleStatement> ::= <ioStatement>\n"); }
            | assignStatement    { printf("<simpleStatement> ::= <assignStatement>\n"); }

defineStatement: DEFINE type IDENTIFIER SEMICOLON { printf("<defineStatement> ::= define <type> <identifier>\n"); }

type: simpleType  { printf("<type> ::= <simpleType>\n"); }
      | arrayType { printf("<type> ::= <arrayType>\n"); }

simpleType: INTEGER_TYPE  { printf("<simpleType> ::= integer\n"); }
            | STRING_TYPE { printf("<simpleType> ::= string\n"); }
            | BOOL_TYPE   { printf("<simpleType> ::= bool\n"); }

arrayType: simpleType OPEN_SQUARE_BRACKET unsignedNumber CLOSED_SQUARE_BRACKET { printf("<arrayType> ::= <simpleType>[<unsignedNumber>]\n"); }

ioStatement: readStatement SEMICOLON    { printf("<ioStatement> ::= <readStatement>\n"); }
             | writeStatement SEMICOLON { printf("<ioStatement> ::= <writeStatement>\n"); }

readStatement: READ OPEN_ROUND_BRACKET IDENTIFIER CLOSED_ROUND_BRACKET { printf("<readStatement> ::= read(<identifier>)\n"); }

writeStatement: DISPLAY OPEN_ROUND_BRACKET displayable CLOSED_ROUND_BRACKET { printf("<writeStatement> ::= display(<displayable>)\n"); }

displayable: IDENTIFIER      { printf("<displayable> ::= <identifier>\n"); }
             | INTEGER_CONST { printf("<displayable> ::= <INTEGER>\n"); }
             | STRING_CONST  { printf("<displayable> ::= <STRING>\n"); }
             | BOOL_CONST    { printf("<displayable> ::= <BOOL>\n"); }

assignStatement: IDENTIFIER ASSIGN expression SEMICOLON { printf("<assignStatement> ::= <identifier> = <expression>\n"); }

expression: term                     { printf("<expression> ::= <term>\n"); }
            | expression PLUS term   { printf("<expression> ::= <expression> + <term>\n"); }
            | expression MINUS term  { printf("<expression> ::= <expression> - <term>\n"); }

term: factor                  { printf("<term> ::= <factor>\n"); }
      | term MULTIPLY factor  { printf("<term> ::= <term> * <factor>\n"); }
      | term DIVIDE factor    { printf("<term> ::= <term> / <factor>\n"); }

factor: OPEN_ROUND_BRACKET expression CLOSED_ROUND_BRACKET                     { printf("<factor> ::= (<expression>)\n"); }
        | IDENTIFIER                                                           { printf("<factor> ::= <identifier>\n"); }
        | INTEGER_CONST                                                        { printf("<factor> ::= <INTEGER>\n"); }
        | IDENTIFIER OPEN_SQUARE_BRACKET unsignedNumber CLOSED_SQUARE_BRACKET  { printf("<factor> ::= <identifier>[<unsignedNumber>]\n"); }

structStatement: ifStatement     { printf("<structStatement> ::= <ifStatement>\n"); }
                 | loopStatement { printf("<structStatement> ::= <loopStatement>\n"); }

ifStatement: ifBranch                                                              { printf("<ifStatement> ::= <ifBranch>\n"); }
             | ifBranch ELSE OPEN_CURLY_BRACKET statementList CLOSED_CURLY_BRACKET { printf("<ifStatement> ::= <ifBranch> else { <statementList> }\n"); }
             | ifBranch ELSE ifStatement                                           { printf("<ifStatement> ::= <ifBranch> else <ifStatement> }\n"); }

ifBranch: IF OPEN_ROUND_BRACKET condition CLOSED_ROUND_BRACKET OPEN_CURLY_BRACKET statementList CLOSED_CURLY_BRACKET { printf("<ifBranch> ::= if (<condition>) { <statementList> }\n"); }

condition: expression relation expression { printf("<expression> <RELATION> <expression>\n"); }

relation: LESS                { printf("<RELATION> ::= <\n"); }
          | LESS_OR_EQUAL     { printf("<RELATION> ::= <=\n"); }
          | GREATER           { printf("<RELATION> ::= >\n"); }
          | GREATER_OR_EQUAL  { printf("<RELATION> ::= >=\n"); }
          | EQUALS            { printf("<RELATION> ::= ==\n"); }
          | DOES_NOT_EQUAL    { printf("<RELATION> ::= !=\n"); }

loopStatement: LOOP OPEN_ROUND_BRACKET ITERATOR ASSIGN IDENTIFIER COMMA FROM ASSIGN expression COMMA STEP ASSIGN expression COMMA UNTIL ASSIGN expression CLOSED_ROUND_BRACKET OPEN_CURLY_BRACKET statementList CLOSED_CURLY_BRACKET { printf("<loopStatement> ::= loop(iterator=<identifier>, from=<expression>, step=<expression>, until=<expression>) { <statementList> }\n"); }

unsignedNumber: NON_ZERO_DIGIT                 { printf("<unsignedNumber> ::= <nonZeroDigit>\n"); }
                | NON_ZERO_DIGIT digitSequence { printf("<unsignedNumber> ::= <nonZeroDigit><digitSequence>\n"); }

digitSequence: DIGIT                 { printf("<digitSequence> ::= <digit>\n"); }
               | DIGIT digitSequence { printf("<digitSequence> ::= <digit><digitSequence>\n"); }

%%

extern FILE *yyin;

int main(int argc, char **argv) {
  if (argc > 1) {
    yyin = fopen(argv[1], "r");
  }

  if ((argc > 2) && (!strcmp(argv[2], "-d"))) {
    yydebug = 1;
  }

  if (!yyparse()) {
    fprintf(stderr,"\tOK\n");
  }
}