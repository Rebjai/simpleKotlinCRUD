package com.example.materialdesign1

import android.content.ContentValues
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Variables para accesar a los views
        val control = findViewById<EditText>(R.id.txtControl)
        val nombre = findViewById<EditText>(R.id.txtNombre)
        val carrera = findViewById<EditText>(R.id.txtCarrera)
        //Botones
        val botonRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val botonEliminar = findViewById<Button>(R.id.btnEliminar)
        val botonModificar = findViewById<Button>(R.id.btnModificar)
        val botonConsultar = findViewById<Button>(R.id.btnConsultar)
        var seleccionado = ""

        botonRegistrar.setOnClickListener{
            if (control.text.isNotEmpty()) {

                val objDBHelper = DBHelper(this, "agenda", null, 1)
                //Abrir la base de datos para escritura y lectura
                val bd = objDBHelper.writableDatabase
                //crear un objeto para llenarlo de datos (Campos)
                val registro = ContentValues()
                registro.put("control", Integer.parseInt(control.text.toString()))
                registro.put("nombre", nombre.text.toString())
                registro.put("carrera", carrera.text.toString())
                //Registrar los datos (registro)
                bd.insert("estudiantes", null, registro)
                //Cerrar la base de datos
                bd.close()
                control.setText("")
                nombre.setText("")
                carrera.setText("")
                control.requestFocus()
                Toast.makeText(this, "Registro Almacenado", Toast.LENGTH_LONG).show()
            }else {
                Toast.makeText(this, "Por favor introduzca un número de control", Toast.LENGTH_LONG)
                    .show()
            }
        }

        botonConsultar.setOnClickListener{
            if (control.text.isNotEmpty()) {
                val objDBHelper = DBHelper(this, "agenda", null, 1)
                //Abrir la base de datos para escritura y lectura
                val bd = objDBHelper.writableDatabase
                seleccionado = control.text.toString()
                // reg es un objeto tipo arreglo que me permite almacenar los datos de la consulta
                val reg = bd.rawQuery(
                    "SELECT nombre, carrera from estudiantes where control=${
                        Integer.parseInt(seleccionado)
                    }", null
                )
                if (reg.moveToFirst()) {
                    nombre.setText(reg.getString(0))
                    carrera.setText(reg.getString(1))
                } else {
                    Toast.makeText(this, "El registro no existe", Toast.LENGTH_LONG).show()
                }
                reg.close()
                bd.close()
            }
            else {
                Toast.makeText(this, "Por favor introduzca un número de control", Toast.LENGTH_LONG)
                    .show()
            }

        }

        botonModificar.setOnClickListener {
            if (seleccionado.isNotEmpty()) {

                createDialog(
                    "Advertencia",
                    "¿Está seguro que quiere eliminar el registro $seleccionado ?"
                ) {
                    val objDBHelper = DBHelper(this, "agenda", null, 1)
                    //Abrir la base de datos para escritura y lectura
                    val bd = objDBHelper.writableDatabase

                    val registro = ContentValues()
                    registro.put("control", Integer.parseInt(control.text.toString()))
                    registro.put("nombre", nombre.text.toString())
                    registro.put("carrera", carrera.text.toString())
                    // reg es un objeto tipo arreglo que me permite almacenar los datos de la consulta
                    bd.update("estudiantes",registro,"control=${control.text.toString()}",null)
                    bd.close()
                    Toast.makeText(this, "Usuario Actualizado", Toast.LENGTH_LONG)
                        .show()
                    control.setText("")
                    nombre.setText("")
                    carrera.setText("")
                    seleccionado=""
                }
            }
            else {
                Toast.makeText(this, "Por favor introduzca un número de control", Toast.LENGTH_LONG)
                    .show()
            }





        }

        botonEliminar.setOnClickListener {
            if (seleccionado.isNotEmpty()) {
                createDialog(
                    "Advertencia",
                    "¿Está seguro que quiere eliminar el registro ${control.text.toString()} ?"
                ) {
                    val objDBHelper = DBHelper(this, "agenda", null, 1)
                    //Abrir la base de datos para escritura y lectura
                    val bd = objDBHelper.writableDatabase
                    // reg es un objeto tipo arreglo que me permite almacenar los datos de la consulta
                    bd.delete(
                        "estudiantes",
                        "control=${Integer.parseInt(control.text.toString())}",null
                    )
                    bd.close()
                    Toast.makeText(this, "Registro Eliminado", Toast.LENGTH_LONG)
                        .show()
                    control.setText("")
                    nombre.setText("")
                    carrera.setText("")
                    seleccionado=""
                }
            }
            else {
                Toast.makeText(this, "Por favor introduzca un número de control", Toast.LENGTH_LONG)
                    .show()
            }

        }



    }
    fun createDialog(title: String,message: String, accepFn: () -> Unit) : AlertDialog {
        val builder = AlertDialog.Builder(this)
        var response = false
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setNegativeButton(
            "Cancelar",
        ) { dialogInterface: DialogInterface, i: Int ->}
        builder.setPositiveButton("Continuar") { dialogInterface: DialogInterface, i: Int ->
            accepFn()
        }
        builder.show()
        return builder.create()
    }

}