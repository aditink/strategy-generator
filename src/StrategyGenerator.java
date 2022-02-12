import edu.cmu.cs.ls.keymaerax.bellerophon.BelleExpr;
import edu.cmu.cs.ls.keymaerax.core.Provable;
import edu.cmu.cs.ls.keymaerax.core.Sequent;
//import edu.cmu.cs.ls.keymaerax.parser;

public interface StrategyGenerator {
    void getTactic(Sequent s);//, Provable p);
}
