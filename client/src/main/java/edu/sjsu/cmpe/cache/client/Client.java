package edu.sjsu.cmpe.cache.client;

import com.google.common.hash.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class Client {
	
	private static HashFunction hashFunction= Hashing.md5();
	private static SortedMap<Integer, String> consistantHashing =new TreeMap<Integer, String>();

    public static void main(String[] args) throws Exception {

        String value[]={"a","b","c","d","e","f","g","h","i","j"};

        System.out.println("Starting Cache Client...");

        String server1="http://localhost:3000";
        String server2="http://localhost:3001";
        String server3="http://localhost:3002";

        List<String> server = new ArrayList<String>();

        server.add(server1);
        server.add(server2);
        server.add(server3);

        for(int x=0; x<server.size(); x++)
        {
            String x1=server.get(x);
            System.out.println("String get " + x1);
            add(x1,x);
        }

        for (int i=0;i<10;i++)
        {
            int bucket = Hashing.consistentHash(Hashing.md5().hashLong(i),consistantHashing.size());
            int key=i+1;
            String latestServer=get(bucket);

            System.out.println("server is: " + latestServer);

            CacheServiceInterface cacheserver = new DistributedCacheService(latestServer);

            cacheserver.put(i+1, String.valueOf(value[i]));
            System.out.println("inserting:");
            System.out.println("key is " + key);
            System.out.println("value is " + value[i]);

        }

        for (int i=0;i<10;i++)
        {
            int bucket = Hashing.consistentHash(Hashing.md5().hashLong(i),consistantHashing.size());
            int key=i+1;
            String latestServer=get(bucket);

            CacheServiceInterface cacheserver = new DistributedCacheService(latestServer);

            cacheserver.get(i+1);
            System.out.println("getting");
            System.out.println("key is: " + key);
            System.out.println("Values is: "+cacheserver.get(i+1));

        }
        
//        <--------code for rendezvous hashing Author Harshit Dubey----->
        
/*        ArrayList cacheServer = new ArrayList();

        cacheServer.add("http://localhost:3000");
        cacheServer.add("http://localhost:3001");
        cacheServer.add("http://localhost:3002");


        RendezvousHash<String> rendHash = new RendezvousHash(cacheServer);
        System.out.println("Adding to Cache");
        for(int i = 1; i<=10; i++){
        	addToRendCache(i, String.valueOf((char) (i + 96)), rendHash);
        }
        System.out.println("Getting from Cache Server");
        for(int i =1; i<=10; i++){
            String value = (String)getFromRendCache(i, rendHash);
            System.out.println("Cache Collected : " + value);
        }*/

    }

	 public static void add(String node,int i) {
		 HashCode hCode=hashFunction.hashLong(i);
		  consistantHashing.put(hCode.asInt(),node);
	 }

	  public void remove(String node) {
		  consistantHashing.remove(Hashing.md5().hashCode());
	  }

	 public static String get(Object key) {
	    if (consistantHashing.isEmpty()) {
	      return null;
	    }
	    int hash = hashFunction.hashLong((Integer)key).asInt();
	    if (!consistantHashing.containsKey(hash)) {
	      SortedMap<Integer, String> tailMap =consistantHashing.tailMap(hash);
	      hash = tailMap.isEmpty() ?
	             consistantHashing.firstKey() : tailMap.firstKey();
	    }
	    return consistantHashing.get(hash);
	  }
	 
//   <--------code for rendezvous hashing Author Harshit Dubey----->
	 
	 /*public static void addToRendCache(int toAddKey, String toAddValue, RendezvousHash rendHash){
         String cacheUrl = (String) rendHash.getCache(toAddKey);
         CacheServiceInterface cache = new DistributedCacheService(cacheUrl);
         cache.put(toAddKey,toAddValue);
         System.out.println("put( " + toAddKey + " => " + toAddValue + ")");
     }
     public static Object getFromRendCache(int key, RendezvousHash rendHash){
         String cacheUrl = (String) rendHash.getCache(key);
         CacheServiceInterface cache = new DistributedCacheService(cacheUrl);
         String value = cache.get(key);
         System.out.println("get( "+ key+ " ) => "+ value);
         return value;
     }*/
}
