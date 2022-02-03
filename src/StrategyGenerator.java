import edu.cmu.cs.ls.keymaerax.bellerophon.BelleExpr;
import edu.cmu.cs.ls.keymaerax.core.Provable;
import edu.cmu.cs.ls.keymaerax.core.Sequent;

public interface StrategyGenerator {
    BelleExpr getTactic(Sequent s, Provable p);
}
