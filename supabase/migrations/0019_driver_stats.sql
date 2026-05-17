-- Driver statistics
CREATE TABLE IF NOT EXISTS vito_driver_stats (
  driver_id VARCHAR(50) PRIMARY KEY,
  total_jobs INTEGER DEFAULT 0,
  total_earnings INTEGER DEFAULT 0,
  average_rating DECIMAL(3,2) DEFAULT 5.00,
  acceptance_rate DECIMAL(5,2) DEFAULT 100.00,
  on_time_rate DECIMAL(5,2) DEFAULT 100.00,
  updated_at TIMESTAMP DEFAULT NOW()
);
