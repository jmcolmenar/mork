package es.urjc.etsii.grafo.algorithms;

import es.urjc.etsii.grafo.annotations.AutoconfigConstructor;
import es.urjc.etsii.grafo.create.Constructive;
import es.urjc.etsii.grafo.improve.Improver;
import es.urjc.etsii.grafo.io.Instance;
import es.urjc.etsii.grafo.solution.Solution;
import es.urjc.etsii.grafo.solution.metrics.Metrics;
import es.urjc.etsii.grafo.solution.metrics.MetricsManager;
import es.urjc.etsii.grafo.util.StringUtil;
import es.urjc.etsii.grafo.util.TimeControl;
import es.urjc.etsii.grafo.util.ValidationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Example simple algorithm, executes:
 * Constructive → (Optional, if present) Local Searches → (Optional, if present) Shake → If did not improve end
 *                                               ^_________________________________________|   else repeat
 * This class can be used to test all the pieces if they are working properly, or as a base for more complex algorithms
 *
 * @param <S> Solution class
 * @param <I> Instance class
 */
public class SimpleAlgorithm<S extends Solution<S,I>, I extends Instance> extends Algorithm<S,I>{

    private static Logger log = Logger.getLogger(SimpleAlgorithm.class.getName());

    protected final Constructive<S,I> constructive;
    protected final List<Improver<S, I>> improvers;

    /**
     * <p>Constructor for SimpleAlgorithm.</p>
     *
     * @param constructive a {@link Constructive} object.
     * @param improver a {@link es.urjc.etsii.grafo.improve.Improver} object.
     */
    @AutoconfigConstructor
    public SimpleAlgorithm(Constructive<S, I> constructive, Improver<S,I> improver){
        this("Simple", constructive, improver);
    }

    /**
     * <p>Constructor for SimpleAlgorithm.</p>
     *
     * @param constructive a {@link Constructive} object.
     */
    public SimpleAlgorithm(Constructive<S, I> constructive){
        this(constructive, Improver.nul());
    }

    /**
     * <p>Constructor for SimpleAlgorithm.</p>
     *
     * @param algorithmName Algorithm name, uniquely identifies the current algorithm. Tip: If you dont care about the name, generate a random one using {@link StringUtil#randomAlgorithmName()}
     * @param constructive a {@link Constructive} object.
     * @param improvers a {@link es.urjc.etsii.grafo.improve.Improver} object.
     */
    @SafeVarargs
    public SimpleAlgorithm(String algorithmName, Constructive<S, I> constructive, Improver<S,I>... improvers){
        super(algorithmName);
        this.constructive = constructive;
        if(improvers != null && improvers.length >= 1){
            this.improvers = Arrays.asList(improvers);
        } else {
            this.improvers = new ArrayList<>();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Algorithm: Execute a single construction and then all the local searchs a single time.
     */
    @Override
    public S algorithm(I instance) {
        var solution = this.newSolution(instance);
        solution = constructive.construct(solution);
        ValidationUtil.assertValidScore(solution);
        MetricsManager.addDatapoint(Metrics.BEST_OBJECTIVE_FUNCTION, solution.getScore());
        printStatus("Constructive", solution);
        solution = localSearch(solution);
        return solution;
    }

    /**
     * <p>localSearch.</p>
     *
     * @param solution a S object.
     * @return a S object.
     */
    protected S localSearch(S solution) {
        for (int i = 0; i < improvers.size(); i++) {
            if(TimeControl.isTimeUp()){
                return solution;
            }
            Improver<S, I> ls = improvers.get(i);
            solution = ls.improve(solution);
            ValidationUtil.assertValidScore(solution);
            MetricsManager.addDatapoint(Metrics.BEST_OBJECTIVE_FUNCTION, solution.getScore());
            printStatus("Improver " + i, solution);
        }
        return solution;
    }

    /**
     * <p>printStatus.</p>
     *
     * @param phase a {@link String} object.
     * @param solution a S object.
     */
    protected void printStatus(String phase, S solution){
        log.fine(() -> String.format("\t\t%s: %s", phase, solution));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "S{" +
                "c=" + constructive +
                ", i=" + improvers +
                '}';
    }
}
