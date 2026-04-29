package com.example.nutrishare_android.ui.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nutrishare_android.data.network.NetworkConfig
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    val kakaoLoginUrl = remember(viewModel) { viewModel.getKakaoLoginUrl() }
    var showWebView by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    BackHandler(enabled = showWebView) {
        showWebView = false
    }

    if (showWebView) {
        KakaoLoginWebView(
            loginUrl = kakaoLoginUrl,
            onAccessTokenReceived = { accessToken ->
                showWebView = false
                viewModel.completeKakaoLogin(accessToken)
            },
            onError = { message ->
                showWebView = false
                viewModel.setError(message)
            }
        )
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "NutriShare",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "신선 식재료 공동구매",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("신선", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "신선한 상품을 함께 구매하고 합리적으로 즐겨보세요.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("절약", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "배송과 유통 비용을 줄여 더 가볍게 주문하세요.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { showWebView = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary)
                } else {
                    Text("카카오로 로그인", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    viewModel.startGuestMode()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                Text("비회원으로 둘러보기", fontWeight = FontWeight.SemiBold)
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun KakaoLoginWebView(
    loginUrl: String,
    onAccessTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val webView = WebView(context)
            webView.layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.loadsImagesAutomatically = true

            Log.d("LoginWebView", "loadUrl=$loginUrl")

            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
            }

            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    Log.d("LoginWebView", "progress=$newProgress currentUrl=${view?.url}")
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    Log.d("LoginWebView", "title=$title currentUrl=${view?.url}")
                }
            }

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    Log.d("LoginWebView", "shouldOverrideUrlLoading(String)=$url")
                    return interceptCallback(
                        view = view,
                        url = url.orEmpty(),
                        onAccessTokenReceived = onAccessTokenReceived,
                        onError = onError
                    )
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    Log.d("LoginWebView", "shouldOverrideUrlLoading(Request)=${request?.url}")
                    return interceptCallback(
                        view = view,
                        url = request?.url?.toString().orEmpty(),
                        onAccessTokenReceived = onAccessTokenReceived,
                        onError = onError
                    )
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    Log.d("LoginWebView", "onPageStarted=$url")
                    interceptCallback(
                        view = view,
                        url = url.orEmpty(),
                        onAccessTokenReceived = onAccessTokenReceived,
                        onError = onError
                    )
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.d("LoginWebView", "onPageFinished=$url")
                    interceptCallback(
                        view = view,
                        url = url.orEmpty(),
                        onAccessTokenReceived = onAccessTokenReceived,
                        onError = onError
                    )
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (request?.isForMainFrame == true) {
                        Log.e(
                            "LoginWebView",
                            "onReceivedError url=${request.url} code=${error?.errorCode} desc=${error?.description}"
                        )
                        onError("로그인 페이지를 불러오지 못했습니다.")
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    if (request?.isForMainFrame == true) {
                        Log.e(
                            "LoginWebView",
                            "onReceivedHttpError url=${request.url} status=${errorResponse?.statusCode} reason=${errorResponse?.reasonPhrase}"
                        )
                    }
                }
            }

            webView.loadUrl(loginUrl)
            webView
        }
    )
}

private fun interceptCallback(
    view: WebView?,
    url: String,
    onAccessTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
): Boolean {
    if (url.isBlank()) {
        return false
    }

    val uri = Uri.parse(url)
    if (!isLoginCallback(uri)) {
        return false
    }

    Log.d("LoginWebView", "interceptCallback hit=$url")
    view?.stopLoading()

    val accessToken = uri.getQueryParameter("accessToken")
    val error = uri.getQueryParameter("error")

    return when {
        !accessToken.isNullOrBlank() -> {
            Log.d("LoginWebView", "accessToken length=${accessToken.length}")
            onAccessTokenReceived(accessToken)
            true
        }

        !error.isNullOrBlank() -> {
            Log.e("LoginWebView", "callback error=$error")
            onError("카카오 로그인에 실패했습니다: $error")
            true
        }

        else -> {
            Log.e("LoginWebView", "callback missing token and error url=$url")
            onError("로그인 콜백에서 액세스 토큰을 찾지 못했습니다.")
            true
        }
    }
}

private fun isLoginCallback(uri: Uri): Boolean {
    val path = uri.path ?: return false
    if (path != NetworkConfig.LOGIN_CALLBACK_PATH) {
        return false
    }

    val hasOAuthResult =
        !uri.getQueryParameter("accessToken").isNullOrBlank() ||
            !uri.getQueryParameter("error").isNullOrBlank()

    if (hasOAuthResult) {
        return true
    }

    val hostBase = Uri.parse(NetworkConfig.HOST_BASE_URL)
    val sameServerHost = uri.host == hostBase.host
    val sameServerScheme = uri.scheme == hostBase.scheme
    val noHost = uri.host.isNullOrBlank()

    return (sameServerHost && sameServerScheme) || noHost
}
