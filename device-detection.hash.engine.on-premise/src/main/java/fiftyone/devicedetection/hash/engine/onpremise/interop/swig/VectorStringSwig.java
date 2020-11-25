/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package fiftyone.devicedetection.hash.engine.onpremise.interop.swig;

public class VectorStringSwig extends java.util.AbstractList<String> implements AutoCloseable, java.util.RandomAccess {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected VectorStringSwig(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(VectorStringSwig obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        DeviceDetectionHashEngineModuleJNI.delete_VectorStringSwig(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  @Override
  public void close() {
    this.delete();
  }

  public VectorStringSwig(String[] initialElements) {
    this();
    reserve(initialElements.length);

    for (String element : initialElements) {
      add(element);
    }
  }

  public VectorStringSwig(Iterable<String> initialElements) {
    this();
    for (String element : initialElements) {
      add(element);
    }
  }

  public String get(int index) {
    return doGet(index);
  }

  public String set(int index, String e) {
    return doSet(index, e);
  }

  public boolean add(String e) {
    modCount++;
    doAdd(e);
    return true;
  }

  public void add(int index, String e) {
    modCount++;
    doAdd(index, e);
  }

  public String remove(int index) {
    modCount++;
    return doRemove(index);
  }

  protected void removeRange(int fromIndex, int toIndex) {
    modCount++;
    doRemoveRange(fromIndex, toIndex);
  }

  public int size() {
    return doSize();
  }

  public VectorStringSwig() {
    this(DeviceDetectionHashEngineModuleJNI.new_VectorStringSwig__SWIG_0(), true);
  }

  public VectorStringSwig(VectorStringSwig other) {
    this(DeviceDetectionHashEngineModuleJNI.new_VectorStringSwig__SWIG_1(VectorStringSwig.getCPtr(other), other), true);
  }

  public long capacity() {
    return DeviceDetectionHashEngineModuleJNI.VectorStringSwig_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    DeviceDetectionHashEngineModuleJNI.VectorStringSwig_reserve(swigCPtr, this, n);
  }

  public boolean isEmpty() {
    return DeviceDetectionHashEngineModuleJNI.VectorStringSwig_isEmpty(swigCPtr, this);
  }

  public void clear() {
    DeviceDetectionHashEngineModuleJNI.VectorStringSwig_clear(swigCPtr, this);
  }

  public VectorStringSwig(int count, String value) {
    this(DeviceDetectionHashEngineModuleJNI.new_VectorStringSwig__SWIG_2(count, value), true);
  }

  private int doSize() {
    return DeviceDetectionHashEngineModuleJNI.VectorStringSwig_doSize(swigCPtr, this);
  }

  private void doAdd(String x) {
    DeviceDetectionHashEngineModuleJNI.VectorStringSwig_doAdd__SWIG_0(swigCPtr, this, x);
  }

  private void doAdd(int index, String x) {
    DeviceDetectionHashEngineModuleJNI.VectorStringSwig_doAdd__SWIG_1(swigCPtr, this, index, x);
  }

  private String doRemove(int index) {
    return DeviceDetectionHashEngineModuleJNI.VectorStringSwig_doRemove(swigCPtr, this, index);
  }

  private String doGet(int index) {
    return DeviceDetectionHashEngineModuleJNI.VectorStringSwig_doGet(swigCPtr, this, index);
  }

  private String doSet(int index, String val) {
    return DeviceDetectionHashEngineModuleJNI.VectorStringSwig_doSet(swigCPtr, this, index, val);
  }

  private void doRemoveRange(int fromIndex, int toIndex) {
    DeviceDetectionHashEngineModuleJNI.VectorStringSwig_doRemoveRange(swigCPtr, this, fromIndex, toIndex);
  }

}
