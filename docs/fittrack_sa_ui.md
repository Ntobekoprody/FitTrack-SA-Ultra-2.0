# FitTrack SA Interface Flows

## Color & Theme
- Theme Toggle → Switch between black primary buttons and lime-on-black dark mode; Data: update theme preference (RoomDB update, sync to cloud profile later); Offline: change persists locally, syncs when online; Toasts: success "Theme updated", error "Theme update failed".

## Login & Registration
- Email Login → Navigate to Dashboard; Data: authenticate credentials (remote read, cache token in RoomDB); Offline: requires connection, cached token enables offline reopen; Toasts: success "Signed in", error "Check email or password".
- Google SSO → Navigate to Dashboard; Data: authenticate via Google (remote read, store profile in RoomDB); Offline: requires connection, cached profile reused offline; Toasts: success "Google sign-in complete", error "Google sign-in failed".
- Biometric Unlock → Navigate to Dashboard; Data: unlock session (RoomDB read of encrypted token); Offline: fully local using cached token; Toasts: success "Unlocked", error "Biometric mismatch".
- Register → Navigate to Dashboard; Data: create account (remote create, profile snapshot in RoomDB); Offline: queues registration until online; Toasts: success "Account created", error "Registration failed".
- Forgot Password → Navigate to Password Reset screen; Data: request reset email (remote create request); Offline: queues request, sends when online; Toasts: success "Reset link sent", error "Unable to send reset".

## Dashboard
- Log Activity → Navigate to Activity Logging screen; Data: no immediate data change; Offline: navigation only; Toasts: none.
- Log Meal → Navigate to Nutrition Log screen; Data: no immediate data change; Offline: navigation only; Toasts: none.
- View Streaks/Achievements → Navigate to Achievements screen; Data: read streak data (RoomDB read with optional sync refresh); Offline: shows cached RoomDB data, syncs when online; Toasts: success "Streaks refreshed" on sync, error "Refresh failed".
- Refresh/Sync → Stay on Dashboard; Data: pull latest summaries (remote read, update RoomDB); Offline: queues sync attempt; Toasts: success "Dashboard synced", error "Sync unavailable offline".

## Activity Logging
- Select Type → Open activity type picker modal; Data: read RoomDB activity templates; Offline: fully local; Toasts: none.
- Set Duration → Open duration picker; Data: update in-memory entry; Offline: fully local; Toasts: none.
- Save Activity → Return to Dashboard; Data: create activity entry (RoomDB create, queue remote sync); Offline: saves locally and syncs later; Toasts: success "Activity saved", error "Save failed".
- Cancel → Navigate back to Dashboard; Data: discard draft (no RoomDB write); Offline: immediate; Toasts: none.

## Nutrition Log
- Add Food → Open food search modal; Data: read from RoomDB cached library with remote refresh when online; Offline: uses cached data, sync fetch queued; Toasts: success "Food list updated" on sync, error "Food lookup failed".
- Use Shortcut → Apply saved meal template; Data: read template (RoomDB read); Offline: fully local; Toasts: success "Shortcut applied", error "Shortcut unavailable".
- Save Meal → Return to Dashboard; Data: create meal entry (RoomDB create, queue remote sync); Offline: saves locally and syncs later; Toasts: success "Meal saved", error "Save failed".
- Edit/Delete Entry → Stay on Nutrition Log; Data: update/delete meal (RoomDB update/delete, queue remote sync); Offline: applies locally and syncs later; Toasts: success "Meal updated" or "Meal deleted", error "Update failed".

## Progress & Reports
- Weekly/Monthly Toggle → Switch chart view; Data: read aggregated stats (RoomDB read, trigger remote refresh when online); Offline: shows cached stats; Toasts: success "Stats refreshed" on sync, error "Refresh failed".
- Filter Range → Open date filter modal; Data: query RoomDB for range stats; Offline: fully local; Toasts: none.
- View Details → Navigate to Detailed Report screen; Data: read detailed metrics (RoomDB read, optional remote sync); Offline: shows cached report, sync later; Toasts: success "Details updated", error "Unable to update".

## Achievements
- View Badge → Open badge detail modal; Data: read badge info (RoomDB read, sync remote progress when online); Offline: shows cached badge; Toasts: success "Badge synced", error "Sync failed".
- Join Challenge → Navigate to Challenge Enrollment screen; Data: create enrollment (RoomDB create, queue remote sync); Offline: enrolls locally, syncs later; Toasts: success "Challenge joined", error "Join failed".
- See Streak Details → Navigate to Streak Detail screen; Data: read streak history (RoomDB read, optional remote sync); Offline: shows cached history; Toasts: success "Streak updated", error "Update failed".

## Settings
- Language Toggle (EN/ZU) → Stay on Settings, refresh UI text; Data: update preference (RoomDB update, sync to profile later); Offline: persists locally, sync later; Toasts: success "Language set to English" or "Language set to isiZulu", error "Language change failed".
- Notifications On/Off → Stay on Settings; Data: update notification preference (RoomDB update, sync to server later); Offline: persists locally, sync later; Toasts: success "Notification settings saved", error "Notification update failed".
- Manage Data → Navigate to Data Management screen; Data: read storage usage (RoomDB read, optional remote sync for cloud usage); Offline: shows local data, sync later; Toasts: success "Data synced" on refresh, error "Sync failed".
- Sign Out → Navigate to Login screen; Data: clear session (RoomDB delete token, remote revoke queued); Offline: clears local token immediately, remote revoke syncs later; Toasts: success "Signed out", error "Sign out failed".
