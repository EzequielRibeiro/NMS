package com.portaladdress.nms.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.portaladdress.nms.DBAdapter;
import com.portaladdress.nms.GlyphsAdaptador;
import com.portaladdress.nms.R;

import static com.portaladdress.nms.ui.main.EncoderFragment.glyphsAdaptador;
import static com.portaladdress.nms.ui.main.EncoderFragment.glyphsArrayList;

public class SaveFragment extends Fragment {

    private static SaveViewModel mViewModel;
    static public ListView listView;
    private static final String ARG_SECTION_NUMBER = "section_number";


    public static SaveFragment newInstance(int index, Context context) {

        SaveFragment portalFragment = new SaveFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        portalFragment.setArguments(bundle);
        return new SaveFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.save_fragment, container, false);
        listView = v.findViewById(R.id.listViewGlyphs);

        DBAdapter dbAdapter = new DBAdapter(getActivity());
        glyphsArrayList = dbAdapter.getAllValuesGlyphs();
        glyphsAdaptador = new GlyphsAdaptador(getActivity(),glyphsArrayList);
        listView.setAdapter(glyphsAdaptador);
        dbAdapter.close();

        return v ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SaveViewModel.class);
        if(listView.getCount() == 0)
            Toast.makeText(getActivity(), "No saved coordinates", Toast.LENGTH_SHORT).show();

    }


}