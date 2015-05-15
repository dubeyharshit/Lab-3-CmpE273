package edu.sjsu.cmpe.cache.client;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.*;
import com.google.common.hash.HashCode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RendezvousHash<K> {

    private final HashFunction hashFunction;
    private final HashMap<Integer,K> nodeList = new HashMap<Integer,K>();

    public RendezvousHash(Collection<K> nodes){

        this.hashFunction = Hashing.md5();
        for(K node: nodes){
            add(node);
        }
        System.out.println("Creating List: "+ nodeList);
    }
    public void add(K node) {
        int hash = hashFunction.newHasher().putString(node.toString()).hash().asInt();
        System.out.println("hash when adding : " + hash);
        nodeList.put(hash, node);

    }

    public K getCache(Object key){
        if(nodeList.isEmpty()){
            return null;
        }
        Integer maxHash = Integer.MIN_VALUE;
        K maxNode = null;

        for (Map.Entry<Integer, K> node : nodeList.entrySet()) {
            int temp = hashFunction.newHasher()
                    .putString(key.toString())
                    .putString(node.getValue().toString()).hash().asInt();

            if (temp > maxHash) {
                maxHash = temp;
                maxNode = node.getValue();

            }
        }

        return maxNode;
    }

}