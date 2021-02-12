package com.portaladdress.nms.ui.main;

import android.text.TextWatcher;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.snackbar.Snackbar;
import com.portaladdress.nms.Glyphs;
import com.portaladdress.nms.R;
import com.portaladdress.nms.SaveImageGlyphs;

import java.io.IOException;
import java.util.Arrays;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String MASK = "####:####:####:####";
    private EditText textView18,textView20;
    private Button resetButton,buttonShareGlyphs;
    private View root;
    private static boolean isUpdating, editTextBox1,editTextBox2,toUpperCase = true;
    private static String old = "";
    private LinearLayout linearLayoutGlyphsMain;
    private AdView mAdView;


    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        // final TextView textView = root.findViewById(R.id.section_label);
        linearLayoutGlyphsMain = root.findViewById(R.id.linearLayoutGlyphsMain);
        resetButton = root.findViewById(R.id.buttonResetGlyphs);
        textView18 = root.findViewById(R.id.textViewGlyphsCode);
        textView20 = root.findViewById(R.id.textViewGlyphsAddress);
        buttonShareGlyphs = root.findViewById(R.id.buttonShareGlyphs);
        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("DB530A1BBBDBFE8567328113528A19EF")).build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        mAdView.loadAd(adRequest);

        buttonShareGlyphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(linearLayoutGlyphsMain.getChildCount() == 12)
                        new SaveImageGlyphs().getPrint(linearLayoutGlyphsMain,getActivity());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        textView18.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextBox1 = false;
                editTextBox2 = true;
                return false;
            }
        });
        textView20.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextBox1 = true;
                editTextBox2 = false;
                return false;
            }
        });


        textView20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() <= 19){
                    applyMask(textView20,s);
                }

                if (s.length() == 19 && editTextBox1) {
                    setCoordinates(String.valueOf(s));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textView18.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 12 && editTextBox2) {
                    Glyphs glyphs = new Glyphs();
                    String result = glyphs.getHexCoords(String.valueOf(s));
                    if (!textView20.getText().toString().equals(result))
                          textView20.setText(result);
                    getGlyphs(s);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String t = s.toString();
                if(!t.equals(s.toString().toUpperCase()))
                {
                    t = s.toString().toUpperCase();
                    textView18.setText(t);
                    textView18.setSelection(textView18.length());
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView18.setText("");
                textView20.setText("");
                linearLayoutGlyphsMain.removeAllViews();
                linearLayoutGlyphsMain.destroyDrawingCache();

            }
        });

        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // textView.setText(s);
            }
        });
        return root;
    }

   public static void applyMask(View v, CharSequence s){
       final String str = unmask(s.toString());
       String mascara = "";
       if (isUpdating) {
           old = str;
           isUpdating = false;
           return;
       }
       int i = 0;
       for (final char m : MASK.toCharArray()) {
           if (m != '#' && str.length() > old.length()) {
               mascara += m;
               continue;
           }
           try {
               mascara += str.charAt(i);
           } catch (final Exception e) {
               break;
           }
           i++;
       }
       isUpdating = true;
       EditText editText = (EditText) v;
       editText.setText(mascara.toUpperCase());
       editText.setSelection(mascara.length());
   }


    private static String unmask(final String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[:]", "").replaceAll("[)]", "");
    }

    private void setCoordinates(String coordinates) {

        String result;
        Glyphs glyphs = new Glyphs();
        result = glyphs.getCoordsDetails(coordinates);
        getGlyphs(result);
        if (result.contains("Incorrect")) {
            Snackbar.make(root, result, Snackbar.LENGTH_LONG)
                    .setAction(result, null).show();

        } else {

            if (!textView18.getText().toString().equals(result))
                  textView18.setText(result);

        }


    }

    public void getGlyphs(CharSequence charSequence) {

        ImageView imageView;

        if(linearLayoutGlyphsMain.getChildCount() > 0) {
            linearLayoutGlyphsMain.removeAllViews();
            linearLayoutGlyphsMain.destroyDrawingCache();
        }

        for (int i = 0 ; i < charSequence.length() ; i++) {

            switch (charSequence.charAt(i)) {
                case '0':
                    //nameGlyphs += "sunset";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.zero_sunset));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '1':
                    //nameGlyphs += "bird";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.um_bird));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '2':
                    //nameGlyphs += "face";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.dois_face));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '3':
                    //nameGlyphs += "diplo";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.tres_diplo));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '4':
                    //nameGlyphs += "eclipse";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.quatro_eclipse));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '5':
                    //nameGlyphs += "balloon";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.cinco_ballon));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '6':
                    //nameGlyphs += "boat";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.seis_boat));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '7':
                    //nameGlyphs += "bug";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.sete_bug));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '8':
                    //nameGlyphs += "dragonfly";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.oito_dragonfly));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case '9':
                    //nameGlyphs += "galaxy";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.nove_galaxy));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case 'A':
                    //nameGlyphs += "voxel";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.a_voxel));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case 'B':
                    //nameGlyphs += "fish";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.b_fish));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case 'C':
                    //nameGlyphs += "tent";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.c_tent));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case 'D':
                    //nameGlyphs += "rocket";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.d_rocket));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case 'E':
                    //nameGlyphs += "tree";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.e_tree));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
                case 'F':
                    //nameGlyphs += "atlas";
                    imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(45, 45));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.f_atlas));
                    linearLayoutGlyphsMain.addView(imageView);

                    break;
            }


        }
    }


}