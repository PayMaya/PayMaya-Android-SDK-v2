/*
 * Copyright (c) 2020  PayMaya Philippines, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.paymaya.sdk.android.vault.internal.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.textfield.TextInputEditText
import com.paymaya.sdk.android.R
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.AndroidString
import com.paymaya.sdk.android.databinding.ActivityPaymayaVaultTokenizeCardBinding
import com.paymaya.sdk.android.vault.internal.di.VaultModule
import com.paymaya.sdk.android.vault.internal.helpers.AutoFormatTextWatcher
import com.paymaya.sdk.android.vault.internal.helpers.CardNumberFormatter
import com.paymaya.sdk.android.vault.internal.helpers.DateFormatter
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import java.util.Calendar

internal class TokenizeCardActivity : AppCompatActivity(),
    TokenizeCardContract.View {

    private lateinit var presenter: TokenizeCardContract.Presenter
    private lateinit var binding: ActivityPaymayaVaultTokenizeCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val intent = requireNotNull(intent)
        val environment =
            requireNotNull(intent.getSerializableExtra(EXTRAS_ENVIRONMENT) as PayMayaEnvironment)
        val clientPublicKey = requireNotNull(intent.getStringExtra(EXTRAS_CLIENT_PUBLIC_KEY))
        val logLevel = requireNotNull(intent.getSerializableExtra(EXTRAS_LOG_LEVEL) as LogLevel)
        val logoResId = intent.getIntExtra(EXTRAS_LOGO_RES_ID, UNDEFINED_RES_ID)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                presenter.backButtonPressed()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        binding = ActivityPaymayaVaultTokenizeCardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        presenter = buildPresenter(environment, clientPublicKey, logLevel)

        initializeView(logoResId)

        presenter.viewCreated(this)
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    private fun initializeView(@DrawableRes logoResId: Int) {
        if (logoResId != UNDEFINED_RES_ID) {
            binding.payMayaVaultLogo.setImageDrawable(AppCompatResources.getDrawable(this, logoResId))
        }

        binding.payMayaVaultPayButton.setOnClickListener {
            presenter.payButtonClicked(
                binding.payMayaVaultCardNumberEditText.text.toString(),
                binding.payMayaVaultCardExpirationDateEditText.text.toString(),
                binding.payMayaVaultCardCvcEditText.text.toString()
            )
        }

        binding.payMayaVaultCardNumberEditText.onFocusChangeListener =
            SimpleFocusLostListener(callbackFocusLost = { presenter.cardNumberFocusLost(it) })
        binding.payMayaVaultCardNumberEditText.addTextChangedListener(
            SimpleTextWatcher { presenter.cardNumberChanged(it) }
        )
        binding.payMayaVaultCardNumberEditText.addTextChangedListener(
            AutoFormatTextWatcher(
                binding.payMayaVaultCardNumberEditText,
                CardNumberFormatter()
            )
        )

        binding.payMayaVaultCardExpirationDateEditText.onFocusChangeListener =
            SimpleFocusLostListener(
                callbackFocusReceived = { presenter.cardExpirationDateFocusReceived() },
                callbackFocusLost = { presenter.cardExpirationDateFocusLost(it) }
            )
        binding.payMayaVaultCardExpirationDateEditText.addTextChangedListener(
            SimpleTextWatcher { presenter.cardExpirationDateChanged() }
        )
        binding.payMayaVaultCardExpirationDateEditText.addTextChangedListener(
            AutoFormatTextWatcher(
                binding.payMayaVaultCardExpirationDateEditText,
                DateFormatter()
            )
        )

        binding.payMayaVaultCardCvcEditText.onFocusChangeListener =
            SimpleFocusLostListener(callbackFocusLost = { presenter.cardCvcFocusLost(it) })
        binding.payMayaVaultCardCvcEditText.addTextChangedListener(
            SimpleTextWatcher { presenter.cardCvcChanged() }
        )
        binding.payMayaVaultScreenMask.setOnClickListener {
            presenter.screenMaskClicked()
        }
        binding.payMayaVaultCardCvcHintButtonMask.setOnClickListener {
            presenter.cardCvcInfoClicked()
        }
    }

    private fun buildPresenter(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ): TokenizeCardContract.Presenter =
        VaultModule.getTokenizeCardPresenter(environment, clientPublicKey, logLevel, Calendar.getInstance())

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
        binding.payMayaVaultProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.payMayaVaultProgressBar.visibility = View.GONE
    }

    override fun showErrorPopup(message: AndroidString) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.paymaya_error_dialog_title))
            .setMessage(message.stringify(this))
            .setPositiveButton(getString(R.string.paymaya_ok)) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    override fun showCardNumberError() {
        binding.payMayaVaultCardNumberTextInputLayout.error = getString(R.string.paymaya_invalid_card_number)
    }

    override fun hideCardNumberError() {
        binding.payMayaVaultCardNumberTextInputLayout.error = null
    }

    override fun hideCardExpirationDateError() {
        binding.payMayaVaultCardExpirationDateTextInputLayout.error = null
    }

    override fun showCardExpirationDateError() {
        binding.payMayaVaultCardExpirationDateTextInputLayout.error = getString(R.string.paymaya_invalid_date)
    }

    override fun hideCardCvcError() {
        binding.payMayaVaultCardCvcTextInputLayout.error = null
    }

    override fun showCardCvcError() {
        binding.payMayaVaultCardCvcTextInputLayout.error = getString(R.string.paymaya_invalid_cvc)
    }

    override fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun hideCardCvcHint() {
        binding.payMayaVaultCardCvcHintImage.visibility = View.GONE
        binding.payMayaVaultScreenMask.visibility = View.GONE
    }

    override fun showCardCvcHint() {
        binding.payMayaVaultCardCvcHintImage.visibility = View.VISIBLE
        binding.payMayaVaultScreenMask.visibility = View.VISIBLE
    }

    override fun showCardExpirationDateHint() {
        binding.payMayaVaultCardExpirationDateEditText.hint = getString(R.string.paymaya_vault_card_exp_date_hint)
    }

    class SimpleFocusLostListener(
        private val callbackFocusLost: ((String) -> Unit)? = null,
        private val callbackFocusReceived: (() -> Unit)? = null
    ) : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (!hasFocus)
                callbackFocusLost?.invoke((v as TextInputEditText).text.toString())
            else
                callbackFocusReceived?.invoke()
        }
    }

    class SimpleTextWatcher(
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
        binding.payMayaVaultCardNumberEditText
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, iconRes, 0)
    }

    override fun hideCardIcon() {
        binding.payMayaVaultCardNumberEditText
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }

    companion object {
        private const val EXTRAS_CLIENT_PUBLIC_KEY = "EXTRAS_CLIENT_PUBLIC_KEY"
        private const val EXTRAS_ENVIRONMENT = "EXTRAS_ENVIRONMENT"
        private const val EXTRAS_LOG_LEVEL = "EXTRAS_LOG_LEVEL"
        private const val EXTRAS_LOGO_RES_ID = "EXTRAS_LOGO_RES_ID"
        private const val UNDEFINED_RES_ID = -1

        const val EXTRAS_RESULT = "EXTRAS_RESULT"
        const val EXTRAS_BUNDLE = "EXTRAS_BUNDLE"

        fun newIntent(
            activity: Activity,
            clientPublicKey: String,
            environment: PayMayaEnvironment,
            logLevel: LogLevel,
            @DrawableRes logoResId: Int?
        ): Intent {
            val intent = Intent(activity, TokenizeCardActivity::class.java)
            intent.putExtra(EXTRAS_CLIENT_PUBLIC_KEY, clientPublicKey)
            intent.putExtra(EXTRAS_ENVIRONMENT, environment)
            intent.putExtra(EXTRAS_LOG_LEVEL, logLevel)
            logoResId?.let { intent.putExtra(EXTRAS_LOGO_RES_ID, it) }
            return intent
        }
    }
}