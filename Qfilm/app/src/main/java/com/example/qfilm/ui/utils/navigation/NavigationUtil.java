package com.example.qfilm.ui.utils.navigation;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.qfilm.R;
import com.example.qfilm.ui.fragments.ProfileFragment;
import com.example.qfilm.ui.fragments.SearchFragment;
import com.example.qfilm.ui.fragments.SignInFragment;

import java.util.ArrayList;
import java.util.List;


/**
 *
* this class has mainly been implemented to be able to create a back stack where i can remove
* duplicate entries. according to my knowledge this it's not possible with built in back stack
* but if i'm wrong you can tell me please.
 *
**/

public class NavigationUtil {

    private static final String TAG = "NavigationUtil";

    // stored in the back stack and reversed if popped of the back stack.
    static class MyFragmentTransaction{

        Fragment currentFragment;

        TransactionType currentTransaction;

        Fragment previousFragment;

        TransactionType previousTransaction;

        int popEnterAnim;

        int popExitAnim;

        public MyFragmentTransaction(Fragment previousFragment, TransactionType previousTransaction,
                                      Fragment currentFragment, TransactionType currentTransaction,
                                      int popEnterAnim, int popExitAnim) {

            this.currentFragment = currentFragment;
            this.previousFragment = previousFragment;
            this.currentTransaction = currentTransaction;
            this.previousTransaction = previousTransaction;
            this.popEnterAnim = popEnterAnim;
            this.popExitAnim = popExitAnim;
        }

        @Override
        public boolean equals(@Nullable Object obj) {

            if(obj == null ||  obj.getClass() != getClass()){
                return false;
            }

            MyFragmentTransaction other = (MyFragmentTransaction)obj;

            return currentFragment == other.currentFragment &&
                    (previousFragment == other.previousFragment || previousFragment == null || other.previousFragment == null);

        }
    }

    private enum TransactionType {
        REPLACE,
        ADD,
        DETACH,
        ATTACH,
        REMOVE
    }

    private FragmentManager fragmentManager;

    private List<MyFragmentTransaction> backStack;


    public NavigationUtil(FragmentManager fragmentManager, List<Class> hasBottomNav) {

        this.fragmentManager = fragmentManager;

        this.backStack = new ArrayList<>();

    }


    // for at first when no fragment is added yet
    public void addFragment(Fragment fragment, int enterAnim, int
                             exitAnim){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(enterAnim, exitAnim);

        fragmentTransaction.add(R.id.container_fragment, fragment);

        addToBackStack(new MyFragmentTransaction(null, null, fragment,
                TransactionType.ADD, 0, 0));

        fragmentTransaction.commit();

    }


    // in the beginning when for example search fragment is added for the first time
    public void detachAndAddFragments(Fragment fragmentAdd, int enterAnim,
                                      int exitAnim, int popEnterAnim, int popExitAnim){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);

        Fragment fragmentDetach = getLastEntryInBackStack();

        fragmentTransaction.detach(fragmentDetach);

        addToBackStack(new MyFragmentTransaction(fragmentDetach, TransactionType.DETACH,
                fragmentAdd, TransactionType.ADD, popEnterAnim, popExitAnim));

        fragmentTransaction.add(R.id.container_fragment, fragmentAdd);

        fragmentTransaction.commit();

    }


    // during application runtime bottom navigation fragments are detached and attached to save state
    public void detachAndAttachFragments(Fragment fragmentAttach, int enterAnim,
                                         int exitAnim, int popEnterAnim, int popExitAnim){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);

        Fragment fragmentDetach = getLastEntryInBackStack();

        fragmentTransaction.detach(fragmentDetach);

        fragmentTransaction.attach(fragmentAttach);

        addToBackStack(new MyFragmentTransaction(fragmentDetach, TransactionType.DETACH,
                fragmentAttach, TransactionType.ATTACH, popEnterAnim, popExitAnim));

        fragmentTransaction.commit();
    }


    // some fragments states are not needed and therefore just replaced
    public void replaceFragment(Fragment fragment, int enterAnim, int exitAnim, int popEnterAnim,
                                int popExitAnim){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);

        fragmentTransaction.replace(R.id.container_fragment, fragment);

        addToBackStack(new MyFragmentTransaction(getCurrentDisplayedFragment(), TransactionType.REPLACE,
                fragment, TransactionType.REPLACE, popEnterAnim, popExitAnim));

        fragmentTransaction.commit();

    }


    // so that MainActivity can change bottom nav menu tab according to shown fragment when navigating back
    public Fragment getLastEntryInBackStack(){

        return backStack.get(backStack.size() - 1).currentFragment;
    }


    public void toggleProfileAndSignInFragments(Fragment fragmentToDisplay){

        backStack.get(backStack.size() - 1).currentFragment = fragmentToDisplay;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container_fragment, fragmentToDisplay);

        fragmentTransaction.commit();

    }

    public boolean popBackStack(){

        /**
         * reversing transactions in backstack and removing last item if size > 1
         * **/

        if(backStack.size() == 1){
            return false;
        }

        MyFragmentTransaction myFragmentTransaction = backStack.get(backStack.size() - 1);

        backStack.remove(myFragmentTransaction);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(myFragmentTransaction.popEnterAnim, myFragmentTransaction.popExitAnim);

        switch(myFragmentTransaction.currentTransaction) {

            case REPLACE:

                if(!(myFragmentTransaction.previousFragment instanceof ProfileFragment)) {

                    fragmentTransaction.replace(R.id.container_fragment, myFragmentTransaction.previousFragment);

                    fragmentTransaction.commit();
                }

                return true;

            case ADD:

                // bottom nav fragments are added at first and should be detached not removed
                if (myFragmentTransaction.currentFragment instanceof ProfileFragment || myFragmentTransaction.currentFragment instanceof SignInFragment
                || myFragmentTransaction.currentFragment instanceof SearchFragment) {

                fragmentTransaction.detach(myFragmentTransaction.currentFragment);
                }

                else {

                fragmentTransaction.remove(myFragmentTransaction.currentFragment);
                }

                break;

            case ATTACH:

                fragmentTransaction.detach(myFragmentTransaction.currentFragment);

                break;
        }


        switch(myFragmentTransaction.previousTransaction) {

            case REMOVE:

                fragmentTransaction.add(R.id.container_fragment, myFragmentTransaction.previousFragment);


            case DETACH:

                fragmentTransaction.attach(myFragmentTransaction.previousFragment);

                break;
        }

        fragmentTransaction.commit();

        return true;

    }


    private Fragment getCurrentDisplayedFragment(){

        Fragment fragment = fragmentManager.findFragmentById(R.id.container_fragment);

        return fragment;
    }



    private void addToBackStack(MyFragmentTransaction myFragmentTransaction){

        /**
         *
         * Adds transaction to backstack and removes potential duplicate destination:
         *
         *  home -> search -> profile ->  adding: search (now the previous search fragment should be removed)
         *
         *  How backstack is affected:
         *
         * [prev: null, curr: home], [prev: home, curr: search], [prev: search, curr: profile] -> adding: [prev profile, curr search], should result in:
         *
         * [prev: null, curr: home], [prev: home, curr: profile], [prev: profile, curr: search] (previous navigation to search is gone)
         *
         *
         **/


        if(myFragmentTransaction.previousFragment != null){

        for(int i = 0; i < backStack.size(); ++i) {

            // check if current fragment already has been visited
            if(myFragmentTransaction.currentFragment.getClass() == backStack.get(i).currentFragment.getClass()){

                // adjust back stack entry so that the current fragment is not a duplicate and instead the same as next transaction's current fragment
                backStack.get(i).currentFragment = backStack.get(i + 1).currentFragment;

                // remove next transaction, not needed anymore
                backStack.remove(i + 1);

                break;
            }

        }}

        backStack.add(myFragmentTransaction);

    }


}
