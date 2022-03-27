/*
TODOS:
1. solve errors (program seems to run fine, but IDE complains)
2. Write getConstAssumpts()
3. More rigorously analyze getDE() and getChoices()
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
import edu.cmu.cs.ls.keymaerax.pt.ProvableSig;
import edu.cmu.cs.ls.keymaerax.tools.ext.JLinkMathematicaLink;
import edu.cmu.cs.ls.keymaerax.tools.ext.MathematicaLink;
import edu.cmu.cs.ls.keymaerax.tools.ext.MathematicaODESolverTool;
import edu.cmu.cs.ls.keymaerax.tools.qe.MathematicaCommandRunner;
import edu.cmu.cs.ls.keymaerax.tools.qe.MathematicaQETool;
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

  public static DifferentialProgram strToDiffProg(String s){
    DLParser parser = new DLParser();
    return parser.differentialProgramParser().apply(s);
  }

  public static Term strToTerm(String s){
    DLParser parser = new DLParser();
    return parser.termParser().apply(s);
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

  public static Formula getSafetyConds(Sequent s){
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

  public static String subVar(String oldVar, String newVar, String term){
    return term.replace(oldVar, newVar);
  }

  public static Boolean isTrue(Formula f){
    ProvableSig proof;
    try {
      proof = TactixLibrary.proveBy(f, TactixLibrary.QE());
    }
    catch(Exception e) {
      return false;
    }
    return proof.isProved();
  }

  public static Formula getInitConds(Sequent s){ // TODO: can assume safety conditions + constant assumptions
    return new And(getSafetyConds(s), getConstAssumpts(s));
  }


  public static Formula getConstAssumpts(Sequent s){ // TODO: get constant assumptions
    return strToFormula("A>0&B>0");
  }

  public static Term getKinemEqForT(){
    // t = (vf-vi)/a
    return strToTerm("(vf-vi)/a");
  }

  // what other program operations do I need to deal with? none?
  public static ODESystem getDE(Program p){ // this only works if the program has 1 differential equation: need to deal with multiple?
    //System.out.println(p.prettyString());
    ODESystem candidate = null;
    if (p instanceof ODESystem){
      return (ODESystem)p;
    }
    else if (p instanceof Loop){
      return getDE(((Loop)p).child());
    }
    else if (p instanceof Choice){
      candidate = getDE(((Choice)p).left());
      if (candidate == null)
        candidate = getDE(((Choice)p).right());
    }
    else if (p instanceof Compose){
      candidate = getDE(((Compose)p).left());
      if (candidate == null)
        candidate = getDE(((Compose)p).right());
    }
    return candidate;
  }

  public static Formula getDomainConst(Program p){
    return getDE(p).constraint();
  }
  

  public static ArrayList<Program> getChoices(Program p, ArrayList<Program> choices){
    //System.out.println(p.prettyString());
    if (p instanceof Loop){
      return getChoices(((Loop)p).child(), choices);
    }
    else if (p instanceof Choice){ // choices we want to pull will be in the form ?P; choice;, so parse accordingly
      Program left_choice = ((Choice)p).left();
      Program right_choice = ((Choice)p).right();
      if (left_choice instanceof Compose){
        choices.add(((Compose)left_choice).right());
      }
      else if(!(left_choice instanceof Choice)){ 
        choices.add(left_choice);
      }
      if (right_choice instanceof Compose){
        choices.add(((Compose)right_choice).right());
      }
      else if (!(right_choice instanceof Choice)){
        choices.add(right_choice);
      }
      choices = getChoices(((Choice)p).left(), choices);
      choices = getChoices(((Choice)p).right(), choices);
    }
    else if (p instanceof Compose){
      choices = getChoices(((Compose)p).left(), choices);
      choices = getChoices(((Compose)p).right(), choices);
    }
    return choices;
  }

}




