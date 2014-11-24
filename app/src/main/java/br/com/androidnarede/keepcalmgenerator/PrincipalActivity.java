package br.com.androidnarede.keepcalmgenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


public class PrincipalActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_gerar) {
            gerarKeepCalm();
            return true;
        } else if (id == R.id.action_sobre){
            exibirDialogoSobre();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exibirDialogoSobre(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sobre")
               .setMessage("Essa app foi desenvolvida durante o CodeLab Introdução ao Android Studio.")
               .setCancelable(false)
               .setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                   }
               });
        AlertDialog dialogSobre = builder.create();
        dialogSobre.show();

    }

    private void gerarKeepCalm() {

        EditText campoLinha1 = (EditText) findViewById(R.id.campo_linha1);
        EditText campoLinha2 = (EditText) findViewById(R.id.campo_linha2);

        String linha1 = campoLinha1.getText().toString();
        String linha2 = campoLinha2.getText().toString();

        if (linha1.equals("") || linha2.equals("")){
            Toast.makeText(this, getString(R.string.mensagem_campos_nao_informados), Toast.LENGTH_SHORT).show();
        } else {
            View v = LayoutInflater.from(this).inflate(R.layout.keep_calm, null);
            LinearLayout viewRaiz = (LinearLayout) v.findViewById(R.id.view_raiz);

            TextView valorLinha1 = (TextView) v.findViewById(R.id.linha1);
            valorLinha1.setText(linha1);

            TextView valorLinha2 = (TextView) v.findViewById(R.id.linha2);
            valorLinha2.setText(linha2);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");

            v.layout(0, 0, 1080, 1920);
            Bitmap imagemDaView = converterViewParaBitmap(viewRaiz);
            intent.putExtra(Intent.EXTRA_STREAM, converterBitmapParaUri(this, imagemDaView));
            startActivity(Intent.createChooser(intent, getString(R.string.titulo_intent_chooser)));
        }
    }

    /**
     * Converte um View para Bitmap.
     *
     * @param view O view a ser convertido para Bitmap
     * @return O Bitmap resultante do view
     */
    private Bitmap converterViewParaBitmap(View view) {
        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        int width = view.getWidth();
        int height = view.getHeight();

        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c);

        return b;
    }

    /**
     * Converte Bitmap para Uri.
     *
     * @param inContext - O contexto do Bitmap (ex: Activity)
     * @param inImage - O Bitmap a ser convertido
     * @return
     */
    private Uri converterBitmapParaUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "keep_calm", null);
        return Uri.parse(path);
    }
}
