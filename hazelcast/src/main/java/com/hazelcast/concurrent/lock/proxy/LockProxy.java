/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.concurrent.lock.proxy;

import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.core.ICondition;
import com.hazelcast.core.ILock;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.NodeEngine;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @author mdogan 2/12/13
 */
public class LockProxy extends AbstractDistributedObject<LockServiceImpl> implements ILock {

    final String name;
    final Data key;
    private final LockProxySupport lockSupport;

    public LockProxy(NodeEngine nodeEngine, LockServiceImpl lockService, final Object object) {
        super(nodeEngine, lockService);
        if (object instanceof String) {
            name = (String) object;
            key = getNameAsPartitionAwareData();
        } else if (object instanceof Data) {
            key = (Data) object;
            name = String.valueOf(nodeEngine.getSerializationService().toObject(key));
        } else {
            name = convertToStringKey(object, nodeEngine.getSerializationService());
            key = getNameAsPartitionAwareData();
        }
        lockSupport = new LockProxySupport(new InternalLockNamespace());
    }

    public boolean isLocked() {
        return lockSupport.isLocked(getNodeEngine(), key);
    }

    public boolean isLockedByCurrentThread() {
        return lockSupport.isLockedByCurrentThread(getNodeEngine(), key);
    }

    public int getLockCount() {
        return lockSupport.getLockCount(getNodeEngine(), key);
    }

    public long getRemainingLeaseTime() {
        return lockSupport.getRemainingLeaseTime(getNodeEngine(), key);
    }

    public void lock() {
        lockSupport.lock(getNodeEngine(), key);
    }

    public void lock(long ttl, TimeUnit timeUnit) {
        lockSupport.lock(getNodeEngine(), key, timeUnit.toMillis(ttl));
    }

    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    public boolean tryLock() {
        return lockSupport.tryLock(getNodeEngine(), key);
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lockSupport.tryLock(getNodeEngine(), key, time, unit);
    }

    public void unlock() {
        lockSupport.unlock(getNodeEngine(), key);
    }

    public void forceUnlock() {
        lockSupport.forceUnlock(getNodeEngine(), key);
    }

    public Condition newCondition() {
        throw new UnsupportedOperationException("Use ICondition.newCondition(String name) instead!");
    }

    public ICondition newCondition(String name) {
        return new ConditionImpl(this, name);
    }

    public Object getId() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getServiceName() {
        return LockService.SERVICE_NAME;
    }

    public Object getKey() {
        return getName();
    }

    // will be removed when HazelcastInstance.getLock(Object key) is removed from API
    public static String convertToStringKey(Object key, SerializationService serializationService) {
        if (key instanceof String) {
            return String.valueOf(key);
        } else {
            Data data = serializationService.toData(key, PARTITIONING_STRATEGY);
//            name = Integer.toString(data.hashCode());
            byte[] buffer = data.getBuffer();
            return Arrays.toString(buffer);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ILock{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
