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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExampleBase {

    private final boolean printOutput;

    public ExampleBase(boolean printOutput) {
        this.printOutput = printOutput;
    }

    protected static Iterable<String> getUserAgents(
        String userAgentsFile,
        final int count) throws IOException {
        Path path = Paths.get(userAgentsFile);
        if (Files.exists(path) == false) {
            throw new IOException(String.format("File '%s' not found", path));
        }
        return new UserAgentIterable(path, count);
    }

    protected static Iterable<String> getUserAgents(
        String userAgentsFile,
        int count,
        int randomness) throws IOException {
        Path path = Paths.get(userAgentsFile);
        if (Files.exists(path) == false) {
            throw new IOException(String.format("File '%s' not found", path));
        }
        return new UserAgentIterable(path, count, randomness);
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
        Iterator<String> input,
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

        private final Path userAgents;
        private final int count;
        private final int randomness;

        public UserAgentIterable(Path userAgents, int count) {
            this(userAgents, count, 0);
        }

        public UserAgentIterable(Path userAgents, int count, int randomness) {
            this.userAgents = userAgents;
            this.count = count;
            this.randomness = randomness;
        }

        @Override
        public Iterator<String> iterator() {
            try {
                return new UserAgentIterator(userAgents, count, randomness);
            } catch (IOException ex) {
                Logger.getLogger(ExampleBase.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
            return null;
        }

        class UserAgentIterator implements Iterator<String> {
            private final Path userAgents;
            private final int count;
            private final int randomness;
            private final Random random = new Random();
            private int returned = 0;
            private FileReader reader = null;
            private BufferedReader buffer = null;

            public UserAgentIterator(
                Path userAgents,
                int count,
                int randomness) throws FileNotFoundException, IOException {
                this.userAgents = userAgents;
                this.count = count;
                this.randomness = randomness;
                createReader();
            }

            @Override
            public boolean hasNext() {
                return returned < count;
            }

            @Override
            public String next() {
                String userAgent = null;
                if (randomness > 0 ) {
                    int skip = random.nextInt(randomness);
                    for (int i = 0; i < skip; i++) {
                        try {
                            nextLine();
                        } catch (IOException ex) {
                            Logger.getLogger(ExampleBase.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                }
                try {
                    userAgent = nextLine();
                    returned++;
                } catch (IOException ex) {
                    Logger.getLogger(ExampleBase.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                return userAgent;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            private String nextLine() throws IOException {
                String line = buffer.readLine();
                if (line == null) {
                    createReader();
                    line = buffer.readLine();
                }
                return line;
            }
            
            private void createReader() throws FileNotFoundException, IOException {
                if (buffer != null) {
                    buffer.close();
                }
                if (reader != null) {
                    reader.close();
                }
                reader = new FileReader(userAgents.toFile());
                buffer = new BufferedReader(reader);
            }
        }
    }

    protected class ReportIterable implements Iterable<String> {

        private final Iterator<String> userAgents;
        private final int count;
        private final double increment;
        private final int maxDistinctUAs;
        private final Random rnd = new Random();
        int current = 0;

        public ReportIterable(Iterator<String> input,
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
                        return userAgents.next();
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
