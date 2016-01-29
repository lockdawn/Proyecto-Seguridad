package net.gdgmisantla.proyecto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends Activity implements View.OnTouchListener {

    private Firebase firebase;

    String dir = "https://torrid-fire-3327.firebaseio.com/";
    String nombre = "";
    String direccion = "";

    Boolean addZone = false;
    String strName = null;

    private TextPaint mTextPaint = null;
    MenuItem menuZona = null;
    Rect imageBounds;
    int intrinsicHeight;
    int intrinsicWidth;
    int scaledHeight;
    int scaledWidth;
    float heightRatio;
    float widthRatio;
    int inicioX = 0;
    int inicioY = 0;
    int finX = 0;
    int finY = 0;
    int inicioX2 = 0;
    int inicioY2 = 0;
    int finX2 = 0;
    int finY2 = 0;
    Bitmap Imag;
    Canvas canvas;
    int picw, pich;
    int pix[];
    ImageView view;
    Bitmap bm;
    Boolean pased = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        Imag = BitmapFactory.decodeResource(getResources(), R.drawable.rgb);
        picw = Imag.getWidth();
        pich = Imag.getHeight();
        pix = new int[picw * pich];
        view = (ImageView) findViewById(R.id.imageView);
        view.setOnTouchListener(this);

        android.graphics.Bitmap.Config bitmapConfig = Imag.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        Imag = Imag.copy(bitmapConfig, true);
        canvas = new Canvas(Imag);
    }

    @Override
    protected void onStart() {
        bm = Bitmap.createBitmap(picw, pich, Bitmap.Config.ARGB_8888);
        firebase = new Firebase(dir);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        //Log.v("zona", zonas[0]);
                        inicioX2 = Integer.parseInt(data.child("inicioX").getValue().toString());
                        inicioY2 = Integer.parseInt(data.child("inicioY").getValue().toString());
                        finX2 = Integer.parseInt(data.child("finX").getValue().toString());
                        finY2 = Integer.parseInt(data.child("finY").getValue().toString());
                        if (data.child("status").getValue().toString().equals("verde")) {
                            if (pased) {
                                pix = ChannelGreen(Imag);
                                bm.setPixels(pix, 0, picw, 0, 0, picw, pich);
                                view.setImageBitmap(bm);
                                pased = false;
                            } else {
                                pix = ChannelGreen(bm);
                                bm.setPixels(pix, 0, picw, 0, 0, picw, pich);
                                view.setImageBitmap(bm);
                            }
                        } else if (data.child("status").getValue().toString().equals("amarillo")) {
                            if (pased) {
                                pix = ChannelYellow(Imag);
                                bm.setPixels(pix, 0, picw, 0, 0, picw, pich);
                                view.setImageBitmap(bm);
                                pased = false;
                            } else {
                                pix = ChannelYellow(bm);
                                bm.setPixels(pix, 0, picw, 0, 0, picw, pich);
                                view.setImageBitmap(bm);
                            }
                        } else {
                            if (pased) {
                                pix = ChannelRed(Imag);
                                bm.setPixels(pix, 0, picw, 0, 0, picw, pich);
                                view.setImageBitmap(bm);
                                pased = false;
                            } else {
                                pix = ChannelRed(bm);
                                bm.setPixels(pix, 0, picw, 0, 0, picw, pich);
                                view.setImageBitmap(bm);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error Loading Coords", e.toString());
                    }
                }
                pased = true;
            }

            @Override
            public void onCancelled(FirebaseError error) {
                Log.i("onCancelled", "Cancelacion de metodo de firebase");
            }
        });
        super.onStart();
    }

    public void dibujar() {
        this.onCreate(null);
        mTextPaint = new TextPaint();
        mTextPaint.setColor(this.getResources().getColor(android.R.color.holo_green_light));
        mTextPaint.setTextSize(20);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(this.getResources().getColor(android.R.color.holo_green_light));
        canvas.drawRect(inicioX, inicioY, finX, finY, paint);
        canvas.drawText("  (" + inicioX + ", " + inicioY + ")" + "," + "(" + finX + ", " + finY + ")", finX, finY, mTextPaint);
        view.setImageBitmap(Imag);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Drawable drawable = view.getDrawable();
            imageBounds = drawable.getBounds();

            // Altura y Anchura del bitmap
            intrinsicHeight = drawable.getIntrinsicHeight();
            intrinsicWidth = drawable.getIntrinsicWidth();

            // Altura y Anchura de la imagen visible (escalada)
            scaledHeight = imageBounds.height();
            scaledWidth = imageBounds.width();

            // Encontramos el radio de la imagen original entre la imagen escalada
            heightRatio = (float) intrinsicHeight / (float) scaledHeight;
            widthRatio = (float) intrinsicWidth / (float) scaledWidth;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int scaledImageOffsetX = (int) (event.getX() - imageBounds.left);
                int scaledImageOffsetY = (int) (event.getY() - imageBounds.top);
                inicioX = (int) (scaledImageOffsetX * widthRatio);
                inicioY = (int) (scaledImageOffsetY * heightRatio);
                view.invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                scaledImageOffsetX = (int) (event.getX() - imageBounds.left);
                scaledImageOffsetY = (int) (event.getY() - imageBounds.top);
                finX = (int) (scaledImageOffsetX * widthRatio);
                finY = (int) (scaledImageOffsetY * heightRatio);
                view.invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (addZone) {
                    dibujar();
                }
                view.invalidate();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuZona = menu.getItem(2);
        menuZona.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.statusZonaMenu:
                finish();
                startActivity(getIntent());
                return true;
            case R.id.marcarZonaMenu:
                addZone = true;
                menuZona.setEnabled(true);
                return true;
            case R.id.agregarZonaMenu:
                if (addZone) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Escriba el nombre de la zona:");
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            nombre = input.getText().toString();
                            if (nombre.equals("")) {
                                Toast.makeText(getApplicationContext(), "ERROR: No debes dejar el campo vac\u00edo", Toast.LENGTH_LONG).show();
                            } else {
                                char[] caracteres = nombre.toCharArray();
                                caracteres[0] = Character.toUpperCase(caracteres[0]);
                                nombre = String.valueOf(caracteres);
                                direccion = dir + nombre;
                                firebase = new Firebase(direccion);
                                firebase.child("finX").setValue(finX);
                                firebase.child("finY").setValue(finY);
                                firebase.child("inicioX").setValue(inicioX);
                                firebase.child("inicioY").setValue(inicioY);
                                firebase.child("status").setValue("verde");
                                addZone = false;
                                finish();
                                startActivity(getIntent());
                                dialog.cancel();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            menuZona.setEnabled(true);
                            addZone = true;
                            dialog.cancel();
                        }
                    });
                    AlertDialog alerta = builder.create();
                    alerta.show();
                    //this.onCreate(null);
                }
                menuZona.setEnabled(false);
                view.invalidate();
                return true;
            case R.id.eliminarZonaMenu:
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                builderSingle.setIcon(R.drawable.delete);
                builderSingle.setTitle("Selecciona la zona a eliminar:");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.select_dialog_singlechoice);

                firebase = new Firebase(dir);
                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            arrayAdapter.add(data.getKey().toString());
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                    }

                });

                builderSingle.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        strName = arrayAdapter.getItem(i);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                        builderInner.setTitle("¿Deseas eliminar la siguiente zona?");
                        builderInner.setMessage(strName);
                        builderInner.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                direccion = dir + strName;
                                firebase = new Firebase(direccion);
                                firebase.removeValue();
                            }
                        });
                        builderInner.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
                view.invalidate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int[] ChannelRed(Bitmap mBitmap) {
        int sx = inicioX2;
        int sy = inicioY2;
        int fx = finX2;
        int fy = finY2;
        //Toast.makeText(getApplicationContext(), "Coordenadas (" + sx + ", " + sy + ", " + fx + ", " + fy + ")", Toast.LENGTH_LONG).show();
        int picw = mBitmap.getWidth();
        int pich = mBitmap.getHeight();
        int[] pix = new int[picw * pich];
        mBitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
        for (int y = sy; y < fy; y++)
            for (int x = sx; x < fx; x++) {
                int index = (y * picw) + x;
                int r = (pix[index] >> 16) & 0xff;
                pix[index] = 0xff000000 | (r << 16) | (0 << 8) | 0;
            }
        return pix;
    }

    private int[] ChannelGreen(Bitmap mBitmap) {
        int sx = inicioX2;
        int sy = inicioY2;
        int fx = finX2;
        int fy = finY2;
        int picw = mBitmap.getWidth();
        int pich = mBitmap.getHeight();
        int[] pix = new int[picw * pich];
        mBitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
        for (int y = sy; y < fy; y++)
            for (int x = sx; x < fx; x++) {
                int index = (y * picw) + x;
                int g = (pix[index] >> 8) & 0xff;
                pix[index] = 0xff000000 | (0 << 16) | (g << 8) | 0;
            }
        return pix;
    }

    private int[] ChannelYellow(Bitmap mBitmap) {
        int sx = inicioX2;
        int sy = inicioY2;
        int fx = finX2;
        int fy = finY2;
        picw = mBitmap.getWidth();
        pich = mBitmap.getHeight();
        int[] pix = new int[picw * pich];
        mBitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
        for (int y = sy; y < fy; y++)
            for (int x = sx; x < fx; x++) {
                int index = (y * picw) + x;
                int r = (pix[index] >> 16) & 0xff;
                int g = (pix[index] >> 8) & 0xff;
                pix[index] = 0xff000000 | (r << 16) | (g << 8) | 0;
            }
        return pix;
    }
}