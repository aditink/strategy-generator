/*
TODOS:
Add actual loop invariant algorithm
*/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import edu.cmu.cs.ls.keymaerax.FileConfiguration$;
import edu.cmu.cs.ls.keymaerax.bellerophon.BelleExpr;
import edu.cmu.cs.ls.keymaerax.cli.Usage;
import edu.cmu.cs.ls.keymaerax.core.*;
import edu.cmu.cs.ls.keymaerax.parser.*;
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

public class TacticGenerator implements StrategyGenerator{

  BelleExpr suggestion;

  public static void main(String[] args){
    
    TacticGenerator gen = new TacticGenerator();


    Symbol tool = Symbol$.MODULE$.apply("tool");
    Tuple2 tuple = Tuple2$.MODULE$.apply(tool, "mathematica");
    Builder<Tuple2<Symbol, Object>, Map<Symbol, Object>> builder = Map$.MODULE$.<Symbol, Object>newBuilder().$plus$eq(tuple);
    Map<Symbol, Object> options = builder.result();
    KeYmaeraX.initializeProver(options, Usage.cliUsage());


    String s = new String("x>0 ==> [{{x'=1};}*]x>0");
    //System.out.println(strToSequent(s).prettyString());


    gen.getTactic(strToSequent(s));
    System.out.println(gen.suggestion.prettyString());
  }


  public void getTactic(Sequent seq){
    String topLevel = getTopLevel(seq);
    if (topLevel.equals("LI"))
      this.suggestion = getLoopInv(seq);
    else if (topLevel.equals("DE"))
      this.suggestion = getDETactic(seq);
    else if (topLevel.equals("other"))
      this.suggestion = TactixLibrary.unfoldProgramNormalize();
    else
      System.out.println("error: top level operation is not valid");
    return;
  }


  private BelleExpr getLoopInv(Sequent s){//, Provable p){
    // TODO: put loop invariant algorithm for lab 2 here
    Formula loop_inv = strToFormula("pL-pC>=0 & pL-pC+(vL-vC)*(vC/B) >= 0");
    BelleExpr tact = TactixLibrary.loop(loop_inv);
    return tact;
  }


  private BelleExpr getDETactic(Sequent s){
    BelleExpr tact = TactixLibrary.solve();
    return tact;
  }


  // Helper functions
  static Sequent strToSequent(String s){
    DLParser parser = new DLParser();
    return parser.sequentParser().apply(s);
  }


  private Formula strToFormula(String s){
    DLParser parser = new DLParser();
    return parser.formulaParser().apply(s);
  }


  private String getTopLevel(Sequent s){
    // get top level op and return as "DE", "LI", or "other"
    // when loop encapsulates conjecture, top level op is LI
    Formula firstSucc = s.succ().head();
    Formula child = ((Box) firstSucc).child();
    Program program = ((Box) firstSucc).program();

    if (program instanceof Loop)
      return "LI";
    else if (program instanceof ODESystem)
      return "DE";
    else
      return "other";
  }

  private void DeconstructExpression(Sequent s) {
    Formula firstSucc = s.succ().head();
    if (firstSucc instanceof Box) {
      Formula child = ((Box) firstSucc).child();
      Program program = ((Box) firstSucc).program();
    }
  }

}

