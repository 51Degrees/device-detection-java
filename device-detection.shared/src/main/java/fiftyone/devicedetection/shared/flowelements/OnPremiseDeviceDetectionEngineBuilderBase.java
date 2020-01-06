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

package fiftyone.devicedetection.shared.flowelements;

import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOneAspectEngine;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOneOnPremiseAspectEngineBuilderBase;
import fiftyone.pipeline.engines.services.DataUpdateService;
import org.slf4j.ILoggerFactory;

public abstract class OnPremiseDeviceDetectionEngineBuilderBase<
    TBuilder extends OnPremiseDeviceDetectionEngineBuilderBase<TBuilder, TEngine>,
    TEngine extends FiftyOneAspectEngine>
    extends FiftyOneOnPremiseAspectEngineBuilderBase<TBuilder, TEngine> {

    public OnPremiseDeviceDetectionEngineBuilderBase() {
        super();
    }

    public OnPremiseDeviceDetectionEngineBuilderBase(
        ILoggerFactory loggerFactory) {
        super(loggerFactory);
    }

    public OnPremiseDeviceDetectionEngineBuilderBase(
        ILoggerFactory loggerFactory,
        DataUpdateService dataUpdateService) {
        super(loggerFactory, dataUpdateService);
    }

    /**
     * Set the maximum difference to allow when processing HTTP headers.
     * The meaning of difference depends on the Device Detection API being
     * used.
     * For Pattern: The difference is a combination of the difference in
     *              character position of matched substrings, and the
     *              difference in ASCII value of each character of matched
     *              substrings. By default this is 10.
     * For Hash: The difference is the difference in hash value between
     *           the hash that was found, and the hash that is being
     *           searched for. By default this is 0.
     * @param difference to allow
     * @return this builder
     */
    public abstract TBuilder setDifference(int difference);

    /**
     * If set to false, a non-matching User-Agent will result in
     * properties without set values. If set to true, a non-matching
     * User-Agent will cause the 'default profiles' to be returned. This
     * means that properties will always have values (i.e. no need to
     * check .HasValue) but some may be inaccurate. By default, this is
     * false.
     * @param allow true if results with no matched hash nodes should be
     *              considered valid
     * @return this builder
     */
    public abstract TBuilder setAllowUnmatched(boolean allow);
}