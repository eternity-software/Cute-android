package ru.etysoft.cute.activities.fragments.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import ru.etysoft.cute.R;

public class ExploreFragment extends Fragment {

    private ExploreViewModel dashboardViewModel;

    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(ExploreViewModel.class);
        View root = inflater.inflate(R.layout.activity_explore, container, false);

        return root;
    }
}