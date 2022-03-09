/*
TODOS:
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
import edu.cmu.cs.ls.keymaerax.tools.ext.JLinkMathematicaLink;
import edu.cmu.cs.ls.keymaerax.tools.ext.MathematicaLink;
import edu.cmu.cs.ls.keymaerax.tools.ext.MathematicaODESolverTool;
import edu.cmu.cs.ls.keymaerax.Configuration;
import edu.cmu.cs.ls.keymaerax.Configuration$;
import edu.cmu.cs.ls.keymaerax.cli.KeYmaeraX;
import edu.cmu.cs.ls.keymaerax.FileConfiguration;
import edu.cmu.cs.ls.keymaerax.btactics.TactixLibrary;

import edu.cmu.cs.ls.keymaerax.btactics.HybridProgramCalculus;
import edu.cmu.cs.ls.keymaerax.bellerophon.parser.*;
import edu.cmu.cs.ls.keymaerax.btactics.TactixLibrary.*;
import scala.Option;
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

  public static DifferentialProgram strToDE(String s){
    DLParser parser = new DLParser();
    return parser.differentialProgramParser().apply(s);
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

  public static Formula getSafetyCond(Sequent s){
    return ((Box) s.succ().head()).child();
  }

  public static Program getProgram(Sequent s){
    return ((Box) s.succ().head()).program();
  }

  public static Option<Formula> getDESolution(DifferentialProgram diffSys, Variable diffArg, Map<Variable,Variable> iv){
    MathematicaODESolverTool odeSolver = new MathematicaODESolverTool(new JLinkMathematicaLink("mathematica"));
    Option<Formula> odeSolution = odeSolver.odeSolve(diffSys, diffArg, iv);
    return odeSolution;
  }

}



