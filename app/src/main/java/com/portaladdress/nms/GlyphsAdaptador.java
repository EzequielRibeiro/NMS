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
import com.portaladdress.nms.ui.main.EncoderFragment;

import java.io.IOException;
import java.util.List;

import static com.portaladdress.nms.PermissionCheck.checkPermission;
import static com.portaladdress.nms.ui.main.EncoderFragment.getGlyphs;


/**
 * Created by Ezequiel on 27/04/2016.
 */
public class GlyphsAdaptador extends ArrayAdapter<Glyphs> implements View.OnClickListener {

    private Context context;
    private Glyphs glyphs;
    private List<Glyphs> glyphsArrayList;
    private ListContent listContent;

    public GlyphsAdaptador(Context context, List<Glyphs> glyphsArrayList) {
        super(context, 0, glyphsArrayList);
        this.context = context;
        this.glyphsArrayList = glyphsArrayList;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        glyphs = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_glyphs, parent, false);
        }

        listContent = new ListContent();


        listContent.textViewCodeList = convertView.findViewById(R.id.textViewCodeList);
        listContent.textViewCodeList.setText(glyphs.getGlyphsCode());

        listContent.textViewComments = convertView.findViewById(R.id.textViewComments);
        listContent.textViewComments.setText(glyphs.getComments());

        listContent.linearLayoutListGlyphs = convertView.findViewById(R.id.linearLayoutListGlyphs);

        getGlyphs(glyphs.getGlyphsCode(), listContent.linearLayoutListGlyphs, context);

        listContent.buttonDelete = convertView.findViewById(R.id.buttonDelete);
        listContent.buttonDelete.setTag(position);
        listContent.buttonDelete.setOnClickListener(this);
        listContent.buttonView = convertView.findViewById(R.id.buttonView);
        listContent.buttonView.setTag(glyphs.getGlyphsCode());
        listContent.buttonView.setOnClickListener(this);

        return convertView;
    }

    @Override
    public int getCount() {
        return glyphsArrayList.size();
    }

    @Override
    public Glyphs getItem(int position) {
        return glyphsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return glyphsArrayList.indexOf(getItem(position));
    }


    private void buildShowDialodDelete(Object object) {

        final int position = Integer.parseInt(String.valueOf(object));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Delete Glyph");

        builder.setMessage("Glyph will be permanently deleted !");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBAdapter dbAdapter = new DBAdapter(context);
                Glyphs glyphs1 = getItem(position);

                if (dbAdapter.deleteGlyphs(glyphs1.getId())) {
                    glyphsArrayList.remove(position);
                    EncoderFragment.glyphsAdaptador.notifyDataSetChanged();
                    dbAdapter.close();
                }
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
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

        switch (button.getId()) {

            case R.id.buttonDelete:
                buildShowDialodDelete(button.getTag());
                break;
            case R.id.buttonView:

                View parentRow = (View) v.getParent();
                LinearLayout linearLayout = parentRow.findViewById(R.id.linearLayoutListGlyphs);
                Activity activity = (Activity) context;
                checkPermission(activity);
                try {
                    new SaveImageGlyphs().getPrint(linearLayout, context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }


    }

    private class ListContent {
        LinearLayout  linearLayoutListGlyphs;
        Button buttonView, buttonDelete;
        TextView textViewCodeList,textViewComments;

    }

}
