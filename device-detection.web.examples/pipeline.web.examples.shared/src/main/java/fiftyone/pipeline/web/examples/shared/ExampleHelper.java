package fiftyone.pipeline.web.examples.shared;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.data.AspectPropertyValueDefault;
import fiftyone.pipeline.engines.exceptions.PropertyMissingException;
import fiftyone.pipeline.engines.fiftyone.data.FiftyOneAspectPropertyMetaData;

import javax.servlet.http.HttpServlet;
import java.io.PrintWriter;

public class ExampleHelper {

    /**
     * Try to carry out a 'get' on a property getter, and catch a
     * {@link PropertyMissingException} to avoid the example breaking if the
     * resource key, or data file are not configured correctly by the user.
     *
     * @param getter to use e.g. DeviceData::getIsMobile()
     * @return value
     */
    public static <T> AspectPropertyValue<T> tryGet(PropertyGetter<T> getter) {
        try {
            return getter.getValue();
        } catch (PropertyMissingException e) {
            String message =
                    "The property '" + e.getPropertyName() + "' is not " +
                            "available in this data file. See data file options " +
                            "<a href=\"https://51degrees.com/pricing\">here</a>";
            AspectPropertyValue<T> result = new AspectPropertyValueDefault<>();
            result.setNoValueMessage(message);
            return result;
        }
    }

    public interface PropertyGetter<T> {
        AspectPropertyValue<T> getValue();
    }

    public static <T> String asString(AspectPropertyValue<T> property) {
        if (property.hasValue()) {
            return property.toString();
        } else {
            return property.getNoValueMessage();
        }
    }

    public static PrintWriter doHtmlPreamble(PrintWriter out, String title) {
        out.append("<!DOCTYPE html>" +
                        "<html>\n" +
                        "<head>\n")
                .format("<title>%s</title>\n", title)
                .append("<style> body {margin: 2em; font-family: sans-serif;}</style>\n" +
                        "</head>\n" +
                        "<body>\n");
        return out;
    }

    public static void doHtmlPostamble(PrintWriter out) {
        out.append("\n</body></html>\n");
    }

    public static PrintWriter doLiteRubric(PrintWriter out, DeviceDetectionHashEngine ddhe) {

        out.append("<div><h2>Lite Data File</h2>" +
                "<p><em><strong>Some values are missing</strong></em> maybe because " +
                "you are using a Lite data file included with this source distribution.\n" +
                "<p>The example requires an Enterprise data file to work fully. " +
                "You can get the Enterprise data file " +
                "<a href='https://51degrees.com/pricing'>here</a>\n" +
                "<p>Here are the properties contained in the Lite data file included with this source:<p>" +
                "<ul>");
        for (FiftyOneAspectPropertyMetaData p : ddhe.getProperties()) {
            out.format("<li>%s\n", p.getName());
        }
        out.append("</ul></div>\n");
        return out;
    }
}
