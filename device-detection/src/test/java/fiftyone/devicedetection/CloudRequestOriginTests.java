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

package fiftyone.devicedetection;

import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.services.HttpClient;
import fiftyone.pipeline.engines.services.HttpClientDefault;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class CloudRequestOriginTests {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { null, true },
            { "test.com", true },
            { "51degrees.com", false }
        });
    }

    private static final HttpClient http = new HttpClientDefault();

    private final String origin;
    private final boolean expectException;
 
    public CloudRequestOriginTests(String origin, 
        boolean expectException) {
         
        this.origin = origin;
        this.expectException = expectException;
    }

    /**
     * Verify that making requests using a resource key that
     * is limited to particular origins will fail or succeed
     * in the expected scenarios. 
     * This is an integration test that uses the live cloud service
     * so any problems with that service could affect the result
     * of this test.
     * @throws Exception
     */
    @Test
    public void ResourceKeyWithOrigin() throws Exception {    
                        
        String resourceKey = "AQS5HKcyVj6B8wNG2Ug";
        boolean exception = false;

        ILoggerFactory loggerFactory = mock(ILoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(loggerFactory.getLogger(anyString())).thenReturn(logger);

        try {
            Pipeline pipeline = new DeviceDetectionPipelineBuilder(loggerFactory, http)
                .useCloud(resourceKey)
                .setCloudRequestOrigin(this.origin)
                .build();

            try(FlowData data = pipeline.createFlowData()) {        
                data.addEvidence("query.user-agent",                         
                        "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL " +
                        "Build/OPD1.170816.004) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/80.0.3987.106 " +
                        "Mobile Safari/537.36")
                    .process();
            }
        } 
        catch (Exception ex) {
            exception = true;
            String exceptionText = ex.getMessage();
            for (Throwable inner: ex.getSuppressed()) {
                exceptionText = exceptionText + " inner: [" + inner.getMessage() + "]";
            }
            if(ex instanceof RuntimeException){
                Throwable cause = ((RuntimeException)ex).getCause();
                if(cause != null) {
                    exceptionText = exceptionText + " cause: [" + cause.getMessage() + "]";
                }
            }
            String originText = this.origin == null ? "" : this.origin;
            String expectedText = "This Resource Key is not authorized " +
                "for use with this domain: '" + originText + "'.";
            String failureMessage = "Exception did not contain expected text '" + 
                expectedText + "' (" + exceptionText + ")";
            assertTrue(failureMessage,
                exceptionText.contains(expectedText));
        }

        assertEquals(this.expectException, exception);
    }
}
