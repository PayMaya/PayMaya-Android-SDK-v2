# PayMayaSDK
The PayMaya Android SDK is a library that allows you to easily add credit and debit card as payment options to your mobile application.

## Compatibility
Android OS version 4.1 (API level 16) or higher

## Integration

##### Grab the latest SDK code from
```
"https://github.com/PayMaya/PayMaya-Android-SDK"
```

## Checkout

#### Initialization
1. To initialize the SDK, build the Checkout Client which include specify API key, environment type (sandbox or production) and a level of console logging. To do this you need to use Builder class which allows to set required properties. 
```
private val payMayaCheckoutClient = PayMayaCheckout.Builder()
        .clientKey("client_key")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()
```

#### Base model - description
The CheckoutRequest class encapsulate exhaustive information about the buyer, the items inside the cart, transaction amount, status of payment, request reference number, URLs and metadata.
```
data class CheckoutRequest(
    val totalAmount: TotalAmount,
    val buyer: Buyer? = null,
    val items: List<Item>,
    val requestReferenceNumber: String,
    val redirectUrl: RedirectUrl,
    val metadata: JSONObject? = null
)
```

#### Using Checkout
1. Create instance of an CheckoutRequest class.
```
val checkoutRequest = CheckoutRequest(
    totalAmount
    buyer
    items
    requestReferenceNumber
    redirectUrl
    metadata
)
```
2. Call ```execute``` function to initiate payment with checkout request data.
```
payMayaCheckoutClient.execute(this, checkoutRequest)
```
3. The function will now check which response code has come and will return the appropriate class object for the required response status. Thanks to this, we are able to handle the payment result in a convenient way for us. An example you can find in the payment result handling section

## Pay with PayMaya
#### Initialization
1. To initialize the SDK, build the Pay With PayMaya Client which include specify API key, environment type (sandbox or production) and a level of console logging. To do this you need to use Builder class which allows to set required properties. 
```
private val payWithPayMayaClient = PayWithPayMaya.Builder()
        .clientKey("client_key")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()
```
### Single Payment

#### Base model - description
Single payment request model encapsulating exhaustive information about transaction amount, request reference number, URLs and metadata.
```
data class SinglePaymentRequest(
    val totalAmount: TotalAmount,
    val requestReferenceNumber: String,
    val redirectUrl: RedirectUrl,
    val metadata: JSONObject? = null
)
```
#### Using Single Payment
1. Create instance of an SinglePaymentRequest class.
```
val singlePaymentRequest = SinglePaymentRequest(
    totalAmount
    requestReferenceNumber
    redirectUrl
    metadata
)
```
2. Call ```execute``` function to initiate payment with single payment request data.
```
payMayaCheckoutClient.execute(this, singlePaymentRequest)
```

### Creating a Wallet Link

#### Base model - description
Create Wallet Link request model encapsulating exhaustive information about request reference number, URLs and metadata.
```
data class CreateWalletLinkRequest(
    val requestReferenceNumber: String,
    val redirectUrl: RedirectUrl,
    val metadata: JSONObject? = null
) : PayMayaRequest
```

### Using Wallet Link
1. Create instance of an CreateWalletLinkRequest class.
```
val createWalletLinkRequest = CreateWalletLinkRequest(
    requestReferenceNumber
    redirectUrl
    metadata
)
```
2. Call ```execute``` function to initiate creating wallet link with wallet link request data.
```
payMayaCheckoutClient.execute(this, createWalletLinkRequest)
```

## Vault

#### Initialization
1. To initialize the SDK, build the Pay With Vault Client which include specify API key, environment type (sandbox or production) and a level of console logging. To do this you need to use Builder class which allows to set required properties. 
```
private val payMayaVaultClient = PayMayaVault.Builder()
        .clientKey("client_key")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()
```

#### Using Payment Vault
Payment Vault provides merchants the ability to store their customer's card details and charge for payments on-demand
1. Call ```execute``` function to initiate vault payment request with unnecessary data
```
payMayaVaultClient.execute(this)
```

## Payment Results handling
1. Example code responsible for handling the result of checkout payments. Handling the results of other payment methods in the same way.
```
private fun processCheckoutResult(result: PayMayaCheckoutResult) {
        when (result) {
            is PayMayaCheckoutResult.Success -> {
                val message = "Success, checkoutId: ${result.checkoutId}"
                Log.i(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is PayMayaCheckoutResult.Cancel -> {
                val message = "Canceled, checkoutId: ${result.checkoutId}"
                Log.w(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is PayMayaCheckoutResult.Failure -> {
                val message =
                    "Failure, checkoutId: ${result.checkoutId}, exception: ${result.exception}"
                Log.e(TAG, message)
                if (result.exception is BadRequestException) {
                    Log.d(TAG, (result.exception as BadRequestException).error.toString())
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
```
