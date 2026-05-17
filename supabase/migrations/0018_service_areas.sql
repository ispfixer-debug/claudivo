-- Service areas table
CREATE TABLE IF NOT EXISTS vito_service_areas (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  city VARCHAR(50) NOT NULL,
  center_lat DECIMAL(10,8) NOT NULL,
  center_lng DECIMAL(11,8) NOT NULL,
  radius_km INTEGER DEFAULT 5,
  active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT NOW()
);
