/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package fiftyone.devicedetection.hash.engine.onpremise.interop.swig;

public class MapStringStringSwig extends java.util.AbstractMap<String, String> implements AutoCloseable {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected MapStringStringSwig(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(MapStringStringSwig obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        DeviceDetectionHashEngineModuleJNI.delete_MapStringStringSwig(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  @Override
  public void close() {
    this.delete();
  }


  public int size() {
    return sizeImpl();
  }

  public boolean containsKey(java.lang.Object key) {
    if (!(key instanceof String)) {
      return false;
    }

    return containsImpl((String)key);
  }

  public String get(java.lang.Object key) {
    if (!(key instanceof String)) {
      return null;
    }

    Iterator itr = find((String) key);
    if (itr.isNot(end())) {
      return itr.getValue();
    }

    return null;
  }

  public String put(String key, String value) {
    Iterator itr = find(key);
    if (itr.isNot(end())) {
      String oldValue = itr.getValue();
      itr.setValue(value);
      return oldValue;
    } else {
      putUnchecked(key, value);
      return null;
    }
  }

  public String remove(java.lang.Object key) {
    if (!(key instanceof String)) {
      return null;
    }

    Iterator itr = find((String) key);
    if (itr.isNot(end())) {
      String oldValue = itr.getValue();
      removeUnchecked(itr);
      return oldValue;
    } else {
      return null;
    }
  }

  public java.util.Set<Entry<String, String>> entrySet() {
    java.util.Set<Entry<String, String>> setToReturn =
        new java.util.HashSet<>();

    Iterator itr = begin();
    final Iterator end = end();
    while (itr.isNot(end)) {
      setToReturn.add(new Entry<String, String>() {
        private Iterator iterator;

        private Entry<String, String> init(Iterator iterator) {
          this.iterator = iterator;
          return this;
        }

        public String getKey() {
          return iterator.getKey();
        }

        public String getValue() {
          return iterator.getValue();
        }

        public String setValue(String newValue) {
          String oldValue = iterator.getValue();
          iterator.setValue(newValue);
          return oldValue;
        }
      }.init(itr));
      itr = itr.getNextUnchecked();
    }

    return setToReturn;
  }

  public MapStringStringSwig() {
    this(DeviceDetectionHashEngineModuleJNI.new_MapStringStringSwig__SWIG_0(), true);
  }

  public MapStringStringSwig(MapStringStringSwig other) {
    this(DeviceDetectionHashEngineModuleJNI.new_MapStringStringSwig__SWIG_1(MapStringStringSwig.getCPtr(other), other), true);
  }

  static protected class Iterator {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;
  
    protected Iterator(long cPtr, boolean cMemoryOwn) {
      swigCMemOwn = cMemoryOwn;
      swigCPtr = cPtr;
    }
  
    protected static long getCPtr(Iterator obj) {
      return (obj == null) ? 0 : obj.swigCPtr;
    }
  
    @SuppressWarnings("deprecation")
    protected void finalize() {
      delete();
    }
  
    public synchronized void delete() {
      if (swigCPtr != 0) {
        if (swigCMemOwn) {
          swigCMemOwn = false;
          DeviceDetectionHashEngineModuleJNI.delete_MapStringStringSwig_Iterator(swigCPtr);
        }
        swigCPtr = 0;
      }
    }
  
    private MapStringStringSwig.Iterator getNextUnchecked() {
      return new MapStringStringSwig.Iterator(DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_Iterator_getNextUnchecked(swigCPtr, this), true);
    }
  
    private boolean isNot(MapStringStringSwig.Iterator other) {
      return DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_Iterator_isNot(swigCPtr, this, MapStringStringSwig.Iterator.getCPtr(other), other);
    }
  
    private String getKey() {
      return DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_Iterator_getKey(swigCPtr, this);
    }
  
    private String getValue() {
      return DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_Iterator_getValue(swigCPtr, this);
    }
  
    private void setValue(String newValue) {
      DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_Iterator_setValue(swigCPtr, this, newValue);
    }
  
  }

  public boolean isEmpty() {
    return DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_isEmpty(swigCPtr, this);
  }

  public void clear() {
    DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_clear(swigCPtr, this);
  }

  private MapStringStringSwig.Iterator find(String key) {
    return new MapStringStringSwig.Iterator(DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_find(swigCPtr, this, key), true);
  }

  private MapStringStringSwig.Iterator begin() {
    return new MapStringStringSwig.Iterator(DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_begin(swigCPtr, this), true);
  }

  private MapStringStringSwig.Iterator end() {
    return new MapStringStringSwig.Iterator(DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_end(swigCPtr, this), true);
  }

  private int sizeImpl() {
    return DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_sizeImpl(swigCPtr, this);
  }

  private boolean containsImpl(String key) {
    return DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_containsImpl(swigCPtr, this, key);
  }

  private void putUnchecked(String key, String value) {
    DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_putUnchecked(swigCPtr, this, key, value);
  }

  private void removeUnchecked(MapStringStringSwig.Iterator itr) {
    DeviceDetectionHashEngineModuleJNI.MapStringStringSwig_removeUnchecked(swigCPtr, this, MapStringStringSwig.Iterator.getCPtr(itr), itr);
  }

}
