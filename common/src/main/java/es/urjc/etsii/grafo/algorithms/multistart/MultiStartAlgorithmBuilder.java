package es.urjc.etsii.grafo.algorithms.multistart;

import es.urjc.etsii.grafo.algorithms.Algorithm;
import es.urjc.etsii.grafo.io.Instance;
import es.urjc.etsii.grafo.solution.Solution;
import es.urjc.etsii.grafo.util.StringUtil;

/**
 * Multi-start algorithm builder based on Java Builder Pattern
 *
 * @param <S> type of the solution of the problem
 * @param <I> type of the instance of the problem
 */
public class MultiStartAlgorithmBuilder<S extends Solution<S, I>, I extends Instance> {

    /**
     * Name of the algorithm
     */
    private String name = StringUtil.randomAlgorithmName();

    /**
     * Maximum number of iterations, set by default to 1073741823
     */
    private int maxIterations = Integer.MAX_VALUE / 2;

    /**
     * Minimum number of iterations, set by default to one
     */
    private int minIterations = 1;

    /**
     * Maximum number of iterations without improving, set by default to 1073741823
     */
    private int maxIterationsWithoutImproving = Integer.MAX_VALUE / 2;

    /**
     * Builder for {@link MultiStartAlgorithm}
     */
    public MultiStartAlgorithmBuilder() {}

    /**
     * <p>withAlgorithmName.</p>
     *
     * @param name name of the algorithm
     * @return MultiStartAlgorithmBuilder
     */
    public MultiStartAlgorithmBuilder<S, I> withAlgorithmName(String name) {
        this.name = name;
        return this;
    }


    /**
     * <p>withMaxIterations.</p>
     *
     * @param maxIterations maximum number of iteration of the algorithm
     * @return MultiStartAlgorithmBuilder
     */
    public MultiStartAlgorithmBuilder<S, I> withMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    /**
     * <p>withMinIterations.</p>
     *
     * @param minIterations minimum number of iterations of the algorithm
     * @return MultiStartAlgorithmBuilder
     */
    public MultiStartAlgorithmBuilder<S, I> withMinIterations(int minIterations) {
        this.minIterations = minIterations;
        return this;
    }

    /**
     * <p>withMaxIterationsWithoutImproving.</p>
     *
     * @param maxIterationsWithoutImproving maximum number of iterations without improving
     * @return MultiStartAlgorithmBuilder
     */
    public MultiStartAlgorithmBuilder<S, I> withMaxIterationsWithoutImproving(int maxIterationsWithoutImproving) {
        this.maxIterationsWithoutImproving = maxIterationsWithoutImproving;
        return this;
    }

    /**
     * build a multistart algorithm with the current configuration
     *
     * @param algorithm algorithm
     * @return the multistart algorithm
     */
    public MultiStartAlgorithm<S, I> build(Algorithm<S, I> algorithm) {
        return new MultiStartAlgorithm<>(name, algorithm, maxIterations, minIterations, maxIterationsWithoutImproving);
    }
}
