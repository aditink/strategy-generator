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
import scala.Option;
import scala.Option$;

import tacticgen.*;
import tacticgenhelper.*;

import scala.math.BigDecimal;
import edu.cmu.cs.ls.keymaerax.core.Number;
import scala.collection.JavaConverters;
import scala.collection.JavaConverters$;

public class TacticGenTest {

  public static void main(String[] args) throws Exception{
    KeYmaeraX.initializeProver(Map$.MODULE$.empty(), Usage.cliUsage());
    TacticGenerator tgen = new TacticGenerator();


    String s = new String("y>0 ==> [{x'=0};]x>=0");
    Sequent seq = TacticGenHelper.strToSequent(s);

 
    // Try using proveBy()
    System.out.println(seq.prettyString());

    BelleExpr solveTactic = TactixLibrary.solve();

    ProvableSig postRule = TactixLibrary.proveBy(seq, solveTactic); // TODO: solve tactic needs position arg
    System.out.println(postRule.prettyString());

    //BelleExpr tactic = tgen.getTactic(TacticGenHelper.strToSequent(s));
    //System.out.println(tactic.prettyString());


    // try solving ODE
    DifferentialProgram diffSys = (DifferentialProgram) TacticGenHelper.strToDE("x'=1");
    Variable diffArg = new BaseVariable("t", Option$.MODULE$.empty(), Real$.MODULE$);
    HashMap<Variable,Variable> iv = new HashMap<Variable,Variable>();
    //iv.put(new BaseVariable("x", Option$.MODULE$.empty(), Real$.MODULE$), new Number(BigDecimal.decimal(0)));
    iv.put(new BaseVariable("x", Option$.MODULE$.empty(), Real$.MODULE$), new BaseVariable("y", Option$.MODULE$.empty(), Real$.MODULE$));
    //Map<Variable,Variable> iv = Map$.MODULE$.empty(); // this needs to map x to 0

    //Option<Formula> odeSolved = TacticGenHelper.getDESolution(diffSys, diffArg, JavaConverters$.asScala(iv)); // TODO: recognize asScala()
    //System.out.println(odeSolved);

  }
}




