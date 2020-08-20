# PayMayaSDK
The PayMaya Android SDK is a library that allows you integrate with PayMaya
payments. It supports following methods:
* Checkout
    * Checkout (WebView)
* Pay With PayMaya
    * Single Payment (WebView)
    * Create Wallet Link (WebView)
* PayMaya Vault
    * Tokenize Card (native activity)
    
The SDK includes Demo application which uses Sandbox environment.

#### Compatibility
Android OS version 6.0 Marshmallow (API level 23) or higher.

## Integration

### Gradle

Refer [here](https://bintray.com/paymaya/maven-android/paymaya-sdk-android/) for the released versions

```
repositories {
    jcenter()
}

dependencies {
    implementation 'com.paymaya:paymaya-sdk-android:+'
}
```


## Checkout

### Initialization

To initialize the SDK, build Checkout Client by specifying API key,
environment type (sandbox or production) and a level of console logging.
```Kotlin
val payMayaCheckoutClient = PayMayaCheckout.newBuilder()
    .clientPublicKey("client_public_key")
    .environment(PayMayaEnvironment.SANDBOX)
    .logLevel(LogLevel.ERROR)
    .build()
```
    
### Using Checkout

1. Create instance of a CheckoutRequest class, which contains information
about the buyer (optional), bought items, transaction amount, merchant's request
reference number, redirect URLs and metadata (optional).

    ```Kotlin
    val request = CheckoutRequest(
        totalAmount,
        buyer,
        items,
        requestReferenceNumber,
        redirectUrl,
        metadata
    )
    ```

    In a typical case, RedirectUrl can be created as follows (with fake URLs):
    ```Kotlin
    RedirectUrl(
        success = "http://success.com",
        failure = "http://failure.com",
        cancel = "http://cancel.com"
    )
    ```
    
1. Call `startCheckoutActivityForResult` method to initiate payment with
the checkout request data.

    ```Kotlin
    payMayaCheckoutClient.startCheckoutActivityForResult(this, request)
    ```
    The method starts a new activity in which the customer securely completes the
    process on a web page hosted by PayMaya. The result of the payment
    is returned to the application using standard Android Activity result mechanism.

1. To get the result, call `PayMayaCheckout.onActivityResult` from your Activity's
`onActivityResult`, which returns `PayMayaCheckoutResult.Success`,
`PayMayaCheckoutResult.Cancel` or `PayMayaCheckoutResult.Failure` with appropriate
details like `checkoutId`. The `checkoutId` can be used for further processing
or to check detailed status of the payment using `checkPaymentStatus` method.

    See example of `onActivityResult` usage in the [Results handling sample](#result-handling-sample)
    section.

     NOTE: If user close Checkout Activity (e.g. by pressing the Back button)
     at any point, status of the payment will be automatically checked (if only
     `checkoutId` has been already retrieved from the PayMaya gateway).
     Payment status will be mapped to PayMayaCheckoutResult:
     
     * PAYMENT_SUCCESS → PayMayaCheckoutResult.Success
     * AUTH_FAILED or PAYMENT_FAILED → PayMayaCheckoutResult.Failed
     * otherwise → PayMayaCheckoutResult.Cancel

1. To manually check status of the payment, use `checkPaymentStatus`. The method is
synchronous, so call it from other thread than Main for the best user experience.
See sample usage in the Demo application (`CartPresenter.payMayaCheckRecentPaymentStatusClicked`).

For Sandbox environment, use credit cards from the following link:
[Credit Cards for Sandbox Testing](https://developers.paymaya.com/blog/entry/checkout-api-test-credit-card-account-numbers)

## Pay With PayMaya

### Initialization

To initialize the SDK, build Pay With PayMaya Client by specifying API key,
environment type (sandbox or production) and a level of console logging.
```Kotlin
val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("client_public_key")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.ERROR)
        .build()
```

### Single Payment

1. Create instance of an SinglePaymentRequest class.
    ```Kotlin
    val request = SinglePaymentRequest(
        totalAmount,
        requestReferenceNumber,
        redirectUrl,
        metadata
    )
    ```
    
    In a typical case, RedirectUrl can be created as follows (with fake URLs):
    ```Kotlin
    RedirectUrl(
        success = "http://success.com",
        failure = "http://failure.com",
        cancel = "http://cancel.com"
    )
    ``` 
    
1. Call `startSinglePaymentActivityForResult` method to initiate payment with
the single payment request data.
    ```
    payMayaCheckoutClient.startSinglePaymentActivityForResult(this, request)
    ```

    The method starts a new activity in which the customer securely completes the
    process on a web page hosted by PayMaya. The result of the payment
    is returned to the application using standard Android Activity result mechanism.

1. To get the result, call `PayWithPayMaya.onActivityResult` from your Activity's
`onActivityResult`, which returns `PayWithPayMayaResult.Success`,
`PayWithPayMayaResult.Cancel` or `PayWithPayMayaResult.Failure` with appropriate
details like `paymentId`. The `paymentId` can be used for further processing
or to check detailed status of the payment using `checkPaymentStatus` method.

    See example of `onActivityResult` usage in the [Results handling sample](#result-handling-sample)
    section.

     NOTE: If user close Pay With PayMaya Activity at any point (e.g. by pressing
     the Back button), status of the payment will be automatically checked (if only the
     `paymentId` has been already retrieved from the PayMaya gateway).
     Payment status will be mapped to PayWithPayMayaResult:
     
     * PAYMENT_SUCCESS → PayWithPayMayaResult.Success
     * AUTH_FAILED or PAYMENT_FAILED → PayWithPayMayaResult.Failed
     * otherwise → PayWithPayMayaResult.Cancel

1. To manually check status of the payment, use `checkPaymentStatus`. The method is
synchronous, so call it from other thread than Main for the best user experience.
See sample usage in the Demo application (`CartPresenter.payMayaCheckRecentPaymentStatusClicked`).

For Sandbox environment, use credit cards from the following link:
[Credit Cards for Sandbox Testing](https://developers.paymaya.com/blog/entry/checkout-api-test-credit-card-account-numbers)


### Wallet Link

Creates a wallet link that allows charging a PayMaya account later (Recurring Payment).

1. Create instance of an CreateWalletLinkRequest class.
    ```
    val request = CreateWalletLinkRequest(
        requestReferenceNumber,
        redirectUrl,
        metadata
    )
    ```

    In a typical case, RedirectUrl can be created as follows (with fake URLs):
    ```Kotlin
    RedirectUrl(
        success = "http://success.com",
        failure = "http://failure.com",
        cancel = "http://cancel.com"
    )
    ```

1. Call `startCreateWalletLinkActivityForResult` method to initiate creating wallet link process.
    ```
    payMayaCheckoutClient.startCreateWalletLinkActivityForResult(this, request)
    ```

    The method starts a new activity in which the customer securely completes the
    process on a web page hosted by PayMaya. The result of the wallet link creation process
    is returned to the application using standard Android Activity result mechanism. 
    
1. To get the result, call `PayWithPayMaya.onActivityResult` from your Activity's
`onActivityResult`, which returns `CreateWalletLinkResult.Success`,
`CreateWalletLinkResult.Cancel` or `CreateWalletLinkResult.Failure` with appropriate
details like `linkId`. The `linkId` can be used for further processing.

    See example of `onActivityResult` usage in the [Results handling sample](#result-handling-sample)
    section.

## Vault

### Initialization
To initialize the SDK, build the Pay With Vault Client by specifying API key,
environment type (sandbox or production) and a level of console logging.
You can also customize merchant's logo by using `Builder.logo` method.
```Kotlin
val payMayaVaultClient = PayMayaVault.newBuilder()
    .clientPublicKey("client_public_key")
    .environment(PayMayaEnvironment.SANDBOX)
    .logLevel(LogLevel.ERROR)
    .build()
```

### Using Payment Vault

The Card Vault helps you manage your customers and the cards linked to them.
You can register customers as resources and vault their cards from payment tokens.
Vaulting a card allows merchants to create a better experience for returning customers.

1. Call `startTokenizeCardActivityForResult` method to initiate card tokenization process.
    ```Kotlin
    payMayaVaultClient.startTokenizeCardActivityForResult(this)
    ```

    The method starts a new activity in which the customer securely completes the process
    on the SDK's activity. The result is returned to the application using
    standard Android Activity result mechanism.

1. To get the result, call `PayMayaVault.onActivityResult` from your Activity's
`onActivityResult`, which returns `PayMayaVaultResult.Success` or
`PayMayaVaultResult.Cancel` with appropriate details like `paymentTokenId` and `state`.
The `paymentTokenId` can be used for further processing.

    See example of `onActivityResult` usage in the [Results handling sample](#result-handling-sample)
    section.

## Result handling example
Sample handling of the Checkout payment result in the merchant's activity.

Results of the other payment processes can be handled in a similar way. See the source
code of the Demo application for more detailed example (`CartActivity.onActivityResult`
and appropriate methods in `CartPresenter`).

```Kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    payMayaCheckoutClient.onActivityResult(requestCode, resultCode, data)?.let {
        processCheckoutResult(it)
    }
}

private fun processCheckoutResult(result: PayMayaCheckoutResult) {
    when (result) {
        is PayMayaCheckoutResult.Success -> {
            val message = "Success, checkoutId: ${result.checkoutId}"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        is PayMayaCheckoutResult.Cancel -> {
            val message = "Canceled, checkoutId: ${result.checkoutId}"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        is PayMayaCheckoutResult.Failure -> {
            val message =
                "Failure, checkoutId: ${result.checkoutId}, exception: ${result.exception}"
            if (result.exception is BadRequestException) {
                Log.d(TAG, (result.exception as BadRequestException).error.toString())
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
```
