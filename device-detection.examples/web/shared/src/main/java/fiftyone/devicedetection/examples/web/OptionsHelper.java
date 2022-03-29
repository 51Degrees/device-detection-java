package fiftyone.devicedetection.examples.web;

import fiftyone.pipeline.core.configuration.ElementOptions;
import fiftyone.pipeline.core.configuration.PipelineOptions;
import java.io.*;

import static fiftyone.pipeline.core.configuration.PipelineOptionsFactory.getOptionsFromFile;

/**
 * Helper allows retrieving values from and manipulating {@link PipelineOptions}
 */
public class OptionsHelper {
    private final PipelineOptions pipelineOptions;

    public OptionsHelper(String file) throws Exception {
        pipelineOptions = getOptionsFromFile(file);
    }

    public OptionsHelper(File file) throws Exception {
        pipelineOptions = getOptionsFromFile(file);
    }

    public String find(String builderName, String buildParameter){
        for (ElementOptions opts: pipelineOptions.elements) {
            if (opts.builderName.equals(builderName)) {
                return (String) opts.buildParameters.get(buildParameter);
            }
        }
        return null;
    }

    public boolean replace(String builderName, String buildParameter, String value){
        for (ElementOptions opts: pipelineOptions.elements) {
            if (opts.builderName.equals(builderName)) {
                    opts.buildParameters.put(buildParameter, value);
                    return true;
            }
        }
        return false;
    }
}
