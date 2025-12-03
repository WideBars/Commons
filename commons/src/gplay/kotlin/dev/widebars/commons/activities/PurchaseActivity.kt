package dev.widebars.commons.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import dev.widebars.commons.R
import dev.widebars.commons.databinding.ActivityPurchaseBinding
import dev.widebars.commons.dialogs.BottomSheetChooserDialog
import dev.widebars.commons.dialogs.ConfirmationDialog
import dev.widebars.commons.extensions.*
import dev.widebars.commons.helpers.*
import dev.widebars.commons.models.MyTheme
import dev.widebars.commons.models.SimpleListItem
import dev.widebars.strings.R as stringsR

class PurchaseActivity : BaseSimpleActivity() {

    private var firstVersionClickTS = 0L
    private var clicksSinceFirstClick = 0

    companion object {
        private const val EASTER_EGG_TIME_LIMIT = 8000L
        private const val EASTER_EGG_REQUIRED_CLICKS = 7
        private const val EASTER_EGG_REQUIRED_CLICKS_NEXT = 10
        private const val THEME_LIGHT = 0
        private const val THEME_DARK = 1
        private const val THEME_BLACK = 2
        private const val THEME_GRAY = 3
    }

    private var appName = ""
    private var primaryColor = 0
    private var surfaceColor = 0
    private var productIdList: ArrayList<String> = ArrayList()
    private var productIdListRu: ArrayList<String> = ArrayList()
    private var subscriptionIdList: ArrayList<String> = ArrayList()
    private var subscriptionIdListRu: ArrayList<String> = ArrayList()
    private var subscriptionYearIdList: ArrayList<String> = ArrayList()
    private var subscriptionYearIdListRu: ArrayList<String> = ArrayList()
    private var showLifebuoy = true
    private var showCollection = false
    private val predefinedThemes = LinkedHashMap<Int, MyTheme>()

    private val purchaseHelper = PlayStoreHelper(this)

    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun getRepositoryName() = null

    private val binding by viewBinding(ActivityPurchaseBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        appName = intent.getStringExtra(APP_NAME) ?: ""
        productIdList = intent.getStringArrayListExtra(PRODUCT_ID_LIST) ?: arrayListOf("", "", "")
        productIdListRu = intent.getStringArrayListExtra(PRODUCT_ID_LIST_RU) ?: arrayListOf("", "", "")
        subscriptionIdList = intent.getStringArrayListExtra(SUBSCRIPTION_ID_LIST) ?: arrayListOf("", "", "")
        subscriptionIdListRu = intent.getStringArrayListExtra(SUBSCRIPTION_ID_LIST_RU) ?: arrayListOf("", "", "")
        subscriptionYearIdList = intent.getStringArrayListExtra(SUBSCRIPTION_YEAR_ID_LIST) ?: arrayListOf("", "", "")
        subscriptionYearIdListRu = intent.getStringArrayListExtra(SUBSCRIPTION_YEAR_ID_LIST_RU) ?: arrayListOf("", "", "")
        primaryColor = getProperPrimaryColor()
        surfaceColor = getSurfaceColor()
        showLifebuoy = intent.getBooleanExtra(SHOW_LIFEBUOY, true)
        showCollection = intent.getBooleanExtra(SHOW_COLLECTION, false)

        purchaseHelper.initBillingClient()
        val subscriptionIdListAll: ArrayList<String> = subscriptionIdList
        subscriptionIdListAll.addAll(subscriptionYearIdList)
        purchaseHelper.retrieveDonation(productIdList, subscriptionIdListAll)

        purchaseHelper.iapSkuDetailsInitialized.observe(this) {
            if (it) setupButtonIapPurchased()
        }

        purchaseHelper.subSkuDetailsInitialized.observe(this) {
            if (it) setupButtonSupPurchased()
        }

        purchaseHelper.isIapPurchased.observe(this) {
            when (it) {
                is Tipping.Succeeded -> {
                    baseConfig.isPro = true
                }
                is Tipping.NoTips -> {
                    baseConfig.isPro = false
                }
                is Tipping.FailedToLoad -> {
                }
                else -> {
                }
            }
        }
        purchaseHelper.isSupPurchased.observe(this) {
            when (it) {
                is Tipping.Succeeded -> {
                    baseConfig.isProSubs = true
                }
                is Tipping.NoTips -> {
                    baseConfig.isProSubs = false
                }
                is Tipping.FailedToLoad -> {
                }
                else -> {
                }
            }
        }

        purchaseHelper.isIapPurchasedList.observe(this) {
            setupButtonIapChecked()
        }
        purchaseHelper.isSupPurchasedList.observe(this) {
            setupButtonSupChecked()
        }
    }

    override fun onResume() {
        super.onResume()
        updateTextColors(binding.purchaseCoordinator)
        setupOptionsMenu()

        val backgroundColor = getProperBackgroundColor()
        setupToolbar(binding.purchaseToolbar, NavigationIcon.Arrow)
        updateToolbarColors(binding.purchaseToolbar, backgroundColor, useOverflowIcon = false)
        binding.purchaseAppBarLayout.setBackgroundColor(backgroundColor)
        binding.collapsingToolbar.setBackgroundColor(backgroundColor)

        setupIcon()
    }

    private fun setupOptionsMenu() {
        binding.purchaseToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.restorePurchases -> {
                    setupButtonReset()

                    val subscriptionIdListAll: ArrayList<String> = subscriptionIdList
                    subscriptionIdListAll.addAll(subscriptionYearIdList)
                    purchaseHelper.retrieveDonation(productIdList, subscriptionIdListAll)

                    true
                }
                R.id.openSubscriptions -> {
                    val url = "https://play.google.com/store/account/subscriptions"
                    launchViewIntent(url)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupButtonIapPurchased() {
        binding.appOneButton.apply {
            val price = purchaseHelper.getPriceDonation(productIdList[0])
            isEnabled = price != getString(stringsR.string.no_connection)
            text = price
            setOnClickListener {
                purchaseHelper.getDonation(productIdList[0])
            }
            background.setTint(primaryColor)
        }

        binding.appTwoButton.apply {
            val price = purchaseHelper.getPriceDonation(productIdList[1])
            isEnabled = price != getString(stringsR.string.no_connection)
            text = price
            setOnClickListener {
                purchaseHelper.getDonation(productIdList[1])
            }
            background.setTint(primaryColor)
        }

        binding.appThreeButton.apply {
            val price = purchaseHelper.getPriceDonation(productIdList[2])
            isEnabled = price != getString(stringsR.string.no_connection)
            text = price
            setOnClickListener {
                purchaseHelper.getDonation(productIdList[2])
            }
            background.setTint(primaryColor)
        }
    }

    private fun setupButtonIapChecked() {
        val check = AppCompatResources.getDrawable(this@PurchaseActivity, R.drawable.ic_check_circle_mini)
        if (purchaseHelper.isIapPurchased(productIdList[0])) {
            binding.appOneButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appOneButton.isEnabled = false
        }
        if (purchaseHelper.isIapPurchased(productIdList[1])) {
            binding.appTwoButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appTwoButton.isEnabled = false
        }
        if (purchaseHelper.isIapPurchased(productIdList[2])) {
            binding.appThreeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appThreeButton.isEnabled = false
        }
    }

    private fun setupButtonSupPurchased() {
        binding.appOneSubButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionIdList[0])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_month), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionIdList[0])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appTwoSubButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionIdList[1])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_month), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionIdList[1])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appThreeSubButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionIdList[2])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_month), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionIdList[2])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appOneSubYearButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionYearIdList[0])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_year), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionYearIdList[0])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appTwoSubYearButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionYearIdList[1])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_year), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionYearIdList[1])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appThreeSubYearButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionYearIdList[2])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_year), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionYearIdList[2])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }
    }

    private fun setupButtonSupChecked() {
        val check = AppCompatResources.getDrawable(this@PurchaseActivity, R.drawable.ic_check_circle_mini)
        if (purchaseHelper.isSubPurchased(subscriptionIdList[0])) {
            binding.appOneSubButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appOneSubButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionIdList[1])) {
            binding.appTwoSubButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appTwoSubButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionIdList[2])) {
            binding.appThreeSubButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appThreeSubButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionYearIdList[0])) {
            binding.appOneSubYearButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appOneSubYearButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionYearIdList[1])) {
            binding.appTwoSubYearButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appTwoSubYearButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionYearIdList[2])) {
            binding.appThreeSubYearButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appThreeSubYearButton.isEnabled = false
        }
    }

    private fun setupButtonReset() {
        binding.appOneButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appOneSubButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appOneSubYearButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appTwoButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appTwoSubButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appTwoSubYearButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appThreeButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appThreeSubButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appThreeSubYearButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
    }

    private fun setupIcon() {
        val appDrawable = resources.getColoredDrawableWithColor(this, R.drawable.ic_plus_support, primaryColor)
        val appBg = resources.getColoredDrawableWithColor(this, R.drawable.squircle_bg, surfaceColor)
        binding.topDetails.appLogo.setImageDrawable(appDrawable)
        binding.topDetails.appLogo.background = appBg

        val textColor = getProperTextColor()
        binding.appOne.background.setTint(surfaceColor)
        binding.appOneName.setTextColor(textColor)
        binding.appTwo.background.setTint(surfaceColor)
        binding.appTwoName.setTextColor(textColor)
        binding.appThree.background.setTint(surfaceColor)
        binding.appThreeName.setTextColor(textColor)


        binding.widebarsLogo.apply {
            applyColorFilter(textColor)
            setOnClickListener {
//                launchViewIntent(getString(R.string.my_website))
                onThemeClick()
            }
        }
        binding.widebarsTitle.setOnClickListener {
//            launchViewIntent(getString(R.string.my_website))
            onThemeClick()
        }
    }

    private fun onThemeClick() {
        if (firstVersionClickTS == 0L) {
            firstVersionClickTS = System.currentTimeMillis()
            Handler(Looper.getMainLooper()).postDelayed({
                firstVersionClickTS = 0L
                clicksSinceFirstClick = 0
            }, EASTER_EGG_TIME_LIMIT)
        }

        clicksSinceFirstClick++
        if (clicksSinceFirstClick == EASTER_EGG_REQUIRED_CLICKS) {
//            toast(R.string.hello)
        } else if (clicksSinceFirstClick >= EASTER_EGG_REQUIRED_CLICKS_NEXT && !isPro()) {
            firstVersionClickTS = 0L
            clicksSinceFirstClick = 0

            if ((0..50).random() == 10 || baseConfig.appRunCount % 100 == 0) {
                toast("You did not hack the system ;(")
            } else if (!isAutoTheme() && !isDynamicTheme()) {
                val text = when {
                    isLightTheme() -> "You hacked the system ;("
                    isGrayTheme() -> "It got dark"
                    isDarkTheme() -> "Blackness"
                    else -> "Light"
                }
                toast(text)

                val themeId = when {
                    isLightTheme() -> THEME_GRAY
                    isGrayTheme() -> THEME_DARK
                    isDarkTheme() -> THEME_BLACK
                    else -> THEME_LIGHT
                }
                updateTheme(themeId)
            } else {
                toast(R.string.hello)
            }
        }
    }

    private fun updateTheme(themeId: Int) {
        setupThemes()
        val theme = predefinedThemes[themeId]!!
        baseConfig.textColor = getColor(theme.textColorId)
        baseConfig.backgroundColor = getColor(theme.backgroundColorId)
        baseConfig.primaryColor = getColor(theme.primaryColorId)

        setTheme(getThemeId())
        recreate()
    }

    private fun setupThemes() {
        predefinedThemes.apply {
            put(
                THEME_LIGHT,
                MyTheme(
                    labelId = R.string.light_theme,
                    textColorId = R.color.theme_light_text_color,
                    backgroundColorId = R.color.theme_light_background_color,
                    primaryColorId = R.color.color_primary,
                    appIconColorId = baseConfig.customAppIconColor
                )
            )
            put(
                THEME_GRAY,
                MyTheme(
                    labelId = stringsR.string.gray_theme,
                    textColorId = R.color.theme_gray_text_color,
                    backgroundColorId = R.color.theme_gray_background_color,
                    primaryColorId = R.color.color_primary,
                    appIconColorId = baseConfig.customAppIconColor
                )
            )
            put(
                THEME_DARK,
                MyTheme(
                    labelId = R.string.dark_theme,
                    textColorId = R.color.theme_dark_text_color,
                    backgroundColorId = R.color.theme_dark_background_color,
                    primaryColorId = R.color.color_primary,
                    appIconColorId = baseConfig.customAppIconColor
                )
            )
            put(
                THEME_BLACK,
                MyTheme(
                    labelId = stringsR.string.black,
                    textColorId = R.color.theme_black_text_color,
                    backgroundColorId = R.color.theme_black_background_color,
                    primaryColorId = R.color.color_primary,
                    appIconColorId = baseConfig.customAppIconColor
                )
            )
        }
    }
}
