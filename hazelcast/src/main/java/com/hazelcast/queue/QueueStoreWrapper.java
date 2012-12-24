/*
 * Copyright (c) 2008-2012, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.queue;

import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.MapStore;
import com.hazelcast.core.QueueStore;
import com.hazelcast.nio.Data;

import java.util.*;

/**
 * @ali 12/14/12
 */
public class QueueStoreWrapper {

    QueueStore store;

    boolean enabled = false;

    public QueueStoreWrapper() {
    }

    public void setConfig(QueueStoreConfig storeConfig) {
        if (storeConfig == null) {
            return;
        }
        try {
            Class<?> storeClass = Class.forName(storeConfig.getClassName());
            store = (QueueStore) storeClass.newInstance();
            enabled = storeConfig.isEnabled();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void store(Long key, Data value) {
        store.store(key, new QueueStoreValue(value));
    }

    public void storeAll(Map map) {
        store.storeAll(map);
    }

    public void delete(Long key) {
        store.delete(key);
    }

    public void deleteAll(Collection keys) {
        store.deleteAll(keys);
    }

    public Data load(Long key) {
        QueueStoreValue val = (QueueStoreValue) store.load(key);
        if (val != null) {
            return val.getData();
        }
        return null;
    }

    public Map<Long, QueueStoreValue> loadAll(Collection keys) {
        return store.loadAll(keys);
    }

    public Set<Long> loadAllKeys() {
        return store.loadAllKeys();
    }
}