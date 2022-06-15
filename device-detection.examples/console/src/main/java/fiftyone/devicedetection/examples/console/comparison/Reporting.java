/*
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2022 51 Degrees Mobile Experts Limited, Davidson House,
 * Forbury Square, Reading, Berkshire, United Kingdom RG1 3EU.
 *
 * This Original Work is licensed under the European Union Public Licence
 *  (EUPL) v.1.2 and is subject to its terms as set out below.
 *
 *  If a copy of the EUPL was not distributed with this file, You can obtain
 *  one at https://opensource.org/licenses/EUPL-1.2.
 *
 *  The 'Compatible Licences' set out in the Appendix to the EUPL (as may be
 *  amended by the European Commission) shall be deemed incompatible for
 *  the purposes of the Work and the provisions of the compatibility
 *  clause in Article 5 of the EUPL shall not apply.
 *
 *   If using the Work as, or as part of, a network application, by
 *   including the attribution notice(s) required under Article 5 of the EUPL
 *   in the end user terms of the application under an appropriate heading,
 *   such notice(s) shall fulfill the requirements of that article.
 */

package fiftyone.devicedetection.examples.console.comparison;

import java.io.PrintWriter;
import java.util.List;

/**
 * Interface proves ability to compare outcomes of detection
 */
public interface Reporting {
    /**
     * Report on the results of benchmarks
     * @param executions a list of executions carried out
     */
    void report(List<Comparer.ExecutionResult> executions, PrintWriter writer);

    /**
     * report on overall execution time and that of the threads
     */

    class Minimal implements Reporting {
         public void report(List<Comparer.ExecutionResult> executions, PrintWriter writer){
            for (Comparer.ExecutionResult execution: executions) {
                writer.format("Vendor: %s, %,d millis\n", execution.solutionId, execution.elapsedMillis);
                for (Detection.BenchmarkResult benchmark: execution.benchmarkResults) {
                    writer.format("Thread: %,d millis. Count: %,d\n",
                            benchmark.elapsedMillis, benchmark.count);
                }
            }
        }

    }
}
