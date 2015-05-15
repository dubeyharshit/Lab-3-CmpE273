package edu.sjsu.cmpe.cache.repository;

import edu.sjsu.cmpe.cache.domain.Entry;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class ChronicleMapCache implements CacheInterface {

    private final Map<Long, Entry> inMemoryMap;

    public ChronicleMapCache() throws IOException {

        String pathname ="file.dat";
        File file=new File(pathname);
        Map<Long, Entry> map= ChronicleMapBuilder.of(Long.class, Entry.class).createPersistedTo(file);
        inMemoryMap=map;
    }
    @Override
    public Entry save(Entry newEntry) {
        checkNotNull(newEntry, "instance could not be null");
        inMemoryMap.putIfAbsent(newEntry.getKey(),newEntry);
        return newEntry;
    }

    @Override
    public Entry get(Long key) {
        checkArgument(key > 0,
                "Key was %s should be greater than zero", key);
        //Entry using=new Entry();
        return inMemoryMap.get(key);
    }

    @Override
    public List<Entry> getAll() {
        return new ArrayList<Entry>(inMemoryMap.values());
    }



}
