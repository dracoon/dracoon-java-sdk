package com.dracoon.sdk.internal;

import java.net.URL;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonApiBuilder;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.auth.AuthChecker;
import com.dracoon.sdk.internal.auth.AuthHolder;
import com.dracoon.sdk.internal.auth.AuthInterceptor;
import com.dracoon.sdk.internal.auth.AuthInterceptorImpl;
import com.dracoon.sdk.internal.auth.AuthTokenRefresher;
import com.dracoon.sdk.internal.auth.AuthTokenRefresherImpl;
import com.dracoon.sdk.internal.auth.AuthTokenRetriever;
import com.dracoon.sdk.internal.auth.AuthTokenRetrieverImpl;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import com.dracoon.sdk.internal.http.HttpClientBuilder;
import com.dracoon.sdk.internal.http.HttpHelper;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.service.Service;
import com.dracoon.sdk.internal.service.ServiceDependencies;
import com.dracoon.sdk.internal.service.ServiceDependenciesImpl;
import com.dracoon.sdk.internal.service.ServiceLocator;
import com.dracoon.sdk.internal.service.ServiceLocatorImpl;
import okhttp3.OkHttpClient;

/**
 * This class and its components are internal to the SDK and are not intended to be used directly.
 * Use {@link DracoonClient.Builder} to obtain an instance.
 *
 * <h2>Architecture Overview</h2>
 *
 * <p>DracoonClientImpl is the implementation of the {@link DracoonClient} interface and acts as the
 * bridge between the public API of {@link DracoonClient} and the internal implementation.</p>
 *
 * <p>The handler interfaces of {@link DracoonClient} (e.g. {@link Account Account}) are not
 * implemented directly. Instead, they are created at runtime using {@link java.lang.reflect.Proxy},
 * which delegates calls to service classes implementing the corresponding SDK functionality.</p>
 *
 * <h2>Key Components</h2>
 *
 * <ul>
 * <li>{@link ClientImpl} / {@link ClientMethodImpl}: Annotations used by the proxy system to
 *     associate service classes and methods with handler interfaces</li>
 * <li>{@link Service}: Classes that implement specific SDK functionality</li>
 * <li>{@link ServiceDependencies}: A common set of dependencies for services</li>
 * <li>{@link ServiceLocator}: A registry to retrieve service instances</li>
 * <li>{@link DynamicServiceProxy}: The class that ties everything together and connects handler
 *     interfaces with actual services using Java dynamic proxies</li>
 * </ul>
 *
 * <h2>Call Flow Example</h2>
 *
 * <ul>
 * <li>The user calls {@code DracoonClient.account().getUserAccount()}.</li>
 * <li>{@code DracoonClientImpl.account()} calls {@code DynamicServiceProxy.account()}.</li>
 * <li>{@code DynamicServiceProxy} returns a Java proxy for {@code DracoonClient.Account}.</li>
 * <li>The proxy routes the method invocation to the actual {@code AccountService} implementation.</li>
 * <li>{@code AccountService.getUserAccount()} performs the API call and returns the result.</li>
 * </ul>
 */
public class DracoonClientImpl extends DracoonClient {

    private final AuthHolder mAuthHolder = new AuthHolder();
    private final EncryptionPasswordHolder mEncPasswordHolder = new EncryptionPasswordHolder();

    private Log mLog = new NullLog();
    private DracoonHttpConfig mHttpConfig = new DracoonHttpConfig();

    private AuthChecker mAuthChecker;
    private AuthTokenRetriever mAuthTokenRetriever;
    private AuthTokenRefresher mAuthTokenRefresher;

    private ServiceLocator mServiceLocator;
    private DynamicServiceProxy mServiceProxy;

    public DracoonClientImpl(URL serverUrl) {
        super(serverUrl);
    }

    public DracoonAuth getAuth() {
        return mAuthHolder.get();
    }

    public void setAuth(DracoonAuth auth) {
        mAuthHolder.set(auth);
    }

    public char[] getEncryptionPassword() {
        return mEncPasswordHolder.get();
    }

    public void setEncryptionPassword(char[] encryptionPassword) {
        mEncPasswordHolder.set(encryptionPassword);
    }

    public Log getLog() {
        return mLog;
    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public DracoonHttpConfig getHttpConfig() {
        return mHttpConfig;
    }

    public void setHttpConfig(DracoonHttpConfig httpConfig) {
        mHttpConfig = httpConfig != null ? httpConfig : new DracoonHttpConfig();
    }

    // --- Initialization methods ---

    public void init() {
        initAuthHelpers();

        initServiceLocator();
        initServiceProxy();
    }

    private void initAuthHelpers() {
        OAuthClient oAuthClient = new OAuthClient(mServerUrl);
        oAuthClient.setLog(mLog);
        oAuthClient.setHttpConfig(mHttpConfig);
        oAuthClient.init();

        mAuthChecker = new AuthChecker(() -> mServiceLocator.getAccountService().pingUser());

        mAuthTokenRetriever = new AuthTokenRetrieverImpl(oAuthClient, mAuthHolder);
        mAuthTokenRefresher = new AuthTokenRefresherImpl(oAuthClient, mAuthHolder);
    }

    private void initServiceLocator() {
        OkHttpClient httpClient = new HttpClientBuilder().build(mHttpConfig);

        HttpHelper httpHelper = new HttpHelper();
        httpHelper.setLog(mLog);
        httpHelper.setRetryEnabled(mHttpConfig.isRetryEnabled());
        httpHelper.setRateLimitingEnabled(mHttpConfig.isRateLimitingEnabled());
        httpHelper.init();

        AuthInterceptor authInterceptor = new AuthInterceptorImpl(mAuthHolder, mAuthTokenRefresher);

        DracoonApi dracoonApi = new DracoonApiBuilder().build(mServerUrl, httpClient, authInterceptor);

        DracoonErrorParser dracoonErrorParser = new DracoonErrorParser();
        dracoonErrorParser.setLog(mLog);

        CryptoWrapper cryptoWrapper = new CryptoWrapper(mLog);

        ServiceDependencies serviceDependencies = new ServiceDependenciesImpl.Builder()
                .setLog(mLog)
                .setHttpConfig(mHttpConfig)
                .setHttpClient(httpClient)
                .setHttpHelper(httpHelper)
                .setServerUrl(mServerUrl)
                .setDracoonApi(dracoonApi)
                .setDracoonErrorParser(dracoonErrorParser)
                .setEncryptionPasswordHolder(mEncPasswordHolder)
                .setCryptoWrapper(cryptoWrapper)
                .build();

        mServiceLocator = new ServiceLocatorImpl(serviceDependencies);
    }

    private void initServiceProxy() {
        mServiceProxy = new DynamicServiceProxy(mServiceLocator);
        mServiceProxy.prepare();
    }

    public void checkApiVersionSupported() throws DracoonNetIOException, DracoonApiException {
        mServiceLocator.getServerInfoService().checkVersionSupported();
    }

    public void retrieveAuthTokens() throws DracoonApiException, DracoonNetIOException {
        mAuthTokenRetriever.retrieve();
    }

    @Override
    public boolean isAuthValid() throws DracoonNetIOException, DracoonApiException {
        return mAuthChecker.isAuthValid();
    }

    @Override
    public void checkAuthValid() throws DracoonNetIOException, DracoonApiException {
        mAuthChecker.checkAuthValid();
    }

    // --- Methods to get public handlers ---

    @Override
    public Server server() {
        return mServiceProxy.server();
    }

    @Override
    public Account account() {
        return mServiceProxy.account();
    }

    @Override
    public Users users() {
        return mServiceProxy.users();
    }

    @Override
    public Groups groups() {
        return mServiceProxy.groups();
    }

    @Override
    public Nodes nodes() {
        return mServiceProxy.nodes();
    }

    @Override
    public Shares shares() {
        return mServiceProxy.shares();
    }

}
