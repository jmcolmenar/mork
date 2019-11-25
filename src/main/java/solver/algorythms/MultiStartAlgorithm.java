package solver.algorythms;

import io.Instance;
import solution.Solution;
import solver.create.Constructor;
import solver.improve.Improver;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class MultiStartAlgorithm extends Algorithm {

    private int executions;

    @SafeVarargs
    public MultiStartAlgorithm(int multiStartIterations, Supplier<Constructor> constructorSupplier, Supplier<Improver>... improvers) {
        super(constructorSupplier, improvers);
        this.executions = multiStartIterations;
    }

    @Override
    public Solution algorithm(Instance ins, int hardCostLimit, int k, double[] weights) {
        Solution s = null;
        for (int i = 0; i < executions; i++) {
            Solution temp = this.constructor.construct(ins, hardCostLimit, k, weights);
            //double bef = temp.getOptimalValue();
            improve(temp);
            //if(bef != temp.getOptimalValue())
            //    System.out.format("Before: %s - After: %s\n", bef, temp.getOptimalValue());
            s = temp.getBetterSolution(s);
        }
        return s;
    }

    private void improve(Solution s) {
        this.improvers.forEach(x -> x.improve(s, 10, TimeUnit.MINUTES));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "executions=" + executions +
                ", constructor=" + constructor +
                ", improvers=" + improvers +
                '}';
    }
}
