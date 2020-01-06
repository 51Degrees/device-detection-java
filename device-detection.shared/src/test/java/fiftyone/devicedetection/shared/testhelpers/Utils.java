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
import java.io.FilenameFilter;

public class Utils {
    public static File getFilePath(String searchPattern) {
        return getFilePath(new SearchFilter(searchPattern));
    }

    public static File getFilePath(FilenameFilter searchFilter) {
        File last = null;
        File directory = new File(System.getProperty("user.dir"));
        while (directory != null) {
            File matchedFile = null;
            for (File file : directory.listFiles()) {
                if (file.isDirectory() && (last == null || file.equals(last) == false)) {
                    matchedFile = find(file, searchFilter);
                    if (matchedFile != null) {
                        return matchedFile;
                    }
                }
            }

            last = directory;
            directory = directory.getParentFile();
        }
        return null;
    }

    static File find(File directory, FilenameFilter searchFilter) {
        File result = null;
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                result = find(file, searchFilter);
                if (result != null) {
                    return result;
                }
            } else if (searchFilter.accept(file, file.getName())) {
                return file;
            }
        }
        return null;
    }

    private static class SearchFilter implements FilenameFilter {

        private final String searchPattern;

        public SearchFilter(String searchPattern) {
            this.searchPattern = searchPattern;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.contains(searchPattern);
        }
    }
}
