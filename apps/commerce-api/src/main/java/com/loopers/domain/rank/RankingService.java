package com.loopers.domain.rank;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
}
