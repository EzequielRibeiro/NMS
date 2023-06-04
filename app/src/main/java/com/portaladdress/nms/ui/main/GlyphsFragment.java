package com.portaladdress.nms.ui.main;
import static com.portaladdress.nms.MainActivity.showInterstitial;
import static com.portaladdress.nms.PermissionCheck.checkPermission;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.portaladdress.nms.Glyphs;
import com.portaladdress.nms.PermissionCheck;
import com.portaladdress.nms.R;
import com.portaladdress.nms.SaveImageGlyphs;

import java.io.IOException;

public class GlyphsFragment extends Fragment implements View.OnClickListener {

    private static int count = 0;
    private GlyphsViewModel mViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private LinearLayout linearLayout;
    private ImageButton imageButton0,imageButton1,imageButton2,imageButton3,imageButton4,imageButton5,imageButton6,imageButton7,imageButton8,imageButton9,
    imageButtonA,imageButtonB,imageButtonC,imageButtonD,imageButtonE,imageButtonF;
    private Button buttonResetGlyphs,buttonShareGlyphs,buttonSaveGlyphs;
    private TextView textViewGlyphsCode, textViewGlyphsAddress;
    LinearLayout.LayoutParams layoutParams ;

    public static GlyphsFragment newInstance(int index) {
        GlyphsFragment glyphsFragment = new GlyphsFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        glyphsFragment.setArguments(bundle);
        return glyphsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.glyphs_fragment, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutGlyphs);
        textViewGlyphsCode = view.findViewById(R.id.textViewGlyphsCode);
        buttonResetGlyphs = view.findViewById(R.id.buttonResetGlyphs);
        buttonShareGlyphs = view.findViewById(R.id.buttonShareGlyphs);
        buttonSaveGlyphs  = view.findViewById(R.id.buttonSaveGlyphs);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1.0f;
        buttonResetGlyphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.removeAllViews();
                linearLayout.destroyDrawingCache();
                textViewGlyphsCode.setText("");
                textViewGlyphsAddress.setText("");
                count = 0;
            }
        });

        buttonShareGlyphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(getActivity());
                try {
                    if(textViewGlyphsCode.length() == 12)
                     if(PermissionCheck.checkPermission(getActivity()))
                          new SaveImageGlyphs().getPrint(linearLayout,getActivity());
                     else PermissionCheck.requestPermission(getActivity());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonSaveGlyphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textViewGlyphsCode.length() == 12) {
                    showDialog();
                }
            }
        });

        textViewGlyphsAddress = view.findViewById(R.id.textViewGlyphsAddress);
        imageButton0 = view.findViewById(R.id.imageButton0);
        imageButton0.setOnClickListener(this);
        imageButton1 = view.findViewById(R.id.imageButton1);
        imageButton1.setOnClickListener(this);
        imageButton2 = view.findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(this);
        imageButton3 = view.findViewById(R.id.imageButton3);
        imageButton3.setOnClickListener(this);
        imageButton4 = view.findViewById(R.id.imageButton4);
        imageButton4.setOnClickListener(this);
        imageButton5 = view.findViewById(R.id.imageButton5);
        imageButton5.setOnClickListener(this);
        imageButton6 = view.findViewById(R.id.imageButton6);
        imageButton6.setOnClickListener(this);
        imageButton7 = view.findViewById(R.id.imageButton7);
        imageButton7.setOnClickListener(this);
        imageButton8 = view.findViewById(R.id.imageButton8);
        imageButton8.setOnClickListener(this);
        imageButton9 = view.findViewById(R.id.imageButton9);
        imageButton9.setOnClickListener(this);
        imageButtonA = view.findViewById(R.id.imageButtonA);
        imageButtonA.setOnClickListener(this);
        imageButtonB = view.findViewById(R.id.imageButtonB);
        imageButtonB.setOnClickListener(this);
        imageButtonC = view.findViewById(R.id.imageButtonC);
        imageButtonC.setOnClickListener(this);
        imageButtonD = view.findViewById(R.id.imageButtonD);
        imageButtonD.setOnClickListener(this);
        imageButtonE = view.findViewById(R.id.imageButtonE);
        imageButtonE.setOnClickListener(this);
        imageButtonF = view.findViewById(R.id.imageButtonF);
        imageButtonF.setOnClickListener(this);

        textViewGlyphsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 12) {
                    Glyphs glyphs = new Glyphs();
                    String result = glyphs.getHexCoords(String.valueOf(s));
                    textViewGlyphsAddress.setText(result);
                    showInterstitial(getActivity());
        
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GlyphsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showDialog() {
        DialogFragment newFragment = EncoderFragment.DialogSaveGlyphs.newInstance("Save glyphs to the Portal",textViewGlyphsCode.getText().toString());
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void onClick(View v) {

        if(count == 12){
            return;
        }

        if(count == 11){
           showInterstitial(getActivity());
        }

        count++;
        ImageView imageView;


        switch (v.getTag().toString().charAt(0)) {
            case '0':
                //nameGlyphs += "sunset";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.zero_sunset));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '1':
                //nameGlyphs += "bird";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.um_bird));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '2':
                //nameGlyphs += "face";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.dois_face));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '3':
                //nameGlyphs += "diplo";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.tres_diplo));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '4':
                //nameGlyphs += "eclipse";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.quatro_eclipse));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '5':
                //nameGlyphs += "balloon";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.cinco_ballon));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '6':
                //nameGlyphs += "boat";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.seis_boat));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '7':
                //nameGlyphs += "bug";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.sete_bug));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '8':
                //nameGlyphs += "dragonfly";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.oito_dragonfly));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case '9':
                //nameGlyphs += "galaxy";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.nove_galaxy));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case 'A':
                //nameGlyphs += "voxel";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.a_voxel));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case 'B':
                //nameGlyphs += "fish";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.b_fish));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case 'C':
                //nameGlyphs += "tent";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.c_tent));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case 'D':
                //nameGlyphs += "rocket";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.d_rocket));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case 'E':
                //nameGlyphs += "tree";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.e_tree));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
            case 'F':
                //nameGlyphs += "atlas";
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.f_atlas));
                linearLayout.addView(imageView);
                textViewGlyphsCode.setText(textViewGlyphsCode.getText().toString().concat(v.getTag().toString()));
                break;
        }


    }
}
