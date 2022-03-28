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

    Sequent seq = TacticGenHelper.strToSequent("(initialConds(pL, pC, vL, vC, aL, aC))  ==>  [  {  {  ?accTest(pL, pC, vL, vC, aL, aC) ;  aC := A;  ++ ?brakeTest(pL, pC, vL, vC, aL, aC); aC := -B;++ ?vC=0; aC := 0;  }  {  aL:=A; ++ aL:=-B; ++ ?vC=0; aL := 0; }  {  t := 0;  { pL'=vL, vL'=aL, pC'=vC, vC'=aC, t'=1 &  vL>=0 & vC>=0 & t<=T }  }  }* ]   ( safetyTest(pL, pC) )");

    System.out.println("getting ODE...");
    ODESystem ode = TacticGenHelper.getDE(TacticGenHelper.getProgram(seq));
    System.out.println("got ODE: "+ode.toString()+", now solving ODE");
    System.out.println();
    DifferentialProgram diffSys = ode.ode();
    Variable diffArg = new BaseVariable("t", Option$.MODULE$.empty(), Real$.MODULE$);

    // TODO: how to get variables for iv map
    Map<Variable,Variable> iv = new Map.Map1(new BaseVariable("pL", Option$.MODULE$.empty(), Real$.MODULE$),
            new BaseVariable("pL", Option$.MODULE$.empty(), Real$.MODULE$)); // need to initialize Map

            iv = iv.updated(new BaseVariable("vL", Option$.MODULE$.empty(), Real$.MODULE$), new BaseVariable("vL", Option$.MODULE$.empty(), Real$.MODULE$));
            iv = iv.updated(new BaseVariable("aL", Option$.MODULE$.empty(), Real$.MODULE$), new BaseVariable("aL", Option$.MODULE$.empty(), Real$.MODULE$));
            iv = iv.updated(new BaseVariable("pC", Option$.MODULE$.empty(), Real$.MODULE$), new BaseVariable("pC", Option$.MODULE$.empty(), Real$.MODULE$));
            iv = iv.updated(new BaseVariable("vC", Option$.MODULE$.empty(), Real$.MODULE$), new BaseVariable("vC", Option$.MODULE$.empty(), Real$.MODULE$));
            iv = iv.updated(new BaseVariable("aC", Option$.MODULE$.empty(), Real$.MODULE$), new BaseVariable("aC", Option$.MODULE$.empty(), Real$.MODULE$));

    //iv.
    Option<Formula> odeSol = TacticGenHelper.getDESolution(diffSys, diffArg, iv);

    // TEST
    System.out.println("PRINTING MAP");
    System.out.println(iv);
    System.out.println("DONE PRINTING MAP");






    
    //Sequent seq = TacticGenHelper.strToSequent("(initialConds(pL, pC, vL, vC, aL, aC))  ==>  [  {  {  ?accTest(pL, pC, vL, vC, aL, aC) ;  aC := A;  ++ ?brakeTest(pL, pC, vL, vC, aL, aC); aC := -B;++ ?vC=0; aC := 0;  }  {  aL:=A; ++ aL:=-B; ++ ?vC=0; aL := 0; }  {  t := 0;  { pL'=vL, vL'=aL, pC'=vC, vC'=aC, t'=1 &  vL>=0 & vC>=0 & t<=T }  }  }* ]   ( safetyTest(pL, pC) )");


    ODESystem de = TacticGenHelper.getDE(TacticGenHelper.getProgram(seq));
    System.out.println("DE = " + de.prettyString());
    Formula constraint = TacticGenHelper.getDomainConst(TacticGenHelper.getProgram(seq));
    System.out.println("domain constraints = " + constraint.prettyString());
    System.out.println();

    BelleExpr tactic = tgen.getTactic(seq);
    System.out.println(tactic.prettyString());
  }
}




