-- Users (password: demo123, BCrypt hash)
INSERT INTO users (username, password_hash, preferences) VALUES
('demo_user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '{"size": "M", "favorite_color": "black", "style": "minimal"}'),
('fashion_lover', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '{"size": "L", "favorite_color": "navy", "style": "smart-casual"}');
