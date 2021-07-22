package ru.geekbrains.notes.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.vk.api.sdk.VK.isLoggedIn
import com.vk.api.sdk.VK.login
import com.vk.api.sdk.VK.logout
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKScope
import ru.geekbrains.notes.Constant
import ru.geekbrains.notes.GlobalVariables
import ru.geekbrains.notes.R
import ru.geekbrains.notes.Settings
import ru.geekbrains.notes.ui.MainActivity
import java.util.*

class AuthFragment : Fragment() {
    // Клиент для регистрации пользователя через Google
    private var googleSignInClient: GoogleSignInClient? = null

    // Кнопка регистрации через Google
    private var buttonSignInGoogle: SignInButton? = null
    private var buttonSignInVK: MaterialButton? = null
    private var buttonSignOut: MaterialButton? = null

    private var textViewEmail: TextView? = null
    private var textViewAuthBy: TextView? = null
    private var textViewYouCan: TextView? = null
    private var authTypeServer = 0
    private var imageViewAva: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("Debug1", "AuthFragment onCreate")
        if (arguments != null) {
            authTypeServer = requireArguments().getInt(ARG, 0)
            if (authTypeServer == Constant.TYPE_AUTH_GOOGLE) {
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .build()
                googleSignInClient = GoogleSignIn.getClient(requireContext(), options)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        Log.v("Debug1", "AuthFragment onCreateView")
        initGoogleSign()
        initView(view)
        enableSign(Constant.TYPE_AUTH_NONE)
        return view
    }

    // Инициализация запроса на аутентификацию
    private fun initGoogleSign() {
        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл
        // (регулируется параметром)
        Log.v("Debug1", "AuthFragment initGoogleSign")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Получаем клиента для регистрации и данные по клиенту
        if (context != null) googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    // Инициализация пользовательских элементов
    private fun initView(view: View) {
        Log.v("Debug1", "AuthFragment initView")
        // Кнопка регистрации пользователя
        buttonSignInGoogle = view.findViewById(R.id.sign_in_google)
        with(buttonSignInGoogle) {
            this?.setOnClickListener { signInGoogle() }
        }
        buttonSignInVK = view.findViewById(R.id.sign_in_vk)
        with(buttonSignInVK) { this?.setOnClickListener { signInVK() } }
        textViewEmail = view.findViewById(R.id.textViewEmail)

        // Кнопка выхода
        buttonSignOut = view.findViewById(R.id.sign_out)
        with(buttonSignOut) { this?.setOnClickListener { signOut() } }
        imageViewAva = view.findViewById(R.id.imageViewAva)
        textViewAuthBy = view.findViewById(R.id.textViewAuthBy)
        textViewYouCan = view.findViewById(R.id.textViewYouCan)
    }

    // Выход из учётной записи в приложении
    private fun signOut() {
        Log.v("Debug1", "AuthFragment signOut")
        //if (getArguments() != null) {
        //authTypeServer = getArguments().getInt(ARG, 0);
        Log.v("Debug1", "AuthFragment signOut authTypeServer=$authTypeServer")
        when (authTypeServer) {
            Constant.TYPE_AUTH_GOOGLE -> googleSignInClient!!.signOut()
                    .addOnCompleteListener { updateUI("", null, authTypeServer) }
            Constant.TYPE_AUTH_VK -> if (isLoggedIn()) {
                logout()
                updateUI("", null, authTypeServer)
            }
        }
        authTypeServer = Constant.TYPE_AUTH_NONE
        enableSign(authTypeServer)
        updateUI("", null, authTypeServer)
        val userProfile = UserProfile()
        userProfile.typeAutService = authTypeServer
        val bundle = Bundle()
        bundle.putParcelable("UserProfile", userProfile)
        parentFragmentManager.setFragmentResult(Constant.AUTH_RESULT, bundle)
        //}
    }

    // Инициируем регистрацию пользователя через Google
    private fun signInGoogle() {
        Log.v("Debug1", "AuthFragment signInGoogle")
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, Constant.RC_SIGN_IN_GOOGLE)
    }

    // Инициируем регистрацию пользователя через VK
    private fun signInVK() {
        Log.v("Debug1", "AuthFragment signInVK")
        val vcScopeList: MutableList<VKScope> = ArrayList()
        vcScopeList.add(VKScope.EMAIL)
        vcScopeList.add(VKScope.PHOTOS)
        vcScopeList.add(VKScope.STATUS)
        if (activity != null) {
            login(requireActivity(), vcScopeList)
        }
    }

    private fun updateUI(email: String?, imageUrl: String?, typeAuthService: Int) {
        Log.v("Debug1", "AuthFragment updateUI typeAuthService=$typeAuthService")
        textViewEmail!!.text = email
        when (typeAuthService) {
            Constant.TYPE_AUTH_GOOGLE -> {
                textViewAuthBy!!.setText(R.string.textViewAuthByGoogle)
                buttonSignOut!!.visibility = View.VISIBLE
                textViewYouCan!!.visibility = View.GONE
            }
            Constant.TYPE_AUTH_VK -> {
                textViewAuthBy!!.setText(R.string.textViewAuthByVK)
                buttonSignOut!!.visibility = View.VISIBLE
                textViewYouCan!!.visibility = View.GONE
            }
            else -> {
                textViewAuthBy!!.setText(R.string.textViewAuthByNONE)
                buttonSignOut!!.visibility = View.GONE
                textViewYouCan!!.visibility = View.VISIBLE
                imageViewAva!!.setImageResource(0)
            }
        }
        if (imageUrl != null) {
            if (context != null) Glide.with(requireContext())
                    .load(imageUrl)
                    .into(imageViewAva!!)
        }
    }

    // Разрешить аутентификацию и запретить остальные действия
    private fun enableSign(authTypeServer: Int) {
        Log.v("Debug1", "AuthFragment enableSign")
        if (arguments != null) {
            //authTypeServer = getArguments().getInt(ARG, 0);
            if (authTypeServer != Constant.TYPE_AUTH_NONE) {
                buttonSignInGoogle!!.isEnabled = false
                buttonSignInVK!!.isEnabled = false
                buttonSignOut!!.isEnabled = true
                buttonSignInGoogle!!.visibility = View.GONE
                buttonSignInVK!!.visibility = View.GONE
            } else {
                buttonSignInGoogle!!.isEnabled = true
                buttonSignInVK!!.isEnabled = true
                buttonSignOut!!.isEnabled = false
                buttonSignInGoogle!!.visibility = View.VISIBLE
                buttonSignInVK!!.visibility = View.VISIBLE
            }
        }
    }

    // Запретить аутентификацию (уже прошла) и разрешить остальные действия
    private fun disableSign(typeAuthService: Int) {
        Log.v("Debug1", "AuthFragment disableSign")
        if (typeAuthService != Constant.TYPE_AUTH_NONE) {
            buttonSignInGoogle!!.isEnabled = false
            buttonSignOut!!.isEnabled = true
            buttonSignInVK!!.isEnabled = false
            buttonSignInGoogle!!.isEnabled = false
            buttonSignInGoogle!!.visibility = View.GONE
            buttonSignInVK!!.visibility = View.GONE
        } else {
            buttonSignInGoogle!!.isEnabled = true
            buttonSignOut!!.isEnabled = false
            buttonSignInVK!!.isEnabled = true
            buttonSignInGoogle!!.isEnabled = true
            buttonSignInGoogle!!.visibility = View.VISIBLE
            buttonSignInVK!!.visibility = View.VISIBLE
        }
    }

    //https://developers.google.com/identity/sign-in/android/backend-auth?authuser=1
    // Получаем данные пользователя
    private fun handleSignInResultGoogle(completedTask: Task<GoogleSignInAccount>) {
        Log.v("Debug1", "AuthFragment handleSignInResultGoogle")
        val userProfile = UserProfile()
        try {
            val account = completedTask.getResult(ApiException::class.java)
            authTypeServer = Constant.TYPE_AUTH_GOOGLE
            if (account != null) {
                userProfile.displayName = account.displayName
                userProfile.email = account.email
                userProfile.familyName = account.familyName
                userProfile.givenName = account.givenName
                userProfile.id = account.id
                userProfile.idToken = account.idToken
                if (account.photoUrl != null) {
                    userProfile.photoURL = account.photoUrl.toString()
                    Log.v("Debug1", "AuthFragment handleSignInResult=" + account.photoUrl.toString())
                    if (context != null) Glide.with(requireContext())
                            .load(account.photoUrl.toString())
                            .into(imageViewAva!!)
                }
                userProfile.serverAuthCode = account.serverAuthCode
            }
            userProfile.typeAutService = Constant.TYPE_AUTH_GOOGLE
            val bundle = Bundle()
            bundle.putParcelable("UserProfile", userProfile)
            parentFragmentManager.setFragmentResult(Constant.AUTH_RESULT, bundle)
            if (activity != null) {
                //Читаем настройки из глобальной переменной
                val settings = (requireActivity().application as GlobalVariables).settings

                //Settings settings = (new SharedPref(getActivity().getApplication()).loadSettings());
                settings.authTypeService = authTypeServer
                settings.isCloudSync = true

                //new SharedPref(getContext()).saveSettings(settings);

                //Сохраняем настройки в глобальную переменную
                (requireActivity().application as GlobalVariables).settings = settings
            }

            // Регистрация прошла успешно
            disableSign(authTypeServer)
            updateUI(userProfile.email, null, authTypeServer)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    // Получаем данные пользователя VK
    fun handleSignInResultVK(vkAccessToken: VKAccessToken?) {
        Log.v("Debug1", "AuthFragment handleVKSignInResult")
        val userProfile = UserProfile()
        if (vkAccessToken != null) {
            Log.v("Debug1", "AuthFragment handleVKSignInResult vkAccessToken != null")
            authTypeServer = Constant.TYPE_AUTH_VK
            userProfile.typeAutService = Constant.TYPE_AUTH_VK
            userProfile.familyName = vkAccessToken.userId.toString()
            userProfile.email = vkAccessToken.email
            if (activity != null) {
                //Читаем настройки из глобальной переменной
                val settings = (requireActivity().application as GlobalVariables).settings

                //Settings settings = (new SharedPref(getActivity().getApplication()).loadSettings());
                settings.userNameVK = userProfile.email
                settings.authTypeService = authTypeServer
                settings.isCloudSync = true

                //new SharedPref(getContext()).saveSettings(settings);

                //Сохраняем настройки в глобальную переменную
                (requireActivity().application as GlobalVariables).settings = settings
            }
            val bundle = Bundle()
            bundle.putParcelable("UserProfile", userProfile)
            parentFragmentManager.setFragmentResult(Constant.AUTH_RESULT, bundle)
            disableSign(authTypeServer)
            updateUI(vkAccessToken.email, null, authTypeServer)
        } else {
            //accountInfo.clear();
            Log.v("Debug1", "AuthFragment handleVKSignInResult vkAccessToken == null")
            authTypeServer = Constant.TYPE_AUTH_NONE
            updateUI("", null, authTypeServer)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("Debug1", "AuthFragment onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.v("Debug1", "AuthFragment onStart")
        if (arguments != null) {
            authTypeServer = requireArguments().getInt(ARG, 0)
            Log.v("Debug1", "AuthFragment onStart authTypeServer=$authTypeServer")
            when (authTypeServer) {
                Constant.TYPE_AUTH_GOOGLE ->                     // Проверим, входил ли пользователь в это приложение через Google
                    if (context != null) {
                        val account = GoogleSignIn.getLastSignedInAccount(context)
                        if (account != null) {
                            // Пользователь уже входил, сделаем кнопку недоступной
                            disableSign(authTypeServer)
                            // Обновим почтовый адрес этого пользователя и выведем его на экран
                            var imageURL: String? = null
                            if (account.photoUrl != null) imageURL = account.photoUrl.toString()
                            updateUI(account.email, imageURL, authTypeServer)
                        } else {
                            authTypeServer = Constant.TYPE_AUTH_NONE
                            enableSign(authTypeServer)
                            updateUI("", null, Constant.TYPE_AUTH_NONE)

                            //Читаем настройки из глобальной переменной
                            if (activity != null) {
                                val settings = (requireActivity().application as GlobalVariables).settings
                                settings.authTypeService = authTypeServer
                                settings.isCloudSync = false
                                //new SharedPref(getContext()).saveSettings(settings);

                                //Сохраняем настройки в глобальную переменную
                                (requireActivity().application as GlobalVariables).settings = settings
                            }
                        }
                    }
                Constant.TYPE_AUTH_VK -> if (isLoggedIn()) {
                    disableSign(authTypeServer)
                    if (activity != null) {
                        //Читаем настройки из глобальной переменной
                        val settings = (requireActivity().application as GlobalVariables).settings

                        //Settings settings = (new SharedPref(getActivity().getApplication()).loadSettings());
                        settings.authTypeService = authTypeServer
                        settings.isCloudSync = true

                        //new SharedPref(getContext()).saveSettings(settings);

                        //Сохраняем настройки в глобальную переменную
                        (requireActivity().application as GlobalVariables).settings = settings
                        updateUI(settings.userNameVK, null, authTypeServer)
                    }
                    Log.v("Debug1", "AuthFragment onStart authTypeServer=" + authTypeServer + ", VK.isLoggedIn()=" + isLoggedIn())
                } else {
                    authTypeServer = Constant.TYPE_AUTH_NONE
                    enableSign(authTypeServer)
                    updateUI("", null, Constant.TYPE_AUTH_NONE)
                    val settings: Settings
                    if (activity != null) {
                        //Читаем настройки из глобальной переменной
                        settings = (requireActivity().application as GlobalVariables).settings
                        settings.authTypeService = authTypeServer
                        settings.isCloudSync = false

                        //new SharedPref(getContext()).saveSettings(settings);

                        //Сохраняем настройки в глобальную переменную
                        (requireActivity().application as GlobalVariables).settings = settings
                    }
                }
                else -> {
                    enableSign(authTypeServer)
                    updateUI("", null, Constant.TYPE_AUTH_NONE)
                }
            }
        } else {
            Log.v("Debug1", "AuthFragment onStart getArguments() == null")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v("Debug1", "AuthFragment onAttach")
        //getActivity().setTitle("Авторизация...");
        MainActivity.setTitle(activity, "Авторизация...")
    }

    override fun onDetach() {
        //navigation = null;
        Log.v("Debug1", "AuthFragment onDetach")
        val userProfile = UserProfile()
        userProfile.typeAutService = authTypeServer
        val bundle = Bundle()
        bundle.putParcelable("UserProfile", userProfile)
        parentFragmentManager.setFragmentResult(Constant.AUTH_RESULT, bundle)
        super.onDetach()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("Debug1", "AuthFragment onActivityResult")
        if (requestCode == Constant.RC_SIGN_IN_GOOGLE) {
            // Когда сюда возвращается Task, результаты аутентификации уже готовы
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResultGoogle(task)
        }
    }

    companion object {
        private const val TAG = "AuthFragment"
        private const val ARG = "AuthTypeServer"
        @JvmStatic
        fun newInstance(typeAuthServe: Int): AuthFragment {
            Log.v("Debug1", "AuthFragment newInstance")
            val fragment = AuthFragment()
            val bundle = Bundle()
            bundle.putInt(ARG, typeAuthServe)
            fragment.arguments = bundle
            return fragment
        }

        @JvmStatic
        fun checkCloudStatusByUserName(settings: Settings, context: Context?, activity: Activity): String? {
            val authTypeService = settings.authTypeService
            var userName: String? = ""
            if (authTypeService != 0) {
                when (authTypeService) {
                    Constant.TYPE_AUTH_GOOGLE -> if (context != null) {
                        val account = GoogleSignIn.getLastSignedInAccount(context)
                        if (account != null) {
                            //isCloudSync = true;
                            userName = account.email
                            //Settings settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
                            settings.authTypeService = Constant.TYPE_AUTH_GOOGLE
                            settings.isCloudSync = true
                            //Сохраняем настройки в глобальную переменную
                            (activity.application as GlobalVariables).settings = settings
                        }
                    }
                    Constant.TYPE_AUTH_VK -> if (isLoggedIn()) {
                        //isCloudSync = true;
                        userName = settings.userNameVK
                        settings.authTypeService = Constant.TYPE_AUTH_VK
                        settings.isCloudSync = true
                        settings.userNameVK = userName
                        //Сохраняем настройки в глобальную переменную
                        (activity.application as GlobalVariables).settings = settings
                    }
                    else -> {
                        //isCloudSync = false;
                        userName = ""
                        settings.authTypeService = Constant.TYPE_AUTH_NONE
                        settings.isCloudSync = false
                        //Сохраняем настройки в глобальную переменную
                        (activity.application as GlobalVariables).settings = settings
                    }
                }
            }
            Log.v("Debug1", "AuthFragment checkCloudStatusByUserName authTypeService=$authTypeService, userName=$userName")
            return userName
        }
    }
}