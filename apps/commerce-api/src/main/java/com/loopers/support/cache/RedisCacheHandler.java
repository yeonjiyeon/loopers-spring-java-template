package com.loopers.support.cache;

import com.loopers.support.page.PageWrapper;
import java.time.Duration;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisCacheHandler {
  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * 캐시 조회 -> (없거나 에러나면) -> DB 조회 -> 캐시 저장
   * @param key        캐시 키
   * @param ttl        만료 시간
   * @param type       반환 타입 (캐스팅용)
   * @param dbFetcher  DB 조회 로직 (람다)
   */
  public <T> T getOrLoad(String key, Duration ttl, Class<T> type, Supplier<T> dbFetcher) {
    try {
      Object cachedData = redisTemplate.opsForValue().get(key);
      if (cachedData != null) {

        if (cachedData instanceof PageWrapper) {
          return (T) ((PageWrapper<?>) cachedData).toPage();
        }
        return type.cast(cachedData);
      }
    } catch (Exception e) {
    }

    T result = dbFetcher.get();

    if (result != null) {
      try {
        Object dataToSave = result;
        if (result instanceof Page) {
          dataToSave = new PageWrapper<>((Page<?>) result);
        }

        redisTemplate.opsForValue().set(key, dataToSave, ttl);
      } catch (Exception e) {
      }
    }

    return result;
  }
}
