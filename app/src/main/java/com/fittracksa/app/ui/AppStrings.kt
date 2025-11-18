package com.fittracksa.app.ui

import com.fittracksa.app.data.preferences.UserSettings

/**
 * FULL STRING DEFINITIONS FOR THE ENTIRE APP
 * Covers every screen, every label, every button.
 */
data class AppStrings(

    // ------------------------- AUTH -------------------------
    val loginTitle: String,
    val email: String,
    val password: String,
    val name: String,
    val emailLogin: String,
    val googleSso: String,
    val biometric: String,
    val biometricSubtitle: String,
    val biometricUnavailable: String,
    val biometricFailed: String,
    val register: String,
    val registerSuccess: String,
    val loginSuccess: String,
    val backToLogin: String,
    val forgotPassword: String,

    // ------------------------- MAIN NAV -------------------------
    val dashboard: String,
    val activity: String,
    val nutrition: String,
    val progress: String,
    val settings: String,
    val achievements: String,

    // ------------------------- DASHBOARD -------------------------
    val logActivity: String,
    val logMeal: String,
    val quickActions: String,
    val recentAchievements: String,
    val noAchievementsYet: String,
    val viewStreaks: String,
    val refresh: String,
    val dayStreak: String,
    val minActive: String,
    val calories: String,

    // ------------------------- ACTIVITY SCREEN -------------------------
    val activityTitle: String,
    val activitySubtitle: String,
    val selectType: String,
    val setDuration: String,
    val saveActivity: String,
    val activitySaveNote: String,
    val recentActivities: String,
    val noActivitiesYet: String,

    // ------------------------- NUTRITION SCREEN -------------------------
    val todaysIntake: String,
    val dailyGoal: String,
    val trackNutrition: String,
    val foodName: String,
    val caloriesInput: String,
    val saveMeal: String,
    val addFood: String,
    val useShortcut: String,
    val recentMeals: String,
    val noMealsYet: String,
    val breakfast: String,
    val lunch: String,

    // ------------------------- ACHIEVEMENTS SCREEN -------------------------
    val joinChallenge: String,
    val seeStreakDetails: String,
    val viewBadge: String,

    // ------------------------- SETTINGS -------------------------
    val profileSectionTitle: String,
    val profileNameLabel: String,
    val profileEmailPlaceholder: String,
    val changePhoto: String,
    val removePhoto: String,
    val saveProfile: String,
    val languageToggle: String,
    val notifications: String,
    val manageData: String,
    val darkMode: String,
    val signOut: String,

    // ------------------------- GENERIC -------------------------
    val cancel: String
)


// ===================================================================
//                        ENGLISH STRINGS
// ===================================================================

private val English = AppStrings(
    loginTitle = "Welcome Back",
    email = "Email",
    password = "Password",
    name = "Full Name",
    emailLogin = "Login with Email",
    googleSso = "Login with Google",
    biometric = "Biometric Login",
    biometricSubtitle = "Use fingerprint or face ID to continue",
    biometricUnavailable = "Biometrics unavailable",
    biometricFailed = "Biometric authentication failed",
    register = "Register",
    registerSuccess = "Registration successful",
    loginSuccess = "Login successful",
    backToLogin = "Back to login",
    forgotPassword = "Forgot password?",

    dashboard = "Dashboard",
    activity = "Activity",
    nutrition = "Nutrition",
    progress = "Progress",
    settings = "Settings",
    achievements = "Achievements",

    logActivity = "Log Activity",
    logMeal = "Log Meal",
    quickActions = "Quick Actions",
    recentAchievements = "Recent Achievements",
    noAchievementsYet = "Keep logging to unlock badges.",
    viewStreaks = "View Streaks / Achievements",
    refresh = "Refresh",
    dayStreak = "Day Streak",
    minActive = "Active (min)",
    calories = "Calories",

    activityTitle = "Activity",
    activitySubtitle = "Track your workouts",
    selectType = "Select Type",
    setDuration = "Duration (minutes)",
    saveActivity = "Save Activity",
    activitySaveNote = "Saved locally • Syncs when online",
    recentActivities = "Recent Activities",
    noActivitiesYet = "No activities yet. Log your first workout!",

    todaysIntake = "Today's Intake",
    dailyGoal = "Goal 2000 cal",
    trackNutrition = "Track your nutrition",
    foodName = "Food name",
    caloriesInput = "Calories",
    saveMeal = "Save Meal",
    addFood = "Add Food",
    useShortcut = "Use Shortcut",
    recentMeals = "Recent Meals",
    noMealsYet = "No meals logged yet",
    breakfast = "Breakfast",
    lunch = "Lunch",

    joinChallenge = "Join Challenge",
    seeStreakDetails = "See Streak Details",
    viewBadge = "View Badge",

    profileSectionTitle = "Profile",
    profileNameLabel = "Display Name",
    profileEmailPlaceholder = "your_email@example.com",
    changePhoto = "Change Photo",
    removePhoto = "Remove Photo",
    saveProfile = "Save Profile",
    languageToggle = "Language",
    notifications = "Notifications",
    manageData = "Manage Data",
    darkMode = "Dark Mode",
    signOut = "Sign Out",

    cancel = "Cancel"
)


// ===================================================================
//                        ISIZULU STRINGS
// ===================================================================

private val IsiZulu = AppStrings(
    loginTitle = "Siyakwamukela",
    email = "I-imeyili",
    password = "Iphasiwedi",
    name = "Igama",
    emailLogin = "Ngena nge-imeyili",
    googleSso = "Ngena nge-Google",
    biometric = "Biometric Login",
    biometricSubtitle = "Sebenzisa iminwe noma ubuso ukuqhubeka",
    biometricUnavailable = "Ama-biometric awatholakali",
    biometricFailed = "Ukuqinisekisa kwe-biometric kwehlulekile",
    register = "Bhalisa",
    registerSuccess = "Ukubhalisa kuphumelele",
    loginSuccess = "Ungene ngempumelelo",
    backToLogin = "Buyela emuva",
    forgotPassword = "Ukhohliwe iphasiwedi?",

    dashboard = "Ideshibhodi",
    activity = "Umsebenzi",
    nutrition = "Ukudla",
    progress = "Inqubekela phambili",
    settings = "Izilungiselelo",
    achievements = "Izimpumelelo",

    logActivity = "Qopha Umsebenzi",
    logMeal = "Qopha Ukudla",
    quickActions = "Izinyathelo Ezisheshayo",
    recentAchievements = "Izimpumelelo Zakamuva",
    noAchievementsYet = "Qhubeka uqopha ukuze uthole amabhaji.",
    viewStreaks = "Buka ama-Streak / Izimpumelelo",
    refresh = "Vuselela",
    dayStreak = "I-Streak Yezinsuku",
    minActive = "Imizuzu Emisebenzini",
    calories = "Amakhalori",

    activityTitle = "Umsebenzi",
    activitySubtitle = "Landela ukusebenza kwakho",
    selectType = "Khetha Uhlobo",
    setDuration = "Isikhathi (imizuzu)",
    saveActivity = "Gcina Umsebenzi",
    activitySaveNote = "Kugcinwe kudivayisi • Kuzovumelaniswa ku-inthanethi",
    recentActivities = "Imisebenzi Yakamuva",
    noActivitiesYet = "Awekho umsebenzi okwamanje. Qopha owokuqala!",

    todaysIntake = "Ukudla Kwakho Namuhla",
    dailyGoal = "Umngcele: 2000 cal",
    trackNutrition = "Landela ukudla kwakho",
    foodName = "Igama lokudla",
    caloriesInput = "Amakhalori",
    saveMeal = "Gcina Ukudla",
    addFood = "Nezela Ukudla",
    useShortcut = "Sebenzisa i-Shortcut",
    recentMeals = "Ukudla Kwakamuva",
    noMealsYet = "Awukalubhalisi ukudla okwamanje",
    breakfast = "Isidlo sasekuseni",
    lunch = "Isidlo sasemini",

    joinChallenge = "Joyina Inselelo",
    seeStreakDetails = "Buka Ama-Streak",
    viewBadge = "Buka I-Badge",

    profileSectionTitle = "Iphrofayela",
    profileNameLabel = "Igama Olibonisayo",
    profileEmailPlaceholder = "i-imeyili yakho",
    changePhoto = "Shintsha Isithombe",
    removePhoto = "Susa Isithombe",
    saveProfile = "Gcina Iphrofayela",
    languageToggle = "Ulimi",
    notifications = "Izaziso",
    manageData = "Phatha Idatha",
    darkMode = "Imodi Emnyama",
    signOut = "Phuma",

    cancel = "Khansela"
)


// ===================================================================
// PUBLIC ACCESSOR
// ===================================================================

fun stringsFor(language: UserSettings.Language): AppStrings =
    when (language) {
        UserSettings.Language.ENGLISH -> English
        UserSettings.Language.ISIZULU -> IsiZulu
    }
