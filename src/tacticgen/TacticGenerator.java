/*
TODOS:
Add actual loop invariant algorithm
Throw exception
*/

package tacticgen;

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
    // TODO: put loop invariant algorithm for lab 2 here
    // Formula loop_inv = strToFormula("pL-pC>=0 & pL-pC+(vL-vC)*(vC/B) >= 0");
    Formula loop_inv = new And(new GreaterEqual(
            new Minus(new BaseVariable("pL", Option$.MODULE$.empty(), Real$.MODULE$),
                      new BaseVariable("pC", Option$.MODULE$.empty(), Real$.MODULE$)),
            zero),
            new GreaterEqual(new Plus(new Minus(new BaseVariable("pL", Option$.MODULE$.empty(), Real$.MODULE$),
                    new BaseVariable("pC", Option$.MODULE$.empty(), Real$.MODULE$)),
                    new Times(new Minus(new BaseVariable("vL", Option$.MODULE$.empty(), Real$.MODULE$),
                            new BaseVariable("vC", Option$.MODULE$.empty(), Real$.MODULE$)),
                            new Divide(new BaseVariable("vC", Option$.MODULE$.empty(), Real$.MODULE$),
                                    new BaseVariable("B", Option$.MODULE$.empty(), Real$.MODULE$)))
            ), zero));
    BelleExpr tact = TactixLibrary.loop(loop_inv);
    return tact;
  }


  private BelleExpr getDETactic(Sequent s){
    BelleExpr tact = TactixLibrary.solve();
    return tact;
  }
}