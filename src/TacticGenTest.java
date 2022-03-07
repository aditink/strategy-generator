/*
TODOS:
Add actual loop invariant algorithm

split TacticGenerator into helper functions (strToSequent, etc, relies on initializeProver())
tactic generator (getTactic, no dependency on initializeProver) 
and tests 
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

import tacticgen.*;
import tacticgenhelper.*;

public class TacticGenTest {

  public static void main(String[] args) throws Exception{
    KeYmaeraX.initializeProver(Map$.MODULE$.empty(), Usage.cliUsage());
    TacticGenerator tgen = new TacticGenerator();


    String s = new String("y>0 ==> x>0 -> x>0");

    BelleExpr tactic = tgen.getTactic(TacticGenHelper.strToSequent(s));
    System.out.println(tactic.prettyString());
  }

}




