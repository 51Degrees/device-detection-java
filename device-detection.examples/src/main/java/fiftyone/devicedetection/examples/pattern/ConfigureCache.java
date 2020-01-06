/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2019 51 Degrees Mobile Experts Limited, 5 Charlotte Close,
 * Caversham, Reading, Berkshire, United Kingdom RG4 7BY.
 *
 * This Original Work is licensed under the European Union Public Licence (EUPL) 
 * v.1.2 and is subject to its terms as set out below.
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

package fiftyone.devicedetection.examples.pattern;

import fiftyone.caching.PutCache;
import fiftyone.devicedetection.examples.ExampleBase;
import fiftyone.devicedetection.examples.ProgramBase;
import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngine;
import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngineBuilder;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.core.flowelements.PipelineBuilder;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.configuration.CacheConfiguration;

import java.io.IOException;

/**
 * @example pattern/ConfigureCache.java
 *
 * Configure cache example of using 51Degrees device detection.
 *
 * The example shows how to:
 *
 * 1. Implement a Guava cache adaptor for the Cache interface in fiftyone.caching.
 * ```
 * public static class GuavaCacheAdaptor<K, V> implements fiftyone.caching.Cache<K, V> {
 *     protected final com.google.common.cache.Cache<K, V> cache;
 *     public GuavaCacheAdaptor(com.google.common.cache.Cache<K, V> cache) {
 *         this.cache = cache;
 *     }
 *     @Override
 *     public V get(K key) {
 *         return cache.getIfPresent(key);
 *     }
 *     @Override
 *     public void close() throws IOException {
 *         cache.cleanUp();
 *     }
 * }
 * ```
 *
 * 2. Extend this adaptor to implement the PutCache interface in fiftyone.caching.
 * ```
 * public static class PutCacheAdaptor<K, V> extends GuavaCacheAdaptor<K, V> implements PutCache<K, V> {
 *     public PutCacheAdaptor(com.google.common.cache.Cache<K, V> cache) {
 *         super(cache);
 *     }
 *     @Override
 *     public void put(K key, V value) {
 *         cache.put(key, value);
 *     }
 * }
 * ```
 *
 * 3. Implement the CacheBuilder interface in fiftyone.caching.
 * ```
 * static class GuavaCacheBuilder implements fiftyone.caching.CacheBuilder {
 *     @Override
 *     public <K, V> fiftyone.caching.Cache<K, V> build(fiftyone.caching.Cache<K, V> c, int cacheSize) {
 *         com.google.common.cache.Cache guavaCache =
 *             com.google.common.cache.CacheBuilder.newBuilder()
 *             .initialCapacity(cacheSize)
 *             .maximumSize(cacheSize)
 *             .concurrencyLevel(5)
 *             .build();
 *         return new PutCacheAdaptor(guavaCache);
 *     }
 * }
 * ```
 *
 * 4. Build an on-premise Pattern engine configured to use the Guava cache.
 * ```
 * DeviceDetectionPatternEngine engine = new DeviceDetectionPatternEngineBuilder()
 *     .setAutoUpdate(false)
 *     .setCache(new CacheConfiguration(new GuavaCacheBuilder(), 100000))
 *     .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
 *     .build(dataFile, false);
 * ```
 */

public class ConfigureCache extends ProgramBase {
    public static void main(String[] args) throws Exception {
        String dataFile = args.length > 0 ? args[0] :
            getDefaultFilePath("51Degrees-LiteV3.2.dat").getAbsolutePath();

        new Example(true).run(dataFile);
        System.out.println("Complete. Press enter to exit.");
        System.in.read();
    }

    /**
     * Class adapting a google Guava cache to the 51Degrees Cache interface.
     * This allows it to be used by the device detection API.
     */
    public static class GuavaCacheAdaptor<K, V> implements fiftyone.caching.Cache<K, V> {
        protected final com.google.common.cache.Cache<K, V> cache;

        public GuavaCacheAdaptor(com.google.common.cache.Cache<K, V> cache) {
            this.cache = cache;
        }

        @Override
        public V get(K key) {
            return cache.getIfPresent(key);
        }

        @Override
        public void close() throws IOException {
            cache.cleanUp();
        }
    }

    public static class PutCacheAdaptor<K, V> extends GuavaCacheAdaptor<K, V> implements PutCache<K, V> {

        public PutCacheAdaptor(com.google.common.cache.Cache<K, V> cache) {
            super(cache);
        }

        @Override
        public void put(K key, V value) {
            cache.put(key, value);
        }
    }

    /**
     * For a cache to be used by the Pipeline, it also needs a corresponding builder
     * that implements the CacheBuidler interface.
     * This is the CacheBuilder implementation for the Guava cache.
     */
    static class GuavaCacheBuilder implements fiftyone.caching.CacheBuilder {
        @Override
        public <K, V> fiftyone.caching.Cache<K, V> build(fiftyone.caching.Cache<K, V> c, int cacheSize) {
            com.google.common.cache.Cache guavaCache =
                com.google.common.cache.CacheBuilder.newBuilder()
                    .initialCapacity(cacheSize)
                    .maximumSize(cacheSize)
                    .concurrencyLevel(5) // set to number of threads that can access cache at same time
                    .build();
            return new PutCacheAdaptor(guavaCache);
        }
    }

    public static class Example extends ExampleBase {
        public Example(boolean printOutput) {
            super(printOutput);
        }

        public void run(String dataFile) throws Exception {

            DeviceDetectionPatternEngine engine =
                new DeviceDetectionPatternEngineBuilder()
                    .setAutoUpdate(false)
                    .setCache(
                        new CacheConfiguration(
                            new GuavaCacheBuilder(),
                            100000))
                    .setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
                    .build(dataFile, false);

            try (Pipeline pipeline = new PipelineBuilder()
                .addFlowElement(engine)
                .setAutoCloseElements(true)
                .build()) {
                // Use the provider to obtain a match on a User-Agent string
                // of a iPhone mobile device.
                String mobileUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone "
                    + "OS 7_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) "
                    + "Version/7.0 Mobile/11D167 Safari/9537.53";
                FlowData flowData = pipeline.createFlowData();
                flowData.addEvidence("header.user-agent", mobileUserAgent)
                    .process();

                println("IsMobile: " + flowData.get(DeviceData.class).getIsMobile().getValue());
            }
        }
    }

}
