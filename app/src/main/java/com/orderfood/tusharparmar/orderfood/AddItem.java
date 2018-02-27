package com.orderfood.tusharparmar.orderfood;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class AddItem extends AppCompatActivity {

    ImageButton imageButton;
    private static final int GALLREQ = 1;
    private EditText name, desc, price;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        name = findViewById(R.id.foodItemName);
        desc = findViewById(R.id.foodItemDesc);
        price = findViewById(R.id.foodItemPrice);

        storageReference = FirebaseStorage.getInstance().getReference("MenuItems");
        mRef = FirebaseDatabase.getInstance().getReference("MenuItems");

    }

    public void imageButtonOnClick(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("Image/*");
        startActivityForResult(galleryIntent,GALLREQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLREQ && resultCode == RESULT_OK)
        {
            uri = data.getData();
            imageButton = findViewById(R.id.foodImageButton);
            imageButton.setImageURI(uri);
        }
    }

    public void foodItemButtonOnClick(View view) {
        final String sName = name.getText().toString().trim();
        final String sDesc = desc.getText().toString().trim();
        final String sPrice = price.getText().toString().trim();

        if(!TextUtils.isEmpty(sName) && !TextUtils.isEmpty(sDesc) && !TextUtils.isEmpty(sPrice))
        {
            StorageReference filePath = storageReference.child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newItem = mRef.push();
                    newItem.child("name").setValue(sName);
                    newItem.child("desc").setValue(sDesc);
                    newItem.child("price").setValue(sPrice);
                    newItem.child("imageURL").setValue(downloadUrl.toString());
                    Toast.makeText(AddItem.this,"Menu item saved.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
