package com.hazelcast.atomicnumber;

import com.hazelcast.spi.Operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

// author: sancar - 24.12.2012
public class SetOperation extends AtomicNumberBackupAwareOperation {

    private long newValue;

    public SetOperation() {
        super();
    }

    public SetOperation(String name, long newValue) {
        super(name);
        this.newValue = newValue;
    }

    @Override
    public void run() throws Exception {
        setNumber(newValue);
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    public void writeInternal(DataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(newValue);
    }

    @Override
    public void readInternal(DataInput in) throws IOException {
        super.readInternal(in);
        newValue = in.readLong();
    }

    public Operation getBackupOperation() {
        return new SetBackupOperation(name, newValue);
    }
}