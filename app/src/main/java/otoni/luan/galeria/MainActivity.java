package otoni.luan.galeria;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static int RESULT_TAKE_PICTURE = 1;
    String currentPhotoPath;
    List<String> photos = new ArrayList<>();

    static int RESULT_REQUEST_PERMISSION = 2;

    MainAdapter mainAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // tbMain deve ser considerado como a ActionBar padrão da tela
        Toolbar toolbar = findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);


        //Acessa o dietório "Pictures"
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //le a lisa
        File[] files = dir.listFiles();
        for(int i = 0; i < files.length;i++) {
            //adiciona na lista de fotos
            photos.add(files[i].getAbsolutePath());
        }

        //Cria o mainadapter e seta no recyclerview
        mainAdapter = new MainAdapter(MainActivity.this, photos);
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);

        // calcula quantas colunas de fotos cabem na tela
        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this,w);
        //configura o recyclerView para exibir as fotos em grid respeitando o maximo de colunas da tela
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);

        //chama os metodos pedindo as permissoes necessarias
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        checkForPermissions(permissions);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        //cria um inflador de menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opCamera) {
            // caso item da camera for clicado executa o código que dispara camera
            dispatchTakePictureIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        //cria o arquivo vazio
        File f = null;

        try{
            f = createImageFile();
        }
        //Caso o arquivo não possa ser criado, é exibida uma mensagem para o usuário e o metodo retorna
        catch(IOException e){
            Toast.makeText(MainActivity.this,"Não foi possível criar arquivo",Toast.LENGTH_LONG).show();
            return;
        }
        //guarda o local do arquivo de foto que esta sendo manipulado no momento
        currentPhotoPath = f.getAbsolutePath();

        if(f != null){
            //gera endereco URI para o arquivo de foto
            Uri fUri = FileProvider.getUriForFile(MainActivity.this,"otoni.luan.galeria.fileprovider",f);
            //Cria Intent para disparar a app de camera
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //URI é passado para a app de câmera via Intent
            i.putExtra(MediaStore.EXTRA_OUTPUT,fUri);
            //inicia app camera e app fica em espera do resultado
            startActivityForResult(i,RESULT_TAKE_PICTURE);

        }
    }

    private File createImageFile() throws IOException{
        //Usa data e hora para criar um nome de arquivo diferente para cada foto tirada
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg",storageDir);
        return f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //verifica se a foto foi criada
        if (requestCode == RESULT_TAKE_PICTURE){
            if (resultCode == Activity.RESULT_OK){
                //adiciona a fota a lista
                photos.add(currentPhotoPath);
                //avisa o mainadapter q a nova foi adicionada e o recyclerview precisa ser atualizado
                mainAdapter.notifyItemInserted(photos.size()-1);
            }
            //se a foto nao foi tirada exclui o arquivo q iria conte-la
            else {
                File f = new File(currentPhotoPath);
                f.delete();
            }
        }
    }
    //metodo chamado quando o usuario clica na foto
    public void startPhotoActivity(String photoPath) {
        Intent i = new Intent(MainActivity.this,PhotoActivity.class);
        //passa o caminho da foto para Photo Activity com intent
        i.putExtra("photo_path",photoPath);
        startActivity(i);
    }

    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();
        //cada permissao eh verificada
        for (String permission : permissions){
            if(!hasPermission(permission)) {
                //caso o usuario nao tenha ainda confirmado uma permissao ela eh add a uma lista de permissoes nao confirmadas
                permissionsNotGranted.add(permission);
            }
        }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (permissionsNotGranted.size()>0){
                //as permissoes nao concedidas sao requisitadas ao usuário
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
            }
        }
    }

    private boolean hasPermission(String permission){
        //verifica se  uma determinada permissão já foi concedida ou não
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        final List<String> permissionsRejected = new ArrayList<>();
        if (requestCode == RESULT_REQUEST_PERMISSION){;
            //Para cada permissao eh verificado se foi concedida ou nao
            for(String permission : permissions){
                if (!hasPermission(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }

        if(permissionsRejected.size() > 0){
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    //exibe mensagem ao usuario informando  que a permissao eh necessaria
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            //requisita as permissoes novamente
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }
    }
}