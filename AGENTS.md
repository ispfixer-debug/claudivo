# VITO Skills V3 — OpenHands Microagent Memory
### Save ALL FOUR sections to .openhands/microagents/ for persistent context

---

## SECTION 1: repo.md — Project Context (refresh each iteration)

```markdown
# Vito Platform — OpenHands Context

## Repo
https://github.com/ispfixer-debug/clauvito
Local: /workspace/vito (cloned)

## What BUILDS (v0.0.1 release has working APKs)
- vito_client-debug.apk  23MB ← compiles and runs
- vito_driver-debug.apk  22MB ← compiles and runs  
- vito_admin-debug.apk   22MB ← compiles and runs

## ALREADY CORRECT — don't recreate, don't break
Infrastructure: settings.gradle.kts, build.gradle.kts, libs.versions.toml, gradle.properties
Migrations: 0001-0008 (extensions, users, jobs, mart, wallet, qr, notifications, triggers)
Design System: VitoColors, VitoTypography, VitoSpacing, VitoShapes, VitoTheme, VitoButton, VitoCard, VitoSkeleton
Domain Models: User.kt, Job.kt, Wallet.kt, Misc.kt, Repositories.kt
App entry points: VitoClientApp.kt, VitoDriverApp.kt, VitoAdminApp.kt
Screens: SplashScreen.kt, HomeScreen.kt (needs fleshing), NavGraph.kt

## WRONG — fix immediately (P0)
LoginScreen.kt: Has phone field. MUST be username TextField + VitoPinField 6 digits.

## MISSING CRITICAL (P0/P1)
- TokenGateScreen.kt (mandatory QR gate)
- VitoPinField.kt (6-circle PIN widget)
- _shared/bcrypt.ts (bcrypt cost 12)
- vito-login/index.ts (username+PIN auth)
- 0009_rls_policies.sql (data protection)
- SessionManager.kt (EncryptedSharedPreferences)
- All 27 other edge functions
- All driver screens (9)
- All admin screens (9)
- 12 client screens

## ABSOLUTE CONSTRAINTS
1. Auth: username + 6-digit PIN. NO phone, NO OTP, NO email.
2. Distribution: side-loaded APK via QR code. NO Play Store.
3. Charts: Compose Canvas DrawScope. NO Vico, NO MPAndroidChart.
4. Realtime: Supabase WebSocket primary. FCM is optional secondary.
5. PIN hashing: bcrypt cost 12. NEVER Argon2, NEVER SHA-256.
6. JWT storage: EncryptedSharedPreferences. NEVER DataStore.
7. Job accept: assign_driver_atomic() RPC. NEVER plain UPDATE.
8. JobRequestModal: undismissable. dismissOnBackPress=false + dismissOnClickOutside=false.
9. LocationService: foreground + android.location fallback (no GMS dependency).
10. Notification text: EXACTLY "Vito — You are online" (driver location service).
```

---

## SECTION 2: qa_patterns.md — Fix Patterns

```markdown
# Vito QA Fix Patterns

## Pattern: Phone→Username (most common P0)
**Symptom:** LoginScreen.kt has phone TextField or parsePhoneNumber import
**Fix:**
  1. Delete phone TextField and all phone-related imports
  2. Add VitoTextField(label="Username", leadingIcon=VitoIcons.Profile)
  3. Add VitoPinField(length=6, isObscured=true) below username
  4. Update LoginViewModel to use `username: String` not `phone: String`
  5. Update vito-login edge function to query by username column
**Prevention:** scripts/inspect.sh grep "phone" on all auth files

## Pattern: Missing EncryptedSharedPreferences
**Symptom:** SessionManager uses DataStore or SharedPreferences directly
**Fix:** Replace with EncryptedSharedPreferences using MasterKey.KeyScheme.AES256_GCM
**Prevention:** inspect.sh checks for "DataStore" in SessionManager.kt

## Pattern: Argon2 instead of bcrypt
**Symptom:** _shared/bcrypt.ts imports argon2 library
**Fix:** Replace with `import * as bcrypt from "https://deno.land/x/bcrypt@v0.4.1/mod.ts"`
         Use bcrypt.genSalt(12) and bcrypt.hash() / bcrypt.compare()
**Prevention:** grep -r "argon2" supabase/functions/ must return 0 results

## Pattern: Chart library dependency
**Symptom:** libs.versions.toml or build.gradle.kts contains vico, mpandroidchart, aay
**Fix:** Remove dependency; implement chart in VitoBarChart.kt using DrawScope only
**Prevention:** grep check in inspect.sh

## Pattern: Dismissable JobRequestModal
**Symptom:** Dialog() without dismissOnBackPress=false
**Fix:** Add DialogProperties(dismissOnBackPress=false, dismissOnClickOutside=false)
         Also add BackHandler(enabled=true) { /* consume */ }
**Prevention:** inspect.sh checks MODAL file for dismissOnBackPress

## Pattern: Plain UPDATE for job acceptance
**Symptom:** vito-accept-job does .update({status:'assigned'}) directly
**Fix:** Use db.rpc("assign_driver_atomic", {p_job_id, p_driver_id})
         Check rowsAffected: 0 = taken (silent dismiss), 1 = success
**Prevention:** grep assign_driver_atomic in accept-job file

## Pattern: Missing LoadingState in ViewModel
**Symptom:** ViewModel UiState sealed class has Content and Error but no Loading
**Fix:** Add `object Loading : UiState` as the initial state value
**Prevention:** deep_verify.py checks for "Loading" in every ViewModel

## Pattern: Raw hex color in Kotlin
**Symptom:** `Color(0xFF1AE694)` appearing outside VitoColors.kt
**Fix:** Replace with `VitoTheme.colorScheme.primary` or the appropriate token
**Prevention:** deep_verify.py pattern: Color\(0xFF[A-Fa-f0-9]{6}\)
```

---

## SECTION 3: vito_skills.md — Implementation Knowledge

### How to wire a new screen end-to-end

```kotlin
// Step 1: Create Route in NavGraph
sealed class Route(val path: String) {
    object MyNew : Route("my_new")
    object MyNewWithArgs : Route("detail/{id}") {
        fun withId(id: String) = "detail/$id"
    }
}

// Step 2: Add to NavHost
composable(Route.MyNew.path) {
    MyNewScreen(
        onNavigateBack = { navController.popBackStack() },
        onNavigateTo = { navController.navigate(it) },
    )
}

// Step 3: Create Screen (collects StateFlow, handles UiEvent)
@Composable
fun MyNewScreen(
    onNavigateBack: () -> Unit,
    onNavigateTo: (String) -> Unit,
    viewModel: MyNewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MyNewViewModel.UiEvent.Navigate -> onNavigateTo(event.route)
                is MyNewViewModel.UiEvent.Snackbar -> { /* show snackbar */ }
            }
        }
    }

    VitoScreenScaffold(
        topBar = { VitoTopAppBar(title = "My Screen", onBack = onNavigateBack) },
        isOnline = isOnline,
    ) { padding ->
        when (val state = uiState) {
            is MyNewViewModel.UiState.Loading -> MyNewSkeleton(Modifier.padding(padding))
            is MyNewViewModel.UiState.Error   -> VitoErrorState(state.message, viewModel::onRetry, Modifier.padding(padding))
            is MyNewViewModel.UiState.Empty   -> VitoEmptyState("Nothing yet", Modifier.padding(padding))
            is MyNewViewModel.UiState.Content -> MyNewContent(state, viewModel::onItemTap, Modifier.padding(padding))
        }
    }
}

// Step 4: Create ViewModel with full state machine
@HiltViewModel
class MyNewViewModel @Inject constructor(
    private val useCase: MyNewUseCase,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    sealed interface UiState {
        object Loading : UiState
        data class Content(val items: List<Item>) : UiState
        object Empty : UiState
        data class Error(val message: String) : UiState
    }
    sealed interface UiEvent {
        data class Navigate(val route: String) : UiEvent
        data class Snackbar(val message: String) : UiEvent
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    val isOnline = networkMonitor.isOnline.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    init { load() }
    private fun load() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        useCase().fold(
            onSuccess = { _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Content(it) },
            onFailure = { _uiState.value = UiState.Error(it.message ?: "Error") },
        )
    }
    fun onRetry() = load()
    fun onItemTap(item: Item) = viewModelScope.launch {
        _uiEvent.emit(UiEvent.Navigate(Route.Detail.withId(item.id)))
    }
}
```

### How to call a Supabase Edge Function from Kotlin

```kotlin
// In a Repository implementation
suspend fun login(username: String, pin: String): Result<LoginResponse> = runCatching {
    val response = supabase.functions.invoke(
        function = "vito-login",
        body = buildJsonObject {
            put("username", username)
            put("pin", pin)
            put("role", "client")
        },
    )
    response.body<LoginResponse>()
}
```

### How to write a complete edge function

```typescript
// Template for any edge function
import { serve } from "https://deno.land/std@0.224.0/http/server.ts";
import { requireAuth } from "../_shared/auth.ts";  // exists already
import { db } from "../_shared/db.ts";             // exists already
import { audit } from "../_shared/audit.ts";       // create if missing
import { ok, badRequest, serverError } from "../_shared/errors.ts"; // create if missing

serve(async (req: Request): Promise<Response> => {
  if (req.method !== "POST") return new Response(null, { status: 405 });
  
  // 1. Auth (skip for public endpoints)
  let ctx;
  try { ctx = await requireAuth(req, ["client"]); }
  catch (e) { return e as Response; }
  
  // 2. Parse + validate body
  let body: { required_field: string };
  try { body = await req.json(); } catch { return badRequest("Invalid JSON"); }
  if (!body.required_field) return badRequest("required_field is required");
  
  // 3. Business logic
  try {
    const { data, error } = await db.from("vito_table")
      .insert({ field: body.required_field, actor_id: ctx.sub })
      .select().single();
    if (error) throw error;
    
    // 4. Audit (required on all mutations)
    await audit({ action: "thing_done", actorId: ctx.sub, actorType: "client",
                  targetId: data.id, targetType: "vito_table" });
    
    return ok(data, 201);
  } catch (e) {
    console.error(JSON.stringify({ fn: "vito-xxx", error: String(e) }));
    return serverError("Operation failed");
  }
});
```

### Full dark map style JSON (complete)

```json
[
  {"featureType":"all","elementType":"geometry","stylers":[{"color":"#0F1923"}]},
  {"featureType":"all","elementType":"labels.text.stroke","stylers":[{"color":"#0F1923"},{"lightness":-80}]},
  {"featureType":"all","elementType":"labels.text.fill","stylers":[{"color":"#6A8099"}]},
  {"featureType":"administrative","elementType":"geometry.stroke","stylers":[{"color":"#1E2C3C"},{"weight":0.5}]},
  {"featureType":"landscape","elementType":"geometry","stylers":[{"color":"#0F1923"}]},
  {"featureType":"landscape.natural","elementType":"geometry","stylers":[{"color":"#0E1C15"}]},
  {"featureType":"poi","elementType":"all","stylers":[{"visibility":"off"}]},
  {"featureType":"road","elementType":"geometry","stylers":[{"color":"#1A2535"},{"lightness":5}]},
  {"featureType":"road.local","elementType":"geometry","stylers":[{"color":"#1E2C3C"}]},
  {"featureType":"road.arterial","elementType":"geometry","stylers":[{"color":"#253444"}]},
  {"featureType":"road.highway","elementType":"geometry","stylers":[{"color":"#2C3E52"}]},
  {"featureType":"road.highway","elementType":"geometry.stroke","stylers":[{"color":"#1A2535"},{"weight":0.5}]},
  {"featureType":"transit","elementType":"all","stylers":[{"visibility":"off"}]},
  {"featureType":"water","elementType":"geometry","stylers":[{"color":"#081218"}]},
  {"featureType":"water","elementType":"labels.text.fill","stylers":[{"color":"#3B5364"}]}
]
```
Save to: `vito_design_system/src/main/res/raw/map_style_vito.json`

### DriverLocationService - complete notification setup

```kotlin
// MUST create notification channel before startForeground
private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Driver Status",
            NotificationManager.IMPORTANCE_LOW,  // LOW: no sound, no pop-up
        ).apply {
            description = "Shows when you are online and receiving job requests"
            setShowBadge(false)
        }
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }
}

private fun buildNotification(): Notification =
    NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Vito — You are online")  // EXACT string
        .setContentText("Receiving job requests")
        .setSmallIcon(R.drawable.ic_vito_mark)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .build()
```

### assign_driver_atomic SQL functions (all three job types)

```sql
-- Rides (likely already in 0003_jobs.sql, verify)
CREATE OR REPLACE FUNCTION assign_driver_atomic(p_job_id UUID, p_driver_id UUID)
RETURNS INTEGER AS $$
DECLARE v_count INTEGER;
BEGIN
  UPDATE vito_rides SET driver_id=p_driver_id, status='assigned', assigned_at=NOW()
  WHERE id=p_job_id AND status='searching' AND driver_id IS NULL;
  GET DIAGNOSTICS v_count = ROW_COUNT;
  RETURN v_count;
END;
$$ LANGUAGE plpgsql;

-- Sends
CREATE OR REPLACE FUNCTION assign_send_driver_atomic(p_job_id UUID, p_driver_id UUID)
RETURNS INTEGER AS $$
DECLARE v_count INTEGER;
BEGIN
  UPDATE vito_sends SET driver_id=p_driver_id, status='assigned'
  WHERE id=p_job_id AND status='searching' AND driver_id IS NULL;
  GET DIAGNOSTICS v_count = ROW_COUNT;
  RETURN v_count;
END;
$$ LANGUAGE plpgsql;

-- Mart orders
CREATE OR REPLACE FUNCTION assign_mart_driver_atomic(p_job_id UUID, p_driver_id UUID)
RETURNS INTEGER AS $$
DECLARE v_count INTEGER;
BEGIN
  UPDATE vito_mart_orders SET driver_id=p_driver_id, status='dispatched'
  WHERE id=p_job_id AND status='ready' AND driver_id IS NULL;
  GET DIAGNOSTICS v_count = ROW_COUNT;
  RETURN v_count;
END;
$$ LANGUAGE plpgsql;

-- Wallet deduction (safe with FOR UPDATE lock)
CREATE OR REPLACE FUNCTION deduct_wallet(p_user_id UUID, p_amount NUMERIC)
RETURNS BOOLEAN AS $$
DECLARE v_balance NUMERIC;
BEGIN
  SELECT wallet_balance INTO v_balance FROM vito_users WHERE id=p_user_id FOR UPDATE;
  IF v_balance < p_amount THEN RETURN FALSE; END IF;
  UPDATE vito_users SET wallet_balance=wallet_balance-p_amount WHERE id=p_user_id;
  RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
```

---

## SECTION 4: gap_registry.md — Pre-seeded gaps

```markdown
# Vito Gap Registry
| ID | Domain | Description | Severity | Status | Fixed in |
|---|---|---|---|---|---|
| G001 | Auth | LoginScreen.kt has phone field — replace with username+VitoPinField | P0 | OPEN | — |
| G002 | Auth | TokenGateScreen.kt missing | P0 | OPEN | — |
| G003 | Design | VitoPinField.kt missing | P0 | OPEN | — |
| G004 | Backend | _shared/bcrypt.ts missing | P0 | OPEN | — |
| G005 | Backend | vito-login edge function missing | P0 | OPEN | — |
| G006 | Backend | vito-register-client edge function missing | P0 | OPEN | — |
| G007 | Backend | 0009_rls_policies.sql missing — all tables open | P0 | OPEN | — |
| G008 | Core | SessionManager.kt missing | P0 | OPEN | — |
| G009 | Core | SupabaseProvider.kt missing | P1 | OPEN | — |
| G010 | Core | NetworkMonitor.kt missing | P1 | OPEN | — |
| G011 | Core | FareCalculator.kt missing | P1 | OPEN | — |
| G012 | Core | RealtimeManager.kt missing | P1 | OPEN | — |
| G013 | Core | All 6 Repository implementations missing | P1 | OPEN | — |
| G014 | Backend | vito-validate-token edge function missing | P0 | OPEN | — |
| G015 | Backend | vito-qr-gen edge function missing | P1 | OPEN | — |
| G016 | Backend | vito-create-ride edge function missing | P0 | OPEN | — |
| G017 | Backend | vito-accept-job edge function missing | P0 | OPEN | — |
| G018 | Backend | vito-stripe-webhook missing | P1 | OPEN | — |
| G019 | Backend | 20 other edge functions missing | P1 | OPEN | — |
| G020 | Design | VitoJobRequestModal.kt missing | P0 | OPEN | — |
| G021 | Design | VitoBarChart.kt missing (must be Canvas) | P1 | OPEN | — |
| G022 | Design | 17 other design components missing | P1 | OPEN | — |
| G023 | Driver | DriverLocationService.kt missing | P1 | OPEN | — |
| G024 | Driver | 9 driver screens missing | P1 | OPEN | — |
| G025 | Client | 12 client screens missing | P1 | OPEN | — |
| G026 | Admin | 9 admin screens missing | P2 | OPEN | — |
| G027 | UX | HomeScreen is a scaffold — no real data | P2 | OPEN | — |
| G028 | UX | No ES translations (values-es/) | P3 | OPEN | — |
| G029 | CI/CD | release.yml missing | P2 | OPEN | — |
| G030 | CI/CD | supabase-deploy.yml missing | P2 | OPEN | — |
```
