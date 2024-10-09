package org.example.cache;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;

public class CacheImplTest {

    private static final int MAX_CAPACITY = 10;
    private CacheImpl<String, String> cache;

    @BeforeEach
    public void setUp() {
        cache = new CacheImpl<>(MAX_CAPACITY, LoggerFactory.getLogger(CacheImpl.class));
    }

    @Test
    public void testPut() {
        String key = "key1";
        String value = "value1";

        assertTrue(cache.isEmpty());

        cache.put(key, value);

        assertTrue(cache.containsKey(key));
    }

    @Test
    public void testPutNotFound(){
        String key = "key1";
        assertNull(cache.get(key));
    }

    @Test
    public void testGet() {
        String key = "key1";
        String value = "value1";

        cache.put(key, value);

        String valorRecuperado = cache.get(key);
        assertEquals(value, valorRecuperado);
    }

    @Test
    public void testGetNotFound() {
        String key = "key1";
        assertNull(cache.get(key));
    }

    @Test
    public void testRemove() {
        String key = "key1";
        String value = "value1";
        cache.put(key, value);
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testRemoveNotFound() {
        String key = "key1";
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testClear() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        cache.put(key1, value1);
        cache.put(key2, value2);
        cache.clear();
        assertNull(cache.get(key1));
        assertNull(cache.get(key2));
    }

    @Test
    public void testSize() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        cache.put(key1, value1);
        cache.put(key2, value2);
        assertEquals(2, cache.size());
    }

    @Test
    public void testKeys() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        cache.put(key1, value1);
        cache.put(key2, value2);
        assertEquals(2, cache.keys().size());
        assertTrue(cache.keys().contains(key1));
        assertTrue(cache.keys().contains(key2));
    }

    @Test
    public void testKeysNotFound() {
        String key = "key1";
        assertFalse(cache.keys().contains(key));
    }

    @Test
    public void testValues() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        cache.put(key1, value1);
        cache.put(key2, value2);
        assertEquals(2, cache.values().size());
        assertTrue(cache.values().contains(value1));
        assertTrue(cache.values().contains(value2));
    }

    @Test
    public void testContainsKey() {
        String key = "key1";
        String value = "value1";
        cache.put(key, value);
        assertTrue(cache.containsKey(key));
    }

    @Test
    public void testContainsKeyNotFound() {
        String key = "key1";
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void testContainsValue() {
        String key = "key1";
        String value = "value1";
        cache.put(key, value);
        assertTrue(cache.containsValue(value));
    }

    @Test
    public void testContainsValueNotFound() {
        assertFalse(cache.containsValue("value1"));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(cache.isEmpty());
        String key = "key1";
        String value = "value1";
        cache.put(key, value);
        assertFalse(cache.isEmpty());
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(cache.isNotEmpty());
        String key = "key1";
        String value = "value1";
        cache.put(key, value);
        assertTrue(cache.isNotEmpty());
    }

    @Test
    public void testMaxCapacity() {
        for (int i = 0; i < MAX_CAPACITY + 1; i++) {
            cache.put("key" + i, "value" + i);
        }
        assertEquals(MAX_CAPACITY, cache.size());
    }

    @Test
    public void testMaxCapacityAfterRemove() {
        for (int i = 0; i < MAX_CAPACITY; i++) {
            cache.put("key" + i, "value" + i);
        }
        cache.remove("key0");
        assertEquals(MAX_CAPACITY - 1, cache.size());
    }
}