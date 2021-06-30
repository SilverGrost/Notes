package ru.geekbrains.notes.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.notes.R;

import static ru.geekbrains.notes.Constant.AUTH_RESULT;
import static ru.geekbrains.notes.Constant.RC_SIGN_IN_GOOGLE;
import static ru.geekbrains.notes.Constant.RC_VK_SIGN_IN;
import static ru.geekbrains.notes.Constant.TYPE_AUTH_GOOGLE;
import static ru.geekbrains.notes.Constant.TYPE_AUTH_NONE;
import static ru.geekbrains.notes.Constant.TYPE_AUTH_VK;

public class AuthFragment extends Fragment {

    private static final String TAG = "AuthFragment";
    private static final String ARG = "AuthTypeServer";

    // Клиент для регистрации пользователя через Google
    private GoogleSignInClient googleSignInClient;

    // Кнопка регистрации через Google
    private com.google.android.gms.common.SignInButton buttonSignInGoogle;
    private MaterialButton buttonSignInVK;
    private MaterialButton buttonSignOut;
    private TextView textViewEmail;
    private TextView textViewAuthBy;
    private TextView textViewYouCan;
    private int authTypeServer;
    private ImageView imageViewAva;


    public static AuthFragment newInstance(int typeAuthServe) {
        Log.v("Debug1", "AuthFragment newInstance");

        AuthFragment fragment = new AuthFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG, typeAuthServe);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("Debug1", "AuthFragment onCreate");

        if (getArguments() != null) {
            authTypeServer = getArguments().getInt(ARG, 0);
            if (authTypeServer == TYPE_AUTH_GOOGLE) {
                GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(requireContext(), options);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        Log.v("Debug1", "AuthFragment onCreateView");

        initGoogleSign();
        initView(view);
        enableSign();
        return view;

    }

    // Инициализация запроса на аутентификацию
    private void initGoogleSign() {
        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл
        // (регулируется параметром)
        Log.v("Debug1", "AuthFragment initGoogleSign");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Получаем клиента для регистрации и данные по клиенту
        if (getContext() != null)
            googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    // Инициализация пользовательских элементов
    private void initView(View view) {
        Log.v("Debug1", "AuthFragment initView");
        // Кнопка регистрации пользователя
        buttonSignInGoogle = view.findViewById(R.id.sign_in_google);
        buttonSignInGoogle.setOnClickListener(v -> signInGoogle()
        );

        buttonSignInVK = view.findViewById(R.id.sign_in_vk);
        buttonSignInVK.setOnClickListener(v -> signInVK());

        textViewEmail = view.findViewById(R.id.textViewEmail);

        // Кнопка выхода
        buttonSignOut = view.findViewById(R.id.sign_out);
        buttonSignOut.setOnClickListener(v -> signOut());

        imageViewAva = view.findViewById(R.id.imageViewAva);
        textViewAuthBy = view.findViewById(R.id.textViewAuthBy);
        textViewYouCan = view.findViewById(R.id.textViewYouCan);
    }

    // Выход из учётной записи в приложении
    private void signOut() {
        Log.v("Debug1", "AuthFragment signOut");
        if (getArguments() != null) {
            authTypeServer = getArguments().getInt(ARG, 0);
            if (authTypeServer == TYPE_AUTH_GOOGLE) {
                googleSignInClient.signOut()
                        .addOnCompleteListener(task -> updateUI("", null, authTypeServer));
            } else if (authTypeServer == TYPE_AUTH_VK) {
                if (VK.isLoggedIn()) {
                    VK.logout();
                    updateUI("", null, authTypeServer);
                }
            }
            enableSign();
        }
    }


    // Инициируем регистрацию пользователя через Google
    private void signInGoogle() {
        Log.v("Debug1", "AuthFragment signInGoogle");
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }


    // Инициируем регистрацию пользователя через VK
    private void signInVK() {
        Log.v("Debug1", "AuthFragment signInVK");
        List<VKScope> vcScopeList = new ArrayList<>();
        vcScopeList.add(VKScope.EMAIL);
        vcScopeList.add(VKScope.PHOTOS);
        vcScopeList.add(VKScope.STATUS);
        if (getActivity() != null) {
            VK.login(getActivity(), vcScopeList);
        }
    }


    private void updateUI(String email, String imageUrl, int typeAuthService) {
        Log.v("Debug1", "AuthFragment updateUI");
        textViewEmail.setText(email);
        switch (typeAuthService) {
            case TYPE_AUTH_GOOGLE:
                textViewAuthBy.setText(R.string.textViewAuthByGoogle);
                buttonSignOut.setVisibility(View.VISIBLE);
                textViewYouCan.setVisibility(View.GONE);
                break;
            case TYPE_AUTH_VK:
                textViewAuthBy.setText(R.string.textViewAuthByVK);
                buttonSignOut.setVisibility(View.VISIBLE);
                textViewYouCan.setVisibility(View.GONE);
                break;
            default:
                textViewAuthBy.setText(R.string.textViewAuthByNONE);
                buttonSignOut.setVisibility(View.GONE);
                textViewYouCan.setVisibility(View.VISIBLE);
        }

        if (imageUrl != null) {
            if (getContext() != null)
                Glide.with(getContext())
                        .load(imageUrl)
                        .into(imageViewAva);
        }
    }

    // Разрешить аутентификацию и запретить остальные действия
    private void enableSign() {
        Log.v("Debug1", "AuthFragment enableSign");

        if (getArguments() != null) {
            authTypeServer = getArguments().getInt(ARG, 0);
            if (authTypeServer == TYPE_AUTH_GOOGLE) {
                buttonSignInGoogle.setEnabled(true);
                //continue_.setEnabled(false);
                buttonSignOut.setEnabled(false);
                buttonSignInVK.setEnabled(true);
            }
        }
    }

    // Запретить аутентификацию (уже прошла) и разрешить остальные действия
    private void disableSign(int typeAuthService) {
        Log.v("Debug1", "AuthFragment disableSign");

        if (authTypeServer != TYPE_AUTH_NONE) {
            buttonSignInGoogle.setEnabled(false);
            //continue_.setEnabled(true);
            buttonSignOut.setEnabled(true);
            buttonSignInVK.setEnabled(false);
        }
        else {
            buttonSignInGoogle.setEnabled(true);
            //continue_.setEnabled(true);
            buttonSignOut.setEnabled(false);
            buttonSignInVK.setEnabled(true);
        }

    }

    //https://developers.google.com/identity/sign-in/android/backend-auth?authuser=1
    // Получаем данные пользователя
    private void handleSignInResultGoogle(Task<GoogleSignInAccount> completedTask) {

        Log.v("Debug1", "AuthFragment handleSignInResultGoogle");
        UserProfile userProfile = new UserProfile();
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            authTypeServer = TYPE_AUTH_GOOGLE;

            try {
                userProfile.setDisplayName(account.getDisplayName());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            userProfile.setEmail(account.getEmail());
            userProfile.setFamilyName(account.getFamilyName());
            userProfile.setGivenName(account.getGivenName());
            userProfile.setiD(account.getId());
            userProfile.setIdToken(account.getIdToken());
            if (account.getPhotoUrl() != null) {
                userProfile.setPhotoURL(account.getPhotoUrl().toString());
                Log.v("Debug1", "AuthFragment handleSignInResult=" + account.getPhotoUrl().toString());
                if (getContext() != null)
                    Glide.with(getContext())
                            .load(account.getPhotoUrl().toString())
                            .circleCrop()
                            .into(imageViewAva);
            }

            userProfile.setServerAuthCode(account.getServerAuthCode());
            userProfile.setTypeAutService(TYPE_AUTH_GOOGLE);


            Bundle bundle = new Bundle();
            bundle.putParcelable("UserProfile", userProfile);

            getParentFragmentManager().setFragmentResult(AUTH_RESULT, bundle);

            // Регистрация прошла успешно
            disableSign(authTypeServer);
            updateUI(account.getEmail(), null, authTypeServer);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // Получаем данные пользователя VK
    public void handleSignInResultVK(VKAccessToken vkAccessToken) {
        Log.v("Debug1", "AuthFragment handleVKSignInResult");

        UserProfile userProfile = new UserProfile();
        if (vkAccessToken != null) {

            Log.v("Debug1", "AuthFragment handleVKSignInResult vkAccessToken != null");

            authTypeServer = TYPE_AUTH_VK;

            userProfile.setTypeAutService(TYPE_AUTH_VK);
            userProfile.setFamilyName(String.valueOf(vkAccessToken.getUserId()));
            userProfile.setEmail(vkAccessToken.getEmail());

            Bundle bundle = new Bundle();
            bundle.putParcelable("UserProfile", userProfile);

            getParentFragmentManager().setFragmentResult(AUTH_RESULT, bundle);

            disableSign(authTypeServer);
            updateUI(vkAccessToken.getEmail(), null, authTypeServer);

        } else {
            //accountInfo.clear();
            Log.v("Debug1", "AuthFragment handleVKSignInResult vkAccessToken == null");
            authTypeServer = TYPE_AUTH_NONE;
            updateUI("", null, authTypeServer);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.v("Debug1", "AuthFragment onViewCreated");

        /*view.findViewById(R.id.sign_in_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN_GOOGLE);
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v("Debug1", "AuthFragment onStart");

        if (getArguments() != null) {
            authTypeServer = getArguments().getInt(ARG, 0);
            Log.v("Debug1", "AuthFragment onStart authTypeServer=" + authTypeServer);
            switch (authTypeServer) {
                case TYPE_AUTH_GOOGLE:
                    // Проверим, входил ли пользователь в это приложение через Google
                    if (getContext() != null) {
                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
                        if (account != null) {
                            // Пользователь уже входил, сделаем кнопку недоступной
                            disableSign(authTypeServer);
                            // Обновим почтовый адрес этого пользователя и выведем его на экран
                            String imageURL = null;
                            if (account.getPhotoUrl() != null)
                                imageURL = account.getPhotoUrl().toString();
                            updateUI(account.getEmail(), imageURL, authTypeServer);

                        }
                    }
                    break;
                case TYPE_AUTH_VK:
                    if (VK.isLoggedIn()) {
                        //VK.ge
                        //updateUI(account.getEmail(), imageURL, authTypeServer);
                        Log.v("Debug1", "AuthFragment onStart authTypeServer=" + authTypeServer + ", VK.isLoggedIn()=" + VK.isLoggedIn());
                    }
                    break;
                default:
                    enableSign();
                    updateUI("", null, authTypeServer);
                    break;
            }
        } else {
            Log.v("Debug1", "AuthFragment onStart getArguments() == null");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "AuthFragment onAttach");
    }

    @Override
    public void onDetach() {
        //navigation = null;
        Log.v("Debug1", "AuthFragment onDetach");
        super.onDetach();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("Debug1", "AuthFragment onActivityResult");
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            // Когда сюда возвращается Task, результаты аутентификации уже готовы
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResultGoogle(task);
        } /*else if (requestCode == RC_VK_SIGN_IN) {
            VKAuthCallback vkAuthCallback = new VKAuthCallback() {
                @Override
                public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                    handleSignInResultVK(vkAccessToken);
                }

                @Override
                public void onLoginFailed(int i) {
                }
            };
            VK.onActivityResult(requestCode, resultCode, data, vkAuthCallback);
        }*/
    }


}
