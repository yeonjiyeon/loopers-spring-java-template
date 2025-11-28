# 인덱스 구조 변경(단일→복합)에 따른 쿼리 비용 개선 분석

## 1. 개요 및 테스트 환경

이커머스 서비스의 핵심 기능인 **'특정 브랜드의 인기 상품 조회'** API의 성능을 개선하기 위해 인덱스 설계를 진행하고 성능 변화를 측정했습니다.

### 1.1 테스트 데이터 규모

- **Total Rows:** 100,000건 (Product 테이블)
- **Brands:** 30개 (균등 분포 가정)
- **DBMS:** MySQL 8.0 (InnoDB)

### 1.2 테스트 대상 쿼리

SQL

`SELECT *
FROM product
WHERE brand_id = 5
ORDER BY like_count DESC
LIMIT 20;`

---

## 2. 인덱스 적용 시나리오 비교

성능 측정은 **1) 인덱스 없음**, **2) 단일 인덱스 적용**, **3) 복합 인덱스 적용** 세 가지 케이스로 나누어 진행했습니다.

| **케이스** | **적용 인덱스 (Key)** | **구성 컬럼** |
| --- | --- | --- |
| **Case 1** | (None) | 없음 (PK만 존재) |
| **Case 2** | `idx_product_brand` | `(brand_id)` |
| **Case 3** | `idx_product_brand_like` | `(brand_id, like_count DESC)` |

---

## 3. 성능 측정 결과 (EXPLAIN ANALYZE)

각 시나리오별 실행 계획 및 소요 시간을 요약한 결과표입니다.

| **구분** | **인덱스명** | **실행 시간 (Actual Time)** | **비용 (Cost)** | **스캔 방식 (Type/Extra)** | **비고** |
| --- | --- | --- | --- | --- | --- |
| **개선 전** | - | **56.3 ms** | 10,131 | `ALL` (Full Table Scan) | 전체 데이터 스캔 및 정렬 발생 |
| **개선 후** | `idx_product_brand` | **10.8 ms** (▼ 80%) | 927 | `ref` (Using filesort) | **Best Performance** |
| **비교군** | `idx_product_brand_like` | 180.0 ms | 2,729 | `ref` (Index Lookup) | 정렬은 생략했으나 랜덤 I/O 비용 발생 |

