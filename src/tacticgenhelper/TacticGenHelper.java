/*
TODOS:
Add actual loop invariant algorithm

split TacticGenerator into helper functions (strToSequent, etc, relies on initializeProver())
tactic generator (getTactic, no dependency on initializeProver) 
and tests 
use "withMathematica" (find reference in keymaerax) instead of initializeProver for test function: function that I can wrap around test functions
*/

package tacticgenhelper;

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

public class TacticGenHelper{


  // Helper functions
  public static Sequent strToSequent(String s){
    DLParser parser = new DLParser();
    return parser.sequentParser().apply(s);
  }


  public static Formula strToFormula(String s){
    DLParser parser = new DLParser();
    return parser.formulaParser().apply(s);
  }


  public static String getTopLevel(Sequent s){
    // get top level op and return as "DE", "LI", or "other"
    // when loop encapsulates conjecture, top level op is LI

    if (s.succ().head() instanceof Box){ // need to extract program
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
    else{
      Formula formula = s.succ().head(); // TODO: test this
      if (formula instanceof Loop)
        return "LI";
      else if (formula instanceof ODESystem)
        return "DE";
      else
        return "other";
    }
  }

}




