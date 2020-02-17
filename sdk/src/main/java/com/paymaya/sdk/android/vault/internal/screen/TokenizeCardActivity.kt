package com.paymaya.sdk.android.vault.internal.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.paymaya.sdk.android.R
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.Resource
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity
import com.paymaya.sdk.android.vault.internal.CardInfoValidator
import com.paymaya.sdk.android.vault.internal.TokenizeCardUseCase
import com.paymaya.sdk.android.vault.internal.VaultRepository
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import kotlinx.android.synthetic.main.activity_paymaya_vault_tokenize_card.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
            requireNotNull(intent.getSerializableExtra(PayMayaPaymentActivity.EXTRAS_ENVIRONMENT) as PayMayaEnvironment)
        val clientKey = requireNotNull(intent.getStringExtra(PayMayaPaymentActivity.EXTRAS_CLIENT_KEY))
        presenter = buildPresenter(environment, clientKey)

        initializeView()

        presenter.viewCreated(this)
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    override fun onBackPressed() {
        presenter.backButtonPressed()
    }

    private fun initializeView() {
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
            SimpleTextWatcher { presenter.cardNumberChanged() }
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
        clientKey: String
    ): TokenizeCardContract.Presenter {
        val json = Json(JsonConfiguration.Stable)
        // TODO JIRA PS-16 http logging level
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        val sendTokenizeCardRequestUseCase =
            TokenizeCardUseCase(
                json,
                VaultRepository(
                    environment,
                    clientKey,
                    json,
                    httpClient
                )
            )
        val cardInfoValidator = CardInfoValidator(Calendar.getInstance())
        return TokenizeCardPresenter(
            sendTokenizeCardRequestUseCase,
            cardInfoValidator
        )
    }

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

    companion object {
        private const val EXTRAS_CLIENT_KEY = "EXTRAS_CLIENT_KEY"
        private const val EXTRAS_ENVIRONMENT = "EXTRAS_ENVIRONMENT"

        const val EXTRAS_RESULT = "EXTRAS_RESULT"
        const val EXTRAS_BUNDLE = "EXTRAS_BUNDLE"

        fun newIntent(
            activity: Activity,
            clientKey: String,
            environment: PayMayaEnvironment
        ): Intent {
            val intent = Intent(activity, TokenizeCardActivity::class.java)
            intent.putExtra(EXTRAS_CLIENT_KEY, clientKey)
            intent.putExtra(EXTRAS_ENVIRONMENT, environment)
            return intent
        }
    }
}
