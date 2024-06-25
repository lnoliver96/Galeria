package otoni.luan.galeria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {
    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo);

        // tbPhoto deve ser considerado como a ActionBar padrão da tela

        Toolbar toolbar = findViewById(R.id.tbPhoto);
        setSupportActionBar(toolbar);

        //habilita o botão de voltar na ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Obtem o caminho da foto passada por intent
        Intent i = getIntent();
        photoPath = i.getStringExtra("photo_path");
        //carrega a foto em um Bitmap
        Bitmap bitmap = Util.getBitmap(photoPath);
        //seta o bitmap no ImageView
        ImageView imPhoto = findViewById(R.id.imPhoto);
        imPhoto.setImageBitmap(bitmap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        //cria um inflador de menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_activity_tb,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == R.id.opShare) {
            // caso item de compartilhar for clicado executa o código que dispara camera
            sharePhoto();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sharePhoto() {
        // Codigo para compartilhar a foto
        Uri photoUri = FileProvider.getUriForFile(PhotoActivity.this,"otoni.luan.galeria.fileprovider", new File(photoPath));
        //intent implicito indicando que queremos enviar  algo para  qualque app que seja capaz de aceitar o envio
        Intent i = new Intent(Intent.ACTION_SEND);
        //indica o arquivo a ser compartilhado
        i.putExtra(Intent.EXTRA_STREAM,photoUri);
        //executa o intent
        i.setType("image/jpeg");
        startActivity(i);
    }
}