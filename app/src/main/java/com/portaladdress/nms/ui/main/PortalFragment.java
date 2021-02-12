package com.portaladdress.nms.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.portaladdress.nms.R;

public class PortalFragment extends Fragment {

    private PortalViewModel mViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static PortalFragment newInstance(int index) {
        PortalFragment portalFragment = new PortalFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        portalFragment.setArguments(bundle);
        return new PortalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.portal_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PortalViewModel.class);
        // TODO: Use the ViewModel
    }

}