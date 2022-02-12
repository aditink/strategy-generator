import java.util.Arrays;
import java.util.ArrayList;

import edu.cmu.cs.ls.keymaerax.bellerophon.BelleExpr;
import edu.cmu.cs.ls.keymaerax.core.Provable;
import edu.cmu.cs.ls.keymaerax.core.Sequent;
import edu.cmu.cs.ls.keymaerax.core.Expression;
import edu.cmu.cs.ls.keymaerax.parser.KeYmaeraXParser;
import edu.cmu.cs.ls.keymaerax.parser.StringConverter;
import edu.cmu.cs.ls.keymaerax.parser.Parser;

import edu.cmu.cs.ls.keymaerax.btactics.TactixLibrary;

import edu.cmu.cs.ls.keymaerax.btactics.HybridProgramCalculus;

public class TacticGenerator implements StrategyGenerator {

  BelleExpr suggestion;

  public static void main(String[] args){
    
    TacticGenerator gen = new TacticGenerator();

    //Expression e = (new StringConverter("x+1")).asExpr(); // replace "s" with expression string

    // get sequent
    StringConverter sCon = new StringConverter("x>=0 ==> x>=0");
    if (sCon == null)
      System.out.println("sCon is still null!");
    else
      System.out.println(sCon);
      System.out.println(sCon.asSequent());


    gen.getTactic(sCon.asSequent());//,p);
    System.out.println(gen.suggestion);
  }

  public void getTactic(Sequent s){//, Provable p){
    this.suggestion = getLoopInv(s);//,p);
    return;

    /* if (top level op is loop invariant){
        return getLoopInv(s, p);
    }*/
    /*if(){ // pattern match to find whether loop inv
        return getLoopInv(s, p);
    }*/

    /* else if (top level op is differential equation){
        return getDETactic(s,p);
    }*/

    /*else{
        return UNFOLD;
    }*/
    //TactixLibrary t = new TactixLibrary();
    //return unfoldProgramNormalize;
  }

  BelleExpr getLoopInv(Sequent s){//, Provable p){
    // put loop invariant algorithm for lab 2 here
    // return "Loop Invariant" with arg J derived from algorithm <- HybridProgramCalculus.scala
    //BelleExpr loopInv = new BelleExpr(loop(new StringConverter("x>=0").asFormula()));
    //BelleExpr loopInv = new StringConverter(loop(new StringConverter("x>=0").asFormula())).asTactic();
    BelleExpr loopInv = new StringConverter("loop(x>=0)(1)").asTactic();
    return loopInv;
    //return new loop(new StringConverter("x>=0").asFormula());//(1); // TESTING SYNTAX WITH loop("x>=0".asFormula)(1)
  }
    // 1.
    /*String J;

    // 2.
    //HashMap<String,String> DESol = new HashMap<String,String>(); // map comp to solved comp
    // FILL DE SOL?

    ArrayList<String> comps = new ArrayList<String>();
    ArrayList<String> solvedComps = new ArrayList<String>();
    // TODO: fill comps and corresponding solvedComps such that comps[i] solved = solvedComps[i]
    comps.addAll(Arrays.asList("pL", "pC"));
    solvedComps.addAll(Arrays.asList("pL + vL*t + aL/2*t^2", "pC + vC*t + aC/2*t^2"));

    // HARDCODE PLACEHOLDER
    String safety = "pL - pC >= 0";

    String JPostDyn = subList(safety, comps, solvedComps);

    // 3.
    ArrayList<String> ccChoices = new ArrayList<String>();
    // TODO: fill ccChoices
    ccChoices.addAll(Arrays.asList("A", "-B"));
    String bestCCChoice = null;
    for (String choice : ccChoices){
      if (bestCCChoice == null){
        bestCCChoice = choice;
      }
      // TODO: how to compare these mathematically since they are strings
      if (sub(JPostDyn, "aC", choice) >  sub(JPostDyn, "aC", bestCCChoice)){
        bestCCChoice = choice;
      }
    }
    // HARDCODE PLACEHOLDER
    bestCCChoice = "-B";

    // 4.
    ArrayList<String> lcChoices = new ArrayList<String>();
    // TODO: fill lcChoices
    lcChoices.addAll(Arrays.asList("A", "-B"));
    String worstLCChoice = null;
    for (String choice : lcChoices){
      if (worstLCChoice == null){
        worstLCChoice = choice;
      }
      // TODO: how to compare these mathematically since they are strings
      if (sub(JPostDyn, "aL", choice) <  sub(JPostDyn, "aL", bestCCChoice)){
        worstLCChoice = choice;
      }
    }
    // HARDCODE PLACEHOLDER
    worstLCChoice = "-B";

    // 5.
    ArrayList<String> dynConds = new ArrayList<String>();
    dynConds.add("vC >= 0");
    // TODO: fill dynConds

    String TUntilBroken = "";
    for (String cond : dynConds){
      if (cond.contains("pC") || cond.contains("vC") || cond.contains("aC")){
        // get t when condition breaks;
        // TODO: how to solve for t here?
        String t = tBreak(cond);
        // TODO: how to compare these mathematically since they are strings
        if (t >= 0 && t < TUntilBroken){
          TUntilBroken = t;
        }
      }
    }
    // HARDCODE PLACEHOLDER
    TUntilBroken = "vC/B";

    // 6.
    if (!TUntilBroken.equals("")){
      // TODO: sub takes in ArrayLists for 2nd and 3rd args, fix this
      J = J + " & " + sub(JPostDyn, "t", TUntilBroken);
    }
    else{
      J = J + " & " + JPostDyn;
    }

    return loop(new StringConverter(J).asFormula());
  }

  String tBreak(String cond){
    // TODO: solve for t here
    // HARDCODE PLACEHOLDER
    String t = "vC/B";
    return t;
  }

  String subList(String f, ArrayList<String> orig, ArrayList<String> rep){
    for(int i = 0; i < orig.size(); i++){
      // loop through f looking for String
      f = sub(f, orig.get(i), rep.get(i));
      // TODO: make sure stuff doesn't get double-replaced
    }
    return f;
  }

  String sub(String f, String orig, String rep){
      f = f.replaceAll(orig, rep);
      // TODO: make sure stuff doesn't get double-replaced
    return f;
  }*/


  BelleExpr getDERule(Sequent s, Provable p){
  //  return "Solution" <- DifferentialEquationCalculus.scala
    //return new BelleExpr("[']"); // solution
    //return TactixLibrary.solve(1) // or is it this
    BelleExpr DESolve = new StringConverter("solve(1)").asTactic();
    return DESolve;
    //return solve(1); // or is it this
    //return AxiomaticODESolver()(1) // or is it this
  }

}