-- RLS Policies - Migration 0009
-- Per PLAN.md §13 - Row Level Security

-- Enable RLS on all user tables
ALTER TABLE public.vito_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vito_drivers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vito_admins ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vito_rides ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vito_sends ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vito_mart_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vito_wallets ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vito_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vito_qr_tokens ENABLE ROW LEVEL SECURITY;

-- Client: can only read own row
CREATE POLICY "clients_read_own" ON public.vito_users
    FOR SELECT USING (auth.uid() = id);

-- Driver: can only read own row
CREATE POLICY "drivers_read_own" ON public.vito_drivers
    FOR SELECT USING (auth.uid() = id);

-- Admin: can read all users
CREATE POLICY "admins_read_all" ON public.vito_users
    FOR SELECT USING (
        EXISTS (SELECT 1 FROM public.vito_admins WHERE id = auth.uid())
    );

CREATE POLICY "admins_read_all_drivers" ON public.vito_drivers
    FOR SELECT USING (
        EXISTS (SELECT 1 FROM public.vito_admins WHERE id = auth.uid())
    );

-- Ride access: client can see own rides, driver can see assigned rides
CREATE POLICY "rides_client_access" ON public.vito_rides
    FOR SELECT USING (
        auth.uid() = client_id OR auth.uid() = driver_id
    );

-- Send access: similar to rides
CREATE POLICY "sends_client_access" ON public.vito_sends
    FOR SELECT USING (
        auth.uid() = client_id OR auth.uid() = driver_id
    );

-- Mart orders: client sees own, driver sees assigned
CREATE POLICY "mart_orders_client_access" ON public.vito_mart_orders
    FOR SELECT USING (
        auth.uid() = client_id OR auth.uid() = driver_id
    );

-- Wallet: user can only see own wallet
CREATE POLICY "wallets_own" ON public.vito_wallets
    FOR SELECT USING (auth.uid() = user_id);

-- Transactions: user can only see own transactions
CREATE POLICY "transactions_own" ON public.vito_transactions
    FOR SELECT USING (auth.uid() = user_id);

-- QR Tokens: users can read tokens for their own referrals
CREATE POLICY "qr_tokens_own" ON public.vito_qr_tokens
    FOR SELECT USING (
        created_by = auth.uid() OR used_by = auth.uid()
    );
