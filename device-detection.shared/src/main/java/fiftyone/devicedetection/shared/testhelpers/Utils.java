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

package fiftyone.devicedetection.shared.testhelpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.isNull;

public class Utils {
    /**
     * MAX depth to iterate when searching for files below specified root
     */
    public static int DEPTH_TO_SEARCH = 30;

    /**
     * Search for a resource (file) in a context {searchRoot}
     *
     * The file sought must end in the supplied string, where the components of the string
     * must wholly match the components of the location it is found in - per {@link Path#endsWith}
     *
     * When using this feature be aware that the first match is returned, not the closest in scope,
     * so try to use unique filenames.
     *
     * @param file       the qualified name of the resource to find
     * @param searchRoot which part of the directory structure to search
     * @return a File representing the resource
     * @throws IllegalArgumentException if the resource can't be found
     */
    public static File getFilePath(String file, String searchRoot) {
        try {
            Optional<Path> p = Files.find(
                            Paths.get(searchRoot),
                            DEPTH_TO_SEARCH,
                            (path, a) -> path.endsWith(file))
                    .findFirst();
            if (p.isPresent()) {
                return p.get().toFile();
            }
        } catch (IOException e) {
            // drop through
        }
        throw new IllegalArgumentException("Cannot find " + file + " in " + searchRoot);
    }

    /**
     * Search (the project directory) for a resource. If it doesn't exist throw an exception.
     *
     * If the system property project.root has been set, probably in
     * the maven surefire plugin config, then this is set as the scope of the search,
     * otherwise the scope is system property user.dir (the directory Java was launched from).
     *
     * @param file the qualified name of the resource to find
     * @return a File representing the resource
     * @see #getFilePath(String, String)
     */
    public static File getFilePath(String file) {
        String searchRoot = isNull(System.getProperty("project.root")) ?
                System.getProperty("user.dir") :
                System.getProperty("project.root");
        return getFilePath(file, searchRoot);
    }

    /**
     * Prefix for temp files that are created by {@link #jarFileHelper(String)}
     **/
    public static String TEMP_FILE_PREFIX = "DDTempFile";

    /**
     * Search the classpath for a resource. If it doesn't exist throw an exception.
     * If the file is in a jar then copy it to a temp file, so it can be used as an actual file.
     *
     * Callers might wish to delete the temp file created when one is created and
     * can assess whether this is the case as the name of the filename created will
     * start with {@link #TEMP_FILE_PREFIX} which they can alter to suit their needs.
     *
     * Files created here have the property deleteOnExit;
     *
     * @param file a filename
     * @return a File representing the resource
     */
    public static File jarFileHelper(String file) {
        // find the resource in the classpath
        URL resource = Objects.requireNonNull(Utils.class.getClassLoader().getResource(file),
                file + " could not be found on the classpath");
        try {
            return new File(resource.toURI());
        } catch (Exception e) {
            if (!resource.getPath().contains(".jar!"))
                throw new IllegalStateException(resource.getPath() + " can't be loaded", e);
        }
        // make a copy of the file if it is in a jar file (assumption is that if the
        // name matches the pattern then it is in a jar, if not, no harm is done
        // but an irrelevant tmp file may be created)
        String filePath;
        try {
            filePath = URLDecoder.decode(resource.getPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        // we will try to match the extension
        int index = filePath.lastIndexOf(".");
        String extension = index < 0 ? "tmp" : filePath.substring(index);
        try {
            Path temp = Files.createTempFile(TEMP_FILE_PREFIX, extension);
            try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(file)) {
                //noinspection ConstantConditions
                Files.copy(is, temp, REPLACE_EXISTING);
            }
            filePath = temp.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        File f = new File(filePath);
        f.deleteOnExit();
        return f;
    }
}