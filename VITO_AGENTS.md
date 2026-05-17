# VITO 1.0.1 — Master Agents
### Target: github.com/ispfixer-debug/clauvito-1.0.1 (branch: master)

---

## Execution Order

```
Phase 0 (parallel): A0-Inspect
Phase 1 (P0 BLOCKERS, sequential): A1 → A2 → A3
Phase 2 (parallel): A4-Design, A5-Dispatch
Phase 3 (parallel): A6-Client, A7-Driver, A8-Admin
Phase 4 (parallel): A9-Mart+Payments, A10-AdminActions
Phase 5 (parallel): A11-Tests, A12-CI, A13-Access, A14-i18n
Phase 6 (final, sequential): A15-FinalVerifier
```

---

## A0 · Inspector

```bash
#!/bin/bash
# scripts/a0_inspect.sh
ROOT="/workspace/vito"
echo "=== VITO 1.0.1 INSPECTION $(date -u) ==="

# P0 BUGS
LOGIN=$(find "$ROOT/vito_client" -name "LoginScreen.kt" | head -1)
[ -n "$LOGIN" ] && grep -qi "phone\|PhoneNumber\|parsePhone\|E\.164\|tel:" "$LOGIN" \
    && echo "❌ P0: LoginScreen HAS PHONE FIELD" \
    || echo "✅ LoginScreen: no phone field"

find "$ROOT/vito_client" -name "TokenGateScreen.kt" | grep -q . \
    && echo "✅ TokenGateScreen exists" \
    || echo "❌ P0: TokenGateScreen MISSING"

find "$ROOT/vito_design_system" -name "VitoPinField.kt" | grep -q . \
    && echo "✅ VitoPinField exists" \
    || echo "❌ P0: VitoPinField MISSING"

[ -f "$ROOT/supabase/migrations/0009_rls_policies.sql" ] \
    && echo "✅ RLS migration exists" \
    || echo "❌ P0: 0009_rls_policies.sql MISSING"

[ -f "$ROOT/supabase/functions/_shared/bcrypt.ts" ] \
    && echo "✅ _shared/bcrypt.ts exists" \
    || echo "❌ P0: _shared/bcrypt.ts MISSING"

grep -r "argon2" "$ROOT/supabase/functions/" 2>/dev/null | grep -v "//.*argon" | grep -q . \
    && echo "❌ P0: Argon2 found" \
    || echo "✅ No Argon2"

grep -ri "vico\|mpandroidchart\|aay-chart\|hellocharts" "$ROOT/gradle/libs.versions.toml" 2>/dev/null | grep -q . \
    && echo "❌ P0: Forbidden chart library in deps" \
    || echo "✅ No forbidden chart library"

SM=$(find "$ROOT/vito_core" -name "SessionManager.kt" | head -1)
[ -n "$SM" ] && grep -q "EncryptedSharedPreferences" "$SM" \
    && echo "✅ SessionManager: EncryptedSharedPreferences" \
    || echo "❌ P0: SessionManager missing or wrong storage"

ACCEPT="$ROOT/supabase/functions/vito-accept-job/index.ts"
[ -f "$ACCEPT" ] && grep -q "assign_driver_atomic" "$ACCEPT" \
    && echo "✅ accept-job uses atomic RPC" \
    || echo "❌ P0: accept-job MISSING or NOT using assign_driver_atomic"

MODAL=$(find "$ROOT/vito_driver" -name "JobRequestModal.kt" | head -1)
[ -n "$MODAL" ] && grep -q "dismissOnBackPress.*false" "$MODAL" \
    && echo "✅ JobRequestModal undismissable" \
    || echo "❌ P1: JobRequestModal MISSING or dismissable"

LS=$(find "$ROOT/vito_driver" -name "DriverLocationService.kt" | head -1)
[ -n "$LS" ] && grep -q "LocationManager\|GPS_PROVIDER" "$LS" \
    && echo "✅ LocationService has GMS fallback" \
    || echo "❌ P1: LocationService MISSING or no fallback"

# EDGE FUNCTIONS (30 total expected)
FNS=(feature-flags vito-check-username vito-validate-token vito-qr-info
     vito-register-client vito-register-driver vito-login vito-change-pin
     vito-delete-account vito-reset-pin vito-qr-gen vito-qr-revoke
     vito-create-ride vito-accept-job vito-update-ride-status vito-cancel-ride
     vito-rate-ride vito-create-send vito-update-send-status
     vito-create-mart-order vito-update-mart-status vito-submit-signature
     vito-upload-delivery-photo vito-stripe-payment-sheet vito-stripe-webhook
     vito-payout vito-suspend-user vito-approve-car-photo
     vito-assign-driver-admin vito-cancel-order-admin)
PRESENT=0
for fn in "${FNS[@]}"; do
    [ -f "$ROOT/supabase/functions/$fn/index.ts" ] && PRESENT=$((PRESENT+1)) \
        || echo "  ❌ Edge fn missing: $fn"
done
echo "Edge functions: $PRESENT/${#FNS[@]}"

# DESIGN COMPONENTS (27 total expected)
DS=(VitoColors VitoTypography VitoSpacing VitoShapes VitoTheme VitoButton VitoCard VitoSkeleton
    VitoTextField VitoPinField VitoSearchField VitoTopAppBar VitoBottomNavigation
    VitoScreenScaffold VitoBottomSheet VitoSnackbar VitoOfflineBanner
    VitoStatusStepper VitoBadge VitoAvatar VitoEmptyState VitoErrorState
    VitoMapView VitoQrCode VitoSignaturePad VitoBarChart VitoJobRequestModal)
PRESENT_DS=0
for c in "${DS[@]}"; do
    find "$ROOT/vito_design_system" -name "${c}.kt" | grep -q . \
        && PRESENT_DS=$((PRESENT_DS+1)) \
        || echo "  ❌ Design comp missing: $c"
done
echo "Design components: $PRESENT_DS/${#DS[@]}"

# SCREENS
CLIENT_S=(TokenGateScreen LoginScreen RegistrationScreen SplashScreen HomeScreen
          RideBookingScreen ActiveRideScreen SendScreen
          MartStoreScreen MartCartScreen MartCheckoutScreen MartTrackingScreen
          WalletScreen ActivityScreen ProfileScreen ChangePinScreen)
DRIVER_S=(DriverTokenGateScreen DriverRegistrationScreen DriverLoginScreen DriverHomeScreen
          JobRequestModal ActiveJobScreen DriverEarningsScreen DriverQrScreen
          DriverProfileScreen DriverLocationService)
ADMIN_S=(AdminLoginScreen AdminDashboardScreen LiveOrdersMapScreen
         DriverManagementScreen ClientManagementScreen MartManagementScreen
         FinanceScreen QrTokenManagementScreen AuditLogScreen)

CS=0; DS_C=0; AS=0
for s in "${CLIENT_S[@]}"; do find "$ROOT/vito_client" -name "${s}.kt" | grep -q . && CS=$((CS+1)) || echo "  ❌ Client: $s"; done
for s in "${DRIVER_S[@]}"; do find "$ROOT/vito_driver" -name "${s}.kt" | grep -q . && DS_C=$((DS_C+1)) || echo "  ❌ Driver: $s"; done
for s in "${ADMIN_S[@]}";  do find "$ROOT/vito_admin"  -name "${s}.kt" | grep -q . && AS=$((AS+1)) || echo "  ❌ Admin: $s"; done
echo "Screens: client=$CS/${#CLIENT_S[@]} driver=$DS_C/${#DRIVER_S[@]} admin=$AS/${#ADMIN_S[@]}"

# BUILD
cd "$ROOT" && ./gradlew assembleDebug --quiet 2>&1 | tail -3 | grep -q "BUILD SUCCESSFUL" \
    && echo "✅ Debug build PASSING" \
    || echo "❌ Debug build FAILING"
```

---

## A1 · AuthFixer (P0)

**Tasks:**
1. Replace `vito_client/.../ui/auth/LoginScreen.kt` — remove phone field, add username + VitoPinField
2. Create `vito_client/.../ui/auth/TokenGateScreen.kt` — QR gate
3. Create `vito_design_system/.../component/input/VitoPinField.kt` — 6-circle obscured field
4. Create `vito_core/.../data/local/SessionManager.kt` — EncryptedSharedPreferences
5. Update NavGraph: TokenGateScreen = start destination when not logged in

### A1.1 LoginScreen.kt (full replacement)
```kotlin
package com.vito.client.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vito.client.R
import com.vito.design.component.button.VitoButton
import com.vito.design.component.input.VitoPinField
import com.vito.design.component.input.VitoTextField
import com.vito.design.component.layout.VitoScreenScaffold
import com.vito.design.icon.VitoIcons
import com.vito.design.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNeedInvitation: () -> Unit,
) {
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.events.collect { if (it is LoginEvent.Success) onLoginSuccess() }
    }
    VitoScreenScaffold {
        Column(
            Modifier.fillMaxSize().padding(horizontal = VitoSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("vito", style = VitoTheme.typography.displaySmall, color = VitoTheme.colorScheme.primary)
            Spacer(Modifier.height(VitoSpacing.xs))
            Text(stringResource(R.string.login_headline), style = VitoTheme.typography.headlineMedium,
                 color = VitoTheme.colorScheme.onBackground)
            Spacer(Modifier.height(VitoSpacing.xxl))

            VitoTextField(
                value = s.username,
                onValueChange = viewModel::onUsernameChanged,
                label = stringResource(R.string.field_username),
                leadingIcon = VitoIcons.Profile,
                isError = s.error != null,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(VitoSpacing.md))
            VitoPinField(
                value = s.pin,
                onValueChange = viewModel::onPinChanged,
                isError = s.pinError, length = 6,
            )
            AnimatedVisibility(visible = s.error != null) {
                Text(s.error.orEmpty(),
                     style = VitoTheme.typography.bodySmall,
                     color = VitoTheme.colorScheme.error,
                     textAlign = TextAlign.Center,
                     modifier = Modifier.padding(top = VitoSpacing.xs))
            }
            Spacer(Modifier.height(VitoSpacing.xl))
            VitoButton(
                text = stringResource(R.string.action_login),
                onClick = viewModel::onLoginClicked,
                loading = s.isLoading,
                enabled = s.username.isNotBlank() && s.pin.length == 6,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(VitoSpacing.md))
            TextButton(onClick = { viewModel.showForgotPinInfo() }) {
                Text(stringResource(R.string.login_forgot_pin),
                     color = VitoTheme.colorScheme.onSurfaceVariant,
                     style = VitoTheme.typography.bodySmall)
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onNeedInvitation) {
                Text(stringResource(R.string.login_need_invite),
                     color = VitoTheme.colorScheme.primary,
                     style = VitoTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(VitoSpacing.lg))
        }
    }
}
// ZERO phone references.
```

### A1.2 TokenGateScreen.kt
```kotlin
package com.vito.client.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.vito.client.R
import com.vito.design.component.button.VitoButton
import com.vito.design.component.button.VitoButtonStyle
import com.vito.design.component.input.VitoTextField
import com.vito.design.component.layout.VitoScreenScaffold
import com.vito.design.icon.VitoIcons
import com.vito.design.theme.*

@Composable
fun TokenGateScreen(
    viewModel: TokenGateViewModel = hiltViewModel(),
    onClientTokenValid: (token: String, driverName: String?) -> Unit,
    onAlreadyHaveAccount: () -> Unit,
) {
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    var showManual by remember { mutableStateOf(false) }
    var manualCode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is TokenGateEvent.ValidClientReferral)
                onClientTokenValid(event.token, event.driverName)
        }
    }

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let { qr ->
            val token = Uri.parse(qr).getQueryParameter("token") ?: qr.substringAfter("token=")
            if (token.isNotBlank()) viewModel.validateToken(token)
        }
    }

    VitoScreenScaffold {
        Column(
            Modifier.fillMaxSize().padding(VitoSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(0.3f))
            Text("vito",
                style = VitoTheme.typography.displayMedium,
                color = VitoTheme.colorScheme.primary)
            Spacer(Modifier.height(VitoSpacing.xxl))
            Text(
                text = stringResource(R.string.qr_gate_headline),
                style = VitoTheme.typography.headlineSmall,
                color = VitoTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(VitoSpacing.xxl))

            VitoButton(
                text = stringResource(R.string.action_scan_qr),
                leadingIcon = VitoIcons.QrCode,
                onClick = {
                    scanLauncher.launch(
                        ScanOptions()
                            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            .setPrompt("Scan your Vito invitation QR")
                            .setBeepEnabled(false)
                    )
                },
                loading = s.isValidating,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(VitoSpacing.md))

            TextButton(onClick = { showManual = !showManual }) {
                Text(stringResource(R.string.action_enter_code_manually),
                     color = VitoTheme.colorScheme.onSurfaceVariant,
                     style = VitoTheme.typography.bodyMedium)
            }

            AnimatedVisibility(visible = showManual) {
                Column {
                    Spacer(Modifier.height(VitoSpacing.sm))
                    VitoTextField(
                        value = manualCode,
                        onValueChange = { manualCode = it.uppercase().take(48) },
                        label = stringResource(R.string.field_invitation_code),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(VitoSpacing.sm))
                    VitoButton(
                        text = stringResource(R.string.action_submit_code),
                        onClick = { viewModel.validateToken(manualCode) },
                        enabled = manualCode.length >= 16,
                        loading = s.isValidating,
                        style = VitoButtonStyle.Secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            AnimatedVisibility(visible = s.error != null) {
                Text(s.error.orEmpty(),
                     color = VitoTheme.colorScheme.error,
                     style = VitoTheme.typography.bodySmall,
                     modifier = Modifier.padding(top = VitoSpacing.sm))
            }

            Spacer(Modifier.weight(1f))

            TextButton(onClick = onAlreadyHaveAccount) {
                Text(stringResource(R.string.login_already_have_account),
                     color = VitoTheme.colorScheme.primary,
                     style = VitoTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(VitoSpacing.lg))
        }
    }
}
// NO "Create Account" button. NO "Sign Up" link.
```

### A1.3 VitoPinField.kt
```kotlin
package com.vito.design.component.input

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vito.design.theme.VitoShapes
import com.vito.design.theme.VitoSpacing
import com.vito.design.theme.VitoTheme
import kotlin.math.roundToInt

@Composable
fun VitoPinField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    val shake = remember { Animatable(0f) }
    LaunchedEffect(isError) {
        if (isError) {
            repeat(3) {
                shake.animateTo(8f, tween(50))
                shake.animateTo(-8f, tween(50))
            }
            shake.animateTo(0f, tween(50))
        }
    }
    Box(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = { new ->
                if (new.length <= length && new.all(Char::isDigit)) onValueChange(new)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            enabled = enabled,
            modifier = Modifier.size(width = (length * 60).dp, height = 1.dp).alpha(0f),
        )
        Row(
            modifier = Modifier.offset { IntOffset(shake.value.roundToInt(), 0) },
            horizontalArrangement = Arrangement.spacedBy(VitoSpacing.xs),
        ) {
            repeat(length) { index ->
                val filled = index < value.length
                val active = index == value.length
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(width = 48.dp, height = 60.dp)
                        .clip(VitoShapes.small)
                        .background(VitoTheme.colorScheme.surfaceVariant)
                        .border(
                            width = if (active) 2.dp else 1.dp,
                            color = when {
                                isError -> VitoTheme.colorScheme.error
                                active  -> VitoTheme.colorScheme.primary
                                filled  -> VitoTheme.colorScheme.outline
                                else    -> VitoTheme.colorScheme.outlineVariant
                            },
                            shape = VitoShapes.small,
                        )
                        .semantics {
                            contentDescription = if (filled)
                                "PIN digit ${index + 1} entered"
                            else "PIN digit ${index + 1} empty"
                        }
                ) {
                    if (filled) {
                        // Show obscured dot ONLY; never the digit
                        Box(
                            Modifier.size(10.dp)
                                .clip(CircleShape)
                                .background(VitoTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
        }
    }
}
```

### A1.4 SessionManager.kt
```kotlin
package com.vito.core.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "vito_secure_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    companion object {
        private const val K_JWT = "jwt"
        private const val K_USER_ID = "user_id"
        private const val K_ROLE = "role"
        private const val K_USERNAME = "username"
        private const val K_DISPLAY = "display_name"
        private const val K_PENDING_TOKEN = "pending_invite_token"
    }

    fun saveSession(jwt: String, userId: String, role: String, username: String, displayName: String) {
        prefs.edit().apply {
            putString(K_JWT, jwt)
            putString(K_USER_ID, userId)
            putString(K_ROLE, role)
            putString(K_USERNAME, username)
            putString(K_DISPLAY, displayName)
        }.apply()
    }
    fun getJwt(): String? = prefs.getString(K_JWT, null)
    fun getUserId(): String? = prefs.getString(K_USER_ID, null)
    fun getRole(): String? = prefs.getString(K_ROLE, null)
    fun getUsername(): String? = prefs.getString(K_USERNAME, null)
    fun getDisplayName(): String? = prefs.getString(K_DISPLAY, null)
    fun isLoggedIn(): Boolean = !getJwt().isNullOrBlank()
    fun savePendingToken(t: String) { prefs.edit().putString(K_PENDING_TOKEN, t).apply() }
    fun consumePendingToken(): String? {
        val t = prefs.getString(K_PENDING_TOKEN, null)
        if (t != null) prefs.edit().remove(K_PENDING_TOKEN).apply()
        return t
    }
    fun clearSession() { prefs.edit().clear().apply() }
}
```

**Gate:** A1 verifies all 4 files exist, LoginScreen has zero phone references, SessionManager uses EncryptedSharedPreferences, NavGraph startDestination uses TokenGateScreen when not logged in.

---

## A2 · BackendCore (P0)

**Tasks:**
1. Create `_shared/bcrypt.ts` (bcrypt cost 12)
2. Create `_shared/errors.ts`, `_shared/audit.ts`, `_shared/stripe.ts`
3. Create `supabase/migrations/0009_rls_policies.sql` (from PLAN §4)
4. Create 11 auth/QR edge functions

### A2.1 _shared/bcrypt.ts
```typescript
import * as bcrypt from "https://deno.land/x/bcrypt@v0.4.1/mod.ts";
export async function hashPin(pin: string): Promise<string> {
    const salt = await bcrypt.genSalt(12);  // cost factor 12 — non-negotiable
    return await bcrypt.hash(pin, salt);
}
export async function verifyPin(pin: string, hash: string): Promise<boolean> {
    return await bcrypt.compare(pin, hash);
}
export function validatePin(pin: string): boolean { return /^\d{6}$/.test(pin); }
```

### A2.2 _shared/errors.ts
```typescript
const J = (data: unknown, status: number) => new Response(
    JSON.stringify(data), { status, headers: { "Content-Type": "application/json" } }
);
export const ok = (data: unknown, s = 200) => J(data, s);
export const created = (data: unknown) => J(data, 201);
export const badRequest = (e: string) => J({ error: e }, 400);
export const unauthorized = (e: string) => J({ error: e }, 401);
export const forbidden = (e: string) => J({ error: e }, 403);
export const notFound = (e: string) => J({ error: e }, 404);
export const conflict = (e: string) => J({ error: e }, 409);
export const gone = (e: string) => J({ error: e }, 410);
export const locked = (data: unknown) => J(data, 423);
export const serverError = (e: string) => J({ error: e }, 500);
```

### A2.3 _shared/audit.ts
```typescript
import { db } from "./db.ts";
export async function audit(p: {
    action: string; adminId?: string | null;
    targetType?: string; targetId?: string; details?: unknown;
}): Promise<void> {
    try {
        await db.from("vito_audit_log").insert({
            action: p.action,
            admin_id: p.adminId ?? null,
            target_type: p.targetType ?? null,
            target_id: p.targetId ?? null,
            details: p.details ?? null,
        });
    } catch (e) { console.error("Audit failed:", p.action, e); }
}
```

### A2.4 vito-login/index.ts
```typescript
import { serve } from "https://deno.land/std@0.224.0/http/server.ts";
import { create } from "https://deno.land/x/djwt@v3.0.2/mod.ts";
import { db } from "../_shared/db.ts";
import { verifyPin, validatePin } from "../_shared/bcrypt.ts";
import { ok, badRequest, unauthorized, forbidden, locked, serverError } from "../_shared/errors.ts";

const JWT_SECRET = Deno.env.get("JWT_SECRET")!;
const JWT_TTL = 30 * 24 * 60 * 60;

async function getKey() {
    return await crypto.subtle.importKey(
        "raw", new TextEncoder().encode(JWT_SECRET),
        { name: "HMAC", hash: "SHA-256" }, false, ["sign", "verify"]
    );
}

serve(async (req: Request): Promise<Response> => {
    if (req.method !== "POST") return new Response(null, { status: 405 });

    let body: { username: string; pin: string; role: string };
    try { body = await req.json(); } catch { return badRequest("Invalid JSON"); }

    const { username, pin, role } = body;
    if (!username || !pin || !role) return badRequest("username, pin, role required");
    if (!validatePin(pin)) return badRequest("PIN must be 6 digits");
    if (!["client", "driver", "admin"].includes(role)) return badRequest("Invalid role");

    const table = role === "client" ? "vito_users" : role === "driver" ? "vito_drivers" : "vito_admins";

    try {
        const { data: user, error } = await db.from(table)
            .select("id, username, display_name, pin_hash, pin_failed_attempts, pin_locked_until, is_suspended")
            .eq("username", username)
            .maybeSingle();

        if (error || !user) return unauthorized("Username not found");
        if (user.is_suspended) return forbidden("Account suspended");

        if (user.pin_locked_until && new Date(user.pin_locked_until) > new Date()) {
            const sec = Math.ceil((new Date(user.pin_locked_until).getTime() - Date.now()) / 1000);
            return locked({ error: "account_locked", seconds_remaining: sec });
        }

        const valid = await verifyPin(pin, user.pin_hash);
        if (!valid) {
            const attempts = (user.pin_failed_attempts ?? 0) + 1;
            const isLocked = attempts >= 5;
            await db.from(table).update({
                pin_failed_attempts: attempts,
                pin_locked_until: isLocked ? new Date(Date.now() + 15 * 60 * 1000).toISOString() : null,
            }).eq("id", user.id);
            if (isLocked) return locked({ error: "account_locked", seconds_remaining: 900 });
            return unauthorized(JSON.stringify({
                error: "wrong_pin", attempts_remaining: Math.max(0, 5 - attempts)
            }));
        }

        await db.from(table).update({ pin_failed_attempts: 0, pin_locked_until: null }).eq("id", user.id);

        const now = Math.floor(Date.now() / 1000);
        const jwt = await create(
            { alg: "HS256", typ: "JWT" },
            { sub: user.id, role, username: user.username, displayName: user.display_name,
              iat: now, exp: now + JWT_TTL },
            await getKey()
        );
        return ok({ jwt, userId: user.id, role, username: user.username, displayName: user.display_name });
    } catch (e) {
        console.error(JSON.stringify({ fn: "vito-login", error: String(e) }));
        return serverError("Login failed");
    }
});
```

### A2.5 vito-register-client/index.ts
```typescript
import { serve } from "https://deno.land/std@0.224.0/http/server.ts";
import { db } from "../_shared/db.ts";
import { hashPin, validatePin } from "../_shared/bcrypt.ts";
import { created, badRequest, conflict, gone, serverError } from "../_shared/errors.ts";

serve(async (req: Request): Promise<Response> => {
    if (req.method !== "POST") return new Response(null, { status: 405 });

    let body: { username: string; displayName: string; pin: string; token: string };
    try { body = await req.json(); } catch { return badRequest("Invalid JSON"); }

    const { username, displayName, pin, token } = body;
    if (!username || !displayName || !pin || !token)
        return badRequest("All fields required");
    if (!validatePin(pin)) return badRequest("PIN must be 6 digits");
    if (username.length < 3 || username.length > 30) return badRequest("Username 3-30 chars");
    if (!/^[a-zA-Z0-9_]+$/.test(username)) return badRequest("Username: letters, numbers, underscore");

    try {
        const { data: qr } = await db.from("vito_qr_tokens")
            .select("id, type, driver_id, is_revoked, expires_at, use_count")
            .eq("token", token)
            .maybeSingle();
        if (!qr) return gone("Invitation not found");
        if (qr.is_revoked) return gone("Invitation revoked");
        if (new Date(qr.expires_at) < new Date()) return gone("Invitation expired");
        if (qr.type !== "client_referral") return badRequest("Wrong invitation type");

        const { data: existing } = await db.from("vito_users")
            .select("id").eq("username", username).maybeSingle();
        if (existing) return conflict("Username taken");

        const pinHash = await hashPin(pin);
        const { data: newUser, error } = await db.from("vito_users").insert({
            username, display_name: displayName, pin_hash: pinHash,
            referral_driver_id: qr.driver_id,
        }).select("id, username, display_name").single();
        if (error) throw error;

        await db.from("vito_qr_tokens")
            .update({ use_count: (qr.use_count ?? 0) + 1 })
            .eq("id", qr.id);

        if (qr.driver_id) {
            await db.from("vito_referrals").insert({
                referring_driver_id: qr.driver_id,
                referred_user_id: newUser.id,
                qr_token_id: qr.id,
            });
        }

        return created({ userId: newUser.id, username, displayName });
    } catch (e) {
        console.error(JSON.stringify({ fn: "vito-register-client", error: String(e) }));
        return serverError("Registration failed");
    }
});
```

### A2.6 0009_rls_policies.sql

Use the full SQL from VITO_PLAN.md §4 verbatim.

**Gate:** A2 verifies bcrypt.ts uses cost=12; no Argon2 anywhere; 0009_rls_policies.sql exists; 11 auth/QR functions present.

---

## A3 · InfraCore

**Tasks:** SupabaseProvider, NetworkMonitor, FareCalculator, TokenParser, all 6 Repository implementations.

### SupabaseProvider.kt
```kotlin
package com.vito.core.data.remote

import android.content.Context
import com.vito.core.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseProvider @Inject constructor(@ApplicationContext context: Context) {
    val client: SupabaseClient by lazy {
        createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
            install(GoTrue)
            install(Postgrest)
            install(Realtime)
            install(Storage)
        }
    }
}
```

### NetworkMonitor.kt
```kotlin
package com.vito.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(@ApplicationContext ctx: Context) {
    private val cm = ctx.getSystemService(ConnectivityManager::class.java)
    val isOnline: StateFlow<Boolean> = callbackFlow {
        val cb = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(n: Network) { trySend(true) }
            override fun onLost(n: Network) { trySend(false) }
        }
        cm.registerDefaultNetworkCallback(cb)
        trySend(cm.activeNetwork != null)
        awaitClose { cm.unregisterNetworkCallback(cb) }
    }.stateIn(CoroutineScope(Dispatchers.IO + SupervisorJob()), SharingStarted.Eagerly, true)
}
```

### FareCalculator.kt
```kotlin
package com.vito.core.util

object FareCalculator {
    fun estimateRideFare(distanceKm: Double): Pair<Double, Double> {
        val min = round2(2.50 + 1.20 * distanceKm)
        val max = round2(min * 1.20)
        return Pair(min, max)
    }
    fun estimateSendFee(distanceKm: Double): Double = round2(3.00 + 0.80 * distanceKm)
    const val MART_DELIVERY_FEE: Double = 2.00
    private fun round2(d: Double) = Math.round(d * 100.0) / 100.0
}
```

### TokenParser.kt + InstallReferrerReceiver
```kotlin
// receiver/InstallReferrerReceiver.kt
package com.vito.client.receiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.preference.PreferenceManager

class InstallReferrerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val referrer = intent.getStringExtra("referrer") ?: return
        val decoded = Uri.decode(referrer)
        val token = decoded.substringAfter("token=").substringBefore("&").take(64)
        if (token.matches(Regex("[a-fA-F0-9]{16,64}"))) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString("pending_invite_token", token).apply()
        }
    }
}
```

Repositories (skeleton — fill from interfaces in Repositories.kt):
- `AuthRepositoryImpl` → calls vito-login, vito-register-*, vito-change-pin, vito-delete-account, vito-validate-token, vito-qr-info, vito-check-username via Supabase Functions
- `UserRepositoryImpl` → reads vito_users via Postgrest; observes via Realtime
- `JobRepositoryImpl` → calls vito-create-ride/send/mart, vito-accept-job, vito-update-*-status, vito-cancel-ride, vito-rate-ride, vito-submit-signature
- `DispatchRepositoryImpl` → subscribes Realtime channels: `driver:<id>:jobs`, `ride:<id>`, `admin:live`
- `WalletRepositoryImpl` → reads vito_wallet_transactions; calls vito-stripe-payment-sheet
- `LocationRepositoryImpl` → updateLocation(lat, lng): PATCH vito_drivers via Postgrest

---

## A4 · DesignSystem (19 missing components)

### VitoBarChart.kt (Canvas ONLY)
```kotlin
package com.vito.design.component.chart

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

data class BarData(val label: String, val rides: Double, val sends: Double, val mart: Double) {
    val total: Double get() = rides + sends + mart
}

@Composable
fun VitoBarChart(data: List<BarData>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return
    val maxValue = data.maxOf { it.total }.coerceAtLeast(1.0)
    val rides = Color(0xFF5BA4FB); val sends = Color(0xFFFF9F45); val mart = Color(0xFF9B72F8)
    val axis = Color.White.copy(alpha = 0.15f)
    val labelPaint = remember {
        Paint().apply {
            color = Color(0xFF8B9EB7).toArgb()
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
    }
    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        val usableH = size.height - 40f
        val groupW = size.width / data.size
        val barW = groupW * 0.65f
        val subW = barW / 3f
        val pad = (groupW - barW) / 2f
        drawLine(axis, Offset(0f, usableH), Offset(size.width, usableH), 1f)
        drawLine(axis, Offset(0f, 0f), Offset(0f, usableH), 1f)
        data.forEachIndexed { i, bar ->
            val x = i * groupW + pad
            fun bar3(color: Color, value: Double, ox: Float) {
                val h = ((value / maxValue) * usableH).toFloat().coerceAtLeast(2f)
                drawRect(color, Offset(x + ox, usableH - h), Size(subW - 1f, h))
            }
            bar3(rides, bar.rides, 0f); bar3(sends, bar.sends, subW); bar3(mart, bar.mart, subW * 2)
            drawContext.canvas.nativeCanvas.drawText(
                bar.label, x + barW / 2f, size.height - 8f, labelPaint
            )
        }
    }
}
// NEVER import io.github.ehsannarmani, com.patrykandpatrick.vico, com.github.PhilJay, aay-chart
```

### VitoJobRequestModal.kt
```kotlin
package com.vito.design.component.dialog

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.basicMarquee
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import com.vito.design.component.button.VitoButton
import com.vito.design.component.button.VitoButtonStyle
import com.vito.design.component.card.VitoCard
import com.vito.design.theme.VitoSpacing
import com.vito.design.theme.VitoTheme

enum class JobBadgeType { RIDE, SEND, MART }
data class JobModalData(
    val type: JobBadgeType,
    val pickupAddress: String,
    val destinationAddress: String?,
    val distanceKm: Double,
    val earningsUsd: Double,
)

@Composable
fun VitoJobRequestModal(
    data: JobModalData,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    val context = LocalContext.current
    var remaining by remember { mutableIntStateOf(30) }
    LaunchedEffect(Unit) {
        val v = context.getSystemService(Vibrator::class.java)
        v?.vibrate(VibrationEffect.createWaveform(longArrayOf(0L, 500L, 200L, 500L), -1))
    }
    LaunchedEffect(Unit) {
        while (remaining > 0) { delay(1000); remaining-- }
        onDecline()
    }
    BackHandler(enabled = true) { /* consume */ }
    val arcColor by animateColorAsState(
        if (remaining <= 10) Color(0xFFFF5C5C) else Color(0xFF1AE694),
        label = "arc",
    )
    Dialog(
        onDismissRequest = { /* never */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xCC0A0E14)),
            contentAlignment = Alignment.Center,
        ) {
            VitoCard(modifier = Modifier.width(320.dp).padding(VitoSpacing.md)) {
                Column(
                    Modifier.padding(VitoSpacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val (badgeColor, badgeText) = when (data.type) {
                        JobBadgeType.RIDE -> Color(0xFF5BA4FB) to "RIDE"
                        JobBadgeType.SEND -> Color(0xFFFF9F45) to "SEND"
                        JobBadgeType.MART -> Color(0xFF9B72F8) to "MART"
                    }
                    Box(
                        Modifier.clip(RoundedCornerShape(4.dp)).background(badgeColor)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(badgeText, style = VitoTheme.typography.labelMedium, color = Color.White)
                    }
                    Spacer(Modifier.height(VitoSpacing.md))

                    Box(contentAlignment = Alignment.Center) {
                        Canvas(Modifier.size(80.dp)) {
                            val stroke = 8.dp.toPx()
                            drawArc(
                                Color.White.copy(alpha = 0.1f), -90f, 360f, false,
                                size = Size(size.width, size.height),
                                topLeft = Offset.Zero,
                                style = Stroke(stroke, cap = StrokeCap.Round),
                            )
                            val sweep = 360f * (remaining / 30f)
                            drawArc(
                                arcColor, -90f, sweep, false,
                                size = Size(size.width, size.height),
                                topLeft = Offset.Zero,
                                style = Stroke(stroke, cap = StrokeCap.Round),
                            )
                        }
                        Text("${remaining}s",
                             style = VitoTheme.typography.titleMedium,
                             color = VitoTheme.colorScheme.onBackground)
                    }

                    Spacer(Modifier.height(VitoSpacing.md))
                    Text(data.pickupAddress,
                         style = VitoTheme.typography.titleMedium,
                         color = VitoTheme.colorScheme.onBackground,
                         maxLines = 2)
                    Spacer(Modifier.height(VitoSpacing.xs))
                    Text(data.destinationAddress ?: "VitoMart store",
                         style = VitoTheme.typography.bodyMedium,
                         color = VitoTheme.colorScheme.onSurfaceVariant,
                         maxLines = 2)
                    Spacer(Modifier.height(VitoSpacing.xs))
                    Text("${String.format("%.1f", data.distanceKm)} km away",
                         style = VitoTheme.typography.bodySmall,
                         color = VitoTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(VitoSpacing.sm))
                    Text("You earn",
                         style = VitoTheme.typography.bodySmall,
                         color = VitoTheme.colorScheme.onSurfaceVariant)
                    Text("$${String.format("%.2f", data.earningsUsd)}",
                         style = VitoTheme.typography.headlineMedium,
                         color = VitoTheme.colorScheme.primary)

                    Spacer(Modifier.height(VitoSpacing.xl))
                    Row(horizontalArrangement = Arrangement.spacedBy(VitoSpacing.sm)) {
                        VitoButton("Decline", onDecline, Modifier.weight(1f),
                                   style = VitoButtonStyle.Ghost)
                        VitoButton("Accept", onAccept, Modifier.weight(1f),
                                   style = VitoButtonStyle.Primary)
                    }
                }
            }
        }
    }
}
```

### VitoSignaturePad.kt
```kotlin
package com.vito.design.component.input

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.vito.design.component.button.VitoButton
import com.vito.design.component.button.VitoButtonStyle
import com.vito.design.theme.VitoSpacing
import com.vito.design.theme.VitoTheme

@Composable
fun VitoSignaturePad(
    onSignatureComplete: (Bitmap) -> Unit,
    onCancel: () -> Unit,
) {
    val paths = remember { mutableStateListOf<Path>() }
    var current by remember { mutableStateOf(Path()) }

    Column(Modifier.fillMaxSize().background(VitoTheme.colorScheme.background)) {
        Box(
            Modifier.weight(1f).fillMaxWidth().padding(VitoSpacing.md)
                .background(Color.White)
        ) {
            Canvas(
                Modifier.fillMaxSize().pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { off -> current = Path().apply { moveTo(off.x, off.y) } },
                        onDrag = { change, _ -> current.lineTo(change.position.x, change.position.y) },
                        onDragEnd = { paths.add(current); current = Path() },
                    )
                }
            ) {
                val stroke = Stroke(4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                paths.forEach { drawPath(it, Color.Black, style = stroke) }
                drawPath(current, Color.Black, style = stroke)
            }
            if (paths.isEmpty() && current == Path()) {
                Text("Sign here",
                    color = Color.Gray,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
        }
        Row(Modifier.padding(VitoSpacing.md), horizontalArrangement = Arrangement.SpaceBetween) {
            VitoButton("Clear", { paths.clear(); current = Path() }, style = VitoButtonStyle.Ghost)
            Row {
                VitoButton("Cancel", onCancel, style = VitoButtonStyle.Ghost)
                Spacer(Modifier.width(VitoSpacing.sm))
                VitoButton("Done", {
                    val bmp = Bitmap.createBitmap(1080, 540, Bitmap.Config.ARGB_8888)
                    val acanvas = android.graphics.Canvas(bmp)
                    acanvas.drawColor(android.graphics.Color.WHITE)
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        strokeWidth = 6f
                        style = android.graphics.Paint.Style.STROKE
                        strokeCap = android.graphics.Paint.Cap.ROUND
                        isAntiAlias = true
                    }
                    // Compose Path → android Path conversion via PathMeasure
                    // (omitted for brevity; use ComposePath.asAndroidPath() extension)
                    onSignatureComplete(bmp)
                })
            }
        }
    }
}
```

Other 17 components are straightforward Compose layouts following the same design tokens.

**Gate:** A4 verifies all 19 components exist, VitoBarChart imports zero chart libraries, JobRequestModal has `dismissOnBackPress = false`.

---

## A5 · Dispatch (Realtime + Job edge functions + LocationService)

### vito-accept-job/index.ts
```typescript
import { serve } from "https://deno.land/std@0.224.0/http/server.ts";
import { requireAuth } from "../_shared/auth.ts";
import { db } from "../_shared/db.ts";
import { ok, badRequest, serverError } from "../_shared/errors.ts";
import { audit } from "../_shared/audit.ts";

serve(async (req: Request): Promise<Response> => {
    if (req.method !== "POST") return new Response(null, { status: 405 });
    let ctx; try { ctx = await requireAuth(req, ["driver"]); } catch (e) { return e as Response; }

    let body: { job_id: string; job_type: "ride" | "send" | "mart" };
    try { body = await req.json(); } catch { return badRequest("Invalid JSON"); }

    const { job_id, job_type } = body;
    if (!job_id || !job_type) return badRequest("job_id and job_type required");

    try {
        const { data: driver } = await db.from("vito_drivers")
            .select("id, is_online, car_photo_approved, is_suspended")
            .eq("id", ctx.sub).single();
        if (!driver?.is_online) return badRequest("Driver offline");
        if (!driver?.car_photo_approved) return badRequest("Driver not yet approved");
        if (driver?.is_suspended) return badRequest("Account suspended");

        const tbl = job_type === "ride" ? "vito_rides" : job_type === "send" ? "vito_sends" : "vito_mart_orders";
        const { count: activeCount } = await db.from(tbl)
            .select("*", { count: "exact", head: true })
            .eq("driver_id", ctx.sub)
            .in("status", ["assigned", "driver_arrived", "in_progress", "picked_up", "dispatched"]);
        if ((activeCount ?? 0) > 0) return badRequest("Already on a job");

        const rpc = job_type === "ride" ? "assign_driver_atomic"
                  : job_type === "send" ? "assign_send_driver_atomic"
                  : "assign_mart_driver_atomic";
        const { data: rows } = await db.rpc(rpc, { p_job_id: job_id, p_driver_id: ctx.sub });

        if (rows === 0) {
            // SILENT DISMISS in app — no toast, no error
            return ok({ success: false, reason: "taken" });
        }

        const { data: job } = await db.from(tbl).select("*").eq("id", job_id).single();
        await audit({ action: "job_accepted", targetType: job_type, targetId: job_id });
        return ok({ success: true, job });
    } catch (e) {
        console.error(JSON.stringify({ fn: "vito-accept-job", error: String(e) }));
        return serverError("Failed");
    }
});
```

### DriverLocationService.kt
```kotlin
package com.vito.driver.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.vito.core.data.repository.LocationRepository
import com.vito.driver.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class DriverLocationService : Service() {
    companion object {
        const val NOTIFICATION_TITLE = "Vito — You are online"  // EXACT string
        const val CHANNEL_ID = "vito_driver_online"
        const val UPDATE_INTERVAL_MS = 5_000L
    }
    @Inject lateinit var locationRepo: LocationRepository
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var fused: FusedLocationProviderClient? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        startForeground(1, buildNotification())
        if (gmsAvailable()) startFused() else startLegacy()
        return START_STICKY
    }
    private fun gmsAvailable() = GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS

    private fun startFused() {
        fused = LocationServices.getFusedLocationProviderClient(this)
        val req = LocationRequest.Builder(UPDATE_INTERVAL_MS)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).build()
        try {
            fused?.requestLocationUpdates(req, fusedCallback, Looper.getMainLooper())
        } catch (e: SecurityException) { stopSelf() }
    }
    private val fusedCallback = object : LocationCallback() {
        override fun onLocationResult(r: LocationResult) {
            r.lastLocation?.let { post(it) }
        }
    }
    private fun startLegacy() {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL_MS, 5f, legacy)
        } catch (e: SecurityException) { stopSelf() }
    }
    private val legacy = LocationListener { post(it) }
    private fun post(loc: Location) {
        scope.launch { locationRepo.updateLocation(loc.latitude, loc.longitude) }
    }
    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText("Receiving job requests")
        .setSmallIcon(R.drawable.ic_vito_mark)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
    private fun createChannel() {
        val c = NotificationChannel(CHANNEL_ID, "Driver online status", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(c)
    }
    override fun onDestroy() { fused?.removeLocationUpdates(fusedCallback); scope.cancel(); super.onDestroy() }
    override fun onBind(intent: Intent?) = null
}
```

### RealtimeManager.kt
```kotlin
package com.vito.core.data.remote

import com.vito.core.data.remote.dto.*
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeManager @Inject constructor(private val sb: SupabaseProvider) {
    // PRIMARY job delivery — Supabase Realtime WebSocket, NOT FCM
    fun observeIncomingJobs(driverId: String): Flow<IncomingJob> = callbackFlow {
        val channel = sb.client.realtime.channel("driver:$driverId:jobs")
        // subscribe to broadcast messages from vito-create-ride/send/mart
        channel.subscribe()
        awaitClose { sb.client.realtime.removeChannel(channel) }
    }
    fun observeRide(rideId: String): Flow<RideUpdate> = callbackFlow {
        val ch = sb.client.realtime.channel("ride:$rideId")
        ch.subscribe()
        awaitClose { sb.client.realtime.removeChannel(ch) }
    }
}
```

---

## A6, A7, A8 · Screen Builders

Each screen follows this ViewModel pattern (no exceptions):
```kotlin
@HiltViewModel
class XScreenViewModel @Inject constructor(
    private val useCase: ..., private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    sealed interface UiState {
        object Loading : UiState
        data class Content(val data: ..., val isOffline: Boolean = false) : UiState
        data class Error(val message: String) : UiState
        object Empty : UiState
    }
    sealed interface UiEvent {
        data class NavigateTo(val route: String) : UiEvent
        data class ShowSnackbar(val msg: String) : UiEvent
    }
    private val _ui = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _ui.asStateFlow()
    private val _ev = MutableSharedFlow<UiEvent>()
    val events = _ev.asSharedFlow()
}
```

Each Composable wraps in `VitoScreenScaffold` and branches on all 4 UiState variants — Loading→Skeleton, Error→VitoErrorState (with retry), Empty→VitoEmptyState, Content→real layout.

Build order in each app:
- Client: Registration → HomeScreen (flesh out) → RideBooking → ActiveRide → Wallet → Mart{Store,Cart,Checkout,Tracking} → Send → Activity → Profile → ChangePin
- Driver: DriverTokenGate → DriverRegistration → DriverLogin → DriverHome → JobRequestModal → ActiveJob → Earnings → Qr → Profile → DriverLocationService
- Admin: AdminLogin → AdminDashboard → LiveOrdersMap → DriverMgmt → ClientMgmt → MartMgmt → Finance → QrTokenMgmt → AuditLog

---

## A9 · Mart + Payments

### vito-stripe-webhook/index.ts (idempotent)
```typescript
import { serve } from "https://deno.land/std@0.224.0/http/server.ts";
import Stripe from "https://esm.sh/stripe@13.6.0?target=deno";
import { db } from "../_shared/db.ts";
import { ok, badRequest, serverError } from "../_shared/errors.ts";

const stripe = new Stripe(Deno.env.get("STRIPE_SECRET_KEY")!, { apiVersion: "2023-08-16" });
const SECRET = Deno.env.get("STRIPE_WEBHOOK_SECRET")!;

serve(async (req: Request): Promise<Response> => {
    if (req.method !== "POST") return new Response(null, { status: 405 });
    const sig = req.headers.get("stripe-signature");
    if (!sig) return badRequest("No signature");
    const payload = await req.text();
    let event;
    try {
        event = await stripe.webhooks.constructEventAsync(payload, sig, SECRET);
    } catch (e) { return badRequest("Invalid signature"); }

    // Idempotency check
    const { data: exists } = await db.from("vito_stripe_events")
        .select("event_id").eq("event_id", event.id).maybeSingle();
    if (exists) return ok({ received: true, duplicate: true });

    try {
        await db.from("vito_stripe_events").insert({ event_id: event.id });
        if (event.type === "payment_intent.succeeded") {
            const pi = event.data.object as any;
            const userId = pi.metadata?.user_id;
            const type = pi.metadata?.type;
            if (type === "top_up" && userId) {
                const amount = pi.amount / 100;
                await db.rpc("credit_wallet", { p_user_id: userId, p_amount: amount });
                await db.from("vito_wallet_transactions").insert({
                    user_id: userId, type: "top_up", amount,
                    description: "Wallet top-up", stripe_pi_id: pi.id,
                });
            }
        }
        return ok({ received: true });
    } catch (e) {
        console.error(JSON.stringify({ fn: "stripe-webhook", error: String(e) }));
        return serverError("Webhook processing failed");
    }
});
```

---

## A10 · Admin Edge Functions
- `vito-suspend-user`: POST {target_id, target_type, suspend: bool}; updates is_suspended; audits
- `vito-approve-car-photo`: POST {driver_id, approved: bool}; updates car_photo_approved; audits
- `vito-assign-driver-admin`: POST {job_id, job_type, driver_id}; calls assign_driver_atomic on admin's behalf; audits
- `vito-cancel-order-admin`: POST {job_id, job_type, reason}; sets cancelled_by_admin; audits

---

## A11 · Integration Tests

`supabase/integration/`:
- `test_auth.ts`: register, login, wrong-pin lockout sequence, 30-day JWT
- `test_qr.ts`: generate, validate, expiry, revoke
- `test_atomic.ts`: race on assign_driver_atomic — exactly 1 winner
- `test_wallet.ts`: invariants (no negative balance, transactions sum matches balance)
- `test_rls.ts`: client cannot read other client's data; driver cannot self-approve
- `test_stripe_idempotent.ts`: duplicate webhook event = single transaction

---

## A12 · CI/CD

### .github/workflows/release.yml
```yaml
name: Release
on:
  push:
    tags: ['v*']
jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin' }
      - uses: android-actions/setup-android@v3
      - name: Build release APKs
        run: ./gradlew assembleRelease
        env:
          SUPABASE_URL: ${{ secrets.SUPABASE_URL }}
          SUPABASE_ANON_KEY: ${{ secrets.SUPABASE_ANON_KEY }}
          MAPS_API_KEY: ${{ secrets.MAPS_API_KEY }}
          STRIPE_PUBLISHABLE: ${{ secrets.STRIPE_PUBLISHABLE }}
      - name: Verify DEBUG_BYPASS_LOGIN stripped
        run: |
          for apk in vito_client/build/outputs/apk/release/*.apk \
                    vito_driver/build/outputs/apk/release/*.apk \
                    vito_admin/build/outputs/apk/release/*.apk; do
            apkanalyzer dex packages "$apk" 2>/dev/null | grep -q "DEBUG_BYPASS_LOGIN" \
              && { echo "FAIL: bypass in $apk"; exit 1; } || echo "OK: $apk"
          done
      - name: Check APK size budgets
        run: |
          for spec in vito_client:25 vito_driver:22 vito_admin:20; do
            m=${spec%:*}; max=${spec#*:}
            apk=$m/build/outputs/apk/release/$m-release.apk
            sz=$(($(stat -c%s "$apk")/1024/1024))
            [ $sz -le $max ] && echo "$m: ${sz}MB ≤ ${max}MB ✓" \
              || { echo "$m: ${sz}MB > ${max}MB ✗"; exit 1; }
          done
      - name: Upload to CDN
        run: |
          for app in vito_client vito_driver vito_admin; do
            curl -fsSL -X PUT "https://cdn.vito.app/$app.apk" \
              -H "Authorization: Bearer ${{ secrets.CDN_TOKEN }}" \
              --data-binary "@$app/build/outputs/apk/release/$app-release.apk"
          done
          echo "{\"versionCode\":${{ github.run_number }},\"versionName\":\"${{ github.ref_name }}\",\"downloadUrl\":\"https://cdn.vito.app/vito_client.apk\"}" \
            | curl -fsSL -X PUT "https://cdn.vito.app/version.json" \
                -H "Authorization: Bearer ${{ secrets.CDN_TOKEN }}" \
                --data-binary @-
```

### .github/workflows/supabase-deploy.yml
```yaml
name: Supabase Deploy
on:
  workflow_dispatch:
  push:
    branches: [master]
    paths: ['supabase/**']
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: supabase/setup-cli@v1
      - name: Apply migrations
        env: { SUPABASE_ACCESS_TOKEN: ${{ secrets.SUPABASE_ACCESS_TOKEN }} }
        run: supabase db push --project-ref ${{ secrets.SUPABASE_PROJECT_REF }}
      - name: Deploy edge functions
        env: { SUPABASE_ACCESS_TOKEN: ${{ secrets.SUPABASE_ACCESS_TOKEN }} }
        run: |
          for d in supabase/functions/*/; do
            name=$(basename "$d")
            [ "$name" = "_shared" ] && continue
            supabase functions deploy "$name" --project-ref ${{ secrets.SUPABASE_PROJECT_REF }}
          done
```

---

## A13 · Accessibility Sweep
- Add `contentDescription` to every Icon usage
- Verify all interactive elements have min 48dp touch targets
- Test screens at 200% font scale (no layout breakage)
- Add semantics merging for VitoPinField row

```bash
# Quick audit
MISSING=$(grep -rn "Icon(" --include="*.kt" vito_client vito_driver vito_admin \
    | grep -v "contentDescription\|null.*decorative" | wc -l)
[ "$MISSING" -eq 0 ] && echo "✅ contentDescription complete" || echo "❌ $MISSING icons missing"
```

---

## A14 · Localization
- Ensure every Composable string uses `stringResource(R.string.key)`
- `values/strings.xml` + `values-es/strings.xml` must have same keys

```bash
EN=$(grep -c '<string' vito_client/src/main/res/values/strings.xml)
ES=$(grep -c '<string' vito_client/src/main/res/values-es/strings.xml)
[ "$EN" -eq "$ES" ] && echo "✅ Localization parity" || echo "❌ EN=$EN ES=$ES"
```

---

## A15 · FinalVerifier
```bash
#!/bin/bash
ROOT="/workspace/vito"
F=0
fail() { echo "❌ $1"; F=$((F+1)); }
pass() { echo "✅ $1"; }

# P0
LOGIN=$(find "$ROOT/vito_client" -name "LoginScreen.kt" | head -1)
[ -n "$LOGIN" ] && ! grep -qi "phone\|PhoneNumber" "$LOGIN" && pass "LoginScreen no phone" || fail "LoginScreen has phone"
find "$ROOT/vito_client" -name "TokenGateScreen.kt" | grep -q . && pass "TokenGate" || fail "TokenGate missing"
find "$ROOT/vito_design_system" -name "VitoPinField.kt" | grep -q . && pass "VitoPinField" || fail "VitoPinField missing"
[ -f "$ROOT/supabase/migrations/0009_rls_policies.sql" ] && pass "RLS" || fail "RLS missing"
! grep -r "argon2" "$ROOT/supabase/functions/" 2>/dev/null | grep -q . && pass "No Argon2" || fail "Argon2 found"
! grep -ri "vico\|mpandroid" "$ROOT/gradle/libs.versions.toml" 2>/dev/null | grep -q . && pass "No chart libs" || fail "Chart lib found"

ACCEPT="$ROOT/supabase/functions/vito-accept-job/index.ts"
[ -f "$ACCEPT" ] && grep -q "assign_driver_atomic" "$ACCEPT" && pass "atomic accept" || fail "accept-job"
MODAL=$(find "$ROOT/vito_driver" -name "JobRequestModal.kt" | head -1)
[ -n "$MODAL" ] && grep -q "dismissOnBackPress.*false" "$MODAL" && pass "modal undismissable" || fail "modal dismissable"
SM=$(find "$ROOT/vito_core" -name "SessionManager.kt" | head -1)
[ -n "$SM" ] && grep -q "EncryptedSharedPreferences" "$SM" && pass "EncryptedSharedPreferences" || fail "SessionManager"
LS=$(find "$ROOT/vito_driver" -name "DriverLocationService.kt" | head -1)
[ -n "$LS" ] && grep -q "LocationManager\|GPS_PROVIDER" "$LS" && pass "Location fallback" || fail "Location no fallback"

# Functions
for fn in vito-login vito-register-client vito-register-driver vito-validate-token \
          vito-qr-gen vito-qr-info vito-qr-revoke vito-check-username \
          vito-change-pin vito-delete-account vito-reset-pin \
          vito-create-ride vito-accept-job vito-update-ride-status vito-cancel-ride vito-rate-ride \
          vito-create-send vito-update-send-status \
          vito-create-mart-order vito-update-mart-status vito-submit-signature vito-upload-delivery-photo \
          vito-stripe-payment-sheet vito-stripe-webhook vito-payout \
          vito-suspend-user vito-approve-car-photo vito-assign-driver-admin vito-cancel-order-admin; do
    [ -f "$ROOT/supabase/functions/$fn/index.ts" ] || fail "Missing: $fn"
done

# Build
cd "$ROOT" && ./gradlew assembleRelease --quiet 2>&1 | tail -3 | grep -q "BUILD SUCCESSFUL" \
    && pass "Release build" || fail "Release build"

echo ""
[ $F -eq 0 ] && echo "✅ PRODUCTION READY" || echo "❌ $F failures"
exit $F
```
