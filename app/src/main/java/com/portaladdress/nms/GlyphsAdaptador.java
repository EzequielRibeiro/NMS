package com.portaladdress.nms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.portaladdress.nms.ui.main.SaveFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.portaladdress.nms.ui.main.PlaceholderFragment.getGlyphs;


/**
 * Created by Ezequiel on 27/04/2016.
 */
public class GlyphsAdaptador extends ArrayAdapter<Glyphs>  implements View.OnClickListener {

    private Context context;
    private Glyphs glyphs;
    private int position;
    private LinearLayout linearLayoutGlyphs;


    public GlyphsAdaptador(Context context, List<Glyphs> glyphsArrayList) {
        super(context, 0, glyphsArrayList);
        this.context = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

         glyphs = getItem(position);
         this.position = position;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_glyphs, parent, false);
        }

        TextView textViewComments = convertView.findViewById(R.id.textViewComments);
        textViewComments.setText(glyphs.getComments());

        linearLayoutGlyphs = convertView.findViewById(R.id.linearLayoutListGlyphs);

        getGlyphs(glyphs.getGlyphsCode(),linearLayoutGlyphs,context);

        Button       buttonDelete       = convertView.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);
        Button       buttonView       = convertView.findViewById(R.id.buttonView);
        buttonView.setOnClickListener(this);

        return convertView;
    }


    private void buildShowDialodDelete() {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Exluir anotação");

                builder.setMessage("A anotação será exluída definitivamente");

                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBAdapter dbAdapter = new DBAdapter(context);

                        if(dbAdapter.deleteGlyphs(glyphs.getId())){
                            SaveFragment.glyphsArrayList.remove(position);
                            SaveFragment.glyphsAdaptador.notifyDataSetChanged();
                            dbAdapter.close();
                        }


                        Toast.makeText(context, "Removido", Toast.LENGTH_SHORT).show();


                      //  ActivityAnotacao.notas.remove(position);
                      //  ActivityAnotacao.notaAdaptador.notifyDataSetChanged();
                      //  DBAdapterFavoritoNota dbAdapterFavoritoNota = new DBAdapterFavoritoNota(getContext());
                      //  dbAdapterFavoritoNota.open();
                      //  dbAdapterFavoritoNota.deleteNota(Long.valueOf(nota.getId()));
                      //  dbAdapterFavoritoNota.close();
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
    }



    @Override
    public void onClick(View v) {

        Button button = (Button) v;

        switch (button.getId()){

            case R.id.buttonDelete:
                 buildShowDialodDelete();
                    break;
            case  R.id.buttonView:
                try {
                    new SaveImageGlyphs().getPrint(linearLayoutGlyphs,context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }


    }

}
