/* A Bison parser, made by GNU Bison 2.3.  */

/* Skeleton interface for Bison's Yacc-like parsers in C

   Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006
   Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301, USA.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     START = 258,
     END = 259,
     DEFINE = 260,
     INTEGER_TYPE = 261,
     STRING_TYPE = 262,
     BOOL_TYPE = 263,
     READ = 264,
     DISPLAY = 265,
     IF = 266,
     ELSE = 267,
     STOP = 268,
     LOOP = 269,
     ITERATOR = 270,
     FROM = 271,
     STEP = 272,
     UNTIL = 273,
     OPEN_ROUND_BRACKET = 274,
     CLOSED_ROUND_BRACKET = 275,
     OPEN_SQUARE_BRACKET = 276,
     CLOSED_SQUARE_BRACKET = 277,
     OPEN_CURLY_BRACKET = 278,
     CLOSED_CURLY_BRACKET = 279,
     COMMA = 280,
     SEMICOLON = 281,
     PLUS = 282,
     MINUS = 283,
     SUBTRACT = 284,
     DIVIDE = 285,
     MULTIPLY = 286,
     ASSIGN = 287,
     EQUALS = 288,
     DOES_NOT_EQUAL = 289,
     GREATER = 290,
     GREATER_OR_EQUAL = 291,
     LESS = 292,
     LESS_OR_EQUAL = 293,
     PERCENT = 294,
     IDENTIFIER = 295,
     INTEGER_CONST = 296,
     BOOL_CONST = 297,
     STRING_CONST = 298,
     DIGIT = 299,
     NON_ZERO_DIGIT = 300
   };
#endif
/* Tokens.  */
#define START 258
#define END 259
#define DEFINE 260
#define INTEGER_TYPE 261
#define STRING_TYPE 262
#define BOOL_TYPE 263
#define READ 264
#define DISPLAY 265
#define IF 266
#define ELSE 267
#define STOP 268
#define LOOP 269
#define ITERATOR 270
#define FROM 271
#define STEP 272
#define UNTIL 273
#define OPEN_ROUND_BRACKET 274
#define CLOSED_ROUND_BRACKET 275
#define OPEN_SQUARE_BRACKET 276
#define CLOSED_SQUARE_BRACKET 277
#define OPEN_CURLY_BRACKET 278
#define CLOSED_CURLY_BRACKET 279
#define COMMA 280
#define SEMICOLON 281
#define PLUS 282
#define MINUS 283
#define SUBTRACT 284
#define DIVIDE 285
#define MULTIPLY 286
#define ASSIGN 287
#define EQUALS 288
#define DOES_NOT_EQUAL 289
#define GREATER 290
#define GREATER_OR_EQUAL 291
#define LESS 292
#define LESS_OR_EQUAL 293
#define PERCENT 294
#define IDENTIFIER 295
#define INTEGER_CONST 296
#define BOOL_CONST 297
#define STRING_CONST 298
#define DIGIT 299
#define NON_ZERO_DIGIT 300




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif

extern YYSTYPE yylval;

