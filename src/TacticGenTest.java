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
import edu.cmu.cs.ls.keymaerax.Configuration;
import edu.cmu.cs.ls.keymaerax.Configuration$;
import edu.cmu.cs.ls.keymaerax.cli.KeYmaeraX;
import edu.cmu.cs.ls.keymaerax.FileConfiguration;
import edu.cmu.cs.ls.keymaerax.btactics.TactixLibrary;

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
import scala.collection.IndexedSeq;
import scala.collection.JavaConverters;
import scala.collection.JavaConverters$;

import scala.collection.immutable.List;

public class TacticGenTest {

  public static void main(String[] args) throws Exception{
    KeYmaeraX.initializeProver(Map$.MODULE$.empty(), Usage.cliUsage());
    TacticGenerator tgen = new TacticGenerator();


    // let's try and initialize a map
    // initialize Tuple first
    Tuple2<BaseVariable,BaseVariable> iv1 = Tuple2$.MODULE$.apply(new BaseVariable("pL", Option$.MODULE$.empty(), Real$.MODULE$),new BaseVariable("pL", Option$.MODULE$.empty(), Real$.MODULE$));

    // then initialize sequence from tuple (need to add several tuples)
    Builder<Tuple2<BaseVariable,BaseVariable>, Seq<Tuple2<BaseVariable,BaseVariable>>> builder = Seq$.MODULE$.newBuilder();
    Seq<Tuple2<BaseVariable,BaseVariable>> ivs =  builder.$plus$eq(iv1).result();

    // then make Map out of sequence like here
    Map<Variable,Variable> iv = new Map.Map1(new BaseVariable("pL", Option$.MODULE$.empty(), Real$.MODULE$),
            new BaseVariable("pL", Option$.MODULE$.empty(), Real$.MODULE$)); //Map$.MODULE$.

    // TEST
    System.out.println("PRINTING MAP");
    //System.out.println(iv);
    System.out.println("DONE PRINTING MAP");






    
    Sequent seq = TacticGenHelper.strToSequent("(initialConds(pL, pC, vL, vC, aL, aC))  ==>  [  {  {  ?accTest(pL, pC, vL, vC, aL, aC) ;  aC := A;  ++ ?brakeTest(pL, pC, vL, vC, aL, aC); aC := -B;++ ?vC=0; aC := 0;  }  {  aL:=A; ++ aL:=-B; ++ ?vC=0; aL := 0; }  {  t := 0;  { pL'=vL, vL'=aL, pC'=vC, vC'=aC, t'=1 &  vL>=0 & vC>=0 & t<=T }  }  }* ]   ( safetyTest(pL, pC) )");


    ODESystem de = TacticGenHelper.getDE(TacticGenHelper.getProgram(seq));
    System.out.println("DE = " + de.prettyString());
    Formula constraint = TacticGenHelper.getDomainConst(TacticGenHelper.getProgram(seq));
    System.out.println("domain constraints = " + constraint.prettyString());
    System.out.println();

    BelleExpr tactic = tgen.getTactic(seq);
    System.out.println(tactic.prettyString());
  }
}




