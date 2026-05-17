# VITO 1.0.1 — Master Plan
### Target repo: github.com/ispfixer-debug/clauvito-1.0.1 (branch: master)
### Languages: Kotlin 63.7%, TypeScript 28.5%, HTML 6.8%, PLpgSQL 1.0%

---

## 1. Repo State Snapshot (verified by inspection)

```
EXISTS AND CORRECT — DO NOT RECREATE:
  settings.gradle.kts, build.gradle.kts (root), gradle/libs.versions.toml
  gradle.properties, gradlew, gradlew.bat, .gitignore
  .github/workflows/ci.yml
  keystore/ (placeholder dir)
  supabase/migrations/0001_extensions.sql  → PostGIS, pgcrypto
  supabase/migrations/0002_users.sql       → user tables
  supabase/migrations/0003_jobs.sql        → jobs + dispatch
  supabase/migrations/0004_mart.sql        → mart tables
  supabase/migrations/0005_wallet.sql      → wallet + transactions
  supabase/migrations/0006_qr_referral.sql → QR + referral tables
  supabase/migrations/0007_notifications.sql → notifications + audit
  supabase/migrations/0008_triggers.sql    → triggers + seed flags
  supabase/functions/_shared/auth.ts
  supabase/functions/_shared/db.ts
  supabase/functions/feature-flags/index.ts
  vito_design_system/...VitoColors.kt
  vito_design_system/...VitoTypography.kt
  vito_design_system/...VitoSpacing.kt
  vito_design_system/...VitoShapes.kt
  vito_design_system/...VitoTheme.kt
  vito_design_system/...VitoButton.kt
  vito_design_system/...VitoCard.kt
  vito_design_system/...VitoSkeleton.kt
  vito_core/.../domain/model/User.kt
  vito_core/.../domain/model/Job.kt
  vito_core/.../domain/model/Wallet.kt
  vito_core/.../domain/model/Misc.kt
  vito_core/.../domain/repository/Repositories.kt
  vito_client/...VitoClientApp.kt + NavGraph
  vito_client/...ui/SplashScreen.kt
  vito_client/...ui/HomeScreen.kt (scaffold only)
  vito_driver/...VitoDriverApp.kt
  vito_admin/...VitoAdminApp.kt
  vito_landing/index.html

EXISTS BUT BROKEN — MUST REPLACE:
  vito_client/...ui/auth/LoginScreen.kt   ← BUILD_LOG: "phone entry" (WRONG)

MISSING — MUST CREATE (P0 first):
  supabase/migrations/0009_rls_policies.sql
  supabase/functions/_shared/bcrypt.ts
  supabase/functions/_shared/errors.ts
  supabase/functions/_shared/audit.ts
  supabase/functions/_shared/stripe.ts
  supabase/functions/vito-login/index.ts
  supabase/functions/vito-register-client/index.ts
  supabase/functions/vito-register-driver/index.ts
  supabase/functions/vito-validate-token/index.ts
  supabase/functions/vito-qr-info/index.ts
  supabase/functions/vito-qr-gen/index.ts
  supabase/functions/vito-qr-revoke/index.ts
  supabase/functions/vito-check-username/index.ts
  supabase/functions/vito-change-pin/index.ts
  supabase/functions/vito-delete-account/index.ts
  supabase/functions/vito-reset-pin/index.ts
  supabase/functions/vito-create-ride/index.ts
  supabase/functions/vito-accept-job/index.ts
  supabase/functions/vito-update-ride-status/index.ts
  supabase/functions/vito-cancel-ride/index.ts
  supabase/functions/vito-rate-ride/index.ts
  supabase/functions/vito-create-send/index.ts
  supabase/functions/vito-update-send-status/index.ts
  supabase/functions/vito-create-mart-order/index.ts
  supabase/functions/vito-update-mart-status/index.ts
  supabase/functions/vito-submit-signature/index.ts
  supabase/functions/vito-upload-delivery-photo/index.ts
  supabase/functions/vito-stripe-payment-sheet/index.ts
  supabase/functions/vito-stripe-webhook/index.ts
  supabase/functions/vito-payout/index.ts
  supabase/functions/vito-suspend-user/index.ts
  supabase/functions/vito-approve-car-photo/index.ts
  supabase/functions/vito-assign-driver-admin/index.ts
  supabase/functions/vito-cancel-order-admin/index.ts
  vito_design_system: VitoPinField, VitoTextField, VitoSearchField,
                      VitoTopAppBar, VitoBottomNavigation, VitoScreenScaffold,
                      VitoBottomSheet, VitoSnackbar, VitoOfflineBanner,
                      VitoStatusStepper, VitoBadge, VitoAvatar,
                      VitoEmptyState, VitoErrorState, VitoMapView,
                      VitoQrCode, VitoSignaturePad, VitoBarChart,
                      VitoJobRequestModal
  vito_core: SessionManager, SupabaseProvider, NetworkMonitor,
             FareCalculator, TokenParser,
             AuthRepositoryImpl, UserRepositoryImpl, JobRepositoryImpl,
             DispatchRepositoryImpl, WalletRepositoryImpl, LocationRepositoryImpl
  vito_client: TokenGateScreen, RegistrationScreen, RideBookingScreen,
               ActiveRideScreen, SendScreen, MartStoreScreen, MartCartScreen,
               MartCheckoutScreen, MartTrackingScreen, WalletScreen,
               ActivityScreen, ProfileScreen, ChangePinScreen,
               InstallReferrerReceiver
  vito_driver: DriverTokenGateScreen, DriverRegistrationScreen,
               DriverLoginScreen, DriverHomeScreen, JobRequestModal,
               ActiveJobScreen, DriverEarningsScreen, DriverQrScreen,
               DriverProfileScreen, DriverLocationService,
               InstallReferrerReceiver
  vito_admin: AdminLoginScreen, AdminDashboardScreen, LiveOrdersMapScreen,
              DriverManagementScreen, ClientManagementScreen,
              MartManagementScreen, FinanceScreen, QrTokenManagementScreen,
              AuditLogScreen
  .github/workflows/release.yml
  .github/workflows/supabase-deploy.yml
```

---

## 2. The 10 Non-Negotiable Constraints

| # | Rule | Detection |
|---|---|---|
| 1 | Auth: username + 6-digit PIN ONLY. No phone, no OTP, no email. | `grep -i phone vito_client/.../auth/*.kt` must return zero |
| 2 | bcrypt cost 12 — never Argon2 | `grep -r argon2 supabase/functions/` must return zero |
| 3 | JWT in EncryptedSharedPreferences — never DataStore for session | SessionManager imports must include `androidx.security.crypto.EncryptedSharedPreferences` |
| 4 | TokenGateScreen is only entry. No "Create Account" without QR token. | `grep -i "create account\|sign up" vito_client/.../auth/TokenGateScreen.kt` must return zero |
| 5 | Charts use Compose Canvas DrawScope only. No Vico, MPAndroidChart, AAY. | `grep -ri vico\|mpandroid\|aay-chart gradle/` must return zero |
| 6 | Job delivery: Supabase Realtime WebSocket = primary. FCM = optional. | DriverHomeViewModel observes Realtime channel, not FirebaseMessagingService |
| 7 | accept-job MUST call `assign_driver_atomic()` RPC — never plain UPDATE | `grep -q assign_driver_atomic supabase/functions/vito-accept-job/index.ts` |
| 8 | JobRequestModal: dismissOnBackPress=false, dismissOnClickOutside=false | `grep -q "dismissOnBackPress.*false" .../JobRequestModal.kt` |
| 9 | DriverLocationService: foreground + android.location.LocationManager fallback | Must use `startForeground()` + check `isGmsAvailable()` with fallback |
| 10 | DEBUG_BYPASS_LOGIN in debug builds ONLY; R8 strips from release | CI: `apkanalyzer dex packages release.apk \| grep -q DEBUG_BYPASS_LOGIN` returns false |

---

## 3. Database Schema (in 0001–0008; 0009 adds RLS)

Tables:
- vito_users(id, username UNIQUE, display_name, pin_hash, pin_failed_attempts, pin_locked_until, wallet_balance, referral_driver_id, is_suspended, language, deleted_at, timestamps)
- vito_drivers(id, username UNIQUE, display_name, pin_hash, pin_failed_attempts, pin_locked_until, plate_number, car_photo_url, car_photo_approved, service_ride, service_send, service_mart, is_online, is_suspended, current_lat, current_lng, location GEOGRAPHY, wallet_balance, stripe_account_id, rating_avg, rating_count, language, timestamps)
- vito_admins(id, username UNIQUE, display_name, pin_hash, role, is_active)
- vito_rides(id, client_id, driver_id, status enum, pickup_*, dest_*, distance_km, fare_min, fare_max, fare_final, payment_method, tip_amount, client_rating, driver_rating, cancel_reason, timestamps)
- vito_sends(similar to rides without rating)
- vito_mart_orders(id, client_id, driver_id, status, items JSONB, subtotal, delivery_fee, total, delivery_address, delivery_lat, delivery_lng, payment_method, delivery_photo_url, signature_url, timestamps)
- vito_mart_products(id, name, category, price, stock_count, description, image_url, is_active)
- vito_wallet_transactions(id, user_id, driver_id, type, amount, balance_after, description, ride_id/send_id/order_id, stripe_pi_id)
- vito_payout_requests(id, driver_id, amount, status, stripe_transfer_id, resolved_at, resolved_by)
- vito_qr_tokens(id, token UNIQUE, type ENUM('client_referral','driver_onboard'), driver_id, is_revoked, use_count, expires_at, created_by_admin)
- vito_referrals(id, referring_driver_id, referred_user_id, qr_token_id)
- vito_audit_log(id BIGSERIAL, admin_id, action, target_type, target_id, details JSONB)
- vito_stripe_events(event_id PRIMARY KEY, processed_at)

Postgres functions:
- `assign_driver_atomic(p_ride_id UUID, p_driver_id UUID) RETURNS INTEGER` — returns 1 on success, 0 if taken
- `assign_send_driver_atomic(...)` — same for sends
- `assign_mart_driver_atomic(...)` — same for mart
- `deduct_wallet(p_user_id UUID, p_amount NUMERIC) RETURNS BOOLEAN` — FOR UPDATE row lock

---

## 4. 0009_rls_policies.sql (full content)

```sql
ALTER TABLE vito_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_drivers ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_admins ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_rides ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_sends ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_mart_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_mart_products ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_wallet_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_payout_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_qr_tokens ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_referrals ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_audit_log ENABLE ROW LEVEL SECURITY;
ALTER TABLE vito_stripe_events ENABLE ROW LEVEL SECURITY;

CREATE OR REPLACE FUNCTION jwt_user_id() RETURNS TEXT AS $$
  SELECT current_setting('request.jwt.claims', TRUE)::jsonb ->> 'sub'
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION jwt_role() RETURNS TEXT AS $$
  SELECT current_setting('request.jwt.claims', TRUE)::jsonb ->> 'role'
$$ LANGUAGE SQL STABLE;

-- users
CREATE POLICY users_self_select ON vito_users FOR SELECT
  USING (id::TEXT = jwt_user_id() AND deleted_at IS NULL);
CREATE POLICY users_self_update ON vito_users FOR UPDATE
  USING (id::TEXT = jwt_user_id())
  WITH CHECK (
    wallet_balance = (SELECT wallet_balance FROM vito_users WHERE id = vito_users.id)
    AND is_suspended = (SELECT is_suspended FROM vito_users WHERE id = vito_users.id)
    AND pin_hash = (SELECT pin_hash FROM vito_users WHERE id = vito_users.id)
  );

-- drivers
CREATE POLICY drivers_self_select ON vito_drivers FOR SELECT
  USING (id::TEXT = jwt_user_id());
CREATE POLICY drivers_client_for_ride ON vito_drivers FOR SELECT
  USING (EXISTS (
    SELECT 1 FROM vito_rides r
    WHERE r.driver_id = vito_drivers.id
      AND r.client_id::TEXT = jwt_user_id()
      AND r.status IN ('assigned','driver_arrived','in_progress')
  ));
CREATE POLICY drivers_self_update ON vito_drivers FOR UPDATE
  USING (id::TEXT = jwt_user_id())
  WITH CHECK (
    car_photo_approved = (SELECT car_photo_approved FROM vito_drivers WHERE id = vito_drivers.id)
    AND is_suspended = (SELECT is_suspended FROM vito_drivers WHERE id = vito_drivers.id)
    AND wallet_balance = (SELECT wallet_balance FROM vito_drivers WHERE id = vito_drivers.id)
    AND pin_hash = (SELECT pin_hash FROM vito_drivers WHERE id = vito_drivers.id)
  );

-- rides: client sees own; driver sees assigned or searching
CREATE POLICY rides_client_select ON vito_rides FOR SELECT
  USING (client_id::TEXT = jwt_user_id());
CREATE POLICY rides_driver_select ON vito_rides FOR SELECT
  USING (
    driver_id::TEXT = jwt_user_id()
    OR (status = 'searching' AND jwt_role() = 'driver')
  );

-- sends: same logic
CREATE POLICY sends_client_select ON vito_sends FOR SELECT
  USING (client_id::TEXT = jwt_user_id());
CREATE POLICY sends_driver_select ON vito_sends FOR SELECT
  USING (driver_id::TEXT = jwt_user_id() OR (status = 'searching' AND jwt_role() = 'driver'));

-- mart products: public read for active
CREATE POLICY mart_products_public ON vito_mart_products FOR SELECT
  USING (is_active = TRUE);

-- mart orders
CREATE POLICY mart_orders_client ON vito_mart_orders FOR SELECT
  USING (client_id::TEXT = jwt_user_id());
CREATE POLICY mart_orders_driver ON vito_mart_orders FOR SELECT
  USING (driver_id::TEXT = jwt_user_id());

-- wallet transactions: own only
CREATE POLICY wallet_tx_own ON vito_wallet_transactions FOR SELECT
  USING (
    user_id::TEXT = jwt_user_id()
    OR driver_id::TEXT = jwt_user_id()
  );

-- payout requests: driver sees own
CREATE POLICY payout_driver ON vito_payout_requests FOR SELECT
  USING (driver_id::TEXT = jwt_user_id());

-- qr tokens: public read for validation
CREATE POLICY qr_public_read ON vito_qr_tokens FOR SELECT USING (TRUE);

-- referrals: driver sees own
CREATE POLICY ref_driver ON vito_referrals FOR SELECT
  USING (referring_driver_id::TEXT = jwt_user_id());

-- admins, audit_log, stripe_events: no policies = service_role only access
```

---

## 5. Edge Function Inventory (28 functions, 1 exists)

| Function | Method | Auth | TTL/Key Logic |
|---|---|---|---|
| feature-flags | GET | public | EXISTS |
| vito-check-username | GET | public | 400ms latency p95 |
| vito-validate-token | GET | public | checks is_revoked=false AND expires_at > now() |
| vito-qr-info | GET | public | returns driverName for client_referral |
| vito-register-client | POST | public+token | re-validates token; bcrypt(pin,12); increment use_count; insert referral |
| vito-register-driver | POST | public+token | bcrypt(pin,12); is_approved=FALSE; car_photo_approved=FALSE |
| vito-login | POST | public | bcrypt.compare; 5-fail lockout 15min; JWT 30-day; payload {sub,role,username,displayName,iat,exp} |
| vito-change-pin | POST | auth | verify old PIN first |
| vito-delete-account | POST | auth | verify PIN; deleted_at=NOW(); revoke own QR tokens |
| vito-reset-pin | POST | admin | random 6-digit; bcrypt; return plain ONCE |
| vito-qr-gen | POST | driver/admin | client_referral 1hr; driver_onboard 7days |
| vito-qr-revoke | POST | admin | is_revoked=true |
| vito-create-ride | POST | client | INSERT status=searching; Realtime broadcast to nearby online drivers (within 10km, service_ride=true) |
| vito-accept-job | POST | driver | calls `assign_driver_atomic()` RPC; returns {success,reason?} |
| vito-update-ride-status | POST | driver | state machine enforced; timestamps |
| vito-cancel-ride | POST | client/driver/admin | sets cancelled_by_*; reason stored |
| vito-rate-ride | POST | client | rating + tip; updates driver rating_avg |
| vito-create-send | POST | client | INSERT status=searching |
| vito-update-send-status | POST | driver | state machine for sends |
| vito-create-mart-order | POST | client | validates stock for each item |
| vito-update-mart-status | POST | driver/admin | mart state machine |
| vito-submit-signature | POST | driver | Base64 PNG → Supabase Storage `mart-signatures/<id>.png` → status=delivered |
| vito-upload-delivery-photo | POST | driver | URL saved → delivery_photo_url |
| vito-stripe-payment-sheet | POST | client | creates/retrieves Customer + PaymentIntent + EphemeralKey |
| vito-stripe-webhook | POST | public+sig | HMAC verify; idempotent via vito_stripe_events |
| vito-payout | POST | admin | Stripe Connect transfer; status=approved |
| vito-suspend-user | POST | admin | toggle is_suspended; audit |
| vito-approve-car-photo | POST | admin | car_photo_approved=TRUE; audit |
| vito-assign-driver-admin | POST | admin | calls assign_driver_atomic on driver's behalf |
| vito-cancel-order-admin | POST | admin | cancel any; reason required |

---

## 6. Design System Tokens (already in repo)

```
Colors (VitoColors.kt — exists):
  backgroundPrimary    #0A0E14
  backgroundSecondary  #10151C
  backgroundTertiary   #1A2028
  primaryAccent        #1AE694   (mint — CTAs, online, active)
  destructive          #FF5C5C
  warning              #FFD166
  contentPrimary       #F0F4F8
  contentSecondary     #8B9EB7
  onPrimary            #000000

Typography (VitoTypography.kt — exists):
  Geist Sans — UI text
  Geist Mono — wallet balances, fares, plate numbers, timestamps

Spacing (VitoSpacing.kt — exists):
  4dp grid: xs=4, sm=8, md=16, lg=24, xl=32, xxl=48, giant=64

Shapes (VitoShapes.kt — exists):
  small=8dp, medium=12dp, large=16dp, extraLarge=24dp
```

---

## 7. Complete UX Spec — All 35 Screens

### CLIENT APP — 16 screens

#### TokenGateScreen (start destination when no JWT)
```
Layout: full-screen #0A0E14
  Vito wordmark (top 40%, 56sp, primaryAccent)
  Text "To join Vito, scan an invitation QR code." (headlineSmall, contentPrimary, centered)
  [Scan QR Code] VitoButton Primary — opens ZXing fullscreen
  [Enter code manually] TextButton — expands TextField
  bottom: [Already have an account? Log in] TextButton primaryAccent

Empty/idle: shown by default
Validating: button shows spinner
Invalid: VitoSnackbar "This invitation is no longer valid"
Valid client_referral: navigate RegistrationScreen(token, driverName)
Valid driver_onboard: VitoSnackbar "This is a driver invite — open Vito Driver app instead"

ZERO: "Create Account", "Sign Up", phone fields, OTP inputs.
```

#### LoginScreen (replaces broken phone-based version)
```
Layout:
  vito wordmark + "Welcome back" headlineMedium
  VitoTextField label="Username" leadingIcon=Profile, autoCapitalize=none
  VitoPinField length=6 obscured
  AnimatedVisibility: error text (destructive, bodySmall, centered)
  [Log In] VitoButton Primary, fullWidth, enabled when username.notBlank && pin.length==6
  TextButton "Forgot PIN? Contact your inviter or admin." (contentSecondary, bodySmall)
  spacer weight 1f
  TextButton "New to Vito? Get an invitation." (primaryAccent, bodyMedium)

States:
  username_not_found → "Username not found"
  wrong_pin → "Wrong PIN — N attempts remaining"
  account_locked → "Account locked. Try again in MM:SS" (live countdown)
  network → "Couldn't reach Vito. Check your connection."

ZERO: phone field, OTP, SMS button.
```

#### RegistrationScreen
```
Layout:
  Conditional banner (non-dismissible, if client_referral):
    VitoCard primaryAccentSubtle background
    Avatar (mint circle, driver initial)
    "You're joining through [Driver Name]" titleMedium
    "Welcome to Vito!" bodySmall

  VitoTextField label="Display name" required (1-80 chars)
  VitoTextField label="Username" trailingIcon = state-driven:
    typing (<3 chars): no icon
    debounced 500ms checking: spinner
    available: green check + "Available" bodySmall
    taken: red X + "Username taken" bodySmall destructive
    invalid format: red X + "Use letters, numbers, underscore only"
  VitoPinField length=6 label="Create PIN"
  VitoPinField length=6 label="Confirm PIN" shake on mismatch + "PINs don't match" bodySmall destructive
  [Create Account] VitoButton Primary fullWidth:
    disabled while: checking, taken, mismatch, any field empty
    loading state during submit

Success: store JWT → navigate HomeScreen with popUpTo(0)
```

#### HomeScreen (flesh out scaffold)
```
TopAppBar (no elevation, transparent):
  Left: "Hello, [displayName]" titleMedium
  Right: wallet chip tappable → WalletScreen
    pill: mint border, "$25.50" labelLarge Geist Mono, primaryAccent

Active order resume (conditional VitoCard, if any active ride/send/mart):
  Service icon (40dp) + service name + status chip (animated color)
  ETA / status sentence (bodySmall)
  [Resume] VitoButton Secondary right-aligned
  Full-width, prominent, just below TopAppBar

Section: Services (3 large VitoCard, equal weight, horizontal):
  🚗 Ride / 📦 Send / 🛍️ Mart
  Each: 40dp icon, titleSmall name, bodySmall tagline (contentSecondary)
  Tap scale spring animation (0.96 → 1.0)
  Tap nav: RideBookingScreen / SendScreen / MartStoreScreen

Section: "Recent activity" (titleMedium, contentSecondary, after services):
  LazyRow of last 5 activities
  Each VitoCard (140dp wide):
    Type icon top-left, status chip top-right
    Address snippet bodyMedium 2-line max
    Fare Geist Mono labelLarge primaryAccent
    Date bodySmall contentSecondary
  [See all] TextButton at end → ActivityScreen

First-time empty state (no activities):
  VitoEmptyState(illust_welcome, "Your first trip awaits", "Pick a service to get started")
```

#### RideBookingScreen
```
Layout: VitoMapView fills screen, BottomSheet over it

Map:
  Dark style JSON (R.raw.map_style_vito)
  Pickup pin: mint circle with tail (draggable)
  Destination pin: coral square (appears after picked)
  Route polyline: mint 6dp wide
  My-location button bottom-right above sheet

BottomSheet (Material 3 ModalBottomSheet):
  State A (no destination, peek 120dp):
    Drag handle
    VitoSearchField "Where to?" full-width → opens PlacePickerScreen
  State B (destination set, peek 320dp):
    Drag handle
    Two-line address row:
      [mint dot] [pickup_address] tappable to edit
      [coral square] [dest_address] tappable to edit
    Divider
    Fare estimate row:
      "$12.50 – $15.00" headlineSmall Geist Mono primaryAccent
      "~8 min · 3.2 km" bodySmall contentSecondary
    Divider
    Payment method chip row (horizontal scroll, single-select):
      [Cash] [Wallet $25.50] [Card] [Google Pay] [Apple Pay]
      Wallet chip: shows balance; if balance<fare_min → chip border destructive, label dimmed
    [Book Ride] VitoButton Primary fullWidth large
      If wallet selected + insufficient → [Insufficient Balance – Top Up] destructive style → WalletScreen
  
On book:
  Searching overlay (modal over map):
    Pulsing mint ring around car icon, animated
    "Finding your driver…" titleMedium
    Elapsed counter mm:ss (Geist Mono)
    [Cancel] VitoButton Ghost
  On driver found: dismiss overlay → navigate ActiveRideScreen
```

#### ActiveRideScreen
```
Layout: VitoMapView full + driver info bar (top, overlay) + status stepper + bottom sheet

Map:
  Driver car marker (rotates to bearing, animates between positions)
  Pickup pin, destination pin, route polyline
  Auto-centers on driver until status='in_progress', then on route midpoint

Driver info card (overlay top, just below status bar):
  VitoCard padding md, elevation lg
  Avatar 48dp (initials)
  column: driver_name titleSmall + star_avg rating + plate_number bodySmall Geist Mono
  [📞 icon button] right — masked call

VitoStatusStepper (below driver card):
  Assigned → Driver Arrived → In Progress → Completed
  Current step: filled mint dot, label primaryAccent
  Completed: filled mint with check icon
  Future: outline only, label contentSecondary

Bottom sheet (contextual, peek 120dp):
  searching/assigned/en_route: [Cancel Ride] Ghost destructive
    → confirmation dialog "Cancel this ride? You may be charged a fee."
    → reason picker chips (optional)
    → vito-cancel-ride
  driver_arrived: VitoCard banner "Your driver has arrived" primaryAccentSubtle
  in_progress: VitoCard banner "Trip in progress" + speed indicator
  completed: RatingBottomSheet modal pushed up

SOS button: top-right corner, always-on:
  Destructive 56dp circle, icon
  → SosBottomSheet: [📞 Call Emergency 911] [📞 Call Vito Support] [📍 Share Location]

RatingBottomSheet (when status='completed', cannot dismiss until submitted):
  "How was your trip?" titleLarge centered
  Star row 1-5 (tap → spring fill animation, haptic per tap)
  Optional comment VitoTextField multiline
  Tip presets: [$0] [$1] [$2] [$5] [$10] [Custom $___]
  [Submit Rating] VitoButton Primary fullWidth → vito-rate-ride → pop to HomeScreen
```

#### SendScreen
```
Same structure as RideBookingScreen with:
  fare = fixed amount (no range) — Geist Mono "$X.XX"
  extra field: VitoTextField "Package description" optional, 1-2 lines
  payment chips: same set
  No rating sheet on completion — just "Delivered" confirmation banner
```

#### MartStoreScreen
```
TopAppBar: "VitoMart" + cart FAB (top-right, with badge)
  Cart FAB: 56dp circle primaryAccent, shopping bag icon, badge = item count
  Tap → MartCartScreen

Category chips (horizontal scroll, single-select):
  [All] [Snacks] [Drinks] [Essentials] [Household] [Pharmacy]
  Active: primaryAccent filled
  Inactive: surfaceVariant outlined

VitoSearchField below: "Search products…" debounced 300ms

Product grid (LazyVerticalGrid, 2 columns, 8dp gap):
  Each VitoCard:
    AsyncImage 4:3 with shimmer skeleton (Coil)
    Product name bodyMedium 2-line ellipsis
    Price labelLarge Geist Mono primaryAccent
    [+] icon button 32dp circle mint, right-aligned
  Out of stock: card alpha 0.5, [+] disabled, "Out of stock" chip overlay

Tap product → ProductDetailBottomSheet:
  Full image, name, price, description bodyMedium
  [Quantity steppers] [- N +]
  [Add to Cart] VitoButton Primary

Empty: VitoEmptyState "No products in this category"
```

#### MartCartScreen
```
LazyColumn of cart items:
  Each row:
    AsyncImage 56×56dp rounded
    Name + per-unit price
    Quantity stepper [-] [N] [+]
  Swipe-to-delete: red trailing action, undo snackbar 3s

Sticky bottom: VitoCard backgroundSecondary
  Subtotal row
  Delivery fee: $2.00
  Divider
  Total: titleLarge Geist Mono primaryAccent
  [Checkout] VitoButton Primary fullWidth → MartCheckoutScreen
  [Continue Shopping] Ghost → popBack

Empty: VitoEmptyState illust_empty_cart "Cart is empty" "Add items from the store"
```

#### MartCheckoutScreen
```
Sections (vertical stack with VitoCard each):

1. Delivery address:
   Map preview thumbnail + address text
   [Change] TextButton primaryAccent → PlacePickerScreen
   VitoTextField "Delivery notes" optional multiline

2. Payment method:
   chip row: [Wallet $X.XX] [Card] [Cash on Delivery]

3. Order summary:
   LazyColumn of items (name, qty, total each)
   Divider
   Subtotal + delivery fee $2.00 + Total

[Place Order] VitoButton Primary fullWidth sticky bottom:
  disabled until address + payment selected
  loading state on submit
  → vito-create-mart-order → MartTrackingScreen
```

#### MartTrackingScreen
```
Status timeline (VitoStatusStepper vertical):
  ● Placed (timestamp)
  ● Confirmed
  ● Preparing
  ● Ready
  ● On the way (when dispatched, map appears below)
  ● Delivered (when delivered, shows photo + signature)
Each step: icon, label, timestamp (when reached)
Active step: pulsing animation

Map (appears when status≥dispatched): driver location, route, ETA banner

Delivery proof (when delivered):
  VitoCard with:
    AsyncImage delivery photo, tap → fullscreen
    Signature image (rendered from signature_url)
    "Delivered to [address] at [time]"

Real-time: subscribes vito_mart_orders changes
```

#### WalletScreen
```
Hero (top):
  "$25.50" displayLarge Geist Mono primaryAccent, centered
  "Available balance" labelMedium contentSecondary, centered
  Spacer md
  [Add Money] VitoButton Primary fullWidth → TopUpBottomSheet

TopUpBottomSheet:
  "How much?" titleMedium
  Quick chips: [$10] [$20] [$50] [$100] [$200] [Custom]
  VitoTextField "$" prefix when Custom selected
  [Pay with Card] VitoButton Primary → Stripe PaymentSheet
    SheetType: Setup with saved payment methods (ephemeralKey from vito-stripe-payment-sheet)
    On dismissed (canceled): no action
    On completed: VitoSnackbar "Payment processing…" → webhook completes → balance refreshes
    On failed: VitoSnackbar destructive "Payment failed: [reason]"

Transaction history:
  Section headers by date: "Today", "This Week", "Last Week", absolute dates
  Each row:
    Type icon (🚗 ride, 📦 send, 🛍️ mart, ↑ topup, ↓ payout, ↻ refund)
    Description bodyMedium "Ride to Downtown"
    Amount labelLarge Geist Mono: green +$20.00 / red -$12.50
    Timestamp labelSmall contentSecondary right-aligned

Empty: VitoEmptyState illust_empty_wallet "No transactions yet" "Top up to get started"
```

#### ActivityScreen
```
TopAppBar: "Activity"
Filter chip row: [All] [Rides] [Deliveries] [Mart]
LazyColumn:
  Each VitoCard tappable:
    Left: type icon 40dp
    Center column: pickup → destination snippet (bodyMedium 2-line), date+time bodySmall
    Right column: fare Geist Mono titleMedium, status chip below
  Status chip colors:
    completed: primaryAccentSubtle bg, primaryAccent text
    cancelled: destructive 30% bg, destructive text
    in_progress: warningSubtle bg, warning text

Tap → ActivityDetailScreen:
  Full route map (if ride/send), full receipt, driver info, timestamps
  Completed rides: [Rebook] Ghost button → RideBookingScreen pre-filled
  Completed mart orders: [Reorder] Ghost button → CartScreen with same items
```

#### ProfileScreen
```
Header:
  Avatar 80dp circle primaryAccentSubtle, initials displayMedium
  Display name titleLarge (inline editable: tap → TextField + save icon → PATCH)
  Username @username bodyMedium contentSecondary
  If referred: "Joined through [Driver Name]" labelMedium

Section: Wallet (VitoCard):
  Balance row + [Top up] TextButton

Section: Settings (LazyColumn of rows):
  Language: [EN] [ES] toggle chip pair, immediate locale apply
  App Lock: VitoSwitch — when ON: require PIN on foreground after 1min
  Change PIN → ChangePinScreen
  Notifications: VitoSwitch — toggles vito_users.notifications_enabled
  
Section: Legal (smaller):
  Terms of Service link → external browser
  Privacy Policy link → external browser
  App version (bodySmall contentSecondary, bottom-right)

Section: Account (bottom):
  [Sign Out] VitoButton Ghost → confirmation → clear JWT → TokenGateScreen
  [Delete Account] TextButton destructive → DeleteAccountModal:
    Warning text destructive
    "Type DELETE to confirm" VitoTextField
    VitoPinField re-entry
    [Delete My Account] VitoButton destructive enabled when both correct
    → vito-delete-account → clear → TokenGateScreen
```

#### ChangePinScreen
```
Current PIN: VitoPinField length=6
Spacer md
New PIN: VitoPinField length=6
Confirm New PIN: VitoPinField length=6 shake on mismatch
[Save] VitoButton Primary fullWidth:
  enabled when: current.length==6 && new.length==6 && new==confirm
  loading on submit → vito-change-pin → VitoSnackbar "PIN updated" → popBack
  Error: wrong_pin → shake current field + error text
```

### DRIVER APP — 10 screens + 1 service

#### DriverTokenGateScreen
```
Same as client TokenGateScreen but:
  Headline: "Welcome, future Vito driver."
  Sub: "Scan your driver onboarding QR to get started."
  If client_referral scanned: error "That's a rider invitation — get the Vito client app instead."
```

#### DriverRegistrationScreen
```
Multi-step (LinearProgressIndicator at top, 4 of 4):

Step 1: Identity
  Display Name VitoTextField
  Username VitoTextField with debounced availability check (same as client)
  [Continue] full-width

Step 2: Security
  Create PIN VitoPinField
  Confirm PIN VitoPinField (shake on mismatch)
  [Continue]

Step 3: Vehicle
  Plate number VitoTextField (auto-uppercase, max 12 chars)
  Vehicle make VitoTextField
  Model VitoTextField
  Color VitoTextField
  Year VitoTextField (numeric, 4 digits)
  [Continue]

Step 4: Car photo
  Empty state: dashed rectangle "Front 3/4 view of your car"
  [Take Photo] VitoButton Primary → CameraX
  Capture flow:
    Viewfinder with overlay frame (dashed rectangle, mint)
    Capture button bottom center 72dp
    After capture: preview screen, [Retake] Ghost / [Use Photo] Primary
  After accept: thumbnail shown + "Pending admin approval" labelSmall warning
  [Complete Registration] VitoButton Primary → vito-register-driver
  → PendingApprovalScreen while admin reviews KYC
```

#### DriverHomeScreen
```
Top: Greeting time-based ("Good morning", "Good afternoon", "Good evening, [username]")
     Today's earnings titleLarge Geist Mono contentSecondary

Online/Offline toggle (DOMINANT element, center stage, VitoCard):
  When OFFLINE:
    Full-width VitoButton Primary "GO ONLINE" 64dp height
    Below subtle text "Tap to start receiving job requests"
  When ONLINE:
    Full-width VitoButton Ghost destructive outline "GO OFFLINE" 64dp
    Pulsing mint dot icon
    Below: "Receiving jobs · within 10 km · 1h 23m online" labelMedium primaryAccent
  Toggle ON: starts DriverLocationService (foreground notification "Vito — You are online")
  Toggle OFF: stops service, exits all Realtime channels

Service toggles (when online, row of 3 VitoSwitch in VitoCard):
  Rides ⬤ | Deliveries ⬤ | Mart ⬤
  Each toggle: PATCH vito_drivers.service_*

Map (lower half, when online):
  VitoMapView centered on driver, dark style
  Driver marker (car icon, not standard)
  Animated ping markers for nearby searching jobs (mint, pulsing)

Bottom navigation (always visible):
  [🏠 Home] [📊 Earnings] [🎟 My QR] [👤 Profile]
```

#### JobRequestModal (overlay — triggered by Realtime)
```
TRIGGER: DriverHomeViewModel observes RealtimeManager.observeIncomingJobs(driverId)
  When new IncomingJob arrives that's not in declinedIds set: show modal.

CANNOT BE DISMISSED:
  DialogProperties(dismissOnBackPress=false, dismissOnClickOutside=false, usePlatformDefaultWidth=false)
  BackHandler(enabled=true) { /* consume, do nothing */ }

Scrim: Color(0xCC0A0E14) covers everything

Card (centered, 320dp wide):
  Service badge chip (top):
    RIDE = #5BA4FB blue / SEND = #FF9F45 orange / MART = #9B72F8 purple
    text white labelMedium
  
  Countdown ring (Canvas):
    80dp diameter
    Background arc: white alpha 0.1, full 360°
    Active arc: drawArc startAngle=-90° sweepAngle=360*(remaining/30), strokeWidth=8dp, cap=Round
    Color animated: primaryAccent when >10s, destructive when ≤10s (animateColorAsState)
    Center text: "${seconds}s" titleMedium

  Pickup address: bodyLarge bold maxLines=2
  Destination: bodyMedium contentSecondary maxLines=2 (or "VitoMart store" for mart)
  Distance: "2.3 km from you" bodySmall contentSecondary
  
  Earnings:
    "You earn" labelSmall contentSecondary
    "$8.50" headlineMedium Geist Mono primaryAccent

  Button row (50/50 split):
    [Decline] VitoButton Ghost 50% width
    [Accept] VitoButton Primary 50% width

On appear:
  LaunchedEffect(Unit) {
    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0L,500L,200L,500L), -1))
    soundPool.play(R.raw.job_alert)
  }

Timer:
  LaunchedEffect(Unit) {
    while (remainingSeconds > 0) { delay(1000); remainingSeconds-- }
    onDecline()  // auto-decline at 0
  }

Accept:
  → vito-accept-job
  → success=true: dismiss modal, navigate to ActiveJobScreen
  → success=false (reason='taken'): dismiss silently, NO TOAST, NO ERROR

Decline:
  → viewModel.declinedIds.add(job.id)
  → dismiss modal
```

#### ActiveJobScreen (adapts to job type)
```
Map: pickup pin + dest pin + route + navigate FAB

Header card: client first name + avatar + masked phone [📞]
VitoStatusStepper showing job-type-specific states

RIDE: [I've Arrived] → [Start Trip] → [Complete Trip]
SEND: [Confirm Pickup] → [Mark Delivered]
MART:
  [Confirm Collected from Store]
  [Take Delivery Photo] → CameraX → upload → vito-upload-delivery-photo
  [Get Customer Signature] → VitoSignaturePad fullscreen:
    White canvas, detectDragGestures, Path strokes
    [Clear] / [Done]
    Done: Canvas → Bitmap (1080×540) → PNG → Base64 → vito-submit-signature → status=delivered

Navigate FAB top-right: launches Google Maps intent
Cancel button (pre-start states only) → confirmation → vito-cancel-ride
```

#### DriverEarningsScreen
```
Tab bar: [Day] [Week] [Month]

VitoBarChart (Canvas DrawScope ONLY — NO library):
  3 series: rides (#5BA4FB), sends (#FF9F45), mart (#9B72F8)
  Side-by-side bars per period (not stacked)
  X-axis labels: day/week/month names via nativeCanvas.drawText
  Y-axis line + X-axis line, white alpha 0.15

Summary row (3 VitoCard):
  Today | This week | This month
  Each: amount in Geist Mono displayMedium + period label

[Request Payout] VitoButton Primary:
  If stripe_account_id null: → StripeConnectWebView (CustomTabs)
  Else: confirmation dialog "Request $X.XX?" → INSERT vito_payout_requests status=pending

Payout history below:
  Each row: amount + status chip + date
```

#### DriverQrScreen
```
"Your referral QR" headlineMedium
"Share with riders to earn bonuses" bodyMedium contentSecondary

QR display:
  ZXing.encodeAsBitmap of https://vito.app/get?token=<token>
  240×240dp, white background black modules
  Wrapped in VitoCard padding xl

Countdown: "Valid for [47] min [32] sec" labelLarge Geist Mono
  At 0: dim QR, text destructive "Expired — refresh below"

Referrals count: "[12] riders joined through you" bodyMedium

[Share] VitoButton Primary → Intent.ACTION_SEND with QR bitmap + text
[Refresh QR] VitoButton Ghost → vito-qr-gen → new QR + reset timer
```

#### DriverProfileScreen
```
Header:
  Car photo 160×100dp rounded card
  Approval badge: "Approved ✓" primaryAccent / "Pending review" warning
  Display name + username + plate Geist Mono
  Rating: ★★★★☆ (count)

Settings:
  Service toggles (3 switches)
  Language toggle
  Change PIN → ChangePinScreen
  Bank Account → StripeConnectWebView
  [Sign Out] Ghost → stops LocationService → clears JWT → DriverTokenGateScreen
```

#### DriverLocationService (foreground service — not a screen)
```kotlin
@AndroidEntryPoint
class DriverLocationService : Service() {
    companion object {
        const val NOTIFICATION_TITLE = "Vito — You are online"  // EXACT
        const val CHANNEL_ID = "vito_driver_online"
        const val UPDATE_INTERVAL_MS = 5_000L
    }
    
    onStartCommand:
        createNotificationChannel(CHANNEL_ID, IMPORTANCE_LOW)
        startForeground(1, buildOngoingNotification())
        if (isGmsAvailable()) startFusedLocationUpdates()
        else startLegacyGpsUpdates()  // android.location.LocationManager.GPS_PROVIDER
        return START_STICKY
    
    notification:
        title = NOTIFICATION_TITLE
        text = "Receiving job requests"
        smallIcon = R.drawable.ic_vito_mark
        priority = LOW
        ongoing = true
    
    onLocation(lat, lng):
        scope.launch { locationRepo.updateLocation(lat, lng) }
        // PostgREST UPDATE vito_drivers SET current_lat, current_lng, location, location_updated_at
        // Supabase Realtime broadcasts the row change to subscribed client (NO FCM needed)
}
```

### ADMIN APP — 9 screens

#### AdminLoginScreen
```
Username VitoTextField + VitoPinField → vito-login(role='admin')
Note: admins seeded directly in DB, no registration screen.
```

#### AdminDashboardScreen
```
4 KPI VitoCards (2x2 grid, all Realtime):
  Online Drivers: count Geist Mono displayMedium + label
  Active Rides: count
  Active Mart Orders: count
  Today's Revenue: "$X.XX" Geist Mono

Activity feed below:
  LazyColumn, Realtime stream
  Each event: icon + description + timestamp
  Color-coded:
    completed: primaryAccent
    unassigned >2min: warning
    unassigned >5min: destructive
```

#### LiveOrdersMapScreen
```
Map (top 60%):
  Online drivers: green car (idle) / blue car (on job, animated along route)
  Order destination pins

Bottom list (40%, sticky):
  All active orders, sorted by age (urgent first)
  Each row: type + status chip + client name + driver/Unassigned + age "3m"
  Color background by urgency (yellow >2min, red >5min)

Tap order → AssignOrderSheet:
  Full addresses, client info
  [Assign Driver] → dropdown of online idle drivers matching service type
  [Cancel Order] → reason field → vito-cancel-order-admin
```

#### DriverManagementScreen
```
Search bar + filter chips: [All] [Online] [Offline] [Suspended] [Pending KYC]

LazyColumn driver list:
  Card: car photo thumbnail 40dp + name + username + plate Geist Mono + online dot + rating

Actions (long-press or swipe):
  [Suspend / Activate] → vito-suspend-user
  [Reset PIN] → vito-reset-pin → showOnce dialog (copy button, then gone)
  [Approve Photo] → full-screen photo viewer → [Approve] [Reject]

Tap → DriverDetailScreen (full profile, history, all admin actions)
```

#### ClientManagementScreen
```
Same structure as DriverManagementScreen
Actions: [Suspend / Activate] | [Delete Account] (with reason)
```

#### MartManagementScreen
```
Tabs: [Products] [Categories] [Orders]

Products tab:
  Filter by category + search by name
  [+ Add Product] FAB → ProductEditBottomSheet:
    Name, Category dropdown, Price, Stock (or "Unlimited"), Description, [Upload Image]
  Each product: image + name + price + stock + [Edit] [Delete]
  Low stock badge when stock < 10

Categories: drag-reorder list + [+ Add]

Orders: filter by status, advance buttons per row [Confirm] [Preparing] [Ready] [Mark Dispatched]
```

#### FinanceScreen
```
Period: [Day] [Week] [Month] [All Time]

Compose Canvas line chart (NO LIBRARY):
  Single line: total daily revenue over time
  DrawScope.drawLine + drawCircle (data points)
  Fill under line: primaryAccentSubtle
  X labels Geist Mono dates, Y labels dollar amounts

3 KPI cards:
  Total Revenue | Platform Fees (20%) | Driver Payouts

Payout Requests section:
  Filter chips [Pending] [Approved] [Rejected]
  Each row: driver name + amount + date + status
  Pending: [Approve] mint → vito-payout / [Reject] ghost → reason field
```

#### QrTokenManagementScreen
```
Tabs: [Client Referral] [Driver Onboarding]

[+ Generate] FAB:
  Client referral: dropdown to select driver → vito-qr-gen
  Driver onboarding: no driver needed → vito-qr-gen type='driver_onboard'
  → shows QR + adds to table

Table columns:
  Token (last 8 chars + "..."), Driver (or —), Created, Expires, Uses, [Revoke]
Revoke: confirmation → vito-qr-revoke → row grays, "Revoked" chip
```

#### AuditLogScreen
```
Filter chips: [All] [Auth] [Jobs] [Payments] [Admin Actions]
Search by admin username

LazyColumn (Paging3):
  Each row expandable:
    Timestamp Geist Mono
    Admin username
    Action label
    Target type + ID
    [Expand] → JSON-formatted details

Real-time: new entries prepend with flash animation
```

---

## 8. FareCalculator (exact spec)

```kotlin
// ride: range — min = 2.50 + 1.20*km; max = min*1.20
fun estimateRideFare(distanceKm: Double): Pair<Double, Double> {
    val min = (2.50 + 1.20 * distanceKm).roundTo2()
    val max = (min * 1.20).roundTo2()
    return Pair(min, max)
}
// send: fixed — 3.00 + 0.80*km
fun estimateSendFee(distanceKm: Double): Double =
    (3.00 + 0.80 * distanceKm).roundTo2()
// mart: delivery fee flat $2.00 (constant, no per-km)
```

---

## 9. CI/CD

`.github/workflows/release.yml`:
- Trigger: push tags 'v*'
- Build all 3 release APKs with R8 minify + resource shrink
- Verify DEBUG_BYPASS_LOGIN stripped: apkanalyzer dex packages | grep returns false
- APK size budgets: client≤25MB, driver≤22MB, admin≤20MB
- Upload APKs to https://cdn.vito.app/
- Update https://cdn.vito.app/version.json

`.github/workflows/supabase-deploy.yml`:
- Trigger: push to master with paths supabase/**
- Apply migrations
- Deploy all 28 edge functions
- Smoke-test feature-flags endpoint

---

## 10. App Update Mechanism

```kotlin
// MainActivity.onCreate after session validation
suspend fun checkForUpdate() {
    val info = httpClient.get("https://cdn.vito.app/version.json").body<VersionInfo>()
    if (info.versionCode > BuildConfig.VERSION_CODE) {
        showNonDismissibleUpdateDialog(info.downloadUrl, info.releaseNotes)
    }
}
```

---

## 11. Localization

values/strings.xml + values-es/strings.xml — every string in both.

Spanish text 30% longer than English. All text containers use softWrap=true.

Required keys (sample):
```
qr_gate_headline                = "To join Vito, scan an invitation QR code."
qr_gate_headline (es)           = "Para unirte a Vito, escanea un código QR de invitación."
login_headline                  = "Welcome back"
login_headline (es)             = "Bienvenido de nuevo"
driver_online_notification_title = "Vito — You are online"
driver_online_notification_title (es) = "Vito — Estás en línea"
job_modal_accept                = "Accept"
job_modal_decline               = "Decline"
insufficient_balance            = "Insufficient Balance – Top Up"
```

---

## 12. Accessibility

- Every Icon: contentDescription mandatory
- Every TextField: explicit semantics label
- Min touch target 48×48dp
- Font scale up to 200% — no layout breakage (use wrapContentHeight, no fixed text heights)
- VitoPinField cells: semantics(role=TextField, contentDescription="PIN digit N entered/empty")
- TalkBack: every screen must announce title on appear
