/*
TODOS:
Add actual loop invariant algorithm
  1. apply tactics to get solved DE
  2. parse out CC choices and LC choices
  3. algebraic manipulation of formulas (solve for t)
  4. finish section 6 of loop invariant algorithm
*/

package tacticgen;

import java.util.ArrayList;

import edu.cmu.cs.ls.keymaerax.bellerophon.BelleExpr;
import edu.cmu.cs.ls.keymaerax.cli.Usage;
import edu.cmu.cs.ls.keymaerax.core.*;
import edu.cmu.cs.ls.keymaerax.core.Number;
import edu.cmu.cs.ls.keymaerax.parser.*;
import edu.cmu.cs.ls.keymaerax.cli.KeYmaeraX;
import edu.cmu.cs.ls.keymaerax.btactics.TactixLibrary;
import scala.*;
import scala.collection.mutable.Builder;
import scala.collection.immutable.Map;
import scala.collection.immutable.Map$;
import scala.math.BigDecimal;

import tacticgenhelper.*;
import tacticgenhelper.TacticGenHelper.*;

public class TacticGenerator implements StrategyGenerator{

  Term zero = new Number(BigDecimal.decimal(0));

  public static void main(String[] args) throws Exception{
    
    TacticGenerator gen = new TacticGenerator();

    BelleExpr tactic = gen.getTactic(TacticGenHelper.strToSequent("x>0 ==> x>0"));
    System.out.println(tactic.prettyString());
  }

  public BelleExpr getTactic(Sequent seq) throws Exception {
    String topLevel = TacticGenHelper.getTopLevel(seq);
    if (topLevel.equals("LI"))
      return getLoopInv(seq);
    else if (topLevel.equals("DE"))
      return getDETactic(seq);
    else if (topLevel.equals("other"))
      return TactixLibrary.unfoldProgramNormalize();
    throw new Exception("error: top level operation is not valid");
    //return null;
  }


  private BelleExpr getLoopInv(Sequent s){//, Provable p){
    // Loop Invariant Algorithm:
    Formula J;
    Formula initialConds = TacticGenHelper.getInitConds(s); // TODO: write getInitConds(), hardcode for now
    
    // 1. set LI = safety condition (safety condition has to be in the form a >= 0 or a > 0)
    J = TacticGenHelper.getSafetyConds(s);

    // 2. solve DE, plug results into safetyPostDyn
    // TODO: extract DE from sequent, solve it, and substitute into the left hand side of the safety condition
    Term lhsSafetyPostDyn = TacticGenHelper.strToTerm("(pL+vL*t+aL*t^2/2)-(pC+vC*t+aC*t^2/2)");  // TODO: remove hardcoding
    Formula safetyPostDyn = new GreaterEqual(lhsSafetyPostDyn, zero);

    // 3. find safest control car choice
    // TODO: extract CC choices from s, fill ccChoices
    ArrayList<Term> ccChoices = new ArrayList<Term>(); // TODO: remove hardcoding
    ccChoices.add(new BaseVariable("A", Option$.MODULE$.empty(), Real$.MODULE$));
    ccChoices.add(zero);
    ccChoices.add(new Minus(zero, new BaseVariable("B", Option$.MODULE$.empty(), Real$.MODULE$)));
    Term bestCCChoice = null;
    for (Term choice : ccChoices){
      if (bestCCChoice == null){
        bestCCChoice = choice; 
      }
      Formula f = new Imply(initialConds, new GreaterEqual(TacticGenHelper.strToTerm(TacticGenHelper.subVar("aC", choice.toString(), lhsSafetyPostDyn.toString())), TacticGenHelper.strToTerm(TacticGenHelper.subVar("aC", bestCCChoice.toString(), lhsSafetyPostDyn.toString()))));
      if (TacticGenHelper.isTrue(f)){ // TODO: substitute into formula, run aC:=choice? TODO: compare formulas (use mathematica (QE) to see if A>0,B>0 ==> safetyPostDyn(choice) > safetyPostDyn(bestCCChoice) is provable?)
        bestCCChoice = choice;
      }
    }

    // 4. find least-safe leading car choice
    // TODO: extract LC choices from s, fill lcChoices
    ArrayList<Term> lcChoices = new ArrayList<Term>(); // TODO: remove hardcoding
    lcChoices.add(new BaseVariable("A", Option$.MODULE$.empty(), Real$.MODULE$));
    lcChoices.add(zero);
    lcChoices.add(new Minus(zero, new BaseVariable("B", Option$.MODULE$.empty(), Real$.MODULE$)));
    Term worstLCChoice = null;
    for (Term choice : lcChoices){
      if (worstLCChoice == null){
        worstLCChoice = choice;
      }
      Formula f = new Imply(initialConds, new LessEqual(TacticGenHelper.strToTerm(TacticGenHelper.subVar("aL", choice.toString(), lhsSafetyPostDyn.toString())), TacticGenHelper.strToTerm(TacticGenHelper.subVar("aL", worstLCChoice.toString(), lhsSafetyPostDyn.toString()))));
      if (TacticGenHelper.isTrue(f)){
        worstLCChoice = choice;
      }
    }

    // 5. Get safety condition post dynamics after substituting in best CC and worst LC choices
    Formula safetySubbedChoices = TacticGenHelper.strToFormula(TacticGenHelper.subVar("aL", worstLCChoice.toString(), TacticGenHelper.subVar("aC", bestCCChoice.toString(), safetyPostDyn.toString()).toString()));

    // System.out.println();
    // System.out.println("J = " + J.prettyString());
    // System.out.println("safetySubbedChoices = " + safetySubbedChoices.prettyString());

    // 6. loop through dynamics conditions that CC controls (conditions with pC, vC, and aC)
    // find 't' when the condition breaks assuming bestCCChoice
    // if t >= 0 && t =< tUntilBroken, tUntilBroken = t 
    ArrayList<Formula> dynConds = TacticGenHelper.getDynConds(s); // TODO: get dynamics conditions from sequent
    Term tUntilBroken;
    for (Formula cond : dynConds){
      if (cond.toString().contains("pC") || cond.toString().contains("vC") || cond.toString().contains("aC")){ // TODO: get vars that CC controls
        // TODO: get t when condition breaks;
        Term t;
        t = TacticGenHelper.strToTerm("vC/B");
        Formula solvedForT = new Equal(new BaseVariable("t", Option$.MODULE$.empty(), Real$.MODULE$), t);
        // TODO: also need constant assumptions and dynamic conditions on the left of the implications
        if (TacticGenHelper.isTrue(new Imply(solvedForT, TacticGenHelper.strToFormula("t>=0"))) && TacticGenHelper.isTrue(new Imply(solvedForT, TacticGenHelper.strToFormula("t<=tUntilBroken")))){
          tUntilBroken = t;
        }
      }
    }
    tUntilBroken = TacticGenHelper.strToTerm("vC/B"); // TODO: remove hardcoding

    // 7. if tUntilBroken != null, replace 't' in safetySubbedChoices with tUntilBroken and add that to J. Otherwise add safetySubbedChoices to J as is (safetySubbedChoices has to stay invariant for all t)
    if (tUntilBroken != null){
      J = new And(J, TacticGenHelper.strToFormula(TacticGenHelper.subVar("t", tUntilBroken.toString(), safetySubbedChoices.toString())));
    }
    else{
      J = new And(J, safetySubbedChoices);
    }

    return TactixLibrary.loop(J);
  }


  private BelleExpr getDETactic(Sequent s){
    BelleExpr tact = TactixLibrary.solve();
    return tact;
  }
}