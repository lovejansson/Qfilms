package com.example.qfilm.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.qfilm.MainApplication;
import com.example.qfilm.R;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.databinding.ActivitySettingsBinding;
import com.example.qfilm.ui.utils.InputValidator;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.SettingsManager;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.example.qfilm.ui.utils.UiUtil.hideKeyboard;


/**
 * I chose to use Activity for the settings functionality so I could recreate the activity to show the
 * user when theme or language changes immediately.
 **/


public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsFragment";

    private String currentLanguage;

    private SharedPreferences sharedPreferences;

    private ActivitySettingsBinding binding;

    @Inject
    public FirebaseAuth firebaseAuth;

    private TextWatcher textWatcher;

    private FirebaseAuthViewModel firebaseAuthViewModel;

    @Inject
    public MyViewModelFactory myViewModelFactory;

    // adjust configuration according to the language the user has selected
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SettingsManager.setAppLanguage(newBase));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ((MainApplication)getApplication()).getApplicationComponent().inject(this);

        SettingsManager.setTheme(this);

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_settings, null, false);


        // the user can change display name
        if(firebaseAuth.getCurrentUser() !=  null){

            firebaseAuthViewModel = new ViewModelProvider(this, myViewModelFactory).get(FirebaseAuthViewModel.class);
            binding.setUser(firebaseAuth.getCurrentUser());
        }


        // set theme and currentLanguage according to preferences

        sharedPreferences = getSharedPreferences("com.example.qfilm", MODE_PRIVATE);

        currentLanguage = sharedPreferences.getString("language", "English");

        setupSettingControls();

        setupListeners();

        setContentView(binding.getRoot());

    }


    private void setupSettingControls() {

        // switch original titles

        binding.switchOriginalTitles.setChecked(sharedPreferences.getBoolean("titles", false));

        // switch theme

        if(sharedPreferences.getInt("theme", R.style.Theme_Qfilm_AppTheme_Dark) == R.style.Theme_Qfilm_AppTheme_Dark){

            binding.switchDarkMode.setChecked(true);

        }else{

            binding.switchDarkMode.setChecked(false);
        }

        // change app language

        setupLanguagesSpinner();

        // change username if signed in

        if(firebaseAuth.getCurrentUser() != null) {

            textWatcher = UiUtil.createAuthenticationInputTextWatcher(binding.layoutEtUsername, binding.etUsername);

            binding.ivEditUsername.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    binding.vsUsername.showNext();

                    binding.vsEditIcons.showNext();

                }
            });


            binding.ivEditUsernameExit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    binding.etUsername.getText().clear();

                    binding.vsUsername.showNext();

                    binding.vsEditIcons.showNext();

                    binding.layoutEtUsername.setError(null);

                    hideKeyboard(view);
                }
            });


            binding.ivEditUsernameSave.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    UiUtil.hideKeyboard(view);

                    if (InputValidator.isValidUsername(getBaseContext(), binding.layoutEtUsername, binding.etUsername)) {

                        firebaseAuthViewModel.upDateUsername(binding.etUsername.getText().toString());

                        binding.etUsername.getText().clear();

                        binding.vsUsername.showNext();

                        binding.vsEditIcons.showNext();



                    } else {

                        binding.etUsername.addTextChangedListener(textWatcher);
                    }
                }
            });

            observeFireStoreEdits();

        }

    }


    private void observeFireStoreEdits() {

        firebaseAuthViewModel.getAuthOperation().observe(this, new Observer<DataResource<FirebaseAuthViewModel.AuthOperation>>() {
            @Override
            public void onChanged(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {

                if(authOperationDataResource.getStatus() == DataResource.Status.SUCCESS){

                    binding.setUser(firebaseAuth.getCurrentUser());

                    binding.executePendingBindings();

                }else{

                    if (UiUtil.isConnectedToInternet(getBaseContext())) {

                        UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_unknown_error),
                                getCurrentFocus(), 4000);
                    } else {

                        UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_offline), getCurrentFocus(), 4000);
                    }
                }

            }
        });
    }


    private void setupLanguagesSpinner(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.app_languages, R.layout.spinner_languages);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerLanguages.setAdapter(adapter);

        List<String> languages = Arrays.asList(getResources().getStringArray(R.array.app_languages));

        binding.spinnerLanguages.setSelection(languages.indexOf(currentLanguage));

    }


    private void setupListeners() {

        binding.switchOriginalTitles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton _switch, boolean checked) {

                SharedPreferences.Editor editor = (getSharedPreferences("com.example.qfilm", MODE_PRIVATE)).edit();

                if(checked){
                    editor.putBoolean("titles", true);

                }else{

                    editor.putBoolean("titles", false);

                }

                editor.apply();
            }
        });


        binding.switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton _switch, boolean checked) {

                SharedPreferences.Editor editor = (getSharedPreferences("com.example.qfilm", MODE_PRIVATE)).edit();

                if(checked){

                    editor.putInt("theme", R.style.Theme_Qfilm_AppTheme_Dark);


                }else{

                    editor.putInt("theme", R.style.Theme_Qfilm_AppTheme_Light);

                }

                editor.apply();

            }
        });


        binding.spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String language = (String) adapterView.getItemAtPosition(i);

                SharedPreferences.Editor editor = (getSharedPreferences("com.example.qfilm", MODE_PRIVATE)).edit();

                editor.putString("language", language);

                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.btnNavigateBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onBackPressed();
            }

        });


        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignOut();
            }
        });

    }


    private void SignOut() {

        String providerId = firebaseAuth.getCurrentUser().getProviderData().get(1).getProviderId();

        if(providerId.equals(FacebookAuthProvider.PROVIDER_ID)){

            LoginManager.getInstance().logOut();

        }

        firebaseAuth.signOut();

        onBackPressed();

    }



    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);

    }


    @Override
    protected void onPause() {

        super.onPause();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onResume() {

        super.onResume();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // restart activity to show changes

        if(key.equals("theme") || key.equals("language")){

            startActivity(getIntent());

            finish();

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
    }

}