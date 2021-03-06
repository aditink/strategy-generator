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


  private BelleExpr getLoopInv(Sequent s){
    // Loop Invariant Algorithm:
    Formula J;
    Formula initialConds = TacticGenHelper.getInitConds(s);
    
    // 1. set LI = safety condition (safety condition has to be in the form a >= 0 or a > 0)
    J = TacticGenHelper.getSafetyConds(s);

    // 2. solve DE, plug results into safetyPostDyn
    ODESystem ode = TacticGenHelper.getDE(TacticGenHelper.getProgram(s));
    
    Formula safetyPostDyn = TacticGenHelper.getSafetyPostDyn( TacticGenHelper.strToSequent( "==> [" + ode.toString() + "]" + J.toString() ) );
    Term lhsSafetyPostDyn = ((GreaterEqual)safetyPostDyn).left();

    // 3. find safest control car choice
    ArrayList<Program> choices = TacticGenHelper.getChoices(TacticGenHelper.getProgram(s), new ArrayList<Program>());
    ArrayList<Term> ccChoices = new ArrayList<Term>();
    for (Program choice : choices){
      if (choice.toString().contains("aC")){ // TODO: change this to get choice.val and compare
        ccChoices.add(((Assign)choice).e());
      }
    }

    Term bestCCChoice = null;
    for (Term choice : ccChoices){
      if (bestCCChoice == null){
        bestCCChoice = choice; 
      }
      // f = initial conditions -> pL-pC after dynamics with acc = choice > pL-pC after dynamics with acc = bestCCchoice (is choice safer than bestCCchoice)
      Formula f = new Imply(initialConds, new GreaterEqual(TacticGenHelper.strToTerm(TacticGenHelper.subVar("aC", choice.toString(), lhsSafetyPostDyn.toString())), TacticGenHelper.strToTerm(TacticGenHelper.subVar("aC", bestCCChoice.toString(), lhsSafetyPostDyn.toString()))));
      if (TacticGenHelper.isTrue(f)){
        bestCCChoice = choice;
      }
    }

    // 4. find least-safe leading car choice
    ArrayList<Term> lcChoices = new ArrayList<Term>();
    for (Program choice : choices){
      if (choice.toString().contains("aL")){ // TODO: change this to get choice.val and compare
        lcChoices.add(((Assign)choice).e());
      }
    }

    Term worstLCChoice = null;
    for (Term choice : lcChoices){
      if (worstLCChoice == null){
        worstLCChoice = choice;
      }
      // f = initial conditions -> pL-pC after dynamics with acc = choice < pL-pC after dynamics with acc = bestCCchoice (is choice lest safe than worstLCchoice)
      Formula f = new Imply(initialConds, new LessEqual(TacticGenHelper.strToTerm(TacticGenHelper.subVar("aL", choice.toString(), lhsSafetyPostDyn.toString())), TacticGenHelper.strToTerm(TacticGenHelper.subVar("aL", worstLCChoice.toString(), lhsSafetyPostDyn.toString()))));
      if (TacticGenHelper.isTrue(f)){
        worstLCChoice = choice;
      }
    }

    // 5. Get safety condition post dynamics after substituting in best CC and worst LC choices
    Formula safetySubbedChoices = TacticGenHelper.strToFormula(TacticGenHelper.subVar("aL", "("+worstLCChoice.toString()+")", TacticGenHelper.subVar("aC", "("+bestCCChoice.toString()+")", safetyPostDyn.toString()).toString()));

    // 6. loop through dynamics conditions that CC controls (conditions with pC, vC, and aC)
    // find 't' when the condition breaks assuming bestCCChoice
    // if t >= 0 && t =< tUntilBroken, tUntilBroken = t 
    Formula dynConds = TacticGenHelper.getDomainConst(TacticGenHelper.getProgram(s));
    ArrayList<Formula> dynCondsList = new ArrayList<Formula>();
    for (String cond : dynConds.toString().split("&")){
      dynCondsList.add(TacticGenHelper.strToFormula(cond));
    }

    Term tUntilBroken = null;
    for (Formula cond : dynCondsList){
      if (cond.toString().contains("pC") || cond.toString().contains("vC") || cond.toString().contains("aC")){ // Assumption: this is not well generalizable
        // assumes constraint is on velocity
        Term t = TacticGenHelper.getKinemEqForT(); 
        Term boundary = TacticGenHelper.getConstraint(cond);
        t = TacticGenHelper.strToTerm(TacticGenHelper.subVar("vi", "vC", t.toString())); // vC is always initial velocity
        t = TacticGenHelper.strToTerm(TacticGenHelper.subVar("a", bestCCChoice.toString(), t.toString())); // bestCCChoice is always acceleration
        t = TacticGenHelper.strToTerm(TacticGenHelper.subVar("vf", boundary.toString(), t.toString())); // fill in condition boundary


        Formula solvedForT = new Equal(new BaseVariable("t", Option$.MODULE$.empty(), Real$.MODULE$), t);


        // if constant assumptions, dynamic conditions, and t=solvedForT => t>=0 & t<=tUntilBroken, then tUntilBroken = t
        if (TacticGenHelper.isTrue(new Imply(new And(new And(solvedForT, TacticGenHelper.getConstAssumpts(s)), TacticGenHelper.getDomainConst(TacticGenHelper.getProgram(s))), TacticGenHelper.strToFormula("t>=0"))) && tUntilBroken == null || TacticGenHelper.isTrue(new Imply(new And(new And(solvedForT, TacticGenHelper.getConstAssumpts(s)), TacticGenHelper.getDomainConst(TacticGenHelper.getProgram(s))), TacticGenHelper.strToFormula("t<=tUntilBroken")))){
          tUntilBroken = t;
        }
      }
    }

    // 7. if tUntilBroken != null, replace 't' in safetySubbedChoices with tUntilBroken and add that to J. Otherwise add safetySubbedChoices to J as is (safetySubbedChoices has to stay invariant for all t)
    if (tUntilBroken != null){
      J = new And(J, TacticGenHelper.strToFormula(TacticGenHelper.subVar("t_", tUntilBroken.toString(), safetySubbedChoices.toString())));
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