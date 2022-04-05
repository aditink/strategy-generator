/*
TODOS:
use "withMathematica" (find reference in keymaerax) instead of initializeProver for test function: function that I can wrap around test functions
*/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import edu.cmu.cs.ls.keymaerax.FileConfiguration$;
import edu.cmu.cs.ls.keymaerax.bellerophon.BelleExpr;
import edu.cmu.cs.ls.keymaerax.cli.Usage;
import edu.cmu.cs.ls.keymaerax.core.*;
import edu.cmu.cs.ls.keymaerax.parser.*;
import edu.cmu.cs.ls.keymaerax.pt.ProvableSig;
import edu.cmu.cs.ls.keymaerax.tools.ext.JLinkMathematicaLink;
import edu.cmu.cs.ls.keymaerax.tools.ext.MathematicaODESolverTool;
import edu.cmu.cs.ls.keymaerax.Configuration;
import edu.cmu.cs.ls.keymaerax.Configuration$;
import edu.cmu.cs.ls.keymaerax.cli.KeYmaeraX;
import edu.cmu.cs.ls.keymaerax.FileConfiguration;
import edu.cmu.cs.ls.keymaerax.btactics.TactixLibrary;
import edu.cmu.cs.ls.keymaerax.btactics.DLBySubst;

import edu.cmu.cs.ls.keymaerax.btactics.HybridProgramCalculus;
import edu.cmu.cs.ls.keymaerax.bellerophon.parser.*;
import edu.cmu.cs.ls.keymaerax.btactics.TactixLibrary.*;
import scala.Symbol;
import scala.Symbol$;
import scala.Tuple2;
import scala.Tuple2$;
import scala.collection.mutable.Builder;
import scala.collection.immutable.Map;
import scala.collection.immutable.Map$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.Option;
import scala.Option$;

import tacticgen.*;
import tacticgenhelper.*;

import scala.math.BigDecimal;
import edu.cmu.cs.ls.keymaerax.core.Number;
import edu.cmu.cs.ls.keymaerax.core.StaticSemantics.VCP;
import scala.collection.IndexedSeq;
import scala.collection.JavaConverters;
import scala.collection.JavaConverters$;

import scala.collection.immutable.List;

public class TacticGenTest {
  // initializeProver(combineConfigs(options, configFromFile(Tools.MATHEMATICA)), ...)
// from KeYmaeraeX.scala


  public static void main(String[] args) throws Exception{
    Symbol tool = Symbol$.MODULE$.apply("tool");
    Tuple2 tuple = Tuple2$.MODULE$.apply(tool, "mathematica");
    Builder<Tuple2<Symbol, Object>, Map<Symbol, Object>> builder = Map$.MODULE$.<Symbol, Object>newBuilder().$plus$eq(tuple);
    Map<Symbol, Object> options = builder.result();
    KeYmaeraX.initializeProver(options, Usage.cliUsage());
    //KeYmaeraX.initializeProver(Map$.MODULE$.empty(), Usage.cliUsage());
    TacticGenerator tgen = new TacticGenerator();



    // test lab 2 loop invariant
    Sequent seq = TacticGenHelper.strToSequent("pC<=pL & A>0 & B>0 & vC>=0 & vL>=0 & T>=0 & ((pL-pC+(vL-vC)*(vC/B) >= 0))  ==>  [  {  {  ?accTest(pL, pC, vL, vC, aL, aC) ;  aC := A;  ++ ?brakeTest(pL, pC, vL, vC, aL, aC); aC := -B;++ ?vC=0; aC := 0;  }  {  aL:=A; ++ aL:=-B; ++ ?vC=0; aL := 0; }  {  t := 0;  { pL'=vL, vL'=aL, pC'=vC, vC'=aC, t'=1 &  vL>=0 & vC>=0 & t<=T }  }  }* ]   ( pL-pC>=0 )");

    BelleExpr tactic = tgen.getTactic(seq);
    System.out.println(tactic.prettyString());
  }
}




