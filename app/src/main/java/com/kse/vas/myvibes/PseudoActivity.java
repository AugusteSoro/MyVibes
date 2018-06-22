package com.kse.vas.myvibes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class PseudoActivity extends AppCompatActivity {


    //SharedPreferences
    private static final String PREFS = "PREFS";
    private static final String PREFS_PSEUDO = "PREFS_PSEUDO";
    SharedPreferences sharedPreferences;

    public static final int RESULT_LOAD_IMAGE = 1;
    ImageView ivAvatar;
    FloatingActionButton fab;
    EditText etPseudo;
    String pseudo;

    //byte[] Byte;
    String photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pseudo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivAvatar = (ImageView)findViewById(R.id.ivAvatar);
        etPseudo = (EditText)findViewById(R.id.etPseudo);

        final Intent intent = new Intent(PseudoActivity.this,ServiceActivity.class);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeryIntent =  new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(galeryIntent, RESULT_LOAD_IMAGE);
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verifier si le champ pseudo est vide
                boolean cancel = false;
                View focusView = null;

                pseudo = etPseudo.getText().toString();
                if (TextUtils.isEmpty(pseudo)) {
                    etPseudo.setError(getString(R.string.error_field_required));
                    focusView = etPseudo;
                    cancel = true;
                }else{

                    //SharedPreferences
                    sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                    sharedPreferences
                            .edit()
                            .putString(PREFS_PSEUDO, pseudo)
                            .apply();
                    Toast.makeText(PseudoActivity.this, "Pseudo enregistré", Toast.LENGTH_SHORT).show();

                    intent.putExtra("photo",photo);
                    startActivity(intent);
                    finish();
                }
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Log.i("SelectedImage", selectedImage.toString());

            ivAvatar.setImageURI(selectedImage);
            photo = selectedImage.toString();


        }
    }



/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Log.i("SelectedImage", selectedImage.toString());

            //ivAvatar.setImageURI(selectedImage);

            //Convertir URI en Byte
            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Byte = getBytes(iStream);
                Log.i("Byte", String.valueOf(Byte));


            } catch (IOException e) {
                e.printStackTrace();
            }

            setImageViewWithByteArray(ivAvatar,Byte);
        }
}


    //Afficher une image à partir d'un tableau de byte


    public static void setImageViewWithByteArray(ImageView view, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        view.setImageBitmap(bitmap);
    }

    //Conversion URI en Byte
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
        */




}
