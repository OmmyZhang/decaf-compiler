//### This file created by BYACC 1.8(/Java extension  1.13)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//###           14 Sep 06  -- Keltin Leung-- ReduceListener support, eliminate underflow report in error recovery
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 11 "Parser.y"
package decaf.frontend;

import decaf.tree.Tree;
import decaf.tree.Tree.*;
import decaf.error.*;
import java.util.*;
//#line 25 "Parser.java"
interface ReduceListener {
  public boolean onReduce(String rule);
}




public class Parser
             extends BaseParser
             implements ReduceListener
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

ReduceListener reduceListener = null;
void yyclearin ()       {yychar = (-1);}
void yyerrok ()         {yyerrflag=0;}
void addReduceListener(ReduceListener l) {
  reduceListener = l;}


//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:SemValue
String   yytext;//user variable to return contextual strings
SemValue yyval; //used to return semantic vals from action routines
SemValue yylval;//the 'lval' (result) I got from yylex()
SemValue valstk[] = new SemValue[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new SemValue();
  yylval=new SemValue();
  valptr=-1;
}
final void val_push(SemValue val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    SemValue[] newstack = new SemValue[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final SemValue val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final SemValue val_peek(int relative)
{
  return valstk[valptr-relative];
}
//#### end semantic value section ####
public final static short VOID=257;
public final static short BOOL=258;
public final static short INT=259;
public final static short STRING=260;
public final static short COMPLEX=261;
public final static short CLASS=262;
public final static short NULL=263;
public final static short EXTENDS=264;
public final static short THIS=265;
public final static short SUPER=266;
public final static short WHILE=267;
public final static short FOR=268;
public final static short IF=269;
public final static short ELSE=270;
public final static short CASE=271;
public final static short DEFAULT=272;
public final static short RETURN=273;
public final static short BREAK=274;
public final static short NEW=275;
public final static short DO=276;
public final static short OD=277;
public final static short SEPA=278;
public final static short PRINT=279;
public final static short PRINTCOMP=280;
public final static short READ_INTEGER=281;
public final static short READ_LINE=282;
public final static short LITERAL=283;
public final static short DCOPY=284;
public final static short SCOPY=285;
public final static short IDENTIFIER=286;
public final static short AND=287;
public final static short OR=288;
public final static short STATIC=289;
public final static short INSTANCEOF=290;
public final static short LESS_EQUAL=291;
public final static short GREATER_EQUAL=292;
public final static short EQUAL=293;
public final static short NOT_EQUAL=294;
public final static short UMINUS=295;
public final static short EMPTY=296;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    4,    5,    5,    5,    5,    5,
    5,    5,    2,    6,    6,    7,    7,    7,    9,    9,
   10,   10,    8,    8,   11,   12,   12,   13,   13,   13,
   13,   13,   13,   13,   13,   13,   13,   13,   22,   23,
   23,   24,   14,   14,   14,   28,   28,   26,   26,   27,
   25,   25,   25,   25,   25,   25,   25,   25,   25,   25,
   25,   25,   25,   25,   25,   25,   25,   25,   25,   25,
   25,   25,   25,   25,   25,   25,   25,   25,   25,   25,
   25,   25,   25,   31,   31,   33,   32,   30,   30,   29,
   29,   34,   34,   16,   17,   21,   15,   35,   35,   18,
   18,   19,   20,
};
final static short yylen[] = {                            2,
    1,    2,    1,    2,    2,    1,    1,    1,    1,    1,
    2,    3,    6,    2,    0,    2,    2,    0,    1,    0,
    3,    1,    7,    6,    3,    2,    0,    1,    2,    1,
    1,    1,    2,    2,    2,    2,    1,    2,    3,    3,
    1,    3,    3,    1,    0,    2,    0,    2,    4,    5,
    1,    1,    1,    3,    3,    3,    3,    3,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    2,    2,    2,
    2,    2,    3,    3,    1,    1,    4,    5,    6,    5,
    8,    4,    4,    2,    0,    4,    4,    1,    1,    1,
    0,    3,    1,    5,    9,    1,    6,    2,    0,    2,
    1,    4,    4,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    3,    0,    2,    0,    0,   14,   18,
    0,    7,    8,    6,    9,   10,    0,    0,   13,   16,
    0,    0,   17,   11,    0,    4,    0,    0,    0,    0,
   12,    0,   22,    0,    0,    0,    0,    5,    0,    0,
    0,   27,   24,   21,   23,    0,   89,   75,   76,    0,
    0,    0,    0,    0,   96,    0,    0,    0,    0,    0,
    0,   88,    0,    0,    0,    0,    0,    0,   25,    0,
    0,    0,   28,   37,   26,    0,   30,   31,   32,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   53,    0,
    0,    0,    0,    0,   51,   52,    0,    0,    0,   41,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   70,   71,   72,   29,   33,   34,   35,   36,
   38,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   46,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   39,    0,    0,    0,    0,    0,
   73,   74,    0,    0,    0,    0,   67,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   77,    0,   40,
   42,    0,  102,  103,   82,   83,    0,    0,   49,    0,
    0,   94,    0,    0,   85,   78,    0,    0,   80,   50,
    0,    0,   97,    0,   79,    0,   98,    0,    0,    0,
   84,    0,    0,    0,   81,   95,    0,    0,   87,   86,
};
final static short yydgoto[] = {                          2,
    3,    4,   73,   21,   34,    8,   11,   23,   35,   36,
   74,   46,   75,   76,   77,   78,   79,   80,   81,   82,
   83,   84,   99,  100,   85,   95,   96,   88,  190,   89,
  204,  210,  211,  149,  203,
};
final static short yysindex[] = {                      -257,
 -277,    0, -257,    0, -246,    0, -259,  -90,    0,    0,
  -71,    0,    0,    0,    0,    0, -241,  -45,    0,    0,
  -10,  -87,    0,    0,  -83,    0,   13,  -36,   21,  -45,
    0,  -45,    0,  -78,   33,   29,   40,    0,  -47,  -45,
  -47,    0,    0,    0,    0,    1,    0,    0,    0,   69,
   73,   74,   77,  309,    0, -130,  309,   78,   79,   81,
   82,    0,   83,   85,   98,  309,  309,   75,    0,  309,
  309,  309,    0,    0,    0,   23,    0,    0,    0,   34,
   47,   84,   88,   89,  862,   93,    0, -144,    0,  309,
  309,  309,  309,  862,    0,    0,  100,   70, -238,    0,
  332,  309,  309,  103,  116,  309,  309,  309,  -32,  -32,
 -126,  463,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  309,  309,  309,  309,  309,  309,  309,  309,  309,
  309,  309,  309,  309,    0,  309,  309,  123,  490,  105,
  516,  567,  128,  101,    0,  309,   35,  862,  -25,   14,
    0,    0,  591,  698,  725,  130,    0,  915,  904,    5,
    5,  936,  936,  -22,  -22,  -32,  -32,  -32,    5,    5,
  751,  862,  309,   35,  309,   35,   49,    0,  775,    0,
    0,  309,    0,    0,    0,    0, -113,  309,    0,  133,
  134,    0,  786,  -86,    0,    0,  862,  151,    0,    0,
  309,   35,    0, -240,    0,  152,    0,  137,  149,   97,
    0,   35,  309,  309,    0,    0,  827,  851,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,  209,    0,   87,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  178,    0,    0,  198,
    0,  198,    0,    0,    0,  232,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -21,    0,    0,    0,    0,
    0,    0,    0,  -15,    0,    0,   -8,    0,    0,    0,
    0,    0,    0,    0,    0,   -8,   -8,   -8,    0,   -8,
   -8,   -8,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  883,  437,    0,    0,   -8,
  -21,   -8,   -8,  220,    0,    0,    0,    0,    0,    0,
    0,   -8,   -8,    0,    0,   -8,   -8,   -8,  108,  138,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   -8,   -8,   -8,   -8,   -8,   -8,   -8,   -8,   -8,
   -8,   -8,   -8,   -8,    0,   -8,   -8,   42,    0,    0,
    0,    0,    0,   -8,    0,   -8,  -21,   53,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  118,   19,  644,
  875,  369,  538,  161,  964,  375,  399,  428,  943,  956,
    0,  -24,  -31,  -21,   -8,  -21,    0,    0,    0,    0,
    0,   -8,    0,    0,    0,    0,    0,   -8,    0,    0,
  246,    0,    0,  -33,    0,    0,   54,    0,    0,    0,
  -30,  -21,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -21,   -8,   -8,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  285,  278,   32,   11,    0,    0,    0,  258,    0,
   66,    0, -146,  -85,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  153, 1205,  317,  365,    0,    0,  109,
    0,    0,    0,  -82,    0,
};
final static int YYTABLESIZE=1419;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         99,
  181,   99,   99,   28,    1,  140,   99,   28,    5,   91,
   45,   99,   28,  135,  132,  183,   43,    7,  182,  130,
  150,   22,   47,  135,  131,   99,    9,  192,   25,  194,
   99,  208,   10,   67,   43,   72,   71,   45,  145,  146,
   68,  132,   62,  101,   24,   66,  130,  128,   26,  129,
  135,  131,   30,   19,  184,  207,   31,  182,  136,   66,
   32,   33,   66,   33,   70,  216,   98,   67,  136,   72,
   71,   44,   40,   39,   68,   42,   66,   66,   48,   66,
   41,  116,   48,   48,   48,   48,   48,   48,   48,   99,
  191,   99,  117,   93,   92,  136,   93,   92,   70,   48,
   48,   48,   48,   48,   43,  118,   45,   67,   90,   72,
   71,   66,   91,   92,   68,  206,   93,  102,  103,   66,
  104,  105,  106,   42,  107,   69,   12,   13,   14,   15,
   16,   17,   48,   67,   48,   72,   71,  108,   70,  143,
   68,  138,  119,  151,   68,   66,  120,  121,   68,   68,
   68,   68,   68,  137,   68,   97,  152,   42,   65,  156,
  144,   65,  173,  175,   70,   68,   68,   68,  178,   68,
  188,  195,  198,  200,   69,   65,   65,  182,   69,   69,
   69,   69,   69,  202,   69,   12,   13,   14,   15,   16,
   17,  205,  212,   31,  213,   69,   69,   69,   27,   69,
   68,   54,   29,   54,   54,   54,  214,   38,    1,   15,
   65,   12,   13,   14,   15,   16,   17,   18,   54,   54,
   54,  215,   54,   99,   99,   99,   99,   99,   99,   99,
   69,   99,   99,   99,   99,   99,    5,   99,   20,   99,
   99,   99,   99,   99,   99,   99,   99,   99,   99,   99,
   99,   99,   99,   54,   47,   47,   99,   12,   13,   14,
   15,   16,   17,   47,   47,   48,   49,   50,   51,   52,
   47,   53,   19,   54,   55,   56,   57,   47,  100,   58,
   59,   60,   61,   62,   63,   64,   90,    6,   20,   37,
   65,   12,   13,   14,   15,   16,   17,   47,  180,   48,
   49,   50,   51,   52,    0,   53,   66,   54,   55,   56,
   57,    0,  209,   58,   59,   60,   61,   62,   63,   64,
    0,    0,    0,    0,   65,    0,    0,    0,   48,   48,
    0,    0,   48,   48,   48,   48,  111,   47,    0,   48,
   49,   67,    0,   72,   71,   53,    0,    0,   68,   56,
    0,    0,    0,   66,    0,   60,   61,   62,   63,   64,
    0,    0,   86,   47,   65,   48,   49,    0,  132,    0,
    0,   53,   70,  130,  128,   56,  129,  135,  131,    0,
    0,   60,   61,   62,   63,   64,    0,    0,    0,  147,
   65,  134,    0,  133,   68,   68,    0,    0,   68,   68,
   68,   68,    0,    0,   65,   65,    0,   86,    0,   59,
   87,   56,   59,    0,    0,   56,   56,   56,   56,   56,
    0,   56,  136,    0,   69,   69,   59,   59,   69,   69,
   69,   69,   56,   56,   56,   57,   56,    0,    0,   57,
   57,   57,   57,   57,    0,   57,    0,   54,   54,    0,
    0,   54,   54,   54,   54,   87,   57,   57,   57,    0,
   57,   59,    0,   86,   58,    0,    0,   56,   58,   58,
   58,   58,   58,   52,   58,    0,    0,   44,   52,   52,
    0,   52,   52,   52,    0,   58,   58,   58,    0,   58,
   86,   57,   86,    0,    0,   44,   52,    0,   52,  132,
    0,    0,    0,  157,  130,  128,    0,  129,  135,  131,
    0,   87,    0,    0,    0,    0,    0,   86,   86,    0,
   58,    0,  134,    0,  133,    0,  132,   52,   86,    0,
  174,  130,  128,    0,  129,  135,  131,    0,   87,    0,
   87,    0,    0,    0,    0,    0,    0,    0,    0,  134,
    0,  133,  132,  136,    0,    0,  176,  130,  128,    0,
  129,  135,  131,    0,    0,   87,   87,    0,    0,    0,
    0,   47,    0,   48,   49,  134,   87,  133,   60,   53,
  136,   60,    0,   56,    0,    0,    0,    0,    0,   60,
   61,   62,   63,   64,    0,   60,   60,    0,   65,    0,
    0,    0,    0,  132,    0,    0,  136,  177,  130,  128,
    0,  129,  135,  131,    0,    0,    0,    0,  122,  123,
    0,    0,  124,  125,  126,  127,  134,  132,  133,    0,
   60,  185,  130,  128,    0,  129,  135,  131,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  134,    0,  133,    0,    0,   59,   59,  136,    0,    0,
    0,   56,   56,    0,    0,   56,   56,   56,   56,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  136,    0,    0,   63,   57,   57,   63,    0,   57,
   57,   57,   57,    0,    0,    0,    0,    0,    0,    0,
    0,   63,   63,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   58,   58,    0,    0,   58,   58,
   58,   58,    0,   52,   52,    0,    0,   52,   52,   52,
   52,    0,    0,    0,  132,    0,   63,    0,  186,  130,
  128,    0,  129,  135,  131,    0,    0,    0,    0,  122,
  123,    0,    0,  124,  125,  126,  127,  134,    0,  133,
    0,  132,    0,    0,    0,    0,  130,  128,  187,  129,
  135,  131,    0,    0,    0,    0,  122,  123,    0,    0,
  124,  125,  126,  127,  134,    0,  133,  132,  136,    0,
    0,    0,  130,  128,    0,  129,  135,  131,    0,    0,
    0,    0,  122,  123,    0,    0,  124,  125,  126,  127,
  134,  132,  133,    0,    0,  136,  130,  128,    0,  129,
  135,  131,  132,    0,   60,   60,    0,  130,  128,    0,
  129,  135,  131,    0,  134,    0,  133,    0,    0,    0,
    0,  136,    0,  189,  201,  134,    0,  133,    0,    0,
    0,    0,    0,  122,  123,    0,    0,  124,  125,  126,
  127,    0,    0,  132,    0,  136,    0,  196,  130,  128,
    0,  129,  135,  131,    0,    0,  136,  122,  123,    0,
    0,  124,  125,  126,  127,  219,  134,  132,  133,    0,
    0,    0,  130,  128,    0,  129,  135,  131,  132,    0,
    0,    0,    0,  130,  128,    0,  129,  135,  131,  220,
  134,    0,  133,    0,    0,   64,    0,  136,   64,   51,
    0,  134,    0,  133,   51,   51,    0,   51,   51,   51,
   63,   63,   64,   64,    0,    0,   63,   63,    0,    0,
  132,  136,   51,    0,   51,  130,  128,    0,  129,  135,
  131,  132,  136,    0,    0,    0,  130,  128,    0,  129,
  135,  131,    0,  134,    0,  133,    0,   64,    0,    0,
    0,    0,  132,   51,  134,    0,  133,  130,  128,    0,
  129,  135,  131,   62,  122,  123,   62,    0,  124,  125,
  126,  127,    0,    0,  136,  134,   61,  133,    0,   61,
   62,   62,    0,    0,   55,  136,   55,   55,   55,    0,
    0,  122,  123,   61,   61,  124,  125,  126,  127,    0,
    0,   55,   55,   55,    0,   55,  136,    0,    0,    0,
    0,    0,    0,    0,    0,   62,    0,  122,  123,    0,
    0,  124,  125,  126,  127,    0,    0,    0,   61,    0,
    0,    0,    0,    0,    0,    0,   55,    0,    0,    0,
    0,  122,  123,    0,    0,  124,  125,  126,  127,    0,
    0,    0,  122,  123,    0,    0,  124,  125,  126,  127,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  122,  123,    0,    0,  124,  125,  126,
  127,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  122,  123,    0,
    0,  124,  125,  126,  127,    0,    0,    0,  122,  123,
    0,    0,  124,  125,  126,  127,    0,    0,    0,    0,
    0,   64,   64,    0,    0,    0,    0,   64,   64,   51,
   51,    0,    0,   51,   51,   51,   51,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  122,    0,    0,    0,  124,  125,  126,  127,    0,    0,
    0,    0,    0,    0,    0,  124,  125,  126,  127,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  124,  125,    0,   62,
   62,    0,    0,    0,    0,   62,   62,    0,    0,    0,
    0,    0,   61,   61,    0,    0,    0,    0,   61,   61,
   55,   55,    0,    0,   55,   55,   55,   55,   94,    0,
    0,  101,    0,    0,    0,    0,    0,    0,    0,    0,
  109,  110,  112,    0,  113,  114,  115,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  139,    0,  141,  142,    0,    0,
    0,    0,    0,    0,    0,    0,  148,  148,    0,    0,
  153,  154,  155,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  158,  159,  160,  161,
  162,  163,  164,  165,  166,  167,  168,  169,  170,    0,
  171,  172,    0,    0,    0,    0,    0,    0,  179,    0,
  101,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  148,    0,  193,
    0,    0,    0,    0,    0,    0,  197,    0,    0,    0,
    0,    0,  199,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  217,  218,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
  147,   35,   36,   91,  262,   91,   40,   91,  286,   41,
   41,   45,   91,   46,   37,   41,   41,  264,   44,   42,
  103,   11,  263,   46,   47,   59,  286,  174,   18,  176,
   64,  272,  123,   33,   59,   35,   36,   59,  277,  278,
   40,   37,  283,   59,  286,   45,   42,   43,   59,   45,
   46,   47,   40,  125,   41,  202,   93,   44,   91,   41,
   40,   30,   44,   32,   64,  212,   56,   33,   91,   35,
   36,   40,   44,   41,   40,  123,   58,   59,   37,   45,
   41,   59,   41,   42,   43,   44,   45,   46,   47,  123,
  173,  125,   59,   41,   41,   91,   44,   44,   64,   58,
   59,   60,   61,   62,   39,   59,   41,   33,   40,   35,
   36,   93,   40,   40,   40,  201,   40,   40,   40,   45,
   40,   40,   40,  123,   40,  125,  257,  258,  259,  260,
  261,  262,   91,   33,   93,   35,   36,   40,   64,   40,
   40,  286,   59,   41,   37,   45,   59,   59,   41,   42,
   43,   44,   45,   61,   47,  286,   41,  123,   41,  286,
   91,   44,   40,   59,   64,   58,   59,   60,   41,   62,
   41,  123,  286,   41,   37,   58,   59,   44,   41,   42,
   43,   44,   45,  270,   47,  257,  258,  259,  260,  261,
  262,   41,   41,   93,   58,   58,   59,   60,  286,   62,
   93,   41,  286,   43,   44,   45,   58,  286,    0,  123,
   93,  257,  258,  259,  260,  261,  262,  289,   58,   59,
   60,  125,   62,  257,  258,  259,  260,  261,  262,  263,
   93,  265,  266,  267,  268,  269,   59,  271,   41,  273,
  274,  275,  276,  277,  278,  279,  280,  281,  282,  283,
  284,  285,  286,   93,  286,  286,  290,  257,  258,  259,
  260,  261,  262,  263,  286,  265,  266,  267,  268,  269,
  286,  271,   41,  273,  274,  275,  276,  286,   59,  279,
  280,  281,  282,  283,  284,  285,   41,    3,   11,   32,
  290,  257,  258,  259,  260,  261,  262,  263,  146,  265,
  266,  267,  268,  269,   -1,  271,  288,  273,  274,  275,
  276,   -1,  204,  279,  280,  281,  282,  283,  284,  285,
   -1,   -1,   -1,   -1,  290,   -1,   -1,   -1,  287,  288,
   -1,   -1,  291,  292,  293,  294,  262,  263,   -1,  265,
  266,   33,   -1,   35,   36,  271,   -1,   -1,   40,  275,
   -1,   -1,   -1,   45,   -1,  281,  282,  283,  284,  285,
   -1,   -1,   46,  263,  290,  265,  266,   -1,   37,   -1,
   -1,  271,   64,   42,   43,  275,   45,   46,   47,   -1,
   -1,  281,  282,  283,  284,  285,   -1,   -1,   -1,   58,
  290,   60,   -1,   62,  287,  288,   -1,   -1,  291,  292,
  293,  294,   -1,   -1,  287,  288,   -1,   91,   -1,   41,
   46,   37,   44,   -1,   -1,   41,   42,   43,   44,   45,
   -1,   47,   91,   -1,  287,  288,   58,   59,  291,  292,
  293,  294,   58,   59,   60,   37,   62,   -1,   -1,   41,
   42,   43,   44,   45,   -1,   47,   -1,  287,  288,   -1,
   -1,  291,  292,  293,  294,   91,   58,   59,   60,   -1,
   62,   93,   -1,  147,   37,   -1,   -1,   93,   41,   42,
   43,   44,   45,   37,   47,   -1,   -1,   41,   42,   43,
   -1,   45,   46,   47,   -1,   58,   59,   60,   -1,   62,
  174,   93,  176,   -1,   -1,   59,   60,   -1,   62,   37,
   -1,   -1,   -1,   41,   42,   43,   -1,   45,   46,   47,
   -1,  147,   -1,   -1,   -1,   -1,   -1,  201,  202,   -1,
   93,   -1,   60,   -1,   62,   -1,   37,   91,  212,   -1,
   41,   42,   43,   -1,   45,   46,   47,   -1,  174,   -1,
  176,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   60,
   -1,   62,   37,   91,   -1,   -1,   41,   42,   43,   -1,
   45,   46,   47,   -1,   -1,  201,  202,   -1,   -1,   -1,
   -1,  263,   -1,  265,  266,   60,  212,   62,   41,  271,
   91,   44,   -1,  275,   -1,   -1,   -1,   -1,   -1,  281,
  282,  283,  284,  285,   -1,   58,   59,   -1,  290,   -1,
   -1,   -1,   -1,   37,   -1,   -1,   91,   41,   42,   43,
   -1,   45,   46,   47,   -1,   -1,   -1,   -1,  287,  288,
   -1,   -1,  291,  292,  293,  294,   60,   37,   62,   -1,
   93,   41,   42,   43,   -1,   45,   46,   47,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   60,   -1,   62,   -1,   -1,  287,  288,   91,   -1,   -1,
   -1,  287,  288,   -1,   -1,  291,  292,  293,  294,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   91,   -1,   -1,   41,  287,  288,   44,   -1,  291,
  292,  293,  294,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   58,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  287,  288,   -1,   -1,  291,  292,
  293,  294,   -1,  287,  288,   -1,   -1,  291,  292,  293,
  294,   -1,   -1,   -1,   37,   -1,   93,   -1,   41,   42,
   43,   -1,   45,   46,   47,   -1,   -1,   -1,   -1,  287,
  288,   -1,   -1,  291,  292,  293,  294,   60,   -1,   62,
   -1,   37,   -1,   -1,   -1,   -1,   42,   43,   44,   45,
   46,   47,   -1,   -1,   -1,   -1,  287,  288,   -1,   -1,
  291,  292,  293,  294,   60,   -1,   62,   37,   91,   -1,
   -1,   -1,   42,   43,   -1,   45,   46,   47,   -1,   -1,
   -1,   -1,  287,  288,   -1,   -1,  291,  292,  293,  294,
   60,   37,   62,   -1,   -1,   91,   42,   43,   -1,   45,
   46,   47,   37,   -1,  287,  288,   -1,   42,   43,   -1,
   45,   46,   47,   -1,   60,   -1,   62,   -1,   -1,   -1,
   -1,   91,   -1,   93,   59,   60,   -1,   62,   -1,   -1,
   -1,   -1,   -1,  287,  288,   -1,   -1,  291,  292,  293,
  294,   -1,   -1,   37,   -1,   91,   -1,   93,   42,   43,
   -1,   45,   46,   47,   -1,   -1,   91,  287,  288,   -1,
   -1,  291,  292,  293,  294,   59,   60,   37,   62,   -1,
   -1,   -1,   42,   43,   -1,   45,   46,   47,   37,   -1,
   -1,   -1,   -1,   42,   43,   -1,   45,   46,   47,   59,
   60,   -1,   62,   -1,   -1,   41,   -1,   91,   44,   37,
   -1,   60,   -1,   62,   42,   43,   -1,   45,   46,   47,
  287,  288,   58,   59,   -1,   -1,  293,  294,   -1,   -1,
   37,   91,   60,   -1,   62,   42,   43,   -1,   45,   46,
   47,   37,   91,   -1,   -1,   -1,   42,   43,   -1,   45,
   46,   47,   -1,   60,   -1,   62,   -1,   93,   -1,   -1,
   -1,   -1,   37,   91,   60,   -1,   62,   42,   43,   -1,
   45,   46,   47,   41,  287,  288,   44,   -1,  291,  292,
  293,  294,   -1,   -1,   91,   60,   41,   62,   -1,   44,
   58,   59,   -1,   -1,   41,   91,   43,   44,   45,   -1,
   -1,  287,  288,   58,   59,  291,  292,  293,  294,   -1,
   -1,   58,   59,   60,   -1,   62,   91,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   93,   -1,  287,  288,   -1,
   -1,  291,  292,  293,  294,   -1,   -1,   -1,   93,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   93,   -1,   -1,   -1,
   -1,  287,  288,   -1,   -1,  291,  292,  293,  294,   -1,
   -1,   -1,  287,  288,   -1,   -1,  291,  292,  293,  294,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  287,  288,   -1,   -1,  291,  292,  293,
  294,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  287,  288,   -1,
   -1,  291,  292,  293,  294,   -1,   -1,   -1,  287,  288,
   -1,   -1,  291,  292,  293,  294,   -1,   -1,   -1,   -1,
   -1,  287,  288,   -1,   -1,   -1,   -1,  293,  294,  287,
  288,   -1,   -1,  291,  292,  293,  294,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  287,   -1,   -1,   -1,  291,  292,  293,  294,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  291,  292,  293,  294,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  291,  292,   -1,  287,
  288,   -1,   -1,   -1,   -1,  293,  294,   -1,   -1,   -1,
   -1,   -1,  287,  288,   -1,   -1,   -1,   -1,  293,  294,
  287,  288,   -1,   -1,  291,  292,  293,  294,   54,   -1,
   -1,   57,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   66,   67,   68,   -1,   70,   71,   72,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   90,   -1,   92,   93,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  102,  103,   -1,   -1,
  106,  107,  108,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  122,  123,  124,  125,
  126,  127,  128,  129,  130,  131,  132,  133,  134,   -1,
  136,  137,   -1,   -1,   -1,   -1,   -1,   -1,  144,   -1,
  146,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  173,   -1,  175,
   -1,   -1,   -1,   -1,   -1,   -1,  182,   -1,   -1,   -1,
   -1,   -1,  188,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  213,  214,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=296;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,"'#'","'$'","'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'","'='","'>'",null,"'@'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"VOID","BOOL","INT","STRING",
"COMPLEX","CLASS","NULL","EXTENDS","THIS","SUPER","WHILE","FOR","IF","ELSE",
"CASE","DEFAULT","RETURN","BREAK","NEW","DO","OD","SEPA","PRINT","PRINTCOMP",
"READ_INTEGER","READ_LINE","LITERAL","DCOPY","SCOPY","IDENTIFIER","AND","OR",
"STATIC","INSTANCEOF","LESS_EQUAL","GREATER_EQUAL","EQUAL","NOT_EQUAL","UMINUS",
"EMPTY",
};
final static String yyrule[] = {
"$accept : Program",
"Program : ClassList",
"ClassList : ClassList ClassDef",
"ClassList : ClassDef",
"VariableDef : Variable ';'",
"Variable : Type IDENTIFIER",
"Type : INT",
"Type : VOID",
"Type : BOOL",
"Type : STRING",
"Type : COMPLEX",
"Type : CLASS IDENTIFIER",
"Type : Type '[' ']'",
"ClassDef : CLASS IDENTIFIER ExtendsClause '{' FieldList '}'",
"ExtendsClause : EXTENDS IDENTIFIER",
"ExtendsClause :",
"FieldList : FieldList VariableDef",
"FieldList : FieldList FunctionDef",
"FieldList :",
"Formals : VariableList",
"Formals :",
"VariableList : VariableList ',' Variable",
"VariableList : Variable",
"FunctionDef : STATIC Type IDENTIFIER '(' Formals ')' StmtBlock",
"FunctionDef : Type IDENTIFIER '(' Formals ')' StmtBlock",
"StmtBlock : '{' StmtList '}'",
"StmtList : StmtList Stmt",
"StmtList :",
"Stmt : VariableDef",
"Stmt : SimpleStmt ';'",
"Stmt : IfStmt",
"Stmt : WhileStmt",
"Stmt : ForStmt",
"Stmt : ReturnStmt ';'",
"Stmt : PrintStmt ';'",
"Stmt : PrintCompStmt ';'",
"Stmt : BreakStmt ';'",
"Stmt : StmtBlock",
"Stmt : DoStmt ';'",
"DoStmt : DO DoBranchList OD",
"DoBranchList : DoBranchList SEPA DoBranch",
"DoBranchList : DoBranch",
"DoBranch : Expr ':' Stmt",
"SimpleStmt : LValue '=' Expr",
"SimpleStmt : Call",
"SimpleStmt :",
"Receiver : Expr '.'",
"Receiver :",
"LValue : Receiver IDENTIFIER",
"LValue : Expr '[' Expr ']'",
"Call : Receiver IDENTIFIER '(' Actuals ')'",
"Expr : LValue",
"Expr : Call",
"Expr : Constant",
"Expr : Expr '+' Expr",
"Expr : Expr '-' Expr",
"Expr : Expr '*' Expr",
"Expr : Expr '/' Expr",
"Expr : Expr '%' Expr",
"Expr : Expr EQUAL Expr",
"Expr : Expr NOT_EQUAL Expr",
"Expr : Expr '<' Expr",
"Expr : Expr '>' Expr",
"Expr : Expr LESS_EQUAL Expr",
"Expr : Expr GREATER_EQUAL Expr",
"Expr : Expr AND Expr",
"Expr : Expr OR Expr",
"Expr : '(' Expr ')'",
"Expr : '-' Expr",
"Expr : '!' Expr",
"Expr : '@' Expr",
"Expr : '$' Expr",
"Expr : '#' Expr",
"Expr : READ_INTEGER '(' ')'",
"Expr : READ_LINE '(' ')'",
"Expr : THIS",
"Expr : SUPER",
"Expr : NEW IDENTIFIER '(' ')'",
"Expr : NEW Type '[' Expr ']'",
"Expr : INSTANCEOF '(' Expr ',' IDENTIFIER ')'",
"Expr : '(' CLASS IDENTIFIER ')' Expr",
"Expr : CASE '(' Expr ')' '{' CaseExprList DefaultExpr '}'",
"Expr : DCOPY '(' Expr ')'",
"Expr : SCOPY '(' Expr ')'",
"CaseExprList : CaseExprList ACaseExpr",
"CaseExprList :",
"ACaseExpr : Constant ':' Expr ';'",
"DefaultExpr : DEFAULT ':' Expr ';'",
"Constant : LITERAL",
"Constant : NULL",
"Actuals : ExprList",
"Actuals :",
"ExprList : ExprList ',' Expr",
"ExprList : Expr",
"WhileStmt : WHILE '(' Expr ')' Stmt",
"ForStmt : FOR '(' SimpleStmt ';' Expr ';' SimpleStmt ')' Stmt",
"BreakStmt : BREAK",
"IfStmt : IF '(' Expr ')' Stmt ElseClause",
"ElseClause : ELSE Stmt",
"ElseClause :",
"ReturnStmt : RETURN Expr",
"ReturnStmt : RETURN",
"PrintStmt : PRINT '(' ExprList ')'",
"PrintCompStmt : PRINTCOMP '(' ExprList ')'",
};

//#line 513 "Parser.y"
    
	/**
	 * 打印当前归约所用的语法规则<br>
	 * 请勿修改。
	 */
    public boolean onReduce(String rule) {
		if (rule.startsWith("$$"))
			return false;
		else
			rule = rule.replaceAll(" \\$\\$\\d+", "");

   	    if (rule.endsWith(":"))
    	    System.out.println(rule + " <empty>");
   	    else
			System.out.println(rule);
		return false;
    }
    
    public void diagnose() {
		addReduceListener(this);
		yyparse();
	}
//#line 708 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    //if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      //if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        //if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        //if (yychar < 0)    //it it didn't work/error
        //  {
        //  yychar = 0;      //change it to default string (no -1!)
          //if (yydebug)
          //  yylexdebug(yystate,yychar);
        //  }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        //if (yydebug)
          //debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      //if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0 || valptr<0)   //check for under & overflow here
            {
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            //if (yydebug)
              //debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            //if (yydebug)
              //debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0 || valptr<0)   //check for under & overflow here
              {
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        //if (yydebug)
          //{
          //yys = null;
          //if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          //if (yys == null) yys = "illegal-symbol";
          //debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          //}
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    //if (yydebug)
      //debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    if (reduceListener == null || reduceListener.onReduce(yyrule[yyn])) // if intercepted!
      switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 57 "Parser.y"
{
						tree = new Tree.TopLevel(val_peek(0).clist, val_peek(0).loc);
					}
break;
case 2:
//#line 63 "Parser.y"
{
						yyval.clist.add(val_peek(0).cdef);
					}
break;
case 3:
//#line 67 "Parser.y"
{
                		yyval.clist = new ArrayList<Tree.ClassDef>();
                		yyval.clist.add(val_peek(0).cdef);
                	}
break;
case 5:
//#line 77 "Parser.y"
{
						yyval.vdef = new Tree.VarDef(val_peek(0).ident, val_peek(1).type, val_peek(0).loc);
					}
break;
case 6:
//#line 83 "Parser.y"
{
						yyval.type = new Tree.TypeIdent(Tree.INT, val_peek(0).loc);
					}
break;
case 7:
//#line 87 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.VOID, val_peek(0).loc);
                	}
break;
case 8:
//#line 91 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.BOOL, val_peek(0).loc);
                	}
break;
case 9:
//#line 95 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.STRING, val_peek(0).loc);
                	}
break;
case 10:
//#line 99 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.COMPLEX, val_peek(0).loc);
                	}
break;
case 11:
//#line 103 "Parser.y"
{
                		yyval.type = new Tree.TypeClass(val_peek(0).ident, val_peek(1).loc);
                	}
break;
case 12:
//#line 107 "Parser.y"
{
                		yyval.type = new Tree.TypeArray(val_peek(2).type, val_peek(2).loc);
                	}
break;
case 13:
//#line 113 "Parser.y"
{
						yyval.cdef = new Tree.ClassDef(val_peek(4).ident, val_peek(3).ident, val_peek(1).flist, val_peek(5).loc);
					}
break;
case 14:
//#line 119 "Parser.y"
{
						yyval.ident = val_peek(0).ident;
					}
break;
case 15:
//#line 123 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 16:
//#line 129 "Parser.y"
{
						yyval.flist.add(val_peek(0).vdef);
					}
break;
case 17:
//#line 133 "Parser.y"
{
						yyval.flist.add(val_peek(0).fdef);
					}
break;
case 18:
//#line 137 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.flist = new ArrayList<Tree>();
                	}
break;
case 20:
//#line 145 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.vlist = new ArrayList<Tree.VarDef>(); 
                	}
break;
case 21:
//#line 152 "Parser.y"
{
						yyval.vlist.add(val_peek(0).vdef);
					}
break;
case 22:
//#line 156 "Parser.y"
{
                		yyval.vlist = new ArrayList<Tree.VarDef>();
						yyval.vlist.add(val_peek(0).vdef);
                	}
break;
case 23:
//#line 163 "Parser.y"
{
						yyval.fdef = new MethodDef(true, val_peek(4).ident, val_peek(5).type, val_peek(2).vlist, (Block) val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 24:
//#line 167 "Parser.y"
{
						yyval.fdef = new MethodDef(false, val_peek(4).ident, val_peek(5).type, val_peek(2).vlist, (Block) val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 25:
//#line 173 "Parser.y"
{
						yyval.stmt = new Block(val_peek(1).slist, val_peek(2).loc);
					}
break;
case 26:
//#line 179 "Parser.y"
{
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 27:
//#line 183 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.slist = new ArrayList<Tree>();
                	}
break;
case 28:
//#line 190 "Parser.y"
{
						yyval.stmt = val_peek(0).vdef;
					}
break;
case 29:
//#line 195 "Parser.y"
{
                		if (yyval.stmt == null) {
                			yyval.stmt = new Tree.Skip(val_peek(0).loc);
                		}
                	}
break;
case 39:
//#line 212 "Parser.y"
{
                    yyval.stmt = new Tree.DoStmt(val_peek(1).dblist, val_peek(2).loc);
                }
break;
case 40:
//#line 218 "Parser.y"
{
                    yyval.dblist.add(val_peek(0).db);
                }
break;
case 41:
//#line 222 "Parser.y"
{
                    yyval.dblist = new ArrayList<Tree.DoBranch>();
                    yyval.dblist.add(val_peek(0).db);
                }
break;
case 42:
//#line 229 "Parser.y"
{
                    yyval.db = new Tree.DoBranch(val_peek(2).expr, val_peek(0).stmt, val_peek(2).loc);
                }
break;
case 43:
//#line 235 "Parser.y"
{
						yyval.stmt = new Tree.Assign(val_peek(2).lvalue, val_peek(0).expr, val_peek(1).loc);
					}
break;
case 44:
//#line 239 "Parser.y"
{
                		yyval.stmt = new Tree.Exec(val_peek(0).expr, val_peek(0).loc);
                	}
break;
case 45:
//#line 243 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 47:
//#line 250 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 48:
//#line 256 "Parser.y"
{
						yyval.lvalue = new Tree.Ident(val_peek(1).expr, val_peek(0).ident, val_peek(0).loc);
						if (val_peek(1).loc == null) {
							yyval.loc = val_peek(0).loc;
						}
					}
break;
case 49:
//#line 263 "Parser.y"
{
                		yyval.lvalue = new Tree.Indexed(val_peek(3).expr, val_peek(1).expr, val_peek(3).loc);
                	}
break;
case 50:
//#line 269 "Parser.y"
{
						yyval.expr = new Tree.CallExpr(val_peek(4).expr, val_peek(3).ident, val_peek(1).elist, val_peek(3).loc);
						if (val_peek(4).loc == null) {
							yyval.loc = val_peek(3).loc;
						}
					}
break;
case 51:
//#line 278 "Parser.y"
{
						yyval.expr = val_peek(0).lvalue;
					}
break;
case 54:
//#line 284 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.PLUS, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 55:
//#line 288 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MINUS, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 56:
//#line 292 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MUL, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 57:
//#line 296 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.DIV, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 58:
//#line 300 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MOD, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 59:
//#line 304 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.EQ, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 60:
//#line 308 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.NE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 61:
//#line 312 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.LT, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 62:
//#line 316 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.GT, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 63:
//#line 320 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.LE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 64:
//#line 324 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.GE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 65:
//#line 328 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.AND, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 66:
//#line 332 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.OR, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 67:
//#line 336 "Parser.y"
{
                		yyval = val_peek(1);
                	}
break;
case 68:
//#line 340 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.NEG, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 69:
//#line 344 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.NOT, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 70:
//#line 348 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.RE, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 71:
//#line 352 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.IM, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 72:
//#line 356 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.COMPCAST, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 73:
//#line 360 "Parser.y"
{
                		yyval.expr = new Tree.ReadIntExpr(val_peek(2).loc);
                	}
break;
case 74:
//#line 364 "Parser.y"
{
                		yyval.expr = new Tree.ReadLineExpr(val_peek(2).loc);
                	}
break;
case 75:
//#line 368 "Parser.y"
{
                		yyval.expr = new Tree.ThisExpr(val_peek(0).loc);
                	}
break;
case 76:
//#line 372 "Parser.y"
{
                        yyval.expr = new Tree.SuperExpr(val_peek(0).loc);
                    }
break;
case 77:
//#line 376 "Parser.y"
{
                		yyval.expr = new Tree.NewClass(val_peek(2).ident, val_peek(3).loc);
                	}
break;
case 78:
//#line 380 "Parser.y"
{
                		yyval.expr = new Tree.NewArray(val_peek(3).type, val_peek(1).expr, val_peek(4).loc);
                	}
break;
case 79:
//#line 384 "Parser.y"
{
                		yyval.expr = new Tree.TypeTest(val_peek(3).expr, val_peek(1).ident, val_peek(5).loc);
                	}
break;
case 80:
//#line 388 "Parser.y"
{
                		yyval.expr = new Tree.TypeCast(val_peek(2).ident, val_peek(0).expr, val_peek(0).loc);
                	}
break;
case 81:
//#line 392 "Parser.y"
{
                		yyval.expr = new Tree.Case(val_peek(5).expr , val_peek(2).caselist , val_peek(1).expr , val_peek(7).loc);
                	}
break;
case 82:
//#line 396 "Parser.y"
{
                        yyval.expr = new Tree.DCopy(val_peek(1).expr, val_peek(3).loc);
                    }
break;
case 83:
//#line 400 "Parser.y"
{
                        yyval.expr = new Tree.SCopy(val_peek(1).expr, val_peek(3).loc);
                    }
break;
case 84:
//#line 406 "Parser.y"
{
						   yyval.caselist.add(val_peek(0).acase);
					}
break;
case 85:
//#line 410 "Parser.y"
{
					    yyval.caselist = new ArrayList<ACaseExpr>();
					}
break;
case 86:
//#line 416 "Parser.y"
{
					    yyval.acase = new Tree.ACaseExpr(val_peek(3).expr, val_peek(1).expr, val_peek(3).loc);
					}
break;
case 87:
//#line 422 "Parser.y"
{
					    yyval.expr = val_peek(1).expr;
					}
break;
case 88:
//#line 428 "Parser.y"
{
						yyval.expr = new Tree.Literal(val_peek(0).typeTag, val_peek(0).literal, val_peek(0).loc);
					}
break;
case 89:
//#line 432 "Parser.y"
{
						yyval.expr = new Null(val_peek(0).loc);
					}
break;
case 91:
//#line 439 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.elist = new ArrayList<Tree.Expr>();
                	}
break;
case 92:
//#line 446 "Parser.y"
{
						yyval.elist.add(val_peek(0).expr);
					}
break;
case 93:
//#line 450 "Parser.y"
{
                		yyval.elist = new ArrayList<Tree.Expr>();
						yyval.elist.add(val_peek(0).expr);
                	}
break;
case 94:
//#line 457 "Parser.y"
{
						yyval.stmt = new Tree.WhileLoop(val_peek(2).expr, val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 95:
//#line 463 "Parser.y"
{
						yyval.stmt = new Tree.ForLoop(val_peek(6).stmt, val_peek(4).expr, val_peek(2).stmt, val_peek(0).stmt, val_peek(8).loc);
					}
break;
case 96:
//#line 469 "Parser.y"
{
						yyval.stmt = new Tree.Break(val_peek(0).loc);
					}
break;
case 97:
//#line 475 "Parser.y"
{
						yyval.stmt = new Tree.If(val_peek(3).expr, val_peek(1).stmt, val_peek(0).stmt, val_peek(5).loc);
					}
break;
case 98:
//#line 481 "Parser.y"
{
						yyval.stmt = val_peek(0).stmt;
					}
break;
case 99:
//#line 485 "Parser.y"
{
						yyval = new SemValue();
					}
break;
case 100:
//#line 491 "Parser.y"
{
						yyval.stmt = new Tree.Return(val_peek(0).expr, val_peek(1).loc);
					}
break;
case 101:
//#line 495 "Parser.y"
{
                		yyval.stmt = new Tree.Return(null, val_peek(0).loc);
                	}
break;
case 102:
//#line 501 "Parser.y"
{
						yyval.stmt = new Print(val_peek(1).elist, val_peek(3).loc);
					}
break;
case 103:
//#line 507 "Parser.y"
{
						yyval.stmt = new PrintComp(val_peek(1).elist, val_peek(3).loc);
					}
break;
//#line 1398 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    //if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      //if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        //if (yychar<0) yychar=0;  //clean, if necessary
        //if (yydebug)
          //yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      //if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
