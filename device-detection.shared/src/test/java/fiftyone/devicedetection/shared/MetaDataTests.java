/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2023 51 Degrees Mobile Experts Limited, Davidson House,
 * Forbury Square, Reading, Berkshire, United Kingdom RG1 3EU.
 *
 * This Original Work is licensed under the European Union Public Licence
 * (EUPL) v.1.2 and is subject to its terms as set out below.
 *
 * If a copy of the EUPL was not distributed with this file, You can obtain
 * one at https://opensource.org/licenses/EUPL-1.2.
 *
 * The 'Compatible Licences' set out in the Appendix to the EUPL (as may be
 * amended by the European Commission) shall be deemed incompatible for
 * the purposes of the Work and the provisions of the compatibility
 * clause in Article 5 of the EUPL shall not apply.
 *
 * If using the Work as, or as part of, a network application, by
 * including the attribution notice(s) required under Article 5 of the EUPL
 * in the end user terms of the application under an appropriate heading,
 * such notice(s) shall fulfill the requirements of that article.
 * ********************************************************************* */

package fiftyone.devicedetection.shared;

import fiftyone.devicedetection.shared.testhelpers.Wrapper;
import fiftyone.devicedetection.shared.testhelpers.data.MetaDataHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class MetaDataTests {

    private static List<Future<Integer>> startHashingThreads(
        int threadCount,
        final Wrapper wrapper,
        final MetaDataHasher hasher,
        ExecutorService executorService) throws InterruptedException {

        List<Callable<Integer>> callables = new ArrayList<>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            callables.add(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {

                    int hash = 0;
                    hash = hasher.hashProperties(hash, wrapper);
                    hash = hasher.hashValues(hash, wrapper);
                    hash = hasher.hashComponents(hash, wrapper);
                    hash = hasher.hashProfiles(hash, wrapper);
                    return hash;
                }
            });
        }
        return executorService.invokeAll(callables);
    }

    public static List<Integer> reload(
        final Wrapper wrapper,
        MetaDataHasher hasher,
        ExecutorService executorService) throws InterruptedException, ExecutionException {
        int threadCount = 4;
        final int[] refreshes = {0};
        final AtomicBoolean done = new AtomicBoolean(false);
        Future<?> reloader = executorService.submit(new Runnable() {
           @Override
            public void run() {
                synchronized(done){
                while (done.get() == false) {
                    wrapper.getEngine().refreshData(wrapper.getEngine().getDataFileMetaData().getIdentifier());
                    refreshes[0]++;
                }
               }
            }
        });
        List<Future<Integer>> threads = startHashingThreads(
            threadCount,
            wrapper,
            hasher,
            executorService);
        List<Integer> hashes = new ArrayList<>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            hashes.add(threads.get(i).get());
        }
        done.set(true);
        reloader.wait();
        return hashes;
    }
}
