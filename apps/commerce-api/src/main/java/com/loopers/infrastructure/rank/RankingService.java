package com.loopers.infrastructure.rank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingService {

  private final RedisTemplate<String, String> redisTemplate;

  public List<Long> getTopRankingIds(String date, int page, int size) {
    String key = "ranking:all:" + date;
    int start = (page - 1) * size;
    int end = start + size - 1;

    Set<String> rankedIds = redisTemplate.opsForZSet().reverseRange(key, start, end);

    if (rankedIds == null || rankedIds.isEmpty()) {
      return List.of();
    }

    return rankedIds.stream()
        .map(Long::valueOf)
        .toList();
  }

  public Integer getProductRank(Long productId) {
    String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String key = "ranking:all:" + today;

    Long rank = redisTemplate.opsForZSet().reverseRank(key, String.valueOf(productId));

    return (rank != null) ? rank.intValue() + 1 : null;
  }

  public void carryOverRanking(String sourceDate, String targetDate, double weight) {
    String sourceKey = "ranking:all:" + sourceDate;
    String targetKey = "ranking:all:" + targetDate;

    redisTemplate.opsForZSet().unionAndStore(sourceKey, List.of(), targetKey,
        Aggregate.SUM, Weights.of(weight));

    redisTemplate.expire(targetKey, 2, TimeUnit.DAYS);
  }
}
