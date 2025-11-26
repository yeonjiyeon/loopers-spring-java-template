CREATE INDEX idx_product_brand_like
ON product (brand_id, like_count DESC);

CREATE INDEX idx_product_brand ON product (brand_id);