/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehcache.internal.store;

import org.ehcache.Cache;
import org.ehcache.config.StoreConfigurationImpl;
import org.ehcache.exceptions.CacheAccessException;
import org.ehcache.function.Predicates;
import org.ehcache.spi.cache.Store;
import org.ehcache.spi.test.SPITest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test the {@link org.ehcache.spi.cache.Store#remove(K key, V value)} contract of the
 * {@link org.ehcache.spi.cache.Store Store} interface.
 * <p/>
 *
 * @author Aurelien Broszniowski
 */

public class StoreRemoveKeyValueTest<K, V> extends SPIStoreTester<K, V> {

  public StoreRemoveKeyValueTest(final StoreFactory<K, V> factory) {
    super(factory);
  }

  @SPITest
  public void removeEntryForKeyIfMappedToValue()
      throws IllegalAccessException, InstantiationException, CacheAccessException {
    final Store<K, V> kvStore = factory.newStore(new StoreConfigurationImpl<K, V>(
        factory.getKeyType(), factory.getValueType(), null, Predicates.<Cache.Entry<K, V>>all(), null));

    K key = factory.getKeyType().newInstance();
    V value = factory.getValueType().newInstance();

    kvStore.put(key, value);

    K equalKey = factory.getKeyType().newInstance();
    V equalValue = factory.getValueType().newInstance();

    //TODO : equality can not be guaranteed with generics - test is currently not reliable
    assertThat(key.equals(equalKey), is(true));
    assertThat(value.equals(equalValue), is(true));

    kvStore.remove(equalKey, equalValue);

    assertThat(kvStore.containsKey(key), is(false));
  }

  @SPITest
  public void doNothingForKeyNotMappedToValue()
      throws IllegalAccessException, InstantiationException, CacheAccessException {
    final Store<K, V> kvStore = factory.newStore(new StoreConfigurationImpl<K, V>(
        factory.getKeyType(), factory.getValueType(), null, Predicates.<Cache.Entry<K, V>>all(), null));

    K key = factory.getKeyType().newInstance();
    V value = factory.getValueType().newInstance();

    assertThat(kvStore.containsKey(key), is(false));

    try {
      boolean isRemoved = kvStore.remove(key, value);
      assertThat(isRemoved, is(false));
    } catch (CacheAccessException e) {
      throw new AssertionError(e);
    }
  }

  @SPITest
  public void doNothingForWrongValue()
      throws IllegalAccessException, InstantiationException, CacheAccessException {
    final Store<K, V> kvStore = factory.newStore(new StoreConfigurationImpl<K, V>(
        factory.getKeyType(), factory.getValueType(), null, Predicates.<Cache.Entry<K, V>>all(), null));

    K key = factory.getKeyType().newInstance();
    V value = factory.getValueType().newInstance();

    kvStore.put(key, value);

    //TODO : non-equality can not be guaranteed here either - test is currently not reliable
    V notEqualValue = factory.getValueType().newInstance();

    assertThat(value.equals(notEqualValue), is(false));

    try {
      boolean isRemoved = kvStore.remove(key, value);
      assertThat(isRemoved, is(false));
    } catch (CacheAccessException e) {
      throw new AssertionError(e);
    }
  }

  @SPITest
  public void returnTrueIfValueWasRemoved()
      throws IllegalAccessException, InstantiationException, CacheAccessException {
    final Store<K, V> kvStore = factory.newStore(new StoreConfigurationImpl<K, V>(
        factory.getKeyType(), factory.getValueType(), null, Predicates.<Cache.Entry<K,V>>all(), null));

    K key = factory.getKeyType().newInstance();
    V value = factory.getValueType().newInstance();

    kvStore.put(key, value);

    assertThat(kvStore.containsKey(key), is(true));

    boolean removed = kvStore.remove(key, value);

    assertThat(removed, is(true));
  }

  @SPITest
  public void returnFalseIfValueWasNotRemoved()
      throws IllegalAccessException, InstantiationException, CacheAccessException {
    final Store<K, V> kvStore = factory.newStore(new StoreConfigurationImpl<K, V>(
        factory.getKeyType(), factory.getValueType(), null, Predicates.<Cache.Entry<K,V>>all(), null));

    K key = factory.getKeyType().newInstance();
    V value = factory.getValueType().newInstance();

    assertThat(kvStore.containsKey(key), is(false));

    boolean removed = kvStore.remove(key, value);

    assertThat(removed, is(false));
  }

  @SPITest
  public void nullKeyThrowsException()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store<K, V> kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    K key = null;
    V value = factory.getValueType().newInstance();

    try {
      kvStore.remove(key, value);
      throw new AssertionError("Expected NullPointerException because the key is null");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @SPITest
  public void nullValueThrowsException()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store<K, V> kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    K key = factory.getKeyType().newInstance();
    V value = null;

    try {
      kvStore.remove(key, value);
      throw new AssertionError("Expected NullPointerException because the value is null");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @SPITest
  @SuppressWarnings("unchecked")
  public void wrongKeyTypeThrowsException()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    V value = factory.getValueType().newInstance();

    try {
      if (this.factory.getKeyType() == String.class) {
        kvStore.remove(1.0f, value);
      } else {
        kvStore.remove("key", value);
      }
      throw new AssertionError("Expected ClassCastException because the key is of the wrong type");
    } catch (ClassCastException e) {
      // expected
    }
  }

  @SPITest
  @SuppressWarnings("unchecked")
  public void wrongValueTypeThrowsException()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    K key = factory.getKeyType().newInstance();

    try {
      if (this.factory.getKeyType() == String.class) {
        kvStore.remove(key, 1.0f);
      } else {
        kvStore.remove(key, "value");
      }
      throw new AssertionError("Expected ClassCastException because the value is of the wrong type");
    } catch (ClassCastException e) {
      // expected
    }
  }

  @SPITest
  public void mappingCantBeRemovedCanThrowException()
      throws IllegalAccessException, InstantiationException {
    final Store<K, V> kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    K key = factory.getKeyType().newInstance();
    V value = factory.getValueType().newInstance();

    try {
      kvStore.remove(key, value);
    } catch (CacheAccessException e) {
      // This will not compile if the CacheAccessException is not thrown
    }
  }
}