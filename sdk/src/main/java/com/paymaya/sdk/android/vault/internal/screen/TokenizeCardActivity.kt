package com.paymaya.sdk.android.vault.internal.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.paymaya.sdk.android.R
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.Resource
import com.paymaya.sdk.android.vault.internal.di.VaultModule
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import kotlinx.android.synthetic.main.activity_paymaya_vault_tokenize_card.*
import java.util.*

internal class TokenizeCardActivity : AppCompatActivity(),
    TokenizeCardContract.View {

    private lateinit var presenter: TokenizeCardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paymaya_vault_tokenize_card)
        supportActionBar?.hide()

        val intent = requireNotNull(intent)
        val environment =
            requireNotNull(intent.getSerializableExtra(EXTRAS_ENVIRONMENT) as PayMayaEnvironment)
        val clientKey = requireNotNull(intent.getStringExtra(EXTRAS_CLIENT_KEY))
        val logLevel = requireNotNull(intent.getSerializableExtra(EXTRAS_LOG_LEVEL) as LogLevel)
        val logoResId = intent.getIntExtra(EXTRAS_LOGO_RES_ID, UNDEFINED_RES_ID)

        presenter = buildPresenter(environment, clientKey, logLevel)

        initializeView(logoResId)

        presenter.viewCreated(this)
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    override fun onBackPressed() {
        presenter.backButtonPressed()
    }

    private fun initializeView(@DrawableRes logoResId: Int) {
        if (logoResId != UNDEFINED_RES_ID) {
            payMayaVaultLogo.setImageDrawable(getDrawable(logoResId))
        }

        payMayaVaultPayButton.setOnClickListener {
            presenter.payButtonClicked(
                payMayaVaultCardNumberEditText.text.toString(),
                payMayaVaultCardExpirationMonthEditText.text.toString(),
                payMayaVaultCardExpirationYearEditText.text.toString(),
                payMayaVaultCardCvcEditText.text.toString()
            )
        }

        payMayaVaultCardNumberEditText.onFocusChangeListener =
            SimpleFocusLostListener { presenter.cardNumberFocusLost(it) }
        payMayaVaultCardNumberEditText.addTextChangedListener(
            CardNumberWatcher { presenter.cardNumberChanged(it) }
        )

        payMayaVaultCardExpirationMonthEditText.onFocusChangeListener =
            SimpleFocusLostListener { presenter.cardExpirationMonthFocusLost(it) }
        payMayaVaultCardExpirationMonthEditText.addTextChangedListener(
            SimpleTextWatcher { presenter.cardExpirationMonthChanged() }
        )

        payMayaVaultCardExpirationYearEditText.onFocusChangeListener =
            SimpleFocusLostListener { presenter.cardExpirationYearFocusLost(it) }
        payMayaVaultCardExpirationYearEditText.addTextChangedListener(
            SimpleTextWatcher { presenter.cardExpirationYearChanged() }
        )

        payMayaVaultCardCvcEditText.onFocusChangeListener =
            SimpleFocusLostListener { presenter.cardCvcFocusLost(it) }
        payMayaVaultCardCvcEditText.addTextChangedListener(
            SimpleTextWatcher { presenter.cardCvcChanged() }
        )
    }

    private fun buildPresenter(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ): TokenizeCardContract.Presenter =
        VaultModule.getTokenizeCardPresenter(environment, clientKey, logLevel, Calendar.getInstance())

    override fun finishSuccess(tokenizeCardResponse: TokenizeCardResponse) {
        val bundle = Bundle()
        bundle.putParcelable(EXTRAS_RESULT, tokenizeCardResponse)
        val intent = Intent()
        intent.putExtra(EXTRAS_BUNDLE, bundle)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun finishCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun showProgressBar() {
        payMayaVaultProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        payMayaVaultProgressBar.visibility = View.GONE
    }

    override fun showErrorPopup(message: Resource) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.paymaya_error_dialog_title))
            .setMessage(message.inContext(this))
            .setPositiveButton(getString(R.string.paymaya_ok)) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    override fun showCardNumberError() {
        payMayaVaultCardNumberTextInputLayout.error = getString(R.string.paymaya_invalid_card_number)
    }

    override fun hideCardNumberError() {
        payMayaVaultCardNumberTextInputLayout.error = null
    }

    override fun hideCardExpirationMonthError() {
        payMayaVaultCardExpirationMonthTextInputLayout.error = null
    }

    override fun showCardExpirationMonthError() {
        payMayaVaultCardExpirationMonthTextInputLayout.error = getString(R.string.paymaya_invalid_month)
    }

    override fun hideCardExpirationYearError() {
        payMayaVaultCardExpirationYearTextInputLayout.error = null
    }

    override fun showCardExpirationYearError() {
        payMayaVaultCardExpirationYearTextInputLayout.error = getString(R.string.paymaya_invalid_year)
    }

    override fun hideCardCvcError() {
        payMayaVaultCardCvcTextInputLayout.error = null
    }

    override fun showCardCvcError() {
        payMayaVaultCardCvcTextInputLayout.error = getString(R.string.paymaya_invalid_cvc)
    }

    override fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    class SimpleFocusLostListener(
        private val callback: (String) -> Unit
    ) : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (!hasFocus) callback.invoke((v as TextInputEditText).text.toString())
        }
    }

    class SimpleTextWatcher(
        private val callback: () -> Unit
    ) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            callback.invoke()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // no-op
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // no-op
        }
    }

    class CardNumberWatcher(
        private val callback: (text: String) -> Unit
    ) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // no-op
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // no-op
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            callback.invoke(s.toString())
        }
    }

    override fun showCardIcon(@DrawableRes iconRes: Int) {
        payMayaVaultCardNumberEditText
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, iconRes, 0)
    }

    override fun hideCardIcon() {
        payMayaVaultCardNumberEditText
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }

    companion object {
        private const val EXTRAS_CLIENT_KEY = "EXTRAS_CLIENT_KEY"
        private const val EXTRAS_ENVIRONMENT = "EXTRAS_ENVIRONMENT"
        private const val EXTRAS_LOG_LEVEL = "EXTRAS_LOG_LEVEL"
        private const val EXTRAS_LOGO_RES_ID = "EXTRAS_LOGO_RES_ID"
        private const val UNDEFINED_RES_ID = -1

        const val EXTRAS_RESULT = "EXTRAS_RESULT"
        const val EXTRAS_BUNDLE = "EXTRAS_BUNDLE"

        fun newIntent(
            activity: Activity,
            clientKey: String,
            environment: PayMayaEnvironment,
            logLevel: LogLevel,
            @DrawableRes logoResId: Int?
        ): Intent {
            val intent = Intent(activity, TokenizeCardActivity::class.java)
            intent.putExtra(EXTRAS_CLIENT_KEY, clientKey)
            intent.putExtra(EXTRAS_ENVIRONMENT, environment)
            intent.putExtra(EXTRAS_LOG_LEVEL, logLevel)
            logoResId?.let { intent.putExtra(EXTRAS_LOGO_RES_ID, it) }
            return intent
        }
    }
}
