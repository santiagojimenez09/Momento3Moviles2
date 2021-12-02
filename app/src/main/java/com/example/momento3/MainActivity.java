package com.example.momento3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    TextView jtvnotafinal;
    EditText jetcodigo,jetnota1,jetnota2,jetnota3;
    Button jbtguardar,jbtconsultar,jbtmodificar,jbteliminar,jbtlimpiar;
    String codigo,nota1,nota2,nota3,idnotas;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="MsgCon";
    String collection="notas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        jtvnotafinal=findViewById(R.id.tvnotafinal);
        jetcodigo=findViewById(R.id.etcodigo);
        jetnota1=findViewById(R.id.etnota1);
        jetnota2=findViewById(R.id.etnota2);
        jetnota3=findViewById(R.id.etnota3);
        jbtguardar=findViewById(R.id.btguardar);
        jbtconsultar=findViewById(R.id.btconsultar);
        jbtmodificar=findViewById(R.id.btmodificar);
        jbteliminar=findViewById(R.id.bteliminar);
        jbtlimpiar=findViewById(R.id.btlimpiar);

    }

    public void calcular_nota(){

        codigo=jetcodigo.getText().toString();
        nota1=jetnota1.getText().toString();
        nota2=jetnota2.getText().toString();
        nota3=jetnota3.getText().toString();
        if (codigo.isEmpty() || nota1.isEmpty() || nota2.isEmpty() || nota3.isEmpty()){
            Toast.makeText(this,"Debe ingresar todos los datos",Toast.LENGTH_LONG).show();
            jetcodigo.requestFocus();
        }else{
            float n1,n2,n3,nota_final;
            n1=Float.parseFloat(nota1);
            n2=Float.parseFloat(nota2);
            n3=Float.parseFloat(nota3);

            nota_final = (n1 + n2 + n3) / 3;
            jtvnotafinal.setText(String.valueOf(nota_final));
        }


    }

    public void Calcular(View view){
        calcular_nota();
    }

    public void Guardar(View view){
        calcular_nota();
        Map<String, Object> user = new HashMap<>();
        user.put("codigo_nota",codigo);
        user.put("nota1",nota1);
        user.put("nota2",nota2);
        user.put("nota3",nota3);

        db.collection("notas")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"Nota adicionado"+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v(TAG,"Error guardando la nota",e);
                    }
                });
            limpiar_campos();

    }

    public void Consultar(View view){
        codigo=jetcodigo.getText().toString();
        if (codigo.isEmpty()){
            Toast.makeText(this,"El codigo es requerido para la consulta",Toast.LENGTH_LONG).show();
            jetcodigo.requestFocus();
        }else{
            db.collection("notas")
                    .whereEqualTo("codigo_nota",codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    Log.i("INFO",document.toString());
                                    idnotas=document.getId();
                                    jetnota1.setText(document.getString("nota1"));
                                    jetnota2.setText(document.getString("nota2"));
                                    jetnota3.setText(document.getString("nota3"));
                                    calcular_nota();
                                }
                            }else{
                                Toast.makeText(MainActivity.this,"Error consultando el registro",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void Modificar(View view){
        calcular_nota();
        Map<String, Object> user = new HashMap<>();
        user.put("codigo_nota",codigo);
        user.put("nota1",nota1);
        user.put("nota2",nota2);
        user.put("nota3",nota3);

        db.collection(collection).document(idnotas)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this,"Notas actualizadas correctamente",Toast.LENGTH_LONG).show();
                        limpiar_campos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Error actualizando las notas",Toast.LENGTH_LONG).show();
                        limpiar_campos();
                    }
                });
    }

    public void Eliminar(View view){
        String codigo;
        codigo=jetcodigo.getText().toString();
        if (codigo.isEmpty()){
            Toast.makeText(this,"El codigo es requerido",Toast.LENGTH_LONG).show();
            jetcodigo.requestFocus();
        }else{
            db.collection(collection).document(idnotas)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this,"Notas eliminadas correctamente",Toast.LENGTH_LONG).show();
                            limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,"Error eliminando las notas",Toast.LENGTH_LONG).show();
                            limpiar_campos();
                        }
                    });
        }
    }

    public void Limpiar(View view){
        limpiar_campos();
    }

    public void limpiar_campos(){
        jetcodigo.setText("");
        jetnota1.setText("");
        jetnota2.setText("");
        jetnota3.setText("");
        jtvnotafinal.setText("0");
        jetcodigo.requestFocus();
    }
}