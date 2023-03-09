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

package fiftyone.devicedetection.examples.shared;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EvidenceHelper {

    /**
     * Prepare evidence for use in examples
     */
    public static List<Map<String, String>> setUpEvidence() {
        Map<String, String> evidence1 = new HashMap<>();
        evidence1.put("header.user-agent",
                "Mozilla/5.0 (Linux; Android 9; SAMSUNG SM-G960U) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "SamsungBrowser/10.1 Chrome/71.0.3578.99 Mobile " +
                        "Safari/537.36");
        Map<String, String> evidence2 = new HashMap<>();
        evidence2.put("header.user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/78.0.3904.108 Safari/537.36");
        Map<String, String> evidence3 = new HashMap<>();
        evidence3.put("header.user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/98.0.4758.102 Safari/537.36");
        evidence3.put("header.sec-ch-ua-mobile", "?0");
        evidence3.put("header.sec-ch-ua",
                "\" Not A; Brand\";v=\"99\", \"Chromium\";v=\"98\", " +
                        "\"Google Chrome\";v=\"98\"");
        evidence3.put("header.sec-ch-ua-platform", "\"Windows\"");
        evidence3.put("header.sec-ch-ua-platform-version", "\"14.0.0\"");
        List<Map<String, String>> evidence = new ArrayList<>();
        evidence.add(evidence1);
        evidence.add(evidence2);
        evidence.add(evidence3);

        return evidence;
    }

    /**
     * Load a Yaml file as a list of documents (each being a Map containing evidence)
     * @param yamlFile a yaml file
     * @param max maximum entries
     * @return a List
     * @throws IOException in case of error
     */
    public static List<Map<String, String>> getEvidenceList(File yamlFile, int max) throws IOException {
        return StreamSupport.stream(getEvidenceIterable(yamlFile).spliterator(), false)
                .limit(max)
                .collect(Collectors.toList());
    }

    /**
     * Create an Iterable<Map<String, String>> for reading documents from the passed yamlFile
     * @param yamlFile a yamlFile
     * @return an Iterable
     * @throws IOException for file errors
     */
    public static Iterable <Map<String, String>> getEvidenceIterable(File yamlFile) throws IOException {
        final Iterator<Object> objectIterator = new YamlReader(new FileReader(yamlFile)).readAll(Object.class);
        return geIterator(objectIterator);
    }
    public static Iterable <Map<String, String>> getEvidenceIterable(InputStream is) throws IOException {
        final Iterator<Object> objectIterator = new YamlReader(new InputStreamReader(is)).readAll(Object.class);
        return geIterator(objectIterator);
    }

    @SuppressWarnings("unchecked")
    private static Iterable<Map<String, String>> geIterator(Iterator<Object> objectIterator) {
        return () -> new Iterator<Map<String, String>>() {
            @Override
            public boolean hasNext() {
                return objectIterator.hasNext();
            }

            @SuppressWarnings("unchecked")
            @Override
            public Map<String, String> next() {
                return (Map<String, String>) objectIterator.next();
            }
        };
    }
}
