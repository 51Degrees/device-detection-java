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

package fiftyone.devicedetection.examples;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ExampleBase {

    private final boolean printOutput;

    public ExampleBase(boolean printOutput) {
        this.printOutput = printOutput;
    }

    protected static Iterable<String> getUserAgents(
        String userAgentsFile,
        final int count) throws IOException {
        List<String> userAgents = Files.readAllLines(
            Paths.get(userAgentsFile),
            Charset.defaultCharset());
        return new UserAgentIterable(userAgents, count);

    }

    protected static Iterable<String> getUserAgents(
        String userAgentsFile,
        int count,
        int randomness) throws IOException {
        List<String> userAgents = Files.readAllLines(
            Paths.get(userAgentsFile),
            Charset.defaultCharset());
        return new UserAgentIterable(userAgents, count, randomness);
    }

    private static void addToMessage(StringBuilder message, String textToAdd, int depth) {
        for (int i = 0; i < depth; i++) {
            message.append("   ");
        }
        message.append(textToAdd);
        message.append("\n");
    }

    protected void print(String string) {
        if (printOutput) {
            System.out.print(string);
        }
    }

    protected void println(String string) {
        if (printOutput) {
            System.out.println(string);
        }
    }

    protected void println() {
        if (printOutput) {
            System.out.println();
        }
    }

    protected void printf(String format, Object... args) {
        if (printOutput) {
            System.out.printf(format, args);
        }
    }

    protected Iterable<String> report(
        List<String> input,
        int count,
        int maxDistinctUAs,
        int marks) {

        return new ReportIterable(input, count, maxDistinctUAs, marks);
    }

    protected void outputException(Throwable ex, int depth) {
        StringBuilder message = new StringBuilder();
        addToMessage(message, ex.getClass().getSimpleName() + " - " + ex.getMessage(), depth);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        addToMessage(message, writer.toString(), depth);
        println(message.toString());
        if (ex.getCause() != null) {
            outputException(ex.getCause(), depth++);
        }
    }

    static class UserAgentIterable implements Iterable<String> {

        private final List<String> userAgents;
        private final int count;
        private final int randomness;

        public UserAgentIterable(List<String> userAgents, int count) {
            this(userAgents, count, 0);
        }

        public UserAgentIterable(List<String> userAgents, int count, int randomness) {
            this.userAgents = userAgents;
            this.count = count;
            this.randomness = randomness;
        }

        @Override
        public Iterator<String> iterator() {
            return new UserAgentIterator(userAgents, count, randomness);
        }

        class UserAgentIterator implements Iterator<String> {
            private final List<String> userAgents;
            private final int count;
            private final int randomness;
            private final Random random = new Random();
            private int returned = 0;
            private int index = 0;

            public UserAgentIterator(
                List<String> userAgents,
                int count,
                int randomness) {
                this.userAgents = userAgents;
                this.count = count;
                this.randomness = randomness;
            }

            @Override
            public boolean hasNext() {
                return returned < count;
            }

            @Override
            public String next() {

                if (index > this.userAgents.size()) {
                    index = 0;
                }
                returned++;
                String userAgent = this.userAgents.get(index++);

                if (randomness > 0) {
                    char[] array = userAgent.toCharArray();
                    for (int i = 0; i < randomness; i++) {
                        int index = random.nextInt(array.length - 1);
                        array[index]++;
                    }
                    userAgent = new String(array);
                }

                return userAgent;

            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    protected class ReportIterable implements Iterable<String> {

        private final List<String> userAgents;
        private final int count;
        private final double increment;
        private final int maxDistinctUAs;
        private final Random rnd = new Random();
        int current = 0;

        public ReportIterable(List<String> input,
                              int count,
                              int maxDistinctUAs,
                              int marks) {
            this.userAgents = input;
            this.count = count;
            increment = count / marks;
            this.maxDistinctUAs = Math.min(maxDistinctUAs, count);
        }

        @Override
        public Iterator<String> iterator() {

            return new Iterator<String>() {

                @Override
                public boolean hasNext() {
                    return current < count;
                }

                @Override
                public String next() {
                    if (hasNext()) {
                        if (current % increment == 0) {
                            print("=");
                        }
                        current++;
                        return userAgents.get(rnd.nextInt(maxDistinctUAs));
                    } else {
                        throw new IndexOutOfBoundsException();
                    }
                }

                @Override
                public void remove() {

                }
            };
        }
    }
}
