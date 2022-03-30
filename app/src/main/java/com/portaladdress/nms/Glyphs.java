package com.portaladdress.nms;

import android.util.Log;
import static java.lang.Integer.parseInt;

public class Glyphs {

    final int bigMod =     0x1000;
    final int smallMod =   0x100;
    private String glyphsCode;
    private String comments;
    private int id;

    private String shiftModUpFill(String val, int shift, int mod) {
        final int i = ((parseInt(val, 16) + shift) % mod);

        return Integer.toHexString(i);

    }

    public void setGlyphsCode(String glyphsCode){
        this.glyphsCode = glyphsCode;
    }

    public String getGlyphsCode(){
        return glyphsCode;
    }

    public void setComments(String comments){
        this.comments = comments;
    }

    public String getComments(){
        return comments;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    private String[] coordsFormated(String coords) {
        // String blocks = coords.matches("/[0-9a-fA-F]{4}/g");

        String isHex = coords;
        if (!isHex.replace(":", "").matches("^[0-9A-Fa-f]+$")) {
            return new String[]{"Incorrect format. Only hexa decimal allowed."};
        }

        if (coords.length() != 19) {
            return new String[]{"Incorrect length. Coordinates must have a length of 16 or 19."};
        }

        return coords.split(":");
    }

    private String glyphsToWords(String coord) {
        String nameGlyphs = " ";

        for(int i = 0; i < coord.length();i++){

            switch (coord.charAt(i)) {
                case '0':
                    nameGlyphs += "sunset";
                    break;
                case '1':
                    nameGlyphs += "bird";
                    break;
                case '2':
                    nameGlyphs += "face";
                    break;
                case '3':
                    nameGlyphs += "diplo";
                    break;
                case '4':
                    nameGlyphs += "eclipse";
                    break;
                case '5':
                    nameGlyphs += "balloon";
                    break;
                case '6':
                    nameGlyphs += "boat";
                    break;
                case '7':
                    nameGlyphs += "bug";
                    break;
                case '8':
                    nameGlyphs += "dragonfly";
                    break;
                case '9':
                    nameGlyphs += "galaxy";
                    break;
                case 'A':
                    nameGlyphs += "voxel";
                    break;
                case 'B':
                    nameGlyphs += "fish";
                    break;
                case 'C':
                    nameGlyphs += "tent";
                    break;
                case 'D':
                    nameGlyphs += "rocket";
                    break;
                case 'E':
                    nameGlyphs += "tree";
                    break;
                case 'F':
                    nameGlyphs += "atlas";
                    break;
            }
            nameGlyphs += ",";
        }

        return nameGlyphs.substring(0,nameGlyphs.length()-1).toUpperCase();

    }


    public String getCoordsDetails(String coordinates) {

        String[] coords = coordsFormated(coordinates);
        String x="",y="",p="",s="",z="";

        if (coords[0].contains("Incorrect")) {
            return coords[0];
        }

        int bigShift =   0x801;
        int smallShift = 0x81;

        try {
            x = coords[0];
            y = coords[1];
            z = coords[2];
            p = coords[3].substring(0, 1);
            s = coords[3].substring(1, 4);
        }catch (StringIndexOutOfBoundsException e){
            Log.e("NMS",e.getMessage());
            return "";
        }

        String a = p.toUpperCase();
        String b = s.toUpperCase();
        String c = shiftModUpFill(y, smallShift, smallMod);
        if(c.length() < 2) c = "0"+ c;
        String d = shiftModUpFill(z, bigShift, bigMod);
        String e = shiftModUpFill(x, bigShift, bigMod);
        coordinates = a + b + c + d + e;
        coordinates =  coordinates.toUpperCase();

        return coordinates.toUpperCase();
    }

    public String getHexCoords(String coords) throws NumberFormatException{

        final int hexConstBig   = 0x7FF;
        final int hexConstSmall = 0x7F;
        String addressHex = "0000:0000:0000:0000";

        if(coords.length() == 12){

            addressHex = "0";
            addressHex += shiftModUpFill(new StringBuilder().append(coords.charAt(9)).append(coords.charAt(10)).append(coords.charAt(11)).toString()
                    ,hexConstBig,bigMod).concat(":00");
            addressHex += shiftModUpFill(new StringBuilder().append(coords.charAt(4)).append(coords.charAt(5)).toString()
                    ,hexConstSmall,smallMod).concat(":0");
            addressHex += shiftModUpFill( new StringBuilder().append(coords.charAt(6)).append(coords.charAt(7)).append(coords.charAt(8)).toString(),hexConstBig,bigMod).concat(":");
            addressHex += new StringBuilder().append("0").append(coords.charAt(1)).append(coords.charAt(2)).
                    append(coords.charAt(3)).toString();

        }

        return addressHex.toUpperCase();

    }

}
