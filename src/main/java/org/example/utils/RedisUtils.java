package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis操作工具类
 * 提供基于Spring Data Redis的通用操作方法，支持String、Set、Hash、List等数据结构
 * 采用泛型设计提高类型安全性，完善异常处理和注释说明
 */
@Component
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate redisTemplate;

    // ====================== 通用操作 ======================

    /**
     * 给指定key设置过期时间
     *
     * @param key      键，不能为空
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 设置结果，true表示成功
     */
    public boolean setExpireTime(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 获取指定key的剩余过期时间
     *
     * @param key      键，不能为空
     * @param timeUnit 时间单位
     * @return 剩余过期时间（秒），-1表示永久有效，-2表示键不存在
     */
    public long getExpireTime(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 检查key是否存在
     *
     * @param key 键，不能为空
     * @return 存在返回true，否则返回false
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 移除key的过期时间，使其永久有效
     *
     * @param key 键，不能为空
     * @return 操作结果，true表示成功
     */
    public boolean persist(String key) {
        return Boolean.TRUE.equals(redisTemplate.boundValueOps(key).persist());
    }

    // ====================== String类型操作 ======================

    /**
     * 根据key获取值
     *
     * @param key 键，不能为空
     * @return 值，若key不存在则返回null
     */
    public <T> T get(String key) {
        return key == null ? null : (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 将值存入缓存（永久有效）
     *
     * @param key   键，不能为空
     * @param value 值，可以是字符串或可序列化对象
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将值存入缓存并设置过期时间
     *
     * @param key     键，不能为空
     * @param value   值，可以是字符串或可序列化对象
     * @param timeout 过期时间（秒），-1表示永久有效
     */
    public void set(String key, Object value, long timeout) {
        if (timeout > 0) {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 批量设置String类型键值对（存在的键会被覆盖）
     *
     * @param keyValueMap 键值对映射，键和值均为字符串
     */
    public void batchSet(Map<String, String> keyValueMap) {
        redisTemplate.opsForValue().multiSet(keyValueMap);
    }

    /**
     * 批量设置String类型键值对（仅当键不存在时设置）
     *
     * @param keyValueMap 键值对映射，键和值均为字符串
     * @return 所有键都不存在时返回true，否则返回false
     */
    public boolean batchSetIfAbsent(Map<String, String> keyValueMap) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().multiSetIfAbsent(keyValueMap));
    }

    /**
     * 对数值类型值进行递增操作
     *
     * @param key   键，不能为空
     * @param delta 递增步长，可为负数
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 对浮点类型值进行递增操作
     *
     * @param key   键，不能为空
     * @param delta 递增步长，可为负数
     * @return 递增后的值
     */
    public Double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // ====================== Set类型操作 ======================

    /**
     * 向Set中添加元素
     *
     * @param key   键，不能为空
     * @param value 元素，不能为空
     * @return 添加结果，true表示新元素被添加，false表示元素已存在
     */
    public boolean sSet(String key, Object value) {
        Long result = redisTemplate.opsForSet().add(key, value);
        return result != null && result > 0;
    }

    /**
     * 获取Set中的所有元素
     *
     * @param key 键，不能为空
     * @return Set集合，若key不存在则返回空Set
     */
    public <T> Set<T> members(String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取Set中指定数量的元素（不删除元素）
     *
     * @param key   键，不能为空
     * @param count 元素数量
     * @return 随机元素集合
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> randomMembers(String key, long count) {
        return (Set<T>) redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取Set中的一个元素（不删除元素）
     *
     * @param key 键，不能为空
     * @return 随机元素，若Set为空则返回null
     */
    public <T> T randomMember(String key) {
        return (T) redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 弹出Set中的一个元素（删除元素）
     *
     * @param key 键，不能为空
     * @return 弹出的元素，若Set为空则返回null
     */
    public <T> T pop(String key) {
        return (T) redisTemplate.opsForSet().pop(key);
    }

    /**
     * 获取Set的大小
     *
     * @param key 键，不能为空
     * @return 元素数量，若key不存在则返回0
     */
    public long size(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        return size == null ? 0 : size;
    }

    /**
     * 检查元素是否在Set中
     *
     * @param key   键，不能为空
     * @param value 元素
     * @return 存在返回true，否则返回false
     */
    public boolean contains(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    /**
     * 将元素从一个Set移动到另一个Set
     *
     * @param sourceKey  源Set的键，不能为空
     * @param destKey    目标Set的键，不能为空
     * @param value      要移动的元素
     * @return 移动结果，true表示移动成功
     */
    public boolean move(String sourceKey, String destKey, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().move(sourceKey, value, destKey));
    }

    /**
     * 从Set中移除指定元素
     *
     * @param key    键，不能为空
     * @param values 要移除的元素（可变参数）
     * @return 移除的元素数量
     */
    public long remove(String key, Object... values) {
        Long result = redisTemplate.opsForSet().remove(key, values);
        return result == null ? 0L : result;
    }


    /**
     * 获取两个Set的差集
     *
     * @param sourceKey  源Set的键，不能为空
     * @param otherKey   另一个Set的键，不能为空
     * @return 差集集合
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> difference(String sourceKey, String otherKey) {
        return (Set<T>) redisTemplate.opsForSet().difference(sourceKey, otherKey);
    }

    // ====================== Hash类型操作 ======================

    /**
     * 批量添加Hash键值对
     *
     * @param key  键，不能为空
     * @param map  键值对映射，key为Hash的字段名，value为字段值
     */
    public void putAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取Hash中的所有键值对
     *
     * @param key 键，不能为空
     * @return 键值对映射，若key不存在则返回空Map
     */
    @SuppressWarnings("unchecked")
    public <HK, HV> Map<HK, HV> entries(String key) {
        return (Map<HK, HV>) redisTemplate.opsForHash().entries(key);
    }

    /**
     * 检查Hash中是否存在指定字段
     *
     * @param key     键，不能为空
     * @param hashKey 字段名，不能为空
     * @return 存在返回true，否则返回false
     */
    public boolean hasHashKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取Hash中指定字段的值（字符串类型）
     *
     * @param key     键，不能为空
     * @param hashKey 字段名，不能为空
     * @return 字段值，若不存在则返回null
     */
    public <T> T getHashValue(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 删除Hash中的指定字段
     *
     * @param key      键，不能为空
     * @param hashKeys 要删除的字段名（可变参数）
     * @return 删除的字段数量
     */
    public long delete(String key, String... hashKeys) {
        return redisTemplate.opsForHash().delete(key, (Object) hashKeys);
    }

    /**
     * 对Hash中的数值类型字段进行递增操作
     *
     * @param key      键，不能为空
     * @param hashKey  字段名，不能为空
     * @param delta    递增步长，可为负数
     * @return 递增后的值
     */
    public Long increment(String key, String hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 对Hash中的浮点类型字段进行递增操作
     *
     * @param key      键，不能为空
     * @param hashKey  字段名，不能为空
     * @param delta    递增步长，可为负数
     * @return 递增后的值
     */
    public Double increment(String key, String hashKey, double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 获取Hash中的所有字段名
     *
     * @param key 键，不能为空
     * @return 字段名集合，若key不存在则返回空Set
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> hashKeys(String key) {
        return (Set<T>) redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取Hash中的字段数量
     *
     * @param key 键，不能为空
     * @return 字段数量，若key不存在则返回0
     */
    public long hashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    // ====================== List类型操作 ======================

    /**
     * 在List左侧添加元素
     *
     * @param key   键，不能为空
     * @param value 元素，不能为空
     * @return 添加后的List长度
     */
    public long leftPush(String key, Object value) {
        Long result = redisTemplate.opsForList().leftPush(key, value);
        return result == null ? -1L : result;
    }


    /**
     * 在List中指定元素前插入新元素
     *
     * @param key   键，不能为空
     * @param pivot 参考元素
     * @param value 新元素，不能为空
     * @return 添加后的List长度，若pivot不存在则返回-1
     */
    public long leftPushBefore(String key, Object pivot, Object value) {
        Long result = redisTemplate.opsForList().leftPush(key, pivot, value);
        return result == null ? -1L : result;
    }

    /**
     * 在List中指定元素后插入新元素
     *
     * @param key   键，不能为空
     * @param pivot 参考元素
     * @param value 新元素，不能为空
     * @return 添加后的List长度，若pivot不存在则返回-1
     */
    public long rightPushAfter(String key, Object pivot, Object value) {
        return redisTemplate.opsForList().rightPush(key, pivot, value);
    }

    /**
     * 批量从List左侧添加元素
     *
     * @param key    键，不能为空
     * @param values 要添加的元素数组
     * @return 添加后的List长度
     */
    public long leftPushAll(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 从List右侧添加元素
     *
     * @param key   键，不能为空
     * @param value 元素，不能为空
     * @return 添加后的List长度
     */
    public long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 批量从List右侧添加元素
     *
     * @param key    键，不能为空
     * @param values 要添加的元素数组
     * @return 添加后的List长度
     */
    public long rightPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 仅当List存在时添加元素
     *
     * @param key   键，不能为空
     * @param value 元素，不能为空
     * @return 添加结果，true表示添加成功
     */
    public boolean rightPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value) != null;
    }

    /**
     * 获取List中指定索引位置的元素
     *
     * @param key   键，不能为空
     * @param index 索引（0-based），-1表示最后一个元素
     * @return 元素值，若索引越界则返回null
     */
    public <T> T index(String key, long index) {
        return (T) redisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取List中指定范围的元素
     *
     * @param key   键，不能为空
     * @param start 起始索引（0-based）
     * @param end   结束索引（0-based），-1表示最后一个元素
     * @return 元素列表，若key不存在则返回空List
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> range(String key, long start, long end) {
        return (List<T>) redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 截取List到指定范围
     *
     * @param key   键，不能为空
     * @param start 起始索引（0-based）
     * @param end   结束索引（0-based），-1表示最后一个元素
     */
    public void trim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 从List左侧弹出元素（删除并返回）
     *
     * @param key 键，不能为空
     * @return 弹出的元素，若List为空则返回null
     */
    public <T> T leftPop(String key) {
        return (T) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从List左侧阻塞弹出元素
     *
     * @param key    键，不能为空
     * @param timeout 超时时间
     * @param unit   时间单位
     * @return 弹出的元素，若超时则返回null
     */
    public <T> T leftPop(String key, long timeout, TimeUnit unit) {
        return (T) redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 从List右侧弹出元素（删除并返回）
     *
     * @param key 键，不能为空
     * @return 弹出的元素，若List为空则返回null
     */
    public <T> T rightPop(String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 从List右侧阻塞弹出元素
     *
     * @param key    键，不能为空
     * @param timeout 超时时间
     * @param unit   时间单位
     * @return 弹出的元素，若超时则返回null
     */
    public <T> T rightPop(String key, long timeout, TimeUnit unit) {
        return (T) redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 从一个List右侧弹出元素并添加到另一个List左侧
     *
     * @param sourceKey 源List的键，不能为空
     * @param destKey   目标List的键，不能为空
     * @return 弹出的元素，若源List为空则返回null
     */
    public <T> T rightPopAndLeftPush(String sourceKey, String destKey) {
        return (T) redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destKey);
    }

    /**
     * 阻塞从一个List右侧弹出元素并添加到另一个List左侧
     *
     * @param sourceKey 源List的键，不能为空
     * @param destKey   目标List的键，不能为空
     * @param timeout   超时时间
     * @param unit      时间单位
     * @return 弹出的元素，若超时则返回null
     */
    public <T> T rightPopAndLeftPush(String sourceKey, String destKey, long timeout, TimeUnit unit) {
        return (T) redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destKey, timeout, unit);
    }

    /**
     * 获取List的长度
     *
     * @param key 键，不能为空
     * @return List长度，若key不存在则返回0
     */
    public long listSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 设置List中指定位置的元素值
     *
     * @param key   键，不能为空
     * @param index 索引（0-based）
     * @param value 新值，不能为空
     */
    public void set(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移除List中指定数量的元素
     *
     * @param key   键，不能为空
     * @param count 移除数量（正数移除等于value的元素，负数移除不等于value的元素，0移除所有）
     * @param value 元素值
     * @return 移除的元素数量
     */
    public long remove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }
}