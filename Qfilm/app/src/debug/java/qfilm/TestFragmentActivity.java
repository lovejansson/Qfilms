package qfilm;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.Video;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.ui.utils.SettingsManager;

public class TestFragmentActivity extends AppCompatActivity implements NavigationInterface {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SettingsManager.setAppLanguage(newBase));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void startFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.container_fragment, fragment);

        fragmentTransaction.commit();
    }

    /*
    *
    * fragments expects the navigation implementation in MainActivity
    *
    * */
    @Override
    public void navigateToDetailPageFragment(Result result) {

    }


    @Override
    public void navigateToTrailerFragment(Video video) {

    }


    @Override
    public void navigateToCreateAccountFragment() {

    }


    @Override
    public void navigateToSignInEmailFragment() {

    }


    @Override
    public void navigateToResetPasswordFragment() {

    }


    @Override
    public void navigateToUserFragment() {

    }


    @Override
    public void navigateToSettingsActivity() {

    }


    @Override
    public void navigateToCollectionFragment(Collection collection) {

    }

}
