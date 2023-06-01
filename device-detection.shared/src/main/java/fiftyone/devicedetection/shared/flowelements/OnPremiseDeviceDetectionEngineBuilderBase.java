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

package fiftyone.devicedetection.shared.flowelements;

import fiftyone.pipeline.engines.data.AspectData;
import fiftyone.pipeline.engines.data.AspectPropertyMetaData;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOneAspectEngine;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOneOnPremiseAspectEngineBuilderBase;
import fiftyone.pipeline.engines.services.DataUpdateService;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * Base builder class for 51Degrees on-premise device detection engines.
 * @param <TBuilder> the specific builder type to use as the return type from
 *                  the fluent builder methods
 * @param <TEngine> the type of the engine that this builder will build
 */
public abstract class OnPremiseDeviceDetectionEngineBuilderBase<
    TBuilder extends OnPremiseDeviceDetectionEngineBuilderBase<TBuilder, TEngine>,
    TEngine extends FiftyOneAspectEngine<? extends AspectData, ? extends AspectPropertyMetaData>>
    extends FiftyOneOnPremiseAspectEngineBuilderBase<TBuilder, TEngine> {

    /**
     * Default constructor which uses the {@link ILoggerFactory} implementation
     * returned by {@link LoggerFactory#getILoggerFactory()}.
     */
    public OnPremiseDeviceDetectionEngineBuilderBase() {
        super();
    }

    /**
     * Construct a new instance using the {@link ILoggerFactory} supplied.
     * @param loggerFactory the logger factory to use
     */
    public OnPremiseDeviceDetectionEngineBuilderBase(
        ILoggerFactory loggerFactory) {
        super(loggerFactory);
    }

    /**
     * Construct a new instance using the {@link ILoggerFactory} and
     * {@link DataUpdateService} supplied.
     * @param loggerFactory the logger factory to use
     * @param dataUpdateService the {@link DataUpdateService} to use when
     *                          automatic updates happen on the data file
     */
    public OnPremiseDeviceDetectionEngineBuilderBase(
        ILoggerFactory loggerFactory,
        DataUpdateService dataUpdateService) {
        super(loggerFactory, dataUpdateService);
    }

    /**
     * Set the maximum difference to allow when processing HTTP headers.
     * The meaning of difference depends on the Device Detection API being
     * used. The difference is the difference in hash value between the
     * hash that was found, and the hash that is being searched for. By
     * default this is 0.
     * @param difference to allow
     * @return this builder
     */
    public abstract TBuilder setDifference(int difference);

    /**
     * If set to false, a non-matching User-Agent will result in
     * properties without set values. If set to true, a non-matching
     * User-Agent will cause the 'default profiles' to be returned. This
     * means that properties will always have values (i.e. no need to
     * check {@link AspectPropertyValue#hasValue()}) but some may be inaccurate.
     * By default, this is false.
     * @param allow true if results with no matched hash nodes should be
     *              considered valid
     * @return this builder
     */
    public abstract TBuilder setAllowUnmatched(boolean allow);
}