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

package fiftyone.devicedetection.pattern.engine.onpremise.interop;

import fiftyone.devicedetection.pattern.engine.onpremise.data.ValueMetaDataPattern;
import fiftyone.devicedetection.pattern.engine.onpremise.flowelements.DeviceDetectionPatternEngine;
import fiftyone.devicedetection.pattern.engine.onpremise.interop.swig.ValueMetaDataCollectionSwig;
import fiftyone.pipeline.engines.fiftyone.data.CollectionIterableBase;
import fiftyone.pipeline.engines.fiftyone.data.ValueMetaData;

public class ValueIterable
    extends CollectionIterableBase<ValueMetaData> {

    private DeviceDetectionPatternEngine engine;

    private ValueMetaDataCollectionSwig collection;

    public ValueIterable(
        DeviceDetectionPatternEngine engine,
        ValueMetaDataCollectionSwig collection) {
        super(collection.getSize());
        this.engine = engine;
        this.collection = collection;
    }

    @Override
    protected ValueMetaData get(long index) {
        return new ValueMetaDataPattern(engine, collection.getByIndex(index));
    }

    @Override
    public void close() throws Exception {
        collection.delete();
    }
}
